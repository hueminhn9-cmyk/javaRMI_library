package admin.pages;

import admin.ManagerController;
import common.CenteredTableCellRenderer;
import common.CustomHeaderRenderer;
import common.Response;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.rmi.RemoteException;

public class CheckoutManagePanel extends JPanel {
    private ManagerController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTable tbl_Checkout;
    private JScrollPane sp_Checkout;
    private JTextField tf_search_Checkout;
    private JButton btn_refresh_Checkout;

    public CheckoutManagePanel(ManagerController controller) {
        this.controller = controller;
        initComponents();
        showTableCheckout();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        
        JLabel searchIcon = new JLabel();
        java.net.URL searchImgUrl = getClass().getResource("/images/search_32.png");
        if (searchImgUrl != null) {
            searchIcon.setIcon(new ImageIcon(searchImgUrl));
        } else {
            searchIcon.setText("🔍");
            searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        }

        JLabel searchLabel = new JLabel("Tìm Kiếm Mượn Trả: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(new Color(30, 41, 59));

        tf_search_Checkout = new JTextField(25);
        tf_search_Checkout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf_search_Checkout.setPreferredSize(new Dimension(240, 38));
        tf_search_Checkout.setMargin(new Insets(4, 8, 4, 8));

        btn_refresh_Checkout = new JButton("Làm Mới");
        btn_refresh_Checkout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_refresh_Checkout.setBackground(new Color(75, 85, 99));
        btn_refresh_Checkout.setForeground(Color.WHITE);
        btn_refresh_Checkout.setFocusPainted(false);
        btn_refresh_Checkout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_refresh_Checkout.setPreferredSize(new Dimension(120, 38));
        java.net.URL refreshImgUrl = getClass().getResource("/images/refresh.png");
        if (refreshImgUrl != null) {
            btn_refresh_Checkout.setIcon(new ImageIcon(refreshImgUrl));
        }
        btn_refresh_Checkout.addActionListener(e -> showTableCheckout());

        topBar.add(searchIcon);
        topBar.add(searchLabel);
        topBar.add(tf_search_Checkout);
        topBar.add(btn_refresh_Checkout);

        add(topBar, BorderLayout.NORTH);

        tbl_Checkout = new JTable();
        sp_Checkout = new JScrollPane(tbl_Checkout);
        add(sp_Checkout, BorderLayout.CENTER);
    }

    public synchronized void showTableCheckout() {
        try {
            Response response = controller.getCheckoutsController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Checkout.setRowSorter(sorter);

                tf_search_Checkout.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e) { search(tf_search_Checkout.getText()); }
                    @Override public void removeUpdate(DocumentEvent e) { search(tf_search_Checkout.getText()); }
                    @Override public void changedUpdate(DocumentEvent e) { search(tf_search_Checkout.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) sorter.setRowFilter(null);
                        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                });

                tbl_Checkout.setModel(model);
                tbl_Checkout.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_Checkout.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tbl_Checkout.setRowHeight(34);
                tbl_Checkout.setDefaultEditor(Object.class, null);
                TableColumn indexColumn = tbl_Checkout.getColumnModel().getColumn(0);
                indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                indexColumn.setMaxWidth(80);
                sp_Checkout.setViewportView(tbl_Checkout);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
