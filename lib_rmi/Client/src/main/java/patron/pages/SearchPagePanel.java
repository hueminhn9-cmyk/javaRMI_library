package patron.pages;

import common.Checkout;
import common.Config;
import common.Response;
import patron.ClientController;
import common.Patron;

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

        // Left Panel: Search Field & Book List Table
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchBarPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblSearch = new JLabel(" Search: ");
        lblSearch.setFont(new Font("Montserrat", Font.BOLD, 14));
        tf_Search_Book = new JTextField();
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

        leftPanel.add(searchBarPanel, BorderLayout.NORTH);
        leftPanel.add(sp_Book, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(550, 0));

        // Right Panel: Book Detail & Borrow Button
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleDetail = new JLabel("Book Detail", SwingConstants.CENTER);
        titleDetail.setFont(new Font("Montserrat ExtraBold", Font.BOLD, 22));
        rightPanel.add(titleDetail, BorderLayout.NORTH);

        JPanel infoGrid = new JPanel(new GridLayout(6, 2, 10, 20));
        infoGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        infoGrid.add(new JLabel("Book Name:"));
        lb_Search_BookName = new JLabel("-");
        infoGrid.add(lb_Search_BookName);

        infoGrid.add(new JLabel("Publisher:"));
        lb_Search_Published = new JLabel("-");
        infoGrid.add(lb_Search_Published);

        infoGrid.add(new JLabel("Year Published:"));
        lb_Search_Year = new JLabel("-");
        infoGrid.add(lb_Search_Year);

        infoGrid.add(new JLabel("Category:"));
        lb_Search_Category = new JLabel("-");
        infoGrid.add(lb_Search_Category);

        infoGrid.add(new JLabel("Author:"));
        lb_Search_Author = new JLabel("-");
        infoGrid.add(lb_Search_Author);

        btn_Borrow = new JButton("Borrow this book !");
        btn_Borrow.setFont(new Font("Montserrat", Font.BOLD, 13));
        btn_Borrow.setBackground(new Color(52, 152, 219));
        btn_Borrow.setForeground(Color.WHITE);
        btn_Borrow.addActionListener(e -> btn_BorrowActionPerformed(e));
        infoGrid.add(btn_Borrow);

        rightPanel.add(infoGrid, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);
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
                tbl_Book.setRowHeight(40);
                tbl_Book.setDefaultEditor(Object.class, null);
                sp_Book.setViewportView(tbl_Book);
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
            JOptionPane.showMessageDialog(this, "Please select a book from the list first!");
        }
    }
}
