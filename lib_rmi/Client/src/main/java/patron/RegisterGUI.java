package patron;

import common.model.*;
import common.rmi.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RegisterGUI extends JFrame {
    private JTextField tf_firstname;
    private JTextField tf_flastname;
    private JTextField tf_email;
    private JPasswordField tf_password;
    private JButton btn_Register;
    private JButton btn_LoginLink;

    public RegisterGUI() {
        setTitle("VKU Library Portal - Đăng Ký Tài Khoản");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        setSize(480, 620);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyleHelper.COLOR_BG_MAIN);

        // Top Header Banner - VKU Navy Slate
        JPanel headerPanel = new JPanel(new BorderLayout(0, 4));
        headerPanel.setBackground(UIStyleHelper.COLOR_NAVBAR_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("VKU LIBRARY", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Hệ Thống Quản Lý Thư Viện - Đăng Ký Tài Khoản", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(148, 163, 184));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Center Form Card Container
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel formCard = UIStyleHelper.createCardPanel("Tạo Tài Khoản Mới");
        formCard.setPreferredSize(new Dimension(440, 360));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name Row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblFname = new JLabel("Họ:");
        lblFname.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblFname.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);
        formGrid.add(lblFname, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        tf_firstname = new JTextField(20);
        UIStyleHelper.styleTextField(tf_firstname);
        formGrid.add(tf_firstname, gbc);

        // Last Name Row
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblLname = new JLabel("Tên:");
        lblLname.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblLname.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);
        formGrid.add(lblLname, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        tf_flastname = new JTextField(20);
        UIStyleHelper.styleTextField(tf_flastname);
        formGrid.add(tf_flastname, gbc);

        // Email Row
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblEmail.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);
        formGrid.add(lblEmail, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        tf_email = new JTextField(20);
        UIStyleHelper.styleTextField(tf_email);
        formGrid.add(tf_email, gbc);

        // Password Row
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblPassword.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);
        formGrid.add(lblPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        tf_password = new JPasswordField(20);
        tf_password.setFont(UIStyleHelper.FONT_BODY);
        tf_password.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyleHelper.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        tf_password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performRegister();
            }
        });
        formGrid.add(tf_password, gbc);

        // Register Button Row - Solid Royal Blue
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(14, 8, 6, 8);
        btn_Register = UIStyleHelper.createBlueButton("Đăng ký ngay", UIStyleHelper.getIcon("/images/enter.png", 16, 16));
        btn_Register.setPreferredSize(new Dimension(380, 42));
        btn_Register.addActionListener(e -> performRegister());
        formGrid.add(btn_Register, gbc);

        // Login Link Row
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 8, 6, 8);

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkPanel.setOpaque(false);

        JLabel lblQuestion = new JLabel("Đã có tài khoản? ");
        lblQuestion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblQuestion.setForeground(new Color(100, 116, 139));

        btn_LoginLink = new JButton("Đăng nhập tại đây");
        btn_LoginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_LoginLink.setForeground(new Color(37, 99, 235));
        btn_LoginLink.setContentAreaFilled(false);
        btn_LoginLink.setBorderPainted(false);
        btn_LoginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_LoginLink.addActionListener(e -> {
            this.dispose();
            new LoginGUI().setVisible(true);
        });

        linkPanel.add(lblQuestion);
        linkPanel.add(btn_LoginLink);
        formGrid.add(linkPanel, gbc);

        formCard.add(formGrid, BorderLayout.CENTER);

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(formCard, cardGbc);

        add(centerPanel, BorderLayout.CENTER);
    }



    private void performRegister() {
        String fname = tf_firstname.getText().trim();
        String lname = tf_flastname.getText().trim();
        String email = tf_email.getText().trim();
        String password = new String(tf_password.getPassword()).trim();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ tất cả thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patron patron = new Patron(0, fname, lname, email, password, true, "PATRON");
        try {
            ClientController controller = new ClientController();
            Response response = controller.registerClient(patron);
            if (response != null && response.getStatus() == 200) {
                JOptionPane.showMessageDialog(this, "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new LoginGUI().setVisible(true);
            } else {
                String msg = (response != null && response.getData() != null) ? response.getData().toString() : "Đăng ký thất bại!";
                JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối máy chủ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new RegisterGUI().setVisible(true));
    }
}


