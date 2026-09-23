package server.db;

import common.chat.ChatPacket;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {
    private static final DBConnection dbConn = new DBConnection();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS `chat_history` ("
                + " `id` INT NOT NULL AUTO_INCREMENT,"
                + " `sender` VARCHAR(255) NOT NULL,"
                + " `receiver` VARCHAR(255) NOT NULL,"
                + " `msg_type` VARCHAR(50) NOT NULL,"
                + " `content` TEXT,"
                + " `image_bytes` LONGBLOB,"
                + " `file_name` VARCHAR(255),"
                + " `file_size` BIGINT DEFAULT 0,"
                + " `sent_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + " PRIMARY KEY (`id`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";

        try (Connection conn = dbConn.getConnect();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sql);
                System.out.println(">> INFO: Verified/Created table `chat_history` successfully.");
            }
        } catch (SQLException e) {
            System.err.println(">> ERROR: Failed to create table `chat_history`: " + e.getMessage());
        }
    }

    public static void saveMessage(ChatPacket packet) {
        if (packet == null || packet.getType() == null) return;
        if (packet.getType() != ChatPacket.Type.TEXT &&
            packet.getType() != ChatPacket.Type.IMAGE &&
            packet.getType() != ChatPacket.Type.FILE_NOTIF) {
            return;
        }

        String sql = "INSERT INTO `chat_history` (sender, receiver, msg_type, content, image_bytes, file_name, file_size, sent_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConn.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return;

            stmt.setString(1, packet.getSender() != null ? packet.getSender() : "Unknown");
            stmt.setString(2, packet.getReceiver() != null ? packet.getReceiver() : "#Sanh-Chung");
            stmt.setString(3, packet.getType().name());
            stmt.setString(4, packet.getContent() != null ? packet.getContent() : "");
            
            if (packet.getImageBytes() != null) {
                stmt.setBytes(5, packet.getImageBytes());
            } else {
                stmt.setNull(5, Types.BLOB);
            }

            stmt.setString(6, packet.getFileName());
            stmt.setLong(7, packet.getFileSize());

            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(8, now);

            stmt.executeUpdate();
            packet.setTimestamp(dateFormat.format(now));
        } catch (SQLException e) {
            System.err.println(">> ERROR: Failed to save chat message: " + e.getMessage());
        }
    }

    public static List<ChatPacket> getHistory(String roomOrUser1, String user2) {
        List<ChatPacket> list = new ArrayList<>();
        String sql;
        boolean isPublicRoom = "#Sanh-Chung".equalsIgnoreCase(roomOrUser1) || user2 == null || user2.isEmpty();

        if (isPublicRoom) {
            sql = "SELECT * FROM `chat_history` WHERE receiver = '#Sanh-Chung' ORDER BY id ASC LIMIT 200";
        } else {
            sql = "SELECT * FROM `chat_history` WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) ORDER BY id ASC LIMIT 200";
        }

        try (Connection conn = dbConn.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return list;

            if (!isPublicRoom) {
                stmt.setString(1, roomOrUser1);
                stmt.setString(2, user2);
                stmt.setString(3, user2);
                stmt.setString(4, roomOrUser1);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChatPacket p = new ChatPacket();
                    p.setType(ChatPacket.Type.valueOf(rs.getString("msg_type")));
                    p.setSender(rs.getString("sender"));
                    p.setReceiver(rs.getString("receiver"));
                    p.setContent(rs.getString("content"));
                    p.setImageBytes(rs.getBytes("image_bytes"));
                    p.setFileName(rs.getString("file_name"));
                    p.setFileSize(rs.getLong("file_size"));
                    
                    Timestamp ts = rs.getTimestamp("sent_at");
                    if (ts != null) {
                        p.setTimestamp(dateFormat.format(ts));
                    }
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println(">> ERROR: Failed to fetch chat history: " + e.getMessage());
        }

        return list;
    }
}
