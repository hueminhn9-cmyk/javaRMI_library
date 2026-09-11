package patron.pages;

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

public class ReturnPagePanel extends JPanel {
    private Patron patron;
    private ClientController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTable tbl_Profile;
    private JScrollPane sp_Profile;
    private JTextField tf_Search_Return;

    private JLabel lb_BookName_Return;
    private JLabel lb_Return_TimeStart;
    private JLabel lb_Return_TimeEnd;
    private JButton btn_Return;

    public ReturnPagePanel(Patron patron, ClientController controller) {
        this.patron = patron;
        this.controller = controller;
        initComponents();
        showCheckouts();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Left Panel: Borrow History Detail & Return Button
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleDetail = new JLabel("Borrow Ticket Detail", SwingConstants.CENTER);
        titleDetail.setFont(new Font("Montserrat ExtraBold", Font.BOLD, 22));
        leftPanel.add(titleDetail, BorderLayout.NORTH);

        JPanel infoGrid = new JPanel(new GridLayout(4, 2, 10, 25));
        infoGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        infoGrid.add(new JLabel("Book Name:"));
        lb_BookName_Return = new JLabel("-");
        infoGrid.add(lb_BookName_Return);

        infoGrid.add(new JLabel("Start Time:"));
        lb_Return_TimeStart = new JLabel("-");
        infoGrid.add(lb_Return_TimeStart);

        infoGrid.add(new JLabel("End Time:"));
        lb_Return_TimeEnd = new JLabel("-");
        infoGrid.add(lb_Return_TimeEnd);

        btn_Return = new JButton("Return this book");
        btn_Return.setFont(new Font("Montserrat", Font.BOLD, 13));
        btn_Return.setBackground(new Color(46, 204, 113));
        btn_Return.setForeground(Color.WHITE);
        btn_Return.addActionListener(e -> btn_ReturnActionPerformed(e));
        infoGrid.add(btn_Return);

        leftPanel.add(infoGrid, BorderLayout.CENTER);

        // Right Panel: Search & Checkout Table
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchBarPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lblSearch = new JLabel(" Search: ");
        lblSearch.setFont(new Font("Montserrat", Font.BOLD, 14));
        tf_Search_Return = new JTextField();
        searchBarPanel.add(lblSearch, BorderLayout.WEST);
        searchBarPanel.add(tf_Search_Return, BorderLayout.CENTER);

        tbl_Profile = new JTable();
        tbl_Profile.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                tbl_ProfileMousePressed(evt);
            }
        });
        sp_Profile = new JScrollPane(tbl_Profile);

        rightPanel.add(searchBarPanel, BorderLayout.NORTH);
        rightPanel.add(sp_Profile, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);
    }

    public void showCheckouts() {
        try {
            Response response = controller.getCheckoutsClient(patron.getId());
            if (response != null && response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }

            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Profile.setRowSorter(sorter);

                tf_Search_Return.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) { search(tf_Search_Return.getText()); }
                    @Override
                    public void removeUpdate(DocumentEvent e) { search(tf_Search_Return.getText()); }
                    @Override
                    public void changedUpdate(DocumentEvent e) { search(tf_Search_Return.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) {
                            sorter.setRowFilter(null);
                        } else {
                            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                        }
                    }
                });

                tbl_Profile.setModel(model);
                tbl_Profile.setRowHeight(40);
                tbl_Profile.setDefaultEditor(Object.class, null);
                sp_Profile.setViewportView(tbl_Profile);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void tbl_ProfileMousePressed(MouseEvent evt) {
        int selectedRow = tbl_Profile.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tbl_Profile.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) tbl_Profile.getModel();

            String title = String.valueOf(model.getValueAt(modelRow, 1));
            String bor = String.valueOf(model.getValueAt(modelRow, 2));
            String ret = String.valueOf(model.getValueAt(modelRow, 3));

            lb_BookName_Return.setText(title);
            lb_Return_TimeStart.setText(bor);
            lb_Return_TimeEnd.setText(ret);
        }
    }

    private void btn_ReturnActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tbl_Profile.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tbl_Profile.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) tbl_Profile.getModel();
            int checkoutId = (int) model.getValueAt(modelRow, 0);

            try {
                Response response = controller.clientReturnBookCopy(checkoutId);
                if (response != null) {
                    JOptionPane.showMessageDialog(this, response.getData());
                    showCheckouts();
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a borrow ticket from the table first!");
        }
    }
}
