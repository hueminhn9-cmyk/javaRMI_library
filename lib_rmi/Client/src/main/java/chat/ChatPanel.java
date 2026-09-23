package chat;

import common.chat.ChatPacket;
import common.chat.FilePacket;
import common.rmi.Config;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class ChatPanel extends JPanel implements TCPChatClient.ChatListener {
    private final String currentUsername;
    private String activeTarget = "#Sanh-Chung"; // Default channel

    private final TCPChatClient chatClient;

    // GUI Components
    private JTextField txtServerIp;
    private JTextField txtPort;
    private JButton btnConnect;
    private JLabel lblStatus;

    private DefaultListModel<String> channelModel;
    private JList<String> lstChannels;
    private DefaultListModel<String> userListModel;
    private JList<String> lstUsers;

    private JTextPane chatPane;
    private StyledDocument chatDoc;
    private JTextField txtMessage;
    private JButton btnSend;
    private JButton btnSendImage;
    private JButton btnSendFile;

    private JLabel lblCurrentTarget;

    public ChatPanel(String currentUsername) {
        this.currentUsername = (currentUsername != null && !currentUsername.trim().isEmpty()) 
                ? currentUsername : "Guest-" + (int)(Math.random() * 1000);
        this.chatClient = new TCPChatClient();
        this.chatClient.addListener(this);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));

        // 1. Top Configuration Bar
        JPanel topConfigPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topConfigPanel.setBackground(new Color(241, 245, 249));
        topConfigPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(203, 213, 225)));

        topConfigPanel.add(new JLabel("Server IP:"));
        txtServerIp = new JTextField(Config.IP_SERVER, 12);
        topConfigPanel.add(txtServerIp);

        topConfigPanel.add(new JLabel("Port:"));
        txtPort = new JTextField(String.valueOf(Config.PORT_CHAT), 5);
        topConfigPanel.add(txtPort);

        btnConnect = new JButton("KẾT NỐI SERVER");
        btnConnect.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnConnect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConnect.setBackground(Color.WHITE);
        btnConnect.setForeground(Color.BLACK);
        btnConnect.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(37, 99, 235), 2, true),
            new javax.swing.border.EmptyBorder(4, 10, 4, 10)
        ));
        btnConnect.setFocusPainted(false);
        btnConnect.addActionListener(e -> toggleConnection());
        topConfigPanel.add(btnConnect);

        lblStatus = new JLabel("● Chưa kết nối");
        lblStatus.setForeground(Color.RED);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        topConfigPanel.add(lblStatus);

        add(topConfigPanel, BorderLayout.NORTH);

        // 2. Left Sidebar (Channels & Online Users)
        JPanel leftSidebar = new JPanel();
        leftSidebar.setLayout(new BoxLayout(leftSidebar, BoxLayout.Y_AXIS));
        leftSidebar.setPreferredSize(new Dimension(200, 0));
        leftSidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(203, 213, 225)));

        // Channels Panel
        JPanel pnlChannels = new JPanel(new BorderLayout());
        pnlChannels.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "Kênh Trò Chuyện", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(71, 85, 105)
        ));
        channelModel = new DefaultListModel<>();
        channelModel.addElement("#Sanh-Chung");
        lstChannels = new JList<>(channelModel);
        lstChannels.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lstChannels.setSelectedValue("#Sanh-Chung", true);
        lstChannels.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && lstChannels.getSelectedValue() != null) {
                lstUsers.clearSelection();
                switchTarget(lstChannels.getSelectedValue());
            }
        });
        pnlChannels.add(new JScrollPane(lstChannels), BorderLayout.CENTER);
        leftSidebar.add(pnlChannels);

        // Online Users Panel
        JPanel pnlUsers = new JPanel(new BorderLayout());
        pnlUsers.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "Đang Online", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(71, 85, 105)
        ));
        userListModel = new DefaultListModel<>();
        lstUsers = new JList<>(userListModel);
        lstUsers.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lstUsers.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && lstUsers.getSelectedValue() != null) {
                lstChannels.clearSelection();
                switchTarget(lstUsers.getSelectedValue());
            }
        });
        pnlUsers.add(new JScrollPane(lstUsers), BorderLayout.CENTER);
        leftSidebar.add(pnlUsers);

        add(leftSidebar, BorderLayout.WEST);

        // 3. Center Main Chat Area
        JPanel centerChatPanel = new JPanel(new BorderLayout());

        // Chat Header
        JPanel chatHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        chatHeader.setBackground(new Color(248, 250, 252));
        chatHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        lblCurrentTarget = new JLabel("Đang nhắn tại: #Sanh-Chung (Broadcast)");
        lblCurrentTarget.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCurrentTarget.setForeground(new Color(30, 41, 59));
        chatHeader.add(lblCurrentTarget);
        centerChatPanel.add(chatHeader, BorderLayout.NORTH);

        // Styled Chat Pane
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatDoc = chatPane.getStyledDocument();
        centerChatPanel.add(new JScrollPane(chatPane), BorderLayout.CENTER);

        // Bottom Input Controls
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        txtMessage = new JTextField();
        txtMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMessage.addActionListener(e -> sendMessage());
        inputPanel.add(txtMessage, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        btnSend = new JButton("Gửi");
        btnSend.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSend.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnSend.setBackground(new Color(224, 242, 254));
        btnSend.setForeground(Color.BLACK);
        ImageIcon paperPlaneIcon = patron.UIStyleHelper.getIcon("/images/paper-plane.png", 16, 16);
        if (paperPlaneIcon != null) btnSend.setIcon(paperPlaneIcon);
        btnSend.addActionListener(e -> sendMessage());
        btnPanel.add(btnSend);

        btnSendImage = new JButton("Gửi ảnh");
        btnSendImage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSendImage.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnSendImage.setBackground(Color.WHITE);
        btnSendImage.setForeground(Color.BLACK);
        btnSendImage.addActionListener(e -> sendImage());
        btnPanel.add(btnSendImage);

        btnSendFile = new JButton("Truyền file TCP");
        btnSendFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSendFile.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnSendFile.setBackground(Color.WHITE);
        btnSendFile.setForeground(Color.BLACK);
        ImageIcon uploadIcon = patron.UIStyleHelper.getIcon("/images/upload.png", 16, 16);
        if (uploadIcon != null) btnSendFile.setIcon(uploadIcon);
        btnSendFile.addActionListener(e -> sendTcpFile());
        btnPanel.add(btnSendFile);

        inputPanel.add(btnPanel, BorderLayout.EAST);
        centerChatPanel.add(inputPanel, BorderLayout.SOUTH);

        add(centerChatPanel, BorderLayout.CENTER);
    }

    private void toggleConnection() {
        if (chatClient.isConnected()) {
            chatClient.disconnect();
        } else {
            String ip = txtServerIp.getText().trim();
            String portStr = txtPort.getText().trim();
            int port = 7777;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Port không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                chatClient.connect(ip, port, currentUsername);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối Server: " + e.getMessage(), "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void switchTarget(String target) {
        if (target == null || target.isEmpty()) return;
        this.activeTarget = target;

        if ("#Sanh-Chung".equalsIgnoreCase(target)) {
            lblCurrentTarget.setText("Đang nhắn tại: #Sanh-Chung (Broadcast)");
        } else {
            lblCurrentTarget.setText("Đang chat 1-1 với: " + target);
        }

        // Clear current screen and request history
        chatPane.setText("");
        if (chatClient.isConnected()) {
            chatClient.requestHistory(activeTarget);
        }
    }

    private void sendMessage() {
        if (!chatClient.isConnected()) {
            JOptionPane.showMessageDialog(this, "Vui lòng kết nối Server trước!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String text = txtMessage.getText();
        if (text != null && !text.trim().isEmpty()) {
            chatClient.sendTextMessage(activeTarget, text);
            txtMessage.setText("");
        }
    }

    private void sendImage() {
        if (!chatClient.isConnected()) {
            JOptionPane.showMessageDialog(this, "Vui lòng kết nối Server trước!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file hình ảnh (PNG/JPG)");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg", "gif"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                chatClient.sendImageMessage(activeTarget, selectedFile);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi đọc file ảnh: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sendTcpFile() {
        if (!chatClient.isConnected()) {
            JOptionPane.showMessageDialog(this, "Vui lòng kết nối Server trước!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn File truyền qua TCP (Port 8888)");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String serverIp = txtServerIp.getText().trim();
            int filePort = Config.PORT_FILE;

            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đang upload file TCP...", true);
            progressDialog.setLayout(new BorderLayout(10, 10));
            progressDialog.add(new JLabel("Đang tải file " + selectedFile.getName() + " lên Server..."), BorderLayout.NORTH);
            progressDialog.add(progressBar, BorderLayout.CENTER);
            progressDialog.setSize(350, 120);
            progressDialog.setLocationRelativeTo(this);

            TCPFileClient.uploadFile(serverIp, filePort, selectedFile, currentUsername, activeTarget, new TCPFileClient.FileProgressListener() {
                @Override
                public void onProgress(int percent, String statusMessage) {
                    SwingUtilities.invokeLater(() -> progressBar.setValue(percent));
                }

                @Override
                public void onComplete(boolean success, String message, File file) {
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        if (success) {
                            JOptionPane.showMessageDialog(ChatPanel.this, message, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                            // Send file notification packet to chat room
                            ChatPacket fileNotif = new ChatPacket(ChatPacket.Type.FILE_NOTIF, currentUsername, activeTarget, "[File TCP]: " + selectedFile.getName());
                            fileNotif.setFileName(selectedFile.getName());
                            fileNotif.setFileSize(selectedFile.length());
                            chatClient.sendPacket(fileNotif);
                        } else {
                            JOptionPane.showMessageDialog(ChatPanel.this, "Upload thất bại: " + message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            });

            progressDialog.setVisible(true);
        }
    }

    private void appendTextMessage(String sender, String timestamp, String content, boolean isSelf) {
        try {
            Style headerStyle = chatPane.addStyle("HeaderStyle", null);
            StyleConstants.setFontFamily(headerStyle, "Segoe UI");
            StyleConstants.setFontSize(headerStyle, 12);
            StyleConstants.setBold(headerStyle, true);
            StyleConstants.setForeground(headerStyle, isSelf ? new Color(29, 78, 216) : new Color(15, 23, 42));

            Style textStyle = chatPane.addStyle("TextStyle", null);
            StyleConstants.setFontFamily(textStyle, "Segoe UI");
            StyleConstants.setFontSize(textStyle, 13);
            StyleConstants.setForeground(textStyle, Color.BLACK);

            String timeStr = (timestamp != null && !timestamp.isEmpty()) ? " [" + timestamp + "]" : "";
            chatDoc.insertString(chatDoc.getLength(), sender + timeStr + ": ", headerStyle);
            chatDoc.insertString(chatDoc.getLength(), content + "\n\n", textStyle);

            chatPane.setCaretPosition(chatDoc.getLength());
        } catch (BadLocationException ignored) {}
    }

    private void appendImageMessage(String sender, String timestamp, byte[] imageBytes, boolean isSelf) {
        try {
            appendTextMessage(sender, timestamp, "[Hình ảnh bên dưới]:", isSelf);
            if (imageBytes != null && imageBytes.length > 0) {
                ImageIcon originalIcon = new ImageIcon(imageBytes);
                // Scale image if larger than 300px width/height
                int maxWidth = 300;
                int originalWidth = originalIcon.getIconWidth();
                int originalHeight = originalIcon.getIconHeight();
                ImageIcon scaledIcon = originalIcon;

                if (originalWidth > maxWidth && originalWidth > 0) {
                    int scaledHeight = (originalHeight * maxWidth) / originalWidth;
                    Image img = originalIcon.getImage().getScaledInstance(maxWidth, scaledHeight, Image.SCALE_SMOOTH);
                    scaledIcon = new ImageIcon(img);
                }

                chatPane.setCaretPosition(chatDoc.getLength());
                chatPane.insertIcon(scaledIcon);
                chatDoc.insertString(chatDoc.getLength(), "\n\n", null);
            }
        } catch (Exception ignored) {}
    }

    private void appendFileMessage(String sender, String timestamp, String fileName, long fileSize, boolean isSelf) {
        try {
            String readableSize = formatFileSize(fileSize);
            appendTextMessage(sender, timestamp, "📁 Đã gửi File: " + fileName + " (" + readableSize + ")", isSelf);

            JButton btnDownload = new JButton("⬇ Tải File TCP (" + fileName + ")");
            btnDownload.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDownload.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDownload.addActionListener(e -> downloadFile(fileName));

            chatPane.setCaretPosition(chatDoc.getLength());
            chatPane.insertComponent(btnDownload);
            chatDoc.insertString(chatDoc.getLength(), "\n\n", null);
        } catch (BadLocationException ignored) {}
    }

    private void downloadFile(String fileName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(fileName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File saveFile = chooser.getSelectedFile();
            String serverIp = txtServerIp.getText().trim();
            int filePort = Config.PORT_FILE;

            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đang tải file TCP...", true);
            progressDialog.setLayout(new BorderLayout(10, 10));
            progressDialog.add(new JLabel("Đang tải xuống file " + fileName + "..."), BorderLayout.NORTH);
            progressDialog.add(progressBar, BorderLayout.CENTER);
            progressDialog.setSize(350, 120);
            progressDialog.setLocationRelativeTo(this);

            TCPFileClient.downloadFile(serverIp, filePort, fileName, saveFile, currentUsername, new TCPFileClient.FileProgressListener() {
                @Override
                public void onProgress(int percent, String statusMessage) {
                    SwingUtilities.invokeLater(() -> progressBar.setValue(percent));
                }

                @Override
                public void onComplete(boolean success, String message, File downloadedFile) {
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        if (success) {
                            JOptionPane.showMessageDialog(ChatPanel.this, message + "\nLưu tại: " + saveFile.getAbsolutePath(), "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(ChatPanel.this, "Tải file thất bại: " + message, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            });

            progressDialog.setVisible(true);
        }
    }

    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    public void onPacketReceived(ChatPacket packet) {
        SwingUtilities.invokeLater(() -> {
            if (packet == null || packet.getType() == null) return;

            switch (packet.getType()) {
                case USER_LIST:
                    userListModel.clear();
                    if (packet.getUserList() != null) {
                        for (String user : packet.getUserList()) {
                            if (!user.equals(currentUsername)) {
                                userListModel.addElement(user);
                            }
                        }
                    }
                    break;

                case HISTORY_RESP:
                    chatPane.setText("");
                    if (packet.getHistoryList() != null) {
                        for (ChatPacket oldPacket : packet.getHistoryList()) {
                            renderSinglePacket(oldPacket);
                        }
                    }
                    break;

                case TEXT:
                case IMAGE:
                case FILE_NOTIF:
                    // Check if packet belongs to current active target
                    boolean isForCurrentTarget = false;
                    if ("#Sanh-Chung".equalsIgnoreCase(activeTarget) && "#Sanh-Chung".equalsIgnoreCase(packet.getReceiver())) {
                        isForCurrentTarget = true;
                    } else if (activeTarget.equalsIgnoreCase(packet.getSender()) || activeTarget.equalsIgnoreCase(packet.getReceiver())) {
                        isForCurrentTarget = true;
                    }

                    if (isForCurrentTarget) {
                        renderSinglePacket(packet);
                    }
                    break;

                default:
                    break;
            }
        });
    }

    private void renderSinglePacket(ChatPacket packet) {
        boolean isSelf = currentUsername.equals(packet.getSender());
        if (packet.getType() == ChatPacket.Type.TEXT) {
            appendTextMessage(packet.getSender(), packet.getTimestamp(), packet.getContent(), isSelf);
        } else if (packet.getType() == ChatPacket.Type.IMAGE) {
            appendImageMessage(packet.getSender(), packet.getTimestamp(), packet.getImageBytes(), isSelf);
        } else if (packet.getType() == ChatPacket.Type.FILE_NOTIF) {
            appendFileMessage(packet.getSender(), packet.getTimestamp(), packet.getFileName(), packet.getFileSize(), isSelf);
        }
    }

    @Override
    public void onConnectionStatusChanged(boolean connected, String message) {
        SwingUtilities.invokeLater(() -> {
            if (connected) {
                lblStatus.setText("● Đã kết nối");
                lblStatus.setForeground(new Color(22, 163, 74));
                btnConnect.setText("NGẮT KẾT NỐI");
                btnConnect.setBackground(new Color(220, 38, 38));
                txtServerIp.setEnabled(false);
                txtPort.setEnabled(false);
            } else {
                lblStatus.setText("● Chưa kết nối");
                lblStatus.setForeground(Color.RED);
                btnConnect.setText("KẾT NỐI SERVER");
                btnConnect.setBackground(new Color(37, 99, 235));
                txtServerIp.setEnabled(true);
                txtPort.setEnabled(true);
                userListModel.clear();
            }
        });
    }
}
