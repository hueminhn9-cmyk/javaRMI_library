package patron;

import common.model.*;
import common.rmi.*;
import admin.ManageGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;

public class LoginGUI extends JFrame {
    private ClientController controller;

    private JTextField tf_email;
    private JPasswordField tf_password;
    private JButton btn_Login;

    public LoginGUI() {
        setTitle("VKU Library - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        try {
            controller = new ClientController();
        } catch (Exception ignored) {}

        initComponents();
        setSize(480, 420);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIStyleHelper.COLOR_BG_LIGHT);

        // Header Panel (Dark Slate Header - No Banner Image)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyleHelper.COLOR_NAVBAR_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("VKU LIBRARY", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Hệ Thống Quản Lý Thư Viện", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(148, 163, 184)); // #94A3B8

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Center Form Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(UIStyleHelper.COLOR_BG_LIGHT);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel formCard = UIStyleHelper.createCardPanel("Đăng Nhập Hệ Thống");
        formCard.setPreferredSize(new Dimension(400, 250));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email Row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblEmail.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);
        formGrid.add(lblEmail, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        tf_email = new JTextField(20);
        UIStyleHelper.styleTextField(tf_email);
        formGrid.add(tf_email, gbc);

        // Password Row
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblPassword.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);
        formGrid.add(lblPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        tf_password = new JPasswordField(20);
        tf_password.setFont(UIStyleHelper.FONT_BODY);
        tf_password.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyleHelper.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        formGrid.add(tf_password, gbc);

        // Enter key listener on password
        tf_password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        // Login Button Row
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(16, 8, 8, 8);
        btn_Login = new JButton("Đăng nhập");
        ImageIcon enterIcon = UIStyleHelper.getIcon("/images/enter.png", 16, 16);
        if (enterIcon != null) btn_Login.setIcon(enterIcon);
        UIStyleHelper.styleButton(btn_Login, UIStyleHelper.COLOR_PRIMARY_BG, UIStyleHelper.COLOR_PRIMARY_BORDER, Color.BLACK);
        btn_Login.addActionListener(e -> performLogin());
        formGrid.add(btn_Login, gbc);

        formCard.add(formGrid, BorderLayout.CENTER);

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(formCard, cardGbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void performLogin() {
        String email = tf_email.getText().trim();
        String password = new String(tf_password.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Email và Mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patron patron = new Patron();
        patron.setEmail(email);
        patron.setPassword(password);

        try {
            if (controller == null) {
                controller = new ClientController();
            }
            Response response = controller.loginClient(patron);
            if (response != null && response.getStatus() == 200) {
                patron = (Patron) response.getData();
                String role = patron.getRole();
                this.dispose();
                boolean isAdmin = (role != null && role.equalsIgnoreCase("ADMIN")) || (email.equalsIgnoreCase("admin@library.com"));
                if (isAdmin) {
                    JOptionPane.showMessageDialog(this, "Xin chào Admin/Librarian: " + patron.getFirstName() + " " + patron.getLastName(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    new ManageGUI(patron).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Xin chào " + patron.getFirstName() + " " + patron.getLastName() + "!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    new ClientGUI(patron).setVisible(true);
                }
            } else {
                String msg = (response != null && response.getData() != null) ? response.getData().toString() : "Đăng nhập thất bại!";
                JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối RMI Server: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}
