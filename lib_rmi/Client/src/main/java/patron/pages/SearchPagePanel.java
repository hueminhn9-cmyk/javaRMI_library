package patron.pages;

import common.model.Checkout;
import common.rmi.Config;
import common.model.Response;
import patron.ClientController;
import common.model.Patron;
import patron.UIStyleHelper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SearchPagePanel extends JPanel {
    private Patron patron;
    private ClientController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTable tbl_Book;
    private JScrollPane sp_Book;
    private JTextField tf_Search_Book;

    private JLabel lb_Search_BookName;
    private JLabel lb_Search_Published;
    private JLabel lb_Search_Year;
    private JLabel lb_Search_Category;
    private JLabel lb_Search_Author;
    private JButton btn_Borrow;

    public SearchPagePanel(Patron patron, ClientController controller) {
        this.patron = patron;
        this.controller = controller;
        initComponents();
        showBookForSearch();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIStyleHelper.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Left Panel: Search & Table Card
        JPanel leftCard = UIStyleHelper.createCardPanel("Danh Sách Sách Trong Thư Viện");

        JPanel searchBarPanel = new JPanel(new BorderLayout(8, 0));
        searchBarPanel.setOpaque(false);
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        ImageIcon searchIcon = UIStyleHelper.getIcon("/images/search.png", 16, 16);
        if (searchIcon != null) lblSearch.setIcon(searchIcon);
        lblSearch.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblSearch.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);

        tf_Search_Book = new JTextField();
        UIStyleHelper.styleTextField(tf_Search_Book);

        searchBarPanel.add(lblSearch, BorderLayout.WEST);
        searchBarPanel.add(tf_Search_Book, BorderLayout.CENTER);

        tbl_Book = new JTable();
        tbl_Book.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tbl_BookMousePressed(evt);
            }
        });
        sp_Book = new JScrollPane(tbl_Book);
        UIStyleHelper.styleTable(tbl_Book, sp_Book);

        leftCard.add(searchBarPanel, BorderLayout.NORTH);
        leftCard.add(sp_Book, BorderLayout.CENTER);

        // Right Panel: Book Detail Card
        JPanel rightCard = UIStyleHelper.createCardPanel("Chi Tiết Sách");
        rightCard.setPreferredSize(new Dimension(380, 0));

        JPanel detailContainer = new JPanel();
        detailContainer.setLayout(new BoxLayout(detailContainer, BoxLayout.Y_AXIS));
        detailContainer.setOpaque(false);
        detailContainer.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        detailContainer.add(createDetailRow("Tên sách:", lb_Search_BookName = new JLabel("-")));
        detailContainer.add(Box.createVerticalStrut(15));
        detailContainer.add(createDetailRow("Tác giả:", lb_Search_Author = new JLabel("-")));
        detailContainer.add(Box.createVerticalStrut(15));
        detailContainer.add(createDetailRow("Thể loại:", lb_Search_Category = new JLabel("-")));
        detailContainer.add(Box.createVerticalStrut(15));
        detailContainer.add(createDetailRow("Nhà xuất bản:", lb_Search_Published = new JLabel("-")));
        detailContainer.add(Box.createVerticalStrut(15));
        detailContainer.add(createDetailRow("Năm xuất bản:", lb_Search_Year = new JLabel("-")));
        detailContainer.add(Box.createVerticalStrut(25));

        btn_Borrow = new JButton("Mượn sách");
        ImageIcon borrowIcon = UIStyleHelper.getIcon("/images/checked.png", 16, 16);
        if (borrowIcon != null) btn_Borrow.setIcon(borrowIcon);
        UIStyleHelper.styleButton(btn_Borrow, UIStyleHelper.COLOR_PRIMARY_BG, UIStyleHelper.COLOR_PRIMARY_BORDER, Color.BLACK);
        btn_Borrow.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn_Borrow.setMaximumSize(new Dimension(220, 40));
        btn_Borrow.addActionListener(e -> btn_BorrowActionPerformed(e));
        detailContainer.add(btn_Borrow);

        rightCard.add(detailContainer, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCard, rightCard);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        splitPane.setBackground(UIStyleHelper.COLOR_BG_LIGHT);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createDetailRow(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lbl.setForeground(UIStyleHelper.COLOR_TEXT_MUTED);
        lbl.setPreferredSize(new Dimension(100, 24));

        valueLabel.setFont(UIStyleHelper.FONT_BODY_BOLD);
        valueLabel.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);

        row.add(lbl, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);
        return row;
    }

    public void showBookForSearch() {
        try {
            Response response = controller.getBookForSearchController();
            if (response != null && response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }

            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Book.setRowSorter(sorter);

                tf_Search_Book.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) { search(tf_Search_Book.getText()); }
                    @Override
                    public void removeUpdate(DocumentEvent e) { search(tf_Search_Book.getText()); }
                    @Override
                    public void changedUpdate(DocumentEvent e) { search(tf_Search_Book.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) {
                            sorter.setRowFilter(null);
                        } else {
                            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                        }
                    }
                });

                tbl_Book.setModel(model);
                tbl_Book.setDefaultEditor(Object.class, null);
                UIStyleHelper.styleTable(tbl_Book, sp_Book);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void tbl_BookMousePressed(MouseEvent evt) {
        int selectedRow = tbl_Book.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tbl_Book.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) tbl_Book.getModel();

            String title = String.valueOf(model.getValueAt(modelRow, 1));
            String category = String.valueOf(model.getValueAt(modelRow, 2));
            String author = String.valueOf(model.getValueAt(modelRow, 3));
            String published = String.valueOf(model.getValueAt(modelRow, 4));
            String year = String.valueOf(model.getValueAt(modelRow, 5));

            lb_Search_BookName.setText(title);
            lb_Search_Category.setText(category);
            lb_Search_Author.setText(author);
            lb_Search_Published.setText(published);
            lb_Search_Year.setText(year);
        }
    }

    private void btn_BorrowActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tbl_Book.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tbl_Book.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) tbl_Book.getModel();
            int bookCopyId = (int) model.getValueAt(modelRow, 0);

            Checkout checkout = new Checkout();
            checkout.setPatron_id(patron.getId());
            checkout.setBook_copy_id(bookCopyId);

            long currentTimeMillis = System.currentTimeMillis();
            Timestamp now = new Timestamp(currentTimeMillis);
            checkout.setStart_time(now);

            LocalDateTime nowW = LocalDateTime.now();
            LocalDateTime afterW = nowW.plus(Config.DAY_FOR_BORROW, ChronoUnit.DAYS);
            Timestamp after = Timestamp.valueOf(afterW);
            checkout.setEnd_time(after);

            try {
                Response response = controller.clientBorrowBookCopy(checkout);
                if (response != null) {
                    JOptionPane.showMessageDialog(this, response.getData());
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách trong danh sách trước!");
        }
    }
}
