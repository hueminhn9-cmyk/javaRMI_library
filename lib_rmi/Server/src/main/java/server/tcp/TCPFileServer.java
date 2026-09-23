package server.tcp;

import common.chat.FilePacket;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPFileServer {
    public static final int FILE_PORT = 8888;
    private static final String UPLOAD_DIR = "uploads";
    private static boolean isRunning = false;

    public static void startServer() {
        if (isRunning) return;
        isRunning = true;

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(FILE_PORT, 50, InetAddress.getByName("0.0.0.0"))) {
                System.out.println(">> TCP File Server running on 0.0.0.0:" + FILE_PORT + " (Storage: " + dir.getAbsolutePath() + ")");

                while (isRunning) {
                    try {
                        Socket socket = serverSocket.accept();
                        new Thread(new FileClientHandler(socket)).start();
                    } catch (IOException e) {
                        if (!isRunning) break;
                        System.err.println(">> ERROR accepting TCP File Client: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println(">> Could not bind TCP File Server on port " + FILE_PORT + ": " + e.getMessage());
            }
        }, "TCPFileServer-Thread").start();
    }

    private static class FileClientHandler implements Runnable {
        private final Socket socket;

        public FileClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.flush();
                Object obj = in.readObject();

                if (obj instanceof FilePacket) {
                    FilePacket packet = (FilePacket) obj;
                    if (packet.getType() == FilePacket.Type.UPLOAD_REQ || packet.getType() == FilePacket.Type.UPLOAD_DATA) {
                        handleUpload(packet, in, out);
                    } else if (packet.getType() == FilePacket.Type.DOWNLOAD_REQ) {
                        handleDownload(packet, out);
                    }
                }
            } catch (Exception e) {
                System.err.println(">> Error handling File Transfer request: " + e.getMessage());
            } finally {
                try {
                    if (!socket.isClosed()) socket.close();
                } catch (IOException ignored) {}
            }
        }

        private void handleUpload(FilePacket initialPacket, ObjectInputStream in, ObjectOutputStream out) throws Exception {
            String fileName = new File(initialPacket.getFileName()).getName();
            File destFile = new File(UPLOAD_DIR, fileName);

            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                if (initialPacket.getFileData() != null && initialPacket.getFileData().length > 0) {
                    fos.write(initialPacket.getFileData());
                }

                while (initialPacket.getCurrentChunk() < initialPacket.getTotalChunks() - 1) {
                    Object nextObj = in.readObject();
                    if (nextObj instanceof FilePacket) {
                        initialPacket = (FilePacket) nextObj;
                        if (initialPacket.getFileData() != null) {
                            fos.write(initialPacket.getFileData());
                        }
                    } else {
                        break;
                    }
                }
            }

            FilePacket successResp = new FilePacket(FilePacket.Type.SUCCESS, fileName, "SERVER", initialPacket.getSender());
            successResp.setStatusMessage("Upload file " + fileName + " thành công!");
            out.writeObject(successResp);
            out.flush();
            System.out.println(">> TCP File Upload complete: " + destFile.getAbsolutePath());
        }

        private void handleDownload(FilePacket requestPacket, ObjectOutputStream out) throws Exception {
            String fileName = new File(requestPacket.getFileName()).getName();
            File srcFile = new File(UPLOAD_DIR, fileName);

            if (!srcFile.exists()) {
                FilePacket errPacket = new FilePacket(FilePacket.Type.ERROR, fileName, "SERVER", requestPacket.getSender());
                errPacket.setStatusMessage("File không tồn tại trên Server!");
                out.writeObject(errPacket);
                out.flush();
                return;
            }

            long totalBytes = srcFile.length();
            int chunkSize = 64 * 1024; // 64KB per chunk
            int totalChunks = (int) Math.ceil((double) totalBytes / chunkSize);

            try (FileInputStream fis = new FileInputStream(srcFile)) {
                byte[] buffer = new byte[chunkSize];
                int bytesRead;
                int chunkIndex = 0;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    FilePacket dataPacket = new FilePacket(FilePacket.Type.DOWNLOAD_DATA, fileName, "SERVER", requestPacket.getSender());
                    dataPacket.setFileSize(totalBytes);
                    dataPacket.setCurrentChunk(chunkIndex);
                    dataPacket.setTotalChunks(totalChunks);

                    byte[] chunkData = new byte[bytesRead];
                    System.arraycopy(buffer, 0, chunkData, 0, bytesRead);
                    dataPacket.setFileData(chunkData);

                    out.writeObject(dataPacket);
                    out.flush();
                    chunkIndex++;
                }

                FilePacket donePacket = new FilePacket(FilePacket.Type.SUCCESS, fileName, "SERVER", requestPacket.getSender());
                donePacket.setStatusMessage("Download file thành công!");
                out.writeObject(donePacket);
                out.flush();
                System.out.println(">> TCP File Download complete: " + srcFile.getName());
            }
        }
    }
}
