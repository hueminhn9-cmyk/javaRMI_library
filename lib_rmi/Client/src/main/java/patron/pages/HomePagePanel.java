package patron.pages;

import common.Response;
import patron.ClientController;
import common.Patron;

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
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("VKU Library - Notification Center");
        titleLabel.setFont(new Font("Montserrat ExtraBold", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        tbl_Notification = new JTable();
        sp_Notification = new JScrollPane(tbl_Notification);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        centerPanel.add(sp_Notification, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void showNotification() {
        try {
            Response response = controller.getNotificationByPatronId(patron.getId());
            if (response != null && response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            if (response != null && response.getData() instanceof DefaultTableModel) {
                tbl_Notification.setModel((DefaultTableModel) response.getData());
                tbl_Notification.setRowHeight(40);
                tbl_Notification.setDefaultEditor(Object.class, null);
                sp_Notification.setViewportView(tbl_Notification);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
