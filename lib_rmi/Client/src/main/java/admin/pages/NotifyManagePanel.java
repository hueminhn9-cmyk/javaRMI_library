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

public class NotifyManagePanel extends JPanel {
    private ManagerController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTable tbl_Notification;
    private JScrollPane sp_Notification;
    private JTextField tf_search_Notification;
    private JButton btn_refresh_Notification;

    public NotifyManagePanel(ManagerController controller) {
        this.controller = controller;
        initComponents();
        showTableNotification();
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

        JLabel searchLabel = new JLabel("Tìm Kiếm Thông Báo: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(new Color(30, 41, 59));

        tf_search_Notification = new JTextField(25);
        tf_search_Notification.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf_search_Notification.setPreferredSize(new Dimension(240, 38));
        tf_search_Notification.setMargin(new Insets(4, 8, 4, 8));

        btn_refresh_Notification = new JButton("Làm Mới");
        btn_refresh_Notification.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_refresh_Notification.setBackground(new Color(75, 85, 99));
        btn_refresh_Notification.setForeground(Color.WHITE);
        btn_refresh_Notification.setFocusPainted(false);
        btn_refresh_Notification.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_refresh_Notification.setPreferredSize(new Dimension(120, 38));
        java.net.URL refreshImgUrl = getClass().getResource("/images/refresh.png");
        if (refreshImgUrl != null) {
            btn_refresh_Notification.setIcon(new ImageIcon(refreshImgUrl));
        }
        btn_refresh_Notification.addActionListener(e -> showTableNotification());

        topBar.add(searchIcon);
        topBar.add(searchLabel);
        topBar.add(tf_search_Notification);
        topBar.add(btn_refresh_Notification);

        add(topBar, BorderLayout.NORTH);

        tbl_Notification = new JTable();
        sp_Notification = new JScrollPane(tbl_Notification);
        add(sp_Notification, BorderLayout.CENTER);
    }

    public synchronized void showTableNotification() {
        try {
            Response response = controller.getNotificationsController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Notification.setRowSorter(sorter);

                tf_search_Notification.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e) { search(tf_search_Notification.getText()); }
                    @Override public void removeUpdate(DocumentEvent e) { search(tf_search_Notification.getText()); }
                    @Override public void changedUpdate(DocumentEvent e) { search(tf_search_Notification.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) sorter.setRowFilter(null);
                        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                });

                tbl_Notification.setModel(model);
                tbl_Notification.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_Notification.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tbl_Notification.setRowHeight(34);
                tbl_Notification.setDefaultEditor(Object.class, null);
                TableColumn indexColumn = tbl_Notification.getColumnModel().getColumn(0);
                indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                indexColumn.setMaxWidth(80);
                sp_Notification.setViewportView(tbl_Notification);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
