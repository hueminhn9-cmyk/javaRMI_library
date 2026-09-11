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

public class HoldManagePanel extends JPanel {
    private ManagerController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTable tbl_Hold;
    private JScrollPane sp_Hold;
    private JTextField tf_search_Hold;
    private JButton btn_refresh_Hold;

    public HoldManagePanel(ManagerController controller) {
        this.controller = controller;
        initComponents();
        showTableHold();
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

        JLabel searchLabel = new JLabel("Tìm Kiếm Đặt Giữ: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(new Color(30, 41, 59));

        tf_search_Hold = new JTextField(25);
        tf_search_Hold.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf_search_Hold.setPreferredSize(new Dimension(240, 38));
        tf_search_Hold.setMargin(new Insets(4, 8, 4, 8));

        btn_refresh_Hold = new JButton("Làm Mới");
        btn_refresh_Hold.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_refresh_Hold.setBackground(new Color(75, 85, 99));
        btn_refresh_Hold.setForeground(Color.WHITE);
        btn_refresh_Hold.setFocusPainted(false);
        btn_refresh_Hold.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_refresh_Hold.setPreferredSize(new Dimension(120, 38));
        java.net.URL refreshImgUrl = getClass().getResource("/images/refresh.png");
        if (refreshImgUrl != null) {
            btn_refresh_Hold.setIcon(new ImageIcon(refreshImgUrl));
        }
        btn_refresh_Hold.addActionListener(e -> showTableHold());

        topBar.add(searchIcon);
        topBar.add(searchLabel);
        topBar.add(tf_search_Hold);
        topBar.add(btn_refresh_Hold);

        add(topBar, BorderLayout.NORTH);

        tbl_Hold = new JTable();
        sp_Hold = new JScrollPane(tbl_Hold);
        add(sp_Hold, BorderLayout.CENTER);
    }

    public synchronized void showTableHold() {
        try {
            Response response = controller.getHoldsController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_Hold.setRowSorter(sorter);

                tf_search_Hold.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e) { search(tf_search_Hold.getText()); }
                    @Override public void removeUpdate(DocumentEvent e) { search(tf_search_Hold.getText()); }
                    @Override public void changedUpdate(DocumentEvent e) { search(tf_search_Hold.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) sorter.setRowFilter(null);
                        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                });

                tbl_Hold.setModel(model);
                tbl_Hold.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_Hold.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tbl_Hold.setRowHeight(34);
                tbl_Hold.setDefaultEditor(Object.class, null);
                TableColumn indexColumn = tbl_Hold.getColumnModel().getColumn(0);
                indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                indexColumn.setMaxWidth(80);
                sp_Hold.setViewportView(tbl_Hold);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
