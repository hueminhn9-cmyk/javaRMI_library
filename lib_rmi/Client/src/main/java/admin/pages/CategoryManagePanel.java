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

public class CategoryManagePanel extends JPanel {
    private ManagerController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField tf_ID_Category;
    private JTextField tf_name_Category;

    private JButton btn_create_Category;
    private JButton btn_update_Category;
    private JButton btn_delete_Category;
    private JButton btn_refresh_Category;

    private JTable tbl_Category;
    private JScrollPane sp_Category;
    private JTextField tf_search_Category;

    public CategoryManagePanel(ManagerController controller) {
        this.controller = controller;
        initComponents();
        showTableCategory();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Thể Loại ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tf_ID_Category = new JTextField();
        tf_ID_Category.setEditable(false);
        styleTextField(tf_ID_Category);

        tf_name_Category = new JTextField();
        styleTextField(tf_name_Category);

        JLabel lblId = new JLabel("Mã Thể Loại (ID):"); lblId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblName = new JLabel("Tên Thể Loại:"); lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(lblId, gbc);
        gbc.gridx = 1; formCard.add(tf_ID_Category, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(lblName, gbc);
        gbc.gridx = 1; formCard.add(tf_name_Category, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        btn_create_Category = createBtn("Thêm Mới", "/images/add.png", new Color(22, 163, 74));
        btn_create_Category.addActionListener(e -> btn_create_CategoryActionPerformed());

        btn_update_Category = createBtn("Cập Nhật", "/images/edit.png", new Color(37, 99, 235));
        btn_update_Category.addActionListener(e -> btn_update_CategoryActionPerformed());

        btn_delete_Category = createBtn("Xóa", "/images/bin.png", new Color(220, 38, 38));
        btn_delete_Category.addActionListener(e -> btn_delete_CategoryActionPerformed());

        btn_refresh_Category = createBtn("Làm Mới", "/images/refresh.png", new Color(75, 85, 99));
        btn_refresh_Category.addActionListener(e -> btn_refresh_CategoryActionPerformed());

        btnPanel.add(btn_create_Category);
        btnPanel.add(btn_update_Category);
        btnPanel.add(btn_delete_Category);
        btnPanel.add(btn_refresh_Category);

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
        searchIcon.setText(" Tìm Kiếm Thể Loại: ");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tf_search_Category = new JTextField(25);
        styleTextField(tf_search_Category);
        searchBar.add(searchIcon);
        searchBar.add(tf_search_Category);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_Category = new JTable();
        tbl_Category.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tbl_CategoryMousePressed();
            }
        });
        sp_Category = new JScrollPane(tbl_Category);
        tablePanel.add(sp_Category, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    public synchronized void showTableCategory() {
        try {
            Response response = controller.getCategoriesController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Category.setRowSorter(sorter);

                tf_search_Category.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e) { search(tf_search_Category.getText()); }
                    @Override public void removeUpdate(DocumentEvent e) { search(tf_search_Category.getText()); }
                    @Override public void changedUpdate(DocumentEvent e) { search(tf_search_Category.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) sorter.setRowFilter(null);
                        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                });

                tbl_Category.setModel(model);
                tbl_Category.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_Category.setRowHeight(32);
                tbl_Category.setDefaultEditor(Object.class, null);
                TableColumn indexColumn = tbl_Category.getColumnModel().getColumn(0);
                indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                indexColumn.setMaxWidth(80);
                sp_Category.setViewportView(tbl_Category);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void tbl_CategoryMousePressed() {
        int selectedRow = tbl_Category.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tbl_Category.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) tbl_Category.getModel();

            tf_ID_Category.setText(String.valueOf(model.getValueAt(modelRow, 0)));
            tf_name_Category.setText(String.valueOf(model.getValueAt(modelRow, 1)));
        }
    }

    private void btn_create_CategoryActionPerformed() {
        String name = tf_name_Category.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên thể loại!");
            return;
        }

        Category category = new Category();
        category.setName(name);

        try {
            Response response = controller.createCategoryController(category);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_CategoryActionPerformed();
                showTableCategory();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void btn_update_CategoryActionPerformed() {
        String idText = tf_ID_Category.getText().trim();
        String name = tf_name_Category.getText().trim();

        if (idText.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần cập nhật!");
            return;
        }

        Category category = new Category();
        category.setId(Integer.parseInt(idText));
        category.setName(name);

        try {
            Response response = controller.updateCategoryController(category);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_CategoryActionPerformed();
                showTableCategory();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void btn_delete_CategoryActionPerformed() {
        String idText = tf_ID_Category.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa thể loại ID " + idText + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Response response = controller.deleteCategoryController(Integer.parseInt(idText));
                if (response != null) {
                    JOptionPane.showMessageDialog(this, response.getData());
                    btn_refresh_CategoryActionPerformed();
                    showTableCategory();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void btn_refresh_CategoryActionPerformed() {
        tf_ID_Category.setText("");
        tf_name_Category.setText("");
        showTableCategory();
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
