package admin.pages;

import admin.ManagerController;
import common.model.*;
import common.rmi.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
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

    // Form fields
    private JTextField tf_ID_BookCopy;
    private JTextField tf_barcode;
    private JComboBox<Book> cb_book_BookCopy;
    private JTextField tf_shelf;
    private JComboBox<Published> cb_published_BookCopy;
    private JTextField tf_year_BookCopy;
    private JComboBox<String> cb_policy;
    private JCheckBox chk_active;

    // Action buttons
    private JButton btn_create_BookCopy;
    private JButton btn_update_BookCopy;
    private JButton btn_delete_BookCopy;
    private JButton btn_refresh_BookCopy;

    // Table & Search
    private JTable tbl_BookCopy;
    private JScrollPane sp_BookCopy;
    private JTextField tf_search_BookCopy;
    private JLabel lblTotalRecords;

    // Colors - Gray & Blue palette
    private final Color BG_MAIN = new Color(248, 250, 252);     // Slate 50
    private final Color BG_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(37, 99, 235); // Blue 600
    private final Color COLOR_PRIMARY_HOVER = new Color(29, 78, 216);
    private final Color COLOR_TEXT_DARK = new Color(30, 41, 59); // Slate 800
    private final Color COLOR_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    private final Color COLOR_BORDER = new Color(226, 232, 240); // Slate 200
    private final Color COLOR_GREEN_BG = new Color(220, 252, 231); // Green 100
    private final Color COLOR_GREEN_TEXT = new Color(22, 101, 52); // Green 800
    private final Color COLOR_DANGER_BG = new Color(254, 226, 226); // Red 100
    private final Color COLOR_DANGER_TEXT = new Color(153, 27, 27); // Red 800

    public BookCopyManagePanel(ManagerController controller) {
        this.controller = controller;
        setBackground(BG_MAIN);
        initComponents();
        showTableBookCopy();
        showDataComboBoxBooks();
        showDataComboBoxPublished();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 20, 15, 20));

        // -------------------------------------------------------------
        // TOP HEADER SECTION: Breadcrumb + Title + Single Stats Card
        // -------------------------------------------------------------
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Breadcrumb & Title Bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);

        JPanel titleLeft = new JPanel();
        titleLeft.setLayout(new BoxLayout(titleLeft, BoxLayout.Y_AXIS));
        titleLeft.setOpaque(false);

        JLabel lblBreadcrumb = new JLabel("KHO LƯU TRỮ TÀI NGUYÊN / QUẢN LÝ BẢN SAO SÁCH (ITEM COPIES)");
        lblBreadcrumb.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBreadcrumb.setForeground(COLOR_TEXT_MUTED);

        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headingPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Danh Mục Bản Sao Sách");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_TEXT_DARK);

        lblTotalRecords = new JLabel("Tổng: -- bản sao hiện vật");
        lblTotalRecords.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotalRecords.setForeground(COLOR_TEXT_MUTED);
        lblTotalRecords.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(3, 10, 3, 10)
        ));

        headingPanel.add(lblTitle);
        headingPanel.add(lblTotalRecords);

        titleLeft.add(lblBreadcrumb);
        titleLeft.add(Box.createVerticalStrut(4));
        titleLeft.add(headingPanel);

        titleBar.add(titleLeft, BorderLayout.WEST);

        topContainer.add(titleBar);
        topContainer.add(Box.createVerticalStrut(10));

        add(topContainer, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // CENTER MAIN AREA: Left Table Column + Right Form Panel Column
        // -------------------------------------------------------------
        JPanel mainContent = new JPanel(new BorderLayout(15, 0));
        mainContent.setOpaque(false);

        // LEFT COLUMN: Search Bar + Status Filter Chips + JTable + Pagination
        JPanel leftTablePanel = new JPanel(new BorderLayout(0, 10));
        leftTablePanel.setOpaque(false);

        // Search & Filter Header
        JPanel searchFilterBar = new JPanel(new BorderLayout(10, 0));
        searchFilterBar.setBackground(BG_CARD);
        searchFilterBar.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        // Search Input
        JPanel searchInputPanel = new JPanel(new BorderLayout(6, 0));
        searchInputPanel.setOpaque(false);
        JLabel lblSearchIcon = new JLabel("🔍");
        lblSearchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tf_search_BookCopy = new JTextField();
        tf_search_BookCopy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf_search_BookCopy.setBorder(null);
        tf_search_BookCopy.putClientProperty("JTextField.placeholderText", "Tìm theo Mã BC, Barcode, Tên sách, Kệ sách...");
        tf_search_BookCopy.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(tf_search_BookCopy.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(tf_search_BookCopy.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(tf_search_BookCopy.getText()); }
        });

        searchInputPanel.add(lblSearchIcon, BorderLayout.WEST);
        searchInputPanel.add(tf_search_BookCopy, BorderLayout.CENTER);

        // Filter Chips Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterPanel.setOpaque(false);

        JLabel lblFilterTag = new JLabel("Trạng thái: ");
        lblFilterTag.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFilterTag.setForeground(COLOR_TEXT_MUTED);
        filterPanel.add(lblFilterTag);

        JButton btnFilterAll = createFilterChip("Tất cả", true);
        JButton btnFilterAvail = createFilterChip("Khả dụng", false);
        JButton btnFilterBorrow = createFilterChip("Đang mượn", false);

        btnFilterAll.addActionListener(e -> applyFilter(""));
        btnFilterAvail.addActionListener(e -> applyFilter("Khả dụng"));
        btnFilterBorrow.addActionListener(e -> applyFilter("Đang mượn"));

        filterPanel.add(btnFilterAll);
        filterPanel.add(btnFilterAvail);
        filterPanel.add(btnFilterBorrow);

        searchFilterBar.add(searchInputPanel, BorderLayout.CENTER);
        searchFilterBar.add(filterPanel, BorderLayout.EAST);

        leftTablePanel.add(searchFilterBar, BorderLayout.NORTH);

        // JTable Area
        tbl_BookCopy = new JTable();
        tbl_BookCopy.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tbl_BookCopyMousePressed();
            }
        });

        sp_BookCopy = new JScrollPane(tbl_BookCopy);
        sp_BookCopy.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        sp_BookCopy.getViewport().setBackground(BG_CARD);

        leftTablePanel.add(sp_BookCopy, BorderLayout.CENTER);

        // Pagination / Summary Footer
        JPanel pageFooter = new JPanel(new BorderLayout());
        pageFooter.setOpaque(false);

        JLabel lblPaginationInfo = new JLabel("Hiển thị dữ liệu thực tế từ máy chủ RMI Server");
        lblPaginationInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblPaginationInfo.setForeground(COLOR_TEXT_MUTED);

        pageFooter.add(lblPaginationInfo, BorderLayout.WEST);
        leftTablePanel.add(pageFooter, BorderLayout.SOUTH);

        mainContent.add(leftTablePanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // RIGHT COLUMN: Form Card "Thông Tin Bản Sao Hiện Vật"
        // -------------------------------------------------------------
        JPanel rightFormCard = new JPanel(new BorderLayout(0, 12));
        rightFormCard.setBackground(BG_CARD);
        rightFormCard.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(15, 18, 15, 18)
        ));
        rightFormCard.setPreferredSize(new Dimension(380, 0));

        // Form Header
        JPanel formHeader = new JPanel(new BorderLayout());
        formHeader.setOpaque(false);

        JLabel lblFormTitle = new JLabel("Thông Tin Bản Sao Hiện Vật");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFormTitle.setForeground(COLOR_TEXT_DARK);

        JLabel lblBadge = new JLabel("Sẵn Sàng Mượn");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(COLOR_GREEN_TEXT);
        lblBadge.setBackground(COLOR_GREEN_BG);
        lblBadge.setOpaque(true);
        lblBadge.setBorder(new EmptyBorder(3, 8, 3, 8));

        formHeader.add(lblFormTitle, BorderLayout.WEST);
        formHeader.add(lblBadge, BorderLayout.EAST);

        rightFormCard.add(formHeader, BorderLayout.NORTH);

        // Form Inputs (GridBagLayout)
        JPanel formBody = new JPanel(new GridBagLayout());
        formBody.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 0: ID + Barcode
        tf_ID_BookCopy = new JTextField();
        tf_ID_BookCopy.setEditable(false);
        styleFormTextField(tf_ID_BookCopy);

        tf_barcode = new JTextField("AUTO-GEN");
        tf_barcode.setEditable(false);
        styleFormTextField(tf_barcode);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formBody.add(createFormLabel("Mã Bản Sao (ID)"), gbc);
        gbc.gridx = 1;
        formBody.add(createFormLabel("Mã Barcode / Quét"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formBody.add(tf_ID_BookCopy, gbc);
        gbc.gridx = 1;
        formBody.add(tf_barcode, gbc);

        // Row 1: Book Title Dropdown (Full width)
        cb_book_BookCopy = new JComboBox<>();
        styleFormComboBox(cb_book_BookCopy);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formBody.add(createFormLabel("Đầu Sách (Title Master) *"), gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        formBody.add(cb_book_BookCopy, gbc);

        // Row 2: Shelf / Location + Year
        tf_shelf = new JTextField("Kệ A2-04 (Tầng 2)");
        styleFormTextField(tf_shelf);

        tf_year_BookCopy = new JTextField();
        styleFormTextField(tf_year_BookCopy);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formBody.add(createFormLabel("Vị Trí Giá / Kệ Sách"), gbc);
        gbc.gridx = 1;
        formBody.add(createFormLabel("Năm Xuất Bản *"), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formBody.add(tf_shelf, gbc);
        gbc.gridx = 1;
        formBody.add(tf_year_BookCopy, gbc);

        // Row 3: Publisher + Circulation Policy
        cb_published_BookCopy = new JComboBox<>();
        styleFormComboBox(cb_published_BookCopy);

        cb_policy = new JComboBox<>(new String[]{"Cho phép mượn về (14 ngày)", "Đọc tại chỗ"});
        styleFormComboBox(cb_policy);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        formBody.add(createFormLabel("Nhà Xuất Bản"), gbc);
        gbc.gridx = 1;
        formBody.add(createFormLabel("Quy Chế Lưu Hành"), gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formBody.add(cb_published_BookCopy, gbc);
        gbc.gridx = 1;
        formBody.add(cb_policy, gbc);

        // Row 4: Active Checkbox
        chk_active = new JCheckBox("Kích hoạt lưu hành (Active)", true);
        chk_active.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chk_active.setForeground(COLOR_TEXT_DARK);
        chk_active.setOpaque(false);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        formBody.add(chk_active, gbc);

        JScrollPane formScroll = new JScrollPane(formBody);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        rightFormCard.add(formScroll, BorderLayout.CENTER);

        // Form Action Buttons Panel
        JPanel actionButtonsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        actionButtonsPanel.setOpaque(false);

        btn_update_BookCopy = createPrimaryButton("Lưu Thay Đổi", COLOR_PRIMARY);
        btn_update_BookCopy.addActionListener(e -> btn_update_BookCopyActionPerformed());

        btn_create_BookCopy = createSecondaryButton("+ Thêm Bản Sao");
        btn_create_BookCopy.addActionListener(e -> btn_create_BookCopyActionPerformed());

        btn_refresh_BookCopy = createSecondaryButton("Làm Mới Form");
        btn_refresh_BookCopy.addActionListener(e -> btn_refresh_BookCopyActionPerformed());

        btn_delete_BookCopy = createDangerButton("Hủy Bản Sao");
        btn_delete_BookCopy.addActionListener(e -> btn_delete_BookCopyActionPerformed());

        actionButtonsPanel.add(btn_update_BookCopy);
        actionButtonsPanel.add(btn_create_BookCopy);
        actionButtonsPanel.add(btn_refresh_BookCopy);
        actionButtonsPanel.add(btn_delete_BookCopy);

        rightFormCard.add(actionButtonsPanel, BorderLayout.SOUTH);

        mainContent.add(rightFormCard, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createSingleStatCard(String title, String val1, String val2) {
        JPanel card = new JPanel(new BorderLayout(10, 6));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(12, 16, 12, 16)
        ));
        card.setPreferredSize(new Dimension(360, 70));

        JLabel iconLabel = new JLabel("✅");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblCardTitle = new JLabel(title);
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCardTitle.setForeground(COLOR_TEXT_MUTED);

        JLabel lblVal1 = new JLabel(val1);
        lblVal1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVal1.setForeground(COLOR_TEXT_DARK);

        JLabel lblVal2 = new JLabel(val2);
        lblVal2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVal2.setForeground(new Color(22, 163, 74));

        textPanel.add(lblCardTitle);
        textPanel.add(lblVal1);
        textPanel.add(lblVal2);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(COLOR_TEXT_DARK);
        return lbl;
    }

    private void styleFormTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(140, 34));
        tf.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
    }

    private void styleFormComboBox(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setPreferredSize(new Dimension(140, 34));
        cb.setBackground(Color.WHITE);
    }

    private JButton createPrimaryButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(BG_CARD);
        btn.setForeground(Color.BLACK);
        btn.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));
        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(COLOR_DANGER_BG);
        btn.setForeground(Color.BLACK);
        btn.setBorder(new LineBorder(new Color(252, 165, 165), 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));
        return btn;
    }

    private JButton createFilterChip(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 12));
        btn.setBackground(active ? COLOR_PRIMARY : BG_CARD);
        btn.setForeground(Color.BLACK);
        btn.setBorder(new LineBorder(active ? COLOR_PRIMARY : COLOR_BORDER, 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(2, 10, 2, 10));
        return btn;
    }

    private void applyFilter(String text) {
        if (sorter != null) {
            if (text == null || text.trim().isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }
    }

    public synchronized void showTableBookCopy() {
        try {
            Response response = controller.getBooksCopyController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_BookCopy.setModel(model);
                tbl_BookCopy.setRowSorter(sorter);

                // Apply active search text filter if any
                String searchStr = tf_search_BookCopy.getText();
                if (searchStr != null && !searchStr.trim().isEmpty()) {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchStr));
                }

                tbl_BookCopy.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_BookCopy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tbl_BookCopy.setRowHeight(36);
                tbl_BookCopy.setSelectionBackground(new Color(224, 242, 254));
                tbl_BookCopy.setSelectionForeground(COLOR_TEXT_DARK);
                tbl_BookCopy.setDefaultEditor(Object.class, null);

                if (tbl_BookCopy.getColumnCount() > 0) {
                    TableColumn indexColumn = tbl_BookCopy.getColumnModel().getColumn(0);
                    indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                    indexColumn.setMaxWidth(80);
                }

                if (lblTotalRecords != null) {
                    lblTotalRecords.setText("Tổng: " + model.getRowCount() + " bản sao hiện vật");
                }

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

            String idVal = String.valueOf(model.getValueAt(modelRow, 0));
            tf_ID_BookCopy.setText(idVal);
            tf_barcode.setText("BC-00" + idVal);
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
        tf_barcode.setText("AUTO-GEN");
        tf_year_BookCopy.setText("");
        if (cb_book_BookCopy.getItemCount() > 0) cb_book_BookCopy.setSelectedIndex(0);
        if (cb_published_BookCopy.getItemCount() > 0) cb_published_BookCopy.setSelectedIndex(0);
        showTableBookCopy();
    }
}
