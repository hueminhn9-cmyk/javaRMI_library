package chat;

import common.chat.ChatPacket;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TCPChatClient {
    public interface ChatListener {
        void onPacketReceived(ChatPacket packet);
        void onConnectionStatusChanged(boolean connected, String message);
    }

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private boolean isConnected = false;
    private final List<ChatListener> listeners = new ArrayList<>();

    public void addListener(ChatListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(ChatListener listener) {
        listeners.remove(listener);
    }

    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }

    public void connect(String host, int port, String username) throws IOException {
        if (isConnected()) {
            disconnect();
        }

        this.username = username;
        this.socket = new Socket(host, port);
        
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());

        this.isConnected = true;

        // Send JOIN packet immediately
        ChatPacket joinPacket = new ChatPacket(ChatPacket.Type.JOIN, username, "#Sanh-Chung", username + " connected.");
        sendPacket(joinPacket);

        notifyConnectionStatus(true, "Kết nối thành công đến " + host + ":" + port);

        // Start listening thread
        new Thread(this::listenLoop, "TCPChatClient-Receiver").start();
    }

    public void disconnect() {
        if (!isConnected) return;
        isConnected = false;

        try {
            if (out != null) {
                ChatPacket leavePacket = new ChatPacket(ChatPacket.Type.LEAVE, username, "#Sanh-Chung", "Disconnected");
                out.writeObject(leavePacket);
                out.flush();
            }
        } catch (Exception ignored) {}

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}

        notifyConnectionStatus(false, "Đã ngắt kết nối Server.");
    }

    private void listenLoop() {
        try {
            while (isConnected && !socket.isClosed()) {
                Object obj = in.readObject();
                if (obj instanceof ChatPacket) {
                    ChatPacket packet = (ChatPacket) obj;
                    notifyPacketReceived(packet);
                }
            }
        } catch (Exception e) {
            if (isConnected) {
                isConnected = false;
                notifyConnectionStatus(false, "Mất kết nối với Server: " + e.getMessage());
            }
        }
    }

    public synchronized void sendPacket(ChatPacket packet) {
        if (!isConnected()) return;
        try {
            out.writeObject(packet);
            out.flush();
            out.reset();
        } catch (IOException e) {
            notifyConnectionStatus(false, "Lỗi gửi dữ liệu: " + e.getMessage());
            disconnect();
        }
    }

    public void sendTextMessage(String receiver, String text) {
        if (text == null || text.trim().isEmpty()) return;
        ChatPacket packet = new ChatPacket(ChatPacket.Type.TEXT, username, receiver, text.trim());
        sendPacket(packet);
    }

    public void sendImageMessage(String receiver, File imageFile) throws IOException {
        if (imageFile == null || !imageFile.exists()) return;
        byte[] bytes = Files.readAllBytes(imageFile.toPath());

        ChatPacket packet = new ChatPacket(ChatPacket.Type.IMAGE, username, receiver, "[Hình ảnh]");
        packet.setImageBytes(bytes);
        packet.setFileName(imageFile.getName());
        packet.setFileSize(imageFile.length());
        sendPacket(packet);
    }

    public void requestHistory(String roomOrUser) {
        ChatPacket packet = new ChatPacket(ChatPacket.Type.HISTORY_REQ, username, roomOrUser, null);
        sendPacket(packet);
    }

    private void notifyPacketReceived(ChatPacket packet) {
        for (ChatListener listener : listeners) {
            listener.onPacketReceived(packet);
        }
    }

    private void notifyConnectionStatus(boolean status, String message) {
        for (ChatListener listener : listeners) {
            listener.onConnectionStatusChanged(status, message);
        }
    }
}
