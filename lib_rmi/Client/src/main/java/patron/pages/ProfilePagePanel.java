package patron.pages;

import common.model.Response;
import patron.ClientController;
import common.model.Patron;
import patron.UIStyleHelper;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class ProfilePagePanel extends JPanel {
    private Patron patron;
    private ClientController controller;

    private JTextField tf_FirstName;
    private JTextField tf_LastName;
    private JTextField tf_Email;
    private JPasswordField tf_Password;
    private JButton btn_Save;

    public ProfilePagePanel(Patron patron, ClientController controller) {
        this.patron = patron;
        this.controller = controller;
        initComponents();
        loadPatronInfo();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 15));
        setBackground(UIStyleHelper.COLOR_BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // Top Notice Banner
        JPanel noticeBanner = UIStyleHelper.createNoticeBanner(
                "Bạn có thể kiểm tra và cập nhật Họ tên, Email và Mật khẩu cá nhân cho tài khoản độc giả Thư viện VKU."
        );
        add(noticeBanner, BorderLayout.NORTH);

        // Center Profile Card
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(UIStyleHelper.COLOR_BORDER, 1, true));

        // Section Banner Teal
        JPanel sectionHeader = UIStyleHelper.createSectionHeaderBar(
                "Thông tin tài khoản độc giả", UIStyleHelper.COLOR_TEAL_HEADER
        );
        card.add(sectionHeader, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblFirstName = new JLabel("Họ:");
        lblFirstName.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblFirstName.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);
        formPanel.add(lblFirstName, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        tf_FirstName = new JTextField(20);
        UIStyleHelper.styleTextField(tf_FirstName);
        formPanel.add(tf_FirstName, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblLastName = new JLabel("Tên:");
        lblLastName.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblLastName.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);
        formPanel.add(lblLastName, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        tf_LastName = new JTextField(20);
        UIStyleHelper.styleTextField(tf_LastName);
        formPanel.add(tf_LastName, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblEmail.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);
        formPanel.add(lblEmail, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        tf_Email = new JTextField(20);
        UIStyleHelper.styleTextField(tf_Email);
        formPanel.add(tf_Email, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblPassword = new JLabel("Mật khẩu mới:");
        lblPassword.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblPassword.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);
        formPanel.add(lblPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        tf_Password = new JPasswordField(20);
        tf_Password.setFont(UIStyleHelper.FONT_BODY);
        tf_Password.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyleHelper.COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        formPanel.add(tf_Password, gbc);

        // Save Button
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        gbc.insets = new Insets(18, 10, 10, 10);
        btn_Save = UIStyleHelper.createTealPillButton("Lưu thay đổi", UIStyleHelper.getIcon("/images/changes.png", 16, 16));
        btn_Save.addActionListener(e -> btn_SaveActionPerformed(e));
        formPanel.add(btn_Save, gbc);

        card.add(formPanel, BorderLayout.CENTER);

        JPanel outerCenter = new JPanel(new GridBagLayout());
        outerCenter.setOpaque(false);
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.anchor = GridBagConstraints.CENTER;
        outerCenter.add(card, cardGbc);

        add(outerCenter, BorderLayout.CENTER);
    }

    private void loadPatronInfo() {
        if (patron != null) {
            tf_FirstName.setText(patron.getFirstName() != null ? patron.getFirstName() : "");
            tf_LastName.setText(patron.getLastName() != null ? patron.getLastName() : "");
            tf_Email.setText(patron.getEmail() != null ? patron.getEmail() : "");
            tf_Password.setText(patron.getPassword() != null ? patron.getPassword() : "");
        }
    }

    private void btn_SaveActionPerformed(java.awt.event.ActionEvent evt) {
        String firstName = tf_FirstName.getText().trim();
        String lastName = tf_LastName.getText().trim();
        String email = tf_Email.getText().trim();
        String password = new String(tf_Password.getPassword()).trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!");
            return;
        }

        patron.setFirstName(firstName);
        patron.setLastName(lastName);
        patron.setEmail(email);
        if (!password.isEmpty()) {
            patron.setPassword(password);
        }

        try {
            Response response = controller.updatePatronAccount(patron);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

