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
import java.util.List;

public class BookManagePanel extends JPanel {
    private ManagerController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField tf_ID_Book;
    private JTextField tf_title_Book;
    private JComboBox<Category> cb_category_Book;
    private JComboBox<Author> cb_author_Book;

    private JButton btn_create_Book;
    private JButton btn_update_Book;
    private JButton btn_delete_Book;
    private JButton btn_refresh_Book;

    private JTable tbl_Book;
    private JScrollPane sp_Book;
    private JTextField tf_search_Book;

    public BookManagePanel(ManagerController controller) {
        this.controller = controller;
        initComponents();
        showTableBook();
        showDataComboBoxCategory();
        showDataComboBoxAuthor();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Left Form Card
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Sách ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tf_ID_Book = new JTextField();
        tf_ID_Book.setEditable(false);
        styleTextField(tf_ID_Book);

        tf_title_Book = new JTextField();
        styleTextField(tf_title_Book);

        cb_category_Book = new JComboBox<>();
        cb_category_Book.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb_category_Book.setPreferredSize(new Dimension(220, 38));

        cb_author_Book = new JComboBox<>();
        cb_author_Book.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb_author_Book.setPreferredSize(new Dimension(220, 38));

        JLabel lblId = new JLabel("Mã Sách (ID):"); lblId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblTitle = new JLabel("Tên Sách:"); lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblCat = new JLabel("Thể Loại:"); lblCat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblAuthor = new JLabel("Tác Giả:"); lblAuthor.setFont(new Font("Segoe UI", Font.BOLD, 13));

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(lblId, gbc);
        gbc.gridx = 1; formCard.add(tf_ID_Book, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(lblTitle, gbc);
        gbc.gridx = 1; formCard.add(tf_title_Book, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formCard.add(lblCat, gbc);
        gbc.gridx = 1; formCard.add(cb_category_Book, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formCard.add(lblAuthor, gbc);
        gbc.gridx = 1; formCard.add(cb_author_Book, gbc);

        // Buttons Panel - Moved UP & enlarged with icons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        btn_create_Book = createBtn("Thêm Mới", "/images/add.png", new Color(22, 163, 74));
        btn_create_Book.addActionListener(e -> btn_create_BookActionPerformed());

        btn_update_Book = createBtn("Cập Nhật", "/images/edit.png", new Color(37, 99, 235));
        btn_update_Book.addActionListener(e -> btn_update_BookActionPerformed());

        btn_delete_Book = createBtn("Xóa", "/images/bin.png", new Color(220, 38, 38));
        btn_delete_Book.addActionListener(e -> btn_delete_BookActionPerformed());

        btn_refresh_Book = createBtn("Làm Mới", "/images/refresh.png", new Color(75, 85, 99));
        btn_refresh_Book.addActionListener(e -> btn_refresh_BookActionPerformed());

        btnPanel.add(btn_create_Book);
        btnPanel.add(btn_update_Book);
        btnPanel.add(btn_delete_Book);
        btnPanel.add(btn_refresh_Book);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formCard);
        formScroll.setBorder(null);
        formScroll.setPreferredSize(new Dimension(420, 500));
        add(formScroll, BorderLayout.WEST);

        // Right Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JLabel searchIcon = new JLabel();
        try {
            searchIcon.setIcon(new ImageIcon(getClass().getResource("/images/search_32.png")));
        } catch (Exception ignored) {}
        searchIcon.setText(" Tìm Kiếm Sách: ");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        tf_search_Book = new JTextField(25);
        styleTextField(tf_search_Book);
        searchBar.add(searchIcon);
        searchBar.add(tf_search_Book);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_Book = new JTable();
        tbl_Book.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tbl_BookMousePressed();
            }
        });
        sp_Book = new JScrollPane(tbl_Book);
        tablePanel.add(sp_Book, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    public synchronized void showTableBook() {
        try {
            Response response = controller.getBooksController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Book.setRowSorter(sorter);

                tf_search_Book.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e) { search(tf_search_Book.getText()); }
                    @Override public void removeUpdate(DocumentEvent e) { search(tf_search_Book.getText()); }
                    @Override public void changedUpdate(DocumentEvent e) { search(tf_search_Book.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) sorter.setRowFilter(null);
                        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                });

                tbl_Book.setModel(model);
                tbl_Book.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_Book.setRowHeight(30);
                tbl_Book.setDefaultEditor(Object.class, null);
                TableColumn indexColumn = tbl_Book.getColumnModel().getColumn(0);
                indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                indexColumn.setMaxWidth(80);
                sp_Book.setViewportView(tbl_Book);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void showDataComboBoxCategory() {
        try {
            Response response = controller.getDataComboBoxCategories();
            cb_category_Book.removeAllItems();
            if (response != null && response.getData() instanceof List) {
                List<Category> categoryList = (List<Category>) response.getData();
                for (Category i : categoryList) cb_category_Book.addItem(i);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void showDataComboBoxAuthor() {
        try {
            Response response = controller.getDataComboBoxAuthors();
            cb_author_Book.removeAllItems();
            if (response != null && response.getData() instanceof List) {
                List<Author> authorList = (List<Author>) response.getData();
                for (Author i : authorList) cb_author_Book.addItem(i);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void tbl_BookMousePressed() {
        int selectedRow = tbl_Book.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tbl_Book.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) tbl_Book.getModel();

            tf_ID_Book.setText(String.valueOf(model.getValueAt(modelRow, 0)));
            tf_title_Book.setText(String.valueOf(model.getValueAt(modelRow, 1)));

            String categoryName = String.valueOf(model.getValueAt(modelRow, 2));
            for (int i = 0; i < cb_category_Book.getItemCount(); i++) {
                if (cb_category_Book.getItemAt(i).getName().equalsIgnoreCase(categoryName)) {
                    cb_category_Book.setSelectedIndex(i);
                    break;
                }
            }

            String authorName = String.valueOf(model.getValueAt(modelRow, 3));
            for (int i = 0; i < cb_author_Book.getItemCount(); i++) {
                if (cb_author_Book.getItemAt(i).getName().equalsIgnoreCase(authorName)) {
                    cb_author_Book.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void btn_create_BookActionPerformed() {
        String title = tf_title_Book.getText().trim();
        Category category = (Category) cb_category_Book.getSelectedItem();
        Author author = (Author) cb_author_Book.getSelectedItem();

        if (title.isEmpty() || category == null || author == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin sách!");
            return;
        }

        Book book = new Book();
        book.setTitle(title);
        book.setCategory_id(category.getId());

        try {
            Response response = controller.createBookController(book, author.getId());
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_BookActionPerformed();
                showTableBook();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void btn_update_BookActionPerformed() {
        String idText = tf_ID_Book.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần cập nhật từ bảng!");
            return;
        }

        String title = tf_title_Book.getText().trim();
        Category category = (Category) cb_category_Book.getSelectedItem();
        Author author = (Author) cb_author_Book.getSelectedItem();

        Book book = new Book();
        book.setId(Integer.parseInt(idText));
        book.setTitle(title);
        book.setCategory_id(category != null ? category.getId() : 0);

        try {
            Response response = controller.updateBookController(book, author != null ? author.getId() : 0);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_BookActionPerformed();
                showTableBook();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void btn_delete_BookActionPerformed() {
        String idText = tf_ID_Book.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sách ID " + idText + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Response response = controller.deleteBookController(Integer.parseInt(idText));
                if (response != null) {
                    JOptionPane.showMessageDialog(this, response.getData());
                    btn_refresh_BookActionPerformed();
                    showTableBook();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void btn_refresh_BookActionPerformed() {
        tf_ID_Book.setText("");
        tf_title_Book.setText("");
        if (cb_category_Book.getItemCount() > 0) cb_category_Book.setSelectedIndex(0);
        if (cb_author_Book.getItemCount() > 0) cb_author_Book.setSelectedIndex(0);
        showTableBook();
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
