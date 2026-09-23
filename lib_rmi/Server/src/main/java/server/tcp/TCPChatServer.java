package server.tcp;

import common.chat.ChatPacket;
import server.db.ChatDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TCPChatServer {
    public static final int CHAT_PORT = 7777;
    private static final ConcurrentHashMap<String, ClientHandler> activeClients = new ConcurrentHashMap<>();
    private static boolean isRunning = false;

    public static void startServer() {
        if (isRunning) return;
        isRunning = true;

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(CHAT_PORT, 50, InetAddress.getByName("0.0.0.0"))) {
                System.out.println(">> TCP Chat Server running on 0.0.0.0:" + CHAT_PORT);

                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println(">> TCP Chat Client connected from: " + clientSocket.getRemoteSocketAddress());
                        ClientHandler handler = new ClientHandler(clientSocket);
                        new Thread(handler).start();
                    } catch (IOException e) {
                        if (!isRunning) break;
                        System.err.println(">> ERROR accepting TCP Chat Client: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println(">> Could not bind TCP Chat Server on port " + CHAT_PORT + ": " + e.getMessage());
            }
        }, "TCPChatServer-Thread").start();
    }

    public static void broadcastUserList() {
        ChatPacket packet = new ChatPacket();
        packet.setType(ChatPacket.Type.USER_LIST);
        packet.setUserList(new ArrayList<>(activeClients.keySet()));

        for (ClientHandler handler : activeClients.values()) {
            handler.sendPacket(packet);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                while (!socket.isClosed()) {
                    Object obj = in.readObject();
                    if (obj instanceof ChatPacket) {
                        ChatPacket packet = (ChatPacket) obj;
                        handlePacket(packet);
                    }
                }
            } catch (Exception e) {
                // Client disconnected
            } finally {
                cleanup();
            }
        }

        private void handlePacket(ChatPacket packet) {
            if (packet == null || packet.getType() == null) return;

            switch (packet.getType()) {
                case JOIN:
                    this.username = packet.getSender();
                    if (this.username != null && !this.username.trim().isEmpty()) {
                        activeClients.put(this.username, this);
                        System.out.println(">> Chat User joined: " + this.username);
                        broadcastUserList();

                        // Send public chat history automatically upon join
                        List<ChatPacket> publicHistory = ChatDAO.getHistory("#Sanh-Chung", null);
                        ChatPacket historyResp = new ChatPacket();
                        historyResp.setType(ChatPacket.Type.HISTORY_RESP);
                        historyResp.setReceiver("#Sanh-Chung");
                        historyResp.setHistoryList(publicHistory);
                        sendPacket(historyResp);
                    }
                    break;

                case LEAVE:
                    cleanup();
                    break;

                case HISTORY_REQ:
                    String user1 = packet.getSender();
                    String user2 = packet.getReceiver();
                    List<ChatPacket> history = ChatDAO.getHistory(user1, user2);
                    ChatPacket historyResp = new ChatPacket();
                    historyResp.setType(ChatPacket.Type.HISTORY_RESP);
                    historyResp.setSender(user1);
                    historyResp.setReceiver(user2);
                    historyResp.setHistoryList(history);
                    sendPacket(historyResp);
                    break;

                case TEXT:
                case IMAGE:
                case FILE_NOTIF:
                    String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    packet.setTimestamp(now);

                    // Save to MySQL DB
                    ChatDAO.saveMessage(packet);

                    if ("#Sanh-Chung".equalsIgnoreCase(packet.getReceiver()) || packet.getReceiver() == null) {
                        // Broadcast to everyone
                        for (ClientHandler client : activeClients.values()) {
                            client.sendPacket(packet);
                        }
                    } else {
                        // Route 1-on-1 private message
                        ClientHandler target = activeClients.get(packet.getReceiver());
                        if (target != null) {
                            target.sendPacket(packet);
                        }
                        // Send back to sender so UI updates immediately (if sender != target)
                        if (target != this) {
                            sendPacket(packet);
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        public synchronized void sendPacket(ChatPacket packet) {
            try {
                if (out != null && !socket.isClosed()) {
                    out.writeObject(packet);
                    out.flush();
                    out.reset();
                }
            } catch (IOException e) {
                System.err.println(">> Error sending packet to " + username + ": " + e.getMessage());
            }
        }

        private void cleanup() {
            if (username != null) {
                activeClients.remove(username);
                System.out.println(">> Chat User left: " + username);
                broadcastUserList();
            }
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ignored) {}
        }
    }
}
