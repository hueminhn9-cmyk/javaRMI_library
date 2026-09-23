package common.chat;

import java.io.Serializable;

public class FilePacket implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        UPLOAD_REQ,
        UPLOAD_DATA,
        DOWNLOAD_REQ,
        DOWNLOAD_DATA,
        SUCCESS,
        ERROR
    }

    private Type type;
    private String fileName;
    private long fileSize;
    private byte[] fileData;
    private String sender;
    private String receiver;
    private int currentChunk;
    private int totalChunks;
    private String statusMessage;

    public FilePacket() {}

    public FilePacket(Type type, String fileName, String sender, String receiver) {
        this.type = type;
        this.fileName = fileName;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getCurrentChunk() {
        return currentChunk;
    }

    public void setCurrentChunk(int currentChunk) {
        this.currentChunk = currentChunk;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
