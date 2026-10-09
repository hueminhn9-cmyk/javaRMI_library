package patron.pages;

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
        setLayout(new BorderLayout(0, 15));
        setBackground(UIStyleHelper.COLOR_BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // Top Notice Banner
        JPanel noticeBanner = UIStyleHelper.createNoticeBanner(
                "Danh sách bên dưới liệt kê tất cả sách bạn đang mượn. Vui lòng kiểm tra hạn trả và thực hiện trả sách đúng quy định."
        );
        add(noticeBanner, BorderLayout.NORTH);

        // Left Panel: Ticket Detail Card
        JPanel leftCard = new JPanel(new BorderLayout());
        leftCard.setBackground(Color.WHITE);
        leftCard.setBorder(BorderFactory.createLineBorder(UIStyleHelper.COLOR_BORDER, 1, true));
        leftCard.setPreferredSize(new Dimension(380, 0));

        // Section Banner Orange
        JPanel leftSectionHeader = UIStyleHelper.createSectionHeaderBar(
                "Chi tiết phiếu mượn", UIStyleHelper.COLOR_ORANGE_HEADER
        );
        leftCard.add(leftSectionHeader, BorderLayout.NORTH);

        JPanel detailContainer = new JPanel();
        detailContainer.setLayout(new BoxLayout(detailContainer, BoxLayout.Y_AXIS));
        detailContainer.setOpaque(false);
        detailContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        detailContainer.add(createDetailRow("Tên sách:", lb_BookName_Return = new JLabel("-")));
        detailContainer.add(Box.createVerticalStrut(15));
        detailContainer.add(createDetailRow("Ngày mượn:", lb_Return_TimeStart = new JLabel("-")));
        detailContainer.add(Box.createVerticalStrut(15));
        detailContainer.add(createDetailRow("Hạn trả:", lb_Return_TimeEnd = new JLabel("-")));
        detailContainer.add(Box.createVerticalStrut(25));

        btn_Return = UIStyleHelper.createTealPillButton("Xác nhận trả sách", UIStyleHelper.getIcon("/images/refresh.png", 16, 16));
        btn_Return.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn_Return.setMaximumSize(new Dimension(220, 42));
        btn_Return.addActionListener(e -> btn_ReturnActionPerformed(e));
        detailContainer.add(btn_Return);

        leftCard.add(detailContainer, BorderLayout.CENTER);

        // Right Panel: Search & Table Card
        JPanel rightCard = new JPanel(new BorderLayout());
        rightCard.setBackground(Color.WHITE);
        rightCard.setBorder(BorderFactory.createLineBorder(UIStyleHelper.COLOR_BORDER, 1, true));

        // Section Banner Teal
        JPanel rightSectionHeader = UIStyleHelper.createSectionHeaderBar(
                "Danh sách sách đang mượn", UIStyleHelper.COLOR_TEAL_HEADER
        );
        rightCard.add(rightSectionHeader, BorderLayout.NORTH);

        JPanel rightContentPanel = new JPanel(new BorderLayout());
        rightContentPanel.setOpaque(false);
        rightContentPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 15, 15));

        JPanel searchBarPanel = new JPanel(new BorderLayout(8, 0));
        searchBarPanel.setOpaque(false);
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel lblSearch = new JLabel("Tìm kiếm phiếu:");
        ImageIcon searchIcon = UIStyleHelper.getIcon("/images/search.png", 16, 16);
        if (searchIcon != null) lblSearch.setIcon(searchIcon);
        lblSearch.setFont(UIStyleHelper.FONT_BODY_BOLD);
        lblSearch.setForeground(UIStyleHelper.COLOR_TEXT_MAIN);

        tf_Search_Return = new JTextField();
        UIStyleHelper.styleTextField(tf_Search_Return);

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
        UIStyleHelper.styleTable(tbl_Profile, sp_Profile);

        rightContentPanel.add(searchBarPanel, BorderLayout.NORTH);
        rightContentPanel.add(sp_Profile, BorderLayout.CENTER);

        rightCard.add(rightContentPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCard, rightCard);
        splitPane.setResizeWeight(0.35);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        splitPane.setBackground(UIStyleHelper.COLOR_BG_MAIN);

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

    public void showCheckouts() {
        try {
            Response response = controller.getCheckoutsClient(patron.getId());
            if (response != null && response.getStatus() == 100 && response.getData() != null && response.getData() instanceof String) {
                String msg = response.getData().toString().trim();
                if (!msg.isEmpty()) {
                    JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
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
                tbl_Profile.setDefaultEditor(Object.class, null);
                UIStyleHelper.styleTable(tbl_Profile, sp_Profile);
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu mượn trong bảng trước!");
        }
    }
}

