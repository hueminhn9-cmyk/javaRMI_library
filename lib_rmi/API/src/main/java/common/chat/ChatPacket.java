package common.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatPacket implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        TEXT,
        IMAGE,
        FILE_NOTIF,
        JOIN,
        LEAVE,
        USER_LIST,
        HISTORY_REQ,
        HISTORY_RESP
    }

    private Type type;
    private String sender;
    private String receiver; // "#Sanh-Chung" for broadcast, or specific user email/name for 1-1 chat
    private String content;
    private byte[] imageBytes;
    private String fileName;
    private long fileSize;
    private String timestamp;
    private List<String> userList = new ArrayList<>();
    private List<ChatPacket> historyList = new ArrayList<>();

    public ChatPacket() {}

    public ChatPacket(Type type, String sender, String receiver, String content) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public List<ChatPacket> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<ChatPacket> historyList) {
        this.historyList = historyList;
    }
}
