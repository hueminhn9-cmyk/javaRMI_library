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

public class PublisherManagePanel extends JPanel {
    private ManagerController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField tf_ID_Published;
    private JTextField tf_name_Published;

    private JButton btn_create_Published;
    private JButton btn_update_Published;
    private JButton btn_delete_Published;
    private JButton btn_refresh_Published;

    private JTable tbl_Published;
    private JScrollPane sp_Published;
    private JTextField tf_search_Published;

    public PublisherManagePanel(ManagerController controller) {
        this.controller = controller;
        initComponents();
        showTablePublished();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Nhà Xuất Bản ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tf_ID_Published = new JTextField();
        tf_ID_Published.setEditable(false);
        styleTextField(tf_ID_Published);

        tf_name_Published = new JTextField();
        styleTextField(tf_name_Published);

        JLabel lblId = new JLabel("Mã NXB (ID):"); lblId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblName = new JLabel("Tên Nhà Xuất Bản:"); lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(lblId, gbc);
        gbc.gridx = 1; formCard.add(tf_ID_Published, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(lblName, gbc);
        gbc.gridx = 1; formCard.add(tf_name_Published, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        btn_create_Published = createBtn("Thêm Mới", "/images/add.png", new Color(22, 163, 74));
        btn_create_Published.addActionListener(e -> btn_create_PublishedActionPerformed());

        btn_update_Published = createBtn("Cập Nhật", "/images/edit.png", new Color(37, 99, 235));
        btn_update_Published.addActionListener(e -> btn_update_PublishedActionPerformed());

        btn_delete_Published = createBtn("Xóa", "/images/bin.png", new Color(220, 38, 38));
        btn_delete_Published.addActionListener(e -> btn_delete_PublishedActionPerformed());

        btn_refresh_Published = createBtn("Làm Mới", "/images/refresh.png", new Color(75, 85, 99));
        btn_refresh_Published.addActionListener(e -> btn_refresh_PublishedActionPerformed());

        btnPanel.add(btn_create_Published);
        btnPanel.add(btn_update_Published);
        btnPanel.add(btn_delete_Published);
        btnPanel.add(btn_refresh_Published);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formCard);
        formScroll.setBorder(null);
        formScroll.setPreferredSize(new Dimension(420, 500));
        add(formScroll, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JLabel searchIcon = new JLabel();
        try {
            searchIcon.setIcon(new ImageIcon(getClass().getResource("/images/search_32.png")));
        } catch (Exception ignored) {}
        searchIcon.setText(" Tìm Kiếm NXB: ");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tf_search_Published = new JTextField(25);
        styleTextField(tf_search_Published);
        searchBar.add(searchIcon);
        searchBar.add(tf_search_Published);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_Published = new JTable();
        tbl_Published.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tbl_PublishedMousePressed();
            }
        });
        sp_Published = new JScrollPane(tbl_Published);
        tablePanel.add(sp_Published, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    public synchronized void showTablePublished() {
        try {
            Response response = controller.getPublishedController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Published.setRowSorter(sorter);

                tf_search_Published.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e) { search(tf_search_Published.getText()); }
                    @Override public void removeUpdate(DocumentEvent e) { search(tf_search_Published.getText()); }
                    @Override public void changedUpdate(DocumentEvent e) { search(tf_search_Published.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) sorter.setRowFilter(null);
                        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                });

                tbl_Published.setModel(model);
                tbl_Published.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_Published.setRowHeight(32);
                tbl_Published.setDefaultEditor(Object.class, null);
                TableColumn indexColumn = tbl_Published.getColumnModel().getColumn(0);
                indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                indexColumn.setMaxWidth(80);
                sp_Published.setViewportView(tbl_Published);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void tbl_PublishedMousePressed() {
        int selectedRow = tbl_Published.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tbl_Published.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) tbl_Published.getModel();

            tf_ID_Published.setText(String.valueOf(model.getValueAt(modelRow, 0)));
            tf_name_Published.setText(String.valueOf(model.getValueAt(modelRow, 1)));
        }
    }

    private void btn_create_PublishedActionPerformed() {
        String name = tf_name_Published.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhà xuất bản!");
            return;
        }

        Published published = new Published();
        published.setName(name);

        try {
            Response response = controller.createPublishedController(published);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_PublishedActionPerformed();
                showTablePublished();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void btn_update_PublishedActionPerformed() {
        String idText = tf_ID_Published.getText().trim();
        String name = tf_name_Published.getText().trim();

        if (idText.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn NXB cần cập nhật!");
            return;
        }

        Published published = new Published();
        published.setId(Integer.parseInt(idText));
        published.setName(name);

        try {
            Response response = controller.updatePublishedController(published);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_PublishedActionPerformed();
                showTablePublished();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void btn_delete_PublishedActionPerformed() {
        String idText = tf_ID_Published.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn NXB cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa NXB ID " + idText + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Response response = controller.deletePublishedController(Integer.parseInt(idText));
                if (response != null) {
                    JOptionPane.showMessageDialog(this, response.getData());
                    btn_refresh_PublishedActionPerformed();
                    showTablePublished();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void btn_refresh_PublishedActionPerformed() {
        tf_ID_Published.setText("");
        tf_name_Published.setText("");
        showTablePublished();
    }

    private JButton createBtn(String text, String iconPath, Color bg) {
        JButton btn = new JButton(text);
        if (iconPath != null) {
            try {
                btn.setIcon(new ImageIcon(getClass().getResource(iconPath)));
            } catch (Exception ignored) {}
        }
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 40));
        return btn;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(220, 38));
    }
}
