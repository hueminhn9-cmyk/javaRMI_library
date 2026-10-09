package patron.pages;

import common.model.Response;
import patron.ClientController;
import common.model.Patron;
import patron.UIStyleHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;

public class HomePagePanel extends JPanel {
    private Patron patron;
    private ClientController controller;
    private JTable tbl_Notification;
    private JScrollPane sp_Notification;

    public HomePagePanel(Patron patron, ClientController controller) {
        this.patron = patron;
        this.controller = controller;
        initComponents();
        showNotification();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 15));
        setBackground(UIStyleHelper.COLOR_BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // Top Notice Banner matching VKU Daotao
        JPanel noticeBanner = UIStyleHelper.createNoticeBanner(
                "Bạn có thể xem các thông báo mới nhất từ Hệ thống Thư viện VKU. Mọi thắc mắc vui lòng liên hệ Thủ thư qua kênh Trò chuyện & File."
        );
        add(noticeBanner, BorderLayout.NORTH);

        // Main Card Container
        JPanel mainCard = new JPanel(new BorderLayout());
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(BorderFactory.createLineBorder(UIStyleHelper.COLOR_BORDER, 1, true));

        // Section Banner (Teal bar matching VKU Daotao screenshot)
        JPanel sectionHeader = UIStyleHelper.createSectionHeaderBar(
                "Lịch thông báo & Cập nhật hôm nay", UIStyleHelper.COLOR_TEAL_HEADER
        );
        mainCard.add(sectionHeader, BorderLayout.NORTH);

        // Table Content
        JPanel tableContentPanel = new JPanel(new BorderLayout());
        tableContentPanel.setOpaque(false);
        tableContentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        tbl_Notification = new JTable();
        sp_Notification = new JScrollPane(tbl_Notification);
        UIStyleHelper.styleTable(tbl_Notification, sp_Notification);

        tableContentPanel.add(sp_Notification, BorderLayout.CENTER);
        mainCard.add(tableContentPanel, BorderLayout.CENTER);

        add(mainCard, BorderLayout.CENTER);
    }

    public void showNotification() {
        try {
            Response response = controller.getNotificationByPatronId(patron.getId());
            if (response != null && response.getStatus() == 100 && response.getData() != null && response.getData() instanceof String) {
                String msg = response.getData().toString().trim();
                if (!msg.isEmpty()) {
                    JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            }
            if (response != null && response.getData() instanceof DefaultTableModel) {
                tbl_Notification.setModel((DefaultTableModel) response.getData());
                tbl_Notification.setDefaultEditor(Object.class, null);
                UIStyleHelper.styleTable(tbl_Notification, sp_Notification);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

