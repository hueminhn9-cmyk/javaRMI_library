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

public class HistoryLogPanel extends JPanel {
    private ManagerController controller;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTable tbl_History;
    private JScrollPane sp_History;
    private JTextField tf_search_History;
    private JButton btn_refresh_History;

    public HistoryLogPanel(ManagerController controller) {
        this.controller = controller;
        initComponents();
        showTableHistory();
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

        JLabel searchLabel = new JLabel("Tìm Kiếm Log: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(new Color(30, 41, 59));

        tf_search_History = new JTextField(25);
        tf_search_History.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf_search_History.setPreferredSize(new Dimension(240, 38));
        tf_search_History.setMargin(new Insets(4, 8, 4, 8));

        btn_refresh_History = new JButton("Làm Mới");
        btn_refresh_History.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_refresh_History.setBackground(new Color(75, 85, 99));
        btn_refresh_History.setForeground(Color.WHITE);
        btn_refresh_History.setFocusPainted(false);
        btn_refresh_History.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_refresh_History.setPreferredSize(new Dimension(120, 38));
        java.net.URL refreshImgUrl = getClass().getResource("/images/refresh.png");
        if (refreshImgUrl != null) {
            btn_refresh_History.setIcon(new ImageIcon(refreshImgUrl));
        }
        btn_refresh_History.addActionListener(e -> showTableHistory());

        topBar.add(searchIcon);
        topBar.add(searchLabel);
        topBar.add(tf_search_History);
        topBar.add(btn_refresh_History);

        add(topBar, BorderLayout.NORTH);

        tbl_History = new JTable();
        sp_History = new JScrollPane(tbl_History);
        add(sp_History, BorderLayout.CENTER);
    }

    public synchronized void showTableHistory() {
        try {
            Response response = controller.getHistoryController();
            if (response != null && response.getData() instanceof DefaultTableModel) {
                DefaultTableModel model = (DefaultTableModel) response.getData();
                sorter = new TableRowSorter<>(model);
                tbl_History.setRowSorter(sorter);

                tf_search_History.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e) { search(tf_search_History.getText()); }
                    @Override public void removeUpdate(DocumentEvent e) { search(tf_search_History.getText()); }
                    @Override public void changedUpdate(DocumentEvent e) { search(tf_search_History.getText()); }

                    private void search(String str) {
                        if (str.length() == 0) sorter.setRowFilter(null);
                        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                });

                tbl_History.setModel(model);
                tbl_History.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
                tbl_History.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tbl_History.setRowHeight(34);
                tbl_History.setDefaultEditor(Object.class, null);
                TableColumn indexColumn = tbl_History.getColumnModel().getColumn(0);
                indexColumn.setCellRenderer(new CenteredTableCellRenderer());
                indexColumn.setMaxWidth(80);
                sp_History.setViewportView(tbl_History);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
