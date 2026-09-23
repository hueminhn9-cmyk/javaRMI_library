package admin.pages;

import admin.ManagerController;
import common.model.*;
import common.rmi.*;
import common.chat.*;

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

    private JButton btn_add_category_quick;
    private JButton btn_add_author_quick;

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
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        tf_ID_Book = new JTextField();
        tf_ID_Book.setEditable(false);
        styleTextField(tf_ID_Book);

        tf_title_Book = new JTextField();
        styleTextField(tf_title_Book);

        // Editable JComboBoxes so user can directly TYPE a new Category / Author name
        cb_category_Book = new JComboBox<>();
        cb_category_Book.setEditable(true);
        cb_category_Book.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb_category_Book.setPreferredSize(new Dimension(180, 36));

        btn_add_category_quick = new JButton("+");
        btn_add_category_quick.setToolTipText("Thêm nhanh Thể Loại mới");
        btn_add_category_quick.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_add_category_quick.setPreferredSize(new Dimension(36, 36));
        btn_add_category_quick.addActionListener(e -> quickAddCategory());

        JPanel catPanel = new JPanel(new BorderLayout(4, 0));
        catPanel.setOpaque(false);
        catPanel.add(cb_category_Book, BorderLayout.CENTER);
        catPanel.add(btn_add_category_quick, BorderLayout.EAST);

        cb_author_Book = new JComboBox<>();
        cb_author_Book.setEditable(true);
        cb_author_Book.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb_author_Book.setPreferredSize(new Dimension(180, 36));

        btn_add_author_quick = new JButton("+");
        btn_add_author_quick.setToolTipText("Thêm nhanh Tác Giả mới");
        btn_add_author_quick.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_add_author_quick.setPreferredSize(new Dimension(36, 36));
        btn_add_author_quick.addActionListener(e -> quickAddAuthor());

        JPanel authorPanel = new JPanel(new BorderLayout(4, 0));
        authorPanel.setOpaque(false);
        authorPanel.add(cb_author_Book, BorderLayout.CENTER);
        authorPanel.add(btn_add_author_quick, BorderLayout.EAST);

        JLabel lblId = new JLabel("Mã Sách (ID):"); lblId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel lblTitle = new JLabel("Tên Sách:"); lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel lblCat = new JLabel("Thể Loại (Tự nhập / Chọn):"); lblCat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel lblAuthor = new JLabel("Tác Giả (Tự nhập / Chọn):"); lblAuthor.setFont(new Font("Segoe UI", Font.BOLD, 12));

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(lblId, gbc);
        gbc.gridx = 1; formCard.add(tf_ID_Book, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(lblTitle, gbc);
        gbc.gridx = 1; formCard.add(tf_title_Book, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formCard.add(lblCat, gbc);
        gbc.gridx = 1; formCard.add(catPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formCard.add(lblAuthor, gbc);
        gbc.gridx = 1; formCard.add(authorPanel, gbc);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        btn_create_Book = createBtn("Thêm Mới Sách", "/images/add.png", new Color(22, 163, 74));
        btn_create_Book.addActionListener(e -> btn_create_BookActionPerformed());

        btn_update_Book = createBtn("Cập Nhật", "/images/edit.png", new Color(37, 99, 235));
        btn_update_Book.addActionListener(e -> btn_update_BookActionPerformed());

        btn_delete_Book = createBtn("Xóa Sách", "/images/bin.png", new Color(220, 38, 38));
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
        
        JLabel searchIcon = new JLabel("🔍 Tìm Kiếm Sách: ");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        tf_search_Book = new JTextField(25);
        styleTextField(tf_search_Book);
        tf_search_Book.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(tf_search_Book.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(tf_search_Book.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(tf_search_Book.getText()); }

            private void applyFilter(String str) {
                if (sorter != null) {
                    if (str == null || str.trim().isEmpty()) sorter.setRowFilter(null);
                    else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                }
            }
        });
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

    private String normalizeString(String input) {
        if (input == null) return "";
        return input.replaceAll("\\s+", " ").trim();
    }

    private void quickAddCategory() {
        String input = JOptionPane.showInputDialog(this, "Nhập tên Thể Loại mới cần thêm:", "Thêm Thể Loại Nhanh", JOptionPane.QUESTION_MESSAGE);
        String catName = normalizeString(input);
        if (!catName.isEmpty()) {
            // Check if already exists in combo box
            for (int i = 0; i < cb_category_Book.getItemCount(); i++) {
                Category c = cb_category_Book.getItemAt(i);
                if (c != null && normalizeString(c.getName()).equalsIgnoreCase(catName)) {
                    cb_category_Book.setSelectedIndex(i);
                    JOptionPane.showMessageDialog(this, "Thể loại '" + c.getName() + "' đã có sẵn trong CSDL! Hệ thống đã tự động chọn.", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            try {
                Category newCat = new Category();
                newCat.setName(catName);
                Response res = controller.createCategoryController(newCat);
                if (res != null) {
                    JOptionPane.showMessageDialog(this, res.getData());
                    showDataComboBoxCategory();
                    for (int i = 0; i < cb_category_Book.getItemCount(); i++) {
                        Category c = cb_category_Book.getItemAt(i);
                        if (c != null && normalizeString(c.getName()).equalsIgnoreCase(catName)) {
                            cb_category_Book.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void quickAddAuthor() {
        String input = JOptionPane.showInputDialog(this, "Nhập tên Tác Giả mới cần thêm:", "Thêm Tác Giả Nhanh", JOptionPane.QUESTION_MESSAGE);
        String authorName = normalizeString(input);
        if (!authorName.isEmpty()) {
            // Check if already exists in combo box
            for (int i = 0; i < cb_author_Book.getItemCount(); i++) {
                Author a = cb_author_Book.getItemAt(i);
                if (a != null && normalizeString(a.getName()).equalsIgnoreCase(authorName)) {
                    cb_author_Book.setSelectedIndex(i);
                    JOptionPane.showMessageDialog(this, "Tác giả '" + a.getName() + "' đã có sẵn trong CSDL! Hệ thống đã tự động chọn.", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            try {
                Author newAuth = new Author();
                newAuth.setName(authorName);
                Response res = controller.createAuthorController(newAuth);
                if (res != null) {
                    JOptionPane.showMessageDialog(this, res.getData());
                    showDataComboBoxAuthor();
                    for (int i = 0; i < cb_author_Book.getItemCount(); i++) {
                        Author a = cb_author_Book.getItemAt(i);
                        if (a != null && normalizeString(a.getName()).equalsIgnoreCase(authorName)) {
                            cb_author_Book.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private int resolveOrCreateCategory(Object selectedItem) {
        if (selectedItem == null) return 0;
        if (selectedItem instanceof Category) {
            return ((Category) selectedItem).getId();
        }

        String name = normalizeString(selectedItem.toString());
        if (name.isEmpty()) return 0;

        // Check if name matches an existing Category (normalized & case-insensitive)
        for (int i = 0; i < cb_category_Book.getItemCount(); i++) {
            Category c = cb_category_Book.getItemAt(i);
            if (c != null && normalizeString(c.getName()).equalsIgnoreCase(name)) {
                cb_category_Book.setSelectedIndex(i);
                return c.getId();
            }
        }

        // Auto create Category in DB via RMI if not exists
        try {
            Category newCat = new Category();
            newCat.setName(name);
            Response res = controller.createCategoryController(newCat);
            if (res != null && res.getStatus() == 200) {
                showDataComboBoxCategory();
                for (int i = 0; i < cb_category_Book.getItemCount(); i++) {
                    Category c = cb_category_Book.getItemAt(i);
                    if (c != null && normalizeString(c.getName()).equalsIgnoreCase(name)) {
                        cb_category_Book.setSelectedIndex(i);
                        return c.getId();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private int resolveOrCreateAuthor(Object selectedItem) {
        if (selectedItem == null) return 0;
        if (selectedItem instanceof Author) {
            return ((Author) selectedItem).getId();
        }

        String name = normalizeString(selectedItem.toString());
        if (name.isEmpty()) return 0;

        // Check if name matches an existing Author (normalized & case-insensitive)
        for (int i = 0; i < cb_author_Book.getItemCount(); i++) {
            Author a = cb_author_Book.getItemAt(i);
            if (a != null && normalizeString(a.getName()).equalsIgnoreCase(name)) {
                cb_author_Book.setSelectedIndex(i);
                return a.getId();
            }
        }

        // Auto create Author in DB via RMI if not exists
        try {
            Author newAuth = new Author();
            newAuth.setName(name);
            Response res = controller.createAuthorController(newAuth);
            if (res != null && res.getStatus() == 200) {
                showDataComboBoxAuthor();
                for (int i = 0; i < cb_author_Book.getItemCount(); i++) {
                    Author a = cb_author_Book.getItemAt(i);
                    if (a != null && normalizeString(a.getName()).equalsIgnoreCase(name)) {
                        cb_author_Book.setSelectedIndex(i);
                        return a.getId();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public synchronized void showTableBook() {
        try {
            Response response = controller.getBooksController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Book.setModel(model);
                tbl_Book.setRowSorter(sorter);

                String searchStr = tf_search_Book.getText();
                if (searchStr != null && !searchStr.trim().isEmpty()) {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchStr));
                }

                tbl_Book.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_Book.setRowHeight(34);
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
                Category c = cb_category_Book.getItemAt(i);
                if (c != null && c.getName().equalsIgnoreCase(categoryName)) {
                    cb_category_Book.setSelectedIndex(i);
                    break;
                }
            }

            String authorName = String.valueOf(model.getValueAt(modelRow, 3));
            for (int i = 0; i < cb_author_Book.getItemCount(); i++) {
                Author a = cb_author_Book.getItemAt(i);
                if (a != null && a.getName().equalsIgnoreCase(authorName)) {
                    cb_author_Book.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void btn_create_BookActionPerformed() {
        String title = tf_title_Book.getText().trim();
        Object selectedCat = cb_category_Book.getSelectedItem();
        Object selectedAuth = cb_author_Book.getSelectedItem();

        if (title.isEmpty() || selectedCat == null || selectedAuth == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên sách, thể loại và tác giả!");
            return;
        }

        int catId = resolveOrCreateCategory(selectedCat);
        int authId = resolveOrCreateAuthor(selectedAuth);

        if (catId == 0 || authId == 0) {
            JOptionPane.showMessageDialog(this, "Không thể xác định hoặc tạo Thể loại / Tác giả!");
            return;
        }

        Book book = new Book();
        book.setTitle(title);
        book.setCategory_id(catId);

        try {
            Response response = controller.createBookController(book, authId);
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
        Object selectedCat = cb_category_Book.getSelectedItem();
        Object selectedAuth = cb_author_Book.getSelectedItem();

        int catId = resolveOrCreateCategory(selectedCat);
        int authId = resolveOrCreateAuthor(selectedAuth);

        Book book = new Book();
        book.setId(Integer.parseInt(idText));
        book.setTitle(title);
        book.setCategory_id(catId);

        try {
            Response response = controller.updateBookController(book, authId);
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
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        if (iconPath != null) {
            try {
                btn.setIcon(new ImageIcon(getClass().getResource(iconPath)));
            } catch (Exception ignored) {}
        }
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(bg, 2, true),
            new javax.swing.border.EmptyBorder(5, 10, 5, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(135, 40));
        return btn;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(220, 38));
    }
}



