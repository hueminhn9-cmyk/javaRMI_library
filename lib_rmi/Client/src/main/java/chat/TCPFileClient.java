package chat;

import common.chat.FilePacket;

import java.io.*;
import java.net.Socket;

public class TCPFileClient {

    public interface FileProgressListener {
        void onProgress(int percent, String statusMessage);
        void onComplete(boolean success, String message, File downloadedFile);
    }

    public static void uploadFile(String host, int port, File file, String sender, String receiver, FileProgressListener listener) {
        new Thread(() -> {
            if (file == null || !file.exists()) {
                if (listener != null) listener.onComplete(false, "File không tồn tại!", null);
                return;
            }

            try (Socket socket = new Socket(host, port);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.flush();
                long totalBytes = file.length();
                int chunkSize = 64 * 1024; // 64KB per chunk
                int totalChunks = (int) Math.ceil((double) totalBytes / chunkSize);

                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[chunkSize];
                    int bytesRead;
                    int chunkIndex = 0;
                    long bytesSent = 0;

                    while ((bytesRead = fis.read(buffer)) != -1) {
                        FilePacket packet = new FilePacket(
                                chunkIndex == 0 ? FilePacket.Type.UPLOAD_REQ : FilePacket.Type.UPLOAD_DATA,
                                file.getName(), sender, receiver
                        );
                        packet.setFileSize(totalBytes);
                        packet.setCurrentChunk(chunkIndex);
                        packet.setTotalChunks(totalChunks);

                        byte[] chunkData = new byte[bytesRead];
                        System.arraycopy(buffer, 0, chunkData, 0, bytesRead);
                        packet.setFileData(chunkData);

                        out.writeObject(packet);
                        out.flush();

                        bytesSent += bytesRead;
                        chunkIndex++;

                        int percent = (int) ((bytesSent * 100) / totalBytes);
                        if (listener != null) {
                            listener.onProgress(percent, "Đang tải lên " + file.getName() + " (" + percent + "%)...");
                        }
                    }
                }

                Object respObj = in.readObject();
                if (respObj instanceof FilePacket) {
                    FilePacket resp = (FilePacket) respObj;
                    if (resp.getType() == FilePacket.Type.SUCCESS) {
                        if (listener != null) listener.onComplete(true, resp.getStatusMessage(), file);
                    } else {
                        if (listener != null) listener.onComplete(false, resp.getStatusMessage(), null);
                    }
                }
            } catch (Exception e) {
                if (listener != null) listener.onComplete(false, "Lỗi truyền file: " + e.getMessage(), null);
            }
        }, "TCPFileClient-Upload").start();
    }

    public static void downloadFile(String host, int port, String fileName, File saveDest, String sender, FileProgressListener listener) {
        new Thread(() -> {
            try (Socket socket = new Socket(host, port);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.flush();
                FilePacket request = new FilePacket(FilePacket.Type.DOWNLOAD_REQ, fileName, sender, "SERVER");
                out.writeObject(request);
                out.flush();

                try (FileOutputStream fos = new FileOutputStream(saveDest)) {
                    long totalBytes = 0;
                    long bytesReceived = 0;

                    while (true) {
                        Object respObj = in.readObject();
                        if (respObj instanceof FilePacket) {
                            FilePacket packet = (FilePacket) respObj;

                            if (packet.getType() == FilePacket.Type.ERROR) {
                                if (listener != null) listener.onComplete(false, packet.getStatusMessage(), null);
                                return;
                            }

                            if (packet.getType() == FilePacket.Type.DOWNLOAD_DATA) {
                                totalBytes = packet.getFileSize();
                                if (packet.getFileData() != null) {
                                    fos.write(packet.getFileData());
                                    bytesReceived += packet.getFileData().length;
                                }

                                int percent = totalBytes > 0 ? (int) ((bytesReceived * 100) / totalBytes) : 0;
                                if (listener != null) {
                                    listener.onProgress(percent, "Đang tải về " + fileName + " (" + percent + "%)...");
                                }
                            }

                            if (packet.getType() == FilePacket.Type.SUCCESS) {
                                if (listener != null) listener.onComplete(true, "Tải file hoàn tất: " + fileName, saveDest);
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                if (listener != null) listener.onComplete(false, "Lỗi tải file: " + e.getMessage(), null);
            }
        }, "TCPFileClient-Download").start();
    }
}
