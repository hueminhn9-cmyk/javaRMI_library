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
        setLayout(new BorderLayout());
        setBackground(UIStyleHelper.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel cardContainer = UIStyleHelper.createCardPanel("Trung Tâm Thông Báo (Notification Center)");

        tbl_Notification = new JTable();
        sp_Notification = new JScrollPane(tbl_Notification);
        UIStyleHelper.styleTable(tbl_Notification, sp_Notification);

        cardContainer.add(sp_Notification, BorderLayout.CENTER);
        add(cardContainer, BorderLayout.CENTER);
    }

    public void showNotification() {
        try {
            Response response = controller.getNotificationByPatronId(patron.getId());
            if (response != null && response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
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
