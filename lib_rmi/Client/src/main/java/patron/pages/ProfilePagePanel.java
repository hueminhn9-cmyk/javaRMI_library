package patron.pages;

import common.Response;
import patron.ClientController;
import common.Patron;

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
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Patron Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblFirstName = new JLabel("First Name:");
        lblFirstName.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        formPanel.add(lblFirstName, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        tf_FirstName = new JTextField(20);
        formPanel.add(tf_FirstName, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblLastName = new JLabel("Last Name:");
        lblLastName.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        formPanel.add(lblLastName, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        tf_LastName = new JTextField(20);
        formPanel.add(tf_LastName, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        formPanel.add(lblEmail, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        tf_Email = new JTextField(20);
        formPanel.add(tf_Email, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        formPanel.add(lblPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        tf_Password = new JPasswordField(20);
        formPanel.add(tf_Password, gbc);

        // Save Button
        gbc.gridx = 1; gbc.gridy = 4;
        btn_Save = new JButton("Save Profile");
        btn_Save.setFont(new Font("Times New Roman", Font.BOLD, 14));
        btn_Save.setBackground(new Color(41, 128, 185));
        btn_Save.setForeground(Color.WHITE);
        btn_Save.setPreferredSize(new Dimension(120, 35));
        btn_Save.addActionListener(e -> btn_SaveActionPerformed(e));
        formPanel.add(btn_Save, gbc);

        add(formPanel, BorderLayout.CENTER);
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
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!");
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
