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

public class BookCopyManagePanel extends JPanel {
    private ManagerController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField tf_ID_BookCopy;
    private JComboBox<Book> cb_book_BookCopy;
    private JComboBox<Published> cb_published_BookCopy;
    private JTextField tf_year_BookCopy;

    private JButton btn_create_BookCopy;
    private JButton btn_update_BookCopy;
    private JButton btn_delete_BookCopy;
    private JButton btn_refresh_BookCopy;

    private JTable tbl_BookCopy;
    private JScrollPane sp_BookCopy;
    private JTextField tf_search_BookCopy;

    public BookCopyManagePanel(ManagerController controller) {
        this.controller = controller;
        initComponents();
        showTableBookCopy();
        showDataComboBoxBooks();
        showDataComboBoxPublished();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Bản Sao Sách ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 15), new Color(30, 41, 59)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        tf_ID_BookCopy = new JTextField();
        tf_ID_BookCopy.setEditable(false);
        styleTextField(tf_ID_BookCopy);

        cb_book_BookCopy = new JComboBox<>();
        cb_book_BookCopy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb_book_BookCopy.setPreferredSize(new Dimension(220, 38));

        cb_published_BookCopy = new JComboBox<>();
        cb_published_BookCopy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb_published_BookCopy.setPreferredSize(new Dimension(220, 38));

        tf_year_BookCopy = new JTextField();
        styleTextField(tf_year_BookCopy);

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(createLabel("Mã Bản Sao (ID):"), gbc);
        gbc.gridx = 1; formCard.add(tf_ID_BookCopy, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(createLabel("Tên Sách:"), gbc);
        gbc.gridx = 1; formCard.add(cb_book_BookCopy, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formCard.add(createLabel("Nhà Xuất Bản:"), gbc);
        gbc.gridx = 1; formCard.add(cb_published_BookCopy, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formCard.add(createLabel("Năm Xuất Bản:"), gbc);
        gbc.gridx = 1; formCard.add(tf_year_BookCopy, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        btn_create_BookCopy = createBtn("Thêm Mới", new Color(22, 163, 74), "/images/add.png");
        btn_create_BookCopy.addActionListener(e -> btn_create_BookCopyActionPerformed());

        btn_update_BookCopy = createBtn("Cập Nhật", new Color(37, 99, 235), "/images/edit.png");
        btn_update_BookCopy.addActionListener(e -> btn_update_BookCopyActionPerformed());

        btn_delete_BookCopy = createBtn("Xóa", new Color(220, 38, 38), "/images/bin.png");
        btn_delete_BookCopy.addActionListener(e -> btn_delete_BookCopyActionPerformed());

        btn_refresh_BookCopy = createBtn("Làm Mới", new Color(75, 85, 99), "/images/refresh.png");
        btn_refresh_BookCopy.addActionListener(e -> btn_refresh_BookCopyActionPerformed());

        btnPanel.add(btn_create_BookCopy);
        btnPanel.add(btn_update_BookCopy);
        btnPanel.add(btn_delete_BookCopy);
        btnPanel.add(btn_refresh_BookCopy);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formCard);
        formScroll.setBorder(null);
        formScroll.setPreferredSize(new Dimension(420, 500));
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
        
        JLabel searchLabel = new JLabel("Tìm Kiếm Bản Sao: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(new Color(30, 41, 59));

        tf_search_BookCopy = new JTextField(25);
        styleTextField(tf_search_BookCopy);
        
        searchBar.add(searchIcon);
        searchBar.add(searchLabel);
        searchBar.add(tf_search_BookCopy);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_BookCopy = new JTable();
        tbl_BookCopy.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tbl_BookCopyMousePressed();
            }
        });
        sp_BookCopy = new JScrollPane(tbl_BookCopy);
        tablePanel.add(sp_BookCopy, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(51, 65, 85));
        return lbl;
    }

    public synchronized void showTableBookCopy() {
        try {
            Response response = controller.getBooksCopyController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_BookCopy.setRowSorter(sorter);

                tf_search_BookCopy.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e) { search(tf_search_BookCopy.getText()); }
                    @Override public void removeUpdate(DocumentEvent e) { search(tf_search_BookCopy.getText()); }
                    @Override public void changedUpdate(DocumentEvent e) { search(tf_search_BookCopy.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) sorter.setRowFilter(null);
                        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                });

