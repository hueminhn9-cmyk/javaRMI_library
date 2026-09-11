package admin.pages;

import admin.ManagerController;
import common.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.Timestamp;

public class PatronManagePanel extends JPanel {
    private ManagerController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField tf_ID_Patron;
    private JTextField tf_fname_Patron;
    private JTextField tf_lname_Patron;
    private JTextField tf_email_Patron;
    private JTextField tf_pass_Patron;
    private JCheckBox checkBox_Patron;

    private JButton btn_update_Patron;
    private JButton btn_refresh_Patron;
    private JButton btn_send;

    private JTable tbl_Patron;
    private JScrollPane sp_Patron;
    private JTextField tf_search_Patron;

    public PatronManagePanel(ManagerController controller) {
        this.controller = controller;
        initComponents();
        showTablePatrons();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Độc Giả ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 15), new Color(30, 41, 59)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        tf_ID_Patron = new JTextField();
        tf_ID_Patron.setEditable(false);
        styleTextField(tf_ID_Patron);

        tf_fname_Patron = new JTextField();
        styleTextField(tf_fname_Patron);

        tf_lname_Patron = new JTextField();
        styleTextField(tf_lname_Patron);

        tf_email_Patron = new JTextField();
        styleTextField(tf_email_Patron);

        tf_pass_Patron = new JTextField();
        styleTextField(tf_pass_Patron);

