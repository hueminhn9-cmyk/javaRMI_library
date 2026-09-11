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

public class AuthorManagePanel extends JPanel {
    private ManagerController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField tf_ID_Author;
    private JTextField tf_name_Author;

    private JButton btn_create_Author;
    private JButton btn_update_Author;
    private JButton btn_delete_Author;
    private JButton btn_refresh_Author;

    private JTable tbl_Author;
    private JScrollPane sp_Author;
    private JTextField tf_search_Author;

    public AuthorManagePanel(ManagerController controller) {
        this.controller = controller;
        initComponents();
        showTableAuthor();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Tác Giả ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tf_ID_Author = new JTextField();
        tf_ID_Author.setEditable(false);
        styleTextField(tf_ID_Author);

        tf_name_Author = new JTextField();
        styleTextField(tf_name_Author);

        JLabel lblId = new JLabel("Mã Tác Giả (ID):"); lblId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblName = new JLabel("Tên Tác Giả:"); lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(lblId, gbc);
        gbc.gridx = 1; formCard.add(tf_ID_Author, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(lblName, gbc);
        gbc.gridx = 1; formCard.add(tf_name_Author, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        btn_create_Author = createBtn("Thêm Mới", "/images/add.png", new Color(22, 163, 74));
        btn_create_Author.addActionListener(e -> btn_create_AuthorActionPerformed());

        btn_update_Author = createBtn("Cập Nhật", "/images/edit.png", new Color(37, 99, 235));
        btn_update_Author.addActionListener(e -> btn_update_AuthorActionPerformed());

        btn_delete_Author = createBtn("Xóa", "/images/bin.png", new Color(220, 38, 38));
        btn_delete_Author.addActionListener(e -> btn_delete_AuthorActionPerformed());

        btn_refresh_Author = createBtn("Làm Mới", "/images/refresh.png", new Color(75, 85, 99));
        btn_refresh_Author.addActionListener(e -> btn_refresh_AuthorActionPerformed());

        btnPanel.add(btn_create_Author);
        btnPanel.add(btn_update_Author);
        btnPanel.add(btn_delete_Author);
        btnPanel.add(btn_refresh_Author);

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
        searchIcon.setText(" Tìm Kiếm Tác Giả: ");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tf_search_Author = new JTextField(25);
        styleTextField(tf_search_Author);
        searchBar.add(searchIcon);
        searchBar.add(tf_search_Author);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_Author = new JTable();
        tbl_Author.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tbl_AuthorMousePressed();
            }
        });
        sp_Author = new JScrollPane(tbl_Author);
        tablePanel.add(sp_Author, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    public synchronized void showTableAuthor() {
        try {
            Response response = controller.getAuthorsController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Author.setRowSorter(sorter);

                tf_search_Author.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e) { search(tf_search_Author.getText()); }
                    @Override public void removeUpdate(DocumentEvent e) { search(tf_search_Author.getText()); }
                    @Override public void changedUpdate(DocumentEvent e) { search(tf_search_Author.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) sorter.setRowFilter(null);
                        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                });

                tbl_Author.setModel(model);
                tbl_Author.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_Author.setRowHeight(32);
                tbl_Author.setDefaultEditor(Object.class, null);
                TableColumn indexColumn = tbl_Author.getColumnModel().getColumn(0);
                indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                indexColumn.setMaxWidth(80);
                sp_Author.setViewportView(tbl_Author);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void tbl_AuthorMousePressed() {
        int selectedRow = tbl_Author.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tbl_Author.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) tbl_Author.getModel();

            tf_ID_Author.setText(String.valueOf(model.getValueAt(modelRow, 0)));
            tf_name_Author.setText(String.valueOf(model.getValueAt(modelRow, 1)));
        }
    }

    private void btn_create_AuthorActionPerformed() {
        String name = tf_name_Author.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên tác giả!");
            return;
        }

        Author author = new Author();
        author.setName(name);

        try {
            Response response = controller.createAuthorController(author);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_AuthorActionPerformed();
                showTableAuthor();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void btn_update_AuthorActionPerformed() {
        String idText = tf_ID_Author.getText().trim();
        String name = tf_name_Author.getText().trim();

        if (idText.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tác giả cần cập nhật!");
            return;
        }

        Author author = new Author();
        author.setId(Integer.parseInt(idText));
        author.setName(name);

        try {
            Response response = controller.updateAuthorController(author);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_AuthorActionPerformed();
                showTableAuthor();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void btn_delete_AuthorActionPerformed() {
        String idText = tf_ID_Author.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tác giả cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tác giả ID " + idText + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Response response = controller.deleteAuthorController(Integer.parseInt(idText));
                if (response != null) {
                    JOptionPane.showMessageDialog(this, response.getData());
                    btn_refresh_AuthorActionPerformed();
                    showTableAuthor();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void btn_refresh_AuthorActionPerformed() {
        tf_ID_Author.setText("");
        tf_name_Author.setText("");
        showTableAuthor();
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