                tbl_BookCopy.setModel(model);
                tbl_BookCopy.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_BookCopy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tbl_BookCopy.setRowHeight(34);
                tbl_BookCopy.setDefaultEditor(Object.class, null);
                TableColumn indexColumn = tbl_BookCopy.getColumnModel().getColumn(0);
                indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                indexColumn.setMaxWidth(80);
                sp_BookCopy.setViewportView(tbl_BookCopy);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void showDataComboBoxBooks() {
        try {
            Response response = controller.getDataComboBoxBooks();
            cb_book_BookCopy.removeAllItems();
            if (response != null && response.getData() instanceof List) {
                List<Book> booksList = (List<Book>) response.getData();
                for (Book i : booksList) cb_book_BookCopy.addItem(i);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void showDataComboBoxPublished() {
        try {
            Response response = controller.getDataComboBoxPublished();
            cb_published_BookCopy.removeAllItems();
            if (response != null && response.getData() instanceof List) {
                List<Published> publishedList = (List<Published>) response.getData();
                for (Published i : publishedList) cb_published_BookCopy.addItem(i);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void tbl_BookCopyMousePressed() {
        int selectedRow = tbl_BookCopy.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tbl_BookCopy.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) tbl_BookCopy.getModel();

            tf_ID_BookCopy.setText(String.valueOf(model.getValueAt(modelRow, 0)));
            tf_year_BookCopy.setText(String.valueOf(model.getValueAt(modelRow, 2)));

            String bookTitle = String.valueOf(model.getValueAt(modelRow, 1));
            for (int i = 0; i < cb_book_BookCopy.getItemCount(); i++) {
                if (cb_book_BookCopy.getItemAt(i).getTitle().equalsIgnoreCase(bookTitle)) {
                    cb_book_BookCopy.setSelectedIndex(i);
                    break;
                }
            }

            String pubName = String.valueOf(model.getValueAt(modelRow, 3));
            for (int i = 0; i < cb_published_BookCopy.getItemCount(); i++) {
                if (cb_published_BookCopy.getItemAt(i).getName().equalsIgnoreCase(pubName)) {
                    cb_published_BookCopy.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void btn_create_BookCopyActionPerformed() {
        Book book = (Book) cb_book_BookCopy.getSelectedItem();
        Published published = (Published) cb_published_BookCopy.getSelectedItem();
        String yearText = tf_year_BookCopy.getText().trim();

        if (book == null || published == null || yearText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bản sao sách!");
            return;
        }

        try {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setBook_id(book.getId());
            bookCopy.setPublished_id(published.getId());
            bookCopy.setYear_published(Integer.parseInt(yearText));

            Response response = controller.createBookCopyController(bookCopy);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_BookCopyActionPerformed();
                showTableBookCopy();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Năm xuất bản phải là số nguyên!");
        }
    }

    private void btn_update_BookCopyActionPerformed() {
        String idText = tf_ID_BookCopy.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bản sao sách cần cập nhật!");
            return;
        }

        Book book = (Book) cb_book_BookCopy.getSelectedItem();
        Published published = (Published) cb_published_BookCopy.getSelectedItem();
        String yearText = tf_year_BookCopy.getText().trim();

        try {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setId(Integer.parseInt(idText));
            bookCopy.setBook_id(book != null ? book.getId() : 0);
            bookCopy.setPublished_id(published != null ? published.getId() : 0);
            bookCopy.setYear_published(Integer.parseInt(yearText));

            Response response = controller.updateBookCopyController(bookCopy);
            if (response != null) {
                JOptionPane.showMessageDialog(this, response.getData());
                btn_refresh_BookCopyActionPerformed();
                showTableBookCopy();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Năm xuất bản phải là số!");
        }
    }

    private void btn_delete_BookCopyActionPerformed() {
        String idText = tf_ID_BookCopy.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bản sao sách cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa bản sao ID " + idText + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Response response = controller.deleteBookCopyController(Integer.parseInt(idText));
                if (response != null) {
                    JOptionPane.showMessageDialog(this, response.getData());
                    btn_refresh_BookCopyActionPerformed();
                    showTableBookCopy();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void btn_refresh_BookCopyActionPerformed() {
        tf_ID_BookCopy.setText("");
        tf_year_BookCopy.setText("");
        if (cb_book_BookCopy.getItemCount() > 0) cb_book_BookCopy.setSelectedIndex(0);
        if (cb_published_BookCopy.getItemCount() > 0) cb_published_BookCopy.setSelectedIndex(0);
        showTableBookCopy();
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