        checkBox_Patron = new JCheckBox("Kích hoạt / Hoạt động");
        checkBox_Patron.setFont(new Font("Segoe UI", Font.BOLD, 13));
        checkBox_Patron.setForeground(new Color(30, 41, 59));

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(createLabel("Mã Độc Giả:"), gbc);
        gbc.gridx = 1; formCard.add(tf_ID_Patron, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(createLabel("Họ:"), gbc);
        gbc.gridx = 1; formCard.add(tf_fname_Patron, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formCard.add(createLabel("Tên:"), gbc);
        gbc.gridx = 1; formCard.add(tf_lname_Patron, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formCard.add(createLabel("Email:"), gbc);
        gbc.gridx = 1; formCard.add(tf_email_Patron, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formCard.add(createLabel("Mật Khẩu:"), gbc);
        gbc.gridx = 1; formCard.add(tf_pass_Patron, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formCard.add(createLabel("Trạng Thái:"), gbc);
        gbc.gridx = 1; formCard.add(checkBox_Patron, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        btn_update_Patron = createBtn("Cập Nhật", new Color(37, 99, 235), "/images/edit.png");
        btn_update_Patron.addActionListener(e -> btn_update_PatronActionPerformed());

        btn_refresh_Patron = createBtn("Làm Mới", new Color(75, 85, 99), "/images/refresh.png");
        btn_refresh_Patron.addActionListener(e -> btn_refresh_PatronActionPerformed());

        btn_send = createBtn("Gửi TB", new Color(139, 92, 246), "/images/paper-plane.png");
        btn_send.addActionListener(e -> btn_sendActionPerformed());

        btnPanel.add(btn_update_Patron);
        btnPanel.add(btn_refresh_Patron);
        btnPanel.add(btn_send);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formCard);
        formScroll.setBorder(null);
        formScroll.setPreferredSize(new Dimension(420, 520));
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(formScroll, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JLabel searchIcon = new JLabel();
        java.net.URL searchImgUrl = getClass().getResource("/images/search_32.png");
        if (searchImgUrl != null) {
            searchIcon.setIcon(new ImageIcon(searchImgUrl));
        } else {
            searchIcon.setText("🔍");
            searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        }
        
        JLabel searchLabel = new JLabel("Tìm Kiếm Độc Giả: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(new Color(30, 41, 59));

        tf_search_Patron = new JTextField(25);
        styleTextField(tf_search_Patron);

        searchBar.add(searchIcon);
        searchBar.add(searchLabel);
        searchBar.add(tf_search_Patron);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_Patron = new JTable();
        tbl_Patron.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tbl_PatronMousePressed();
            }
        });
        sp_Patron = new JScrollPane(tbl_Patron);
        tablePanel.add(sp_Patron, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(51, 65, 85));
        return lbl;
    }

    public synchronized void showTablePatrons() {
        try {
            Response response = controller.getPatronsController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Patron.setRowSorter(sorter);

                tf_search_Patron.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e) { search(tf_search_Patron.getText()); }
                    @Override public void removeUpdate(DocumentEvent e) { search(tf_search_Patron.getText()); }
                    @Override public void changedUpdate(DocumentEvent e) { search(tf_search_Patron.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) sorter.setRowFilter(null);
                        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                });

                tbl_Patron.setModel(model);
                tbl_Patron.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_Patron.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tbl_Patron.setRowHeight(34);
                tbl_Patron.setDefaultEditor(Object.class, null);
                TableColumn indexColumn = tbl_Patron.getColumnModel().getColumn(0);
                indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                indexColumn.setMaxWidth(80);
                sp_Patron.setViewportView(tbl_Patron);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void tbl_PatronMousePressed() {
        int selectedRow = tbl_Patron.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tbl_Patron.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) tbl_Patron.getModel();

            tf_ID_Patron.setText(String.valueOf(model.getValueAt(modelRow, 0)));
            tf_fname_Patron.setText(String.valueOf(model.getValueAt(modelRow, 1)));
            tf_lname_Patron.setText(String.valueOf(model.getValueAt(modelRow, 2)));
            tf_email_Patron.setText(String.valueOf(model.getValueAt(modelRow, 3)));
            
            String statusStr = String.valueOf(model.getValueAt(modelRow, 4));
            checkBox_Patron.setSelected(statusStr.equalsIgnoreCase("Available") || statusStr.equalsIgnoreCase("true") || statusStr.equalsIgnoreCase("1"));
        }
    }

    private void btn_update_PatronActionPerformed() {
        String idText = tf_ID_Patron.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả cần cập nhật!");
            return;
        }

        Patron patron = new Patron();
        patron.setId(Integer.parseInt(idText));
        patron.setFirstName(tf_fname_Patron.getText().trim());
        patron.setLastName(tf_lname_Patron.getText().trim());
        patron.setEmail(tf_email_Patron.getText().trim());
        patron.setStatus(checkBox_Patron.isSelected());

        try {
            Response response = controller.updatePatronController(patron);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_PatronActionPerformed();
                showTablePatrons();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void btn_sendActionPerformed() {
        String idText = tf_ID_Patron.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn độc giả để gửi thông báo!");
            return;
        }

        String msg = JOptionPane.showInputDialog(this, "Nhập nội dung thông báo gửi tới " + tf_email_Patron.getText() + ":");
        if (msg != null && !msg.trim().isEmpty()) {
            Notification notification = new Notification();
            notification.setPatron_id(Integer.parseInt(idText));
            notification.setMessage(msg.trim());
            notification.setSend_at(new Timestamp(System.currentTimeMillis()));

            try {
                Response response = controller.createNotificationController(notification);
                if (response != null) {
                    JOptionPane.showMessageDialog(this, "Đã gửi thông báo thành công!");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void btn_refresh_PatronActionPerformed() {
        tf_ID_Patron.setText("");
        tf_fname_Patron.setText("");
        tf_lname_Patron.setText("");
        tf_email_Patron.setText("");
        tf_pass_Patron.setText("");
        checkBox_Patron.setSelected(true);
        showTablePatrons();
    }

    private JButton createBtn(String text, Color bg, String iconPath) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 40));
        
        if (iconPath != null) {
            java.net.URL imgUrl = getClass().getResource(iconPath);
            if (imgUrl != null) {
                btn.setIcon(new ImageIcon(imgUrl));
            }
        }
        return btn;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(220, 38));
        tf.setMargin(new Insets(4, 8, 4, 8));
    }
}
