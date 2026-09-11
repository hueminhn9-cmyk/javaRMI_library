package patron;

import common.ClientInterface;
import common.NOTIFY;
import common.Patron;
import patron.pages.HomePagePanel;
import patron.pages.ProfilePagePanel;
import patron.pages.ReturnPagePanel;
import patron.pages.SearchPagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientGUI extends JFrame {
    private Patron patron;
    private ClientController controller;

    private HomePagePanel homePagePanel;
    private SearchPagePanel searchPagePanel;
    private ReturnPagePanel returnPagePanel;
    private ProfilePagePanel profilePagePanel;

    private JTabbedPane panel_main;

    class ClientImpl extends UnicastRemoteObject implements ClientInterface {
        public ClientImpl() throws RemoteException {}

        @Override
        public void notify(NOTIFY notify) throws RemoteException {
            if (notify == NOTIFY.CLIENT_UPDATE_NOTIFICATION && homePagePanel != null) {
                homePagePanel.showNotification();
            }
            if (notify == NOTIFY.CLIENT_UPDATE_CHECKOUT && returnPagePanel != null) {
                returnPagePanel.showCheckouts();
            }
            if ((notify == NOTIFY.UPDATE_BOOK || notify == NOTIFY.UPDATE_BOOK_COPY) && searchPagePanel != null) {
                searchPagePanel.showBookForSearch();
            }
        }
    }

    public ClientGUI(Patron patron) {
        this.patron = patron;
        setTitle("VKU Library - " + (patron != null ? patron.getEmail() : ""));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (controller != null) controller.exit();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        try {
            controller = new ClientController(new ClientImpl());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        initComponents();
        setSize(1100, 750);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top Banner Panel
        JPanel bannerPanel = new JPanel(new BorderLayout());
        JLabel bannerLabel = new JLabel();
        try {
            bannerLabel.setIcon(new ImageIcon(getClass().getResource("/images/client_banner.png")));
        } catch (Exception e) {
            bannerLabel.setText("VKU LIBRARY SYSTEM");
            bannerLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        }
        bannerPanel.add(bannerLabel, BorderLayout.CENTER);

        // Header Action Bar (Title + Logout Button)
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel appTitle = new JLabel("VKU Library");
        appTitle.setFont(new Font("Montserrat ExtraBold", Font.BOLD, 20));
        try {
            appTitle.setIcon(new ImageIcon(getClass().getResource("/images/reading_1.png")));
        } catch (Exception ignored) {}
        headerBar.add(appTitle, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Montserrat", Font.PLAIN, 14));
        try {
            btnLogout.setIcon(new ImageIcon(getClass().getResource("/images/exit.png")));
        } catch (Exception ignored) {}
        btnLogout.addActionListener(e -> btn_LogoutActionPerformed());
        headerBar.add(btnLogout, BorderLayout.EAST);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(bannerPanel, BorderLayout.NORTH);
        topContainer.add(headerBar, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);

        // Initialize Tabbed Panels
        homePagePanel = new HomePagePanel(patron, controller);
        searchPagePanel = new SearchPagePanel(patron, controller);
        returnPagePanel = new ReturnPagePanel(patron, controller);
        profilePagePanel = new ProfilePagePanel(patron, controller);

        panel_main = new JTabbedPane();
        panel_main.setTabPlacement(JTabbedPane.TOP);

        try {
            panel_main.addTab("Home", new ImageIcon(getClass().getResource("/images/notification.png")), homePagePanel);
            panel_main.addTab("Search", new ImageIcon(getClass().getResource("/images/paper-plane.png")), searchPagePanel);
            panel_main.addTab("Return", new ImageIcon(getClass().getResource("/images/reading_24.png")), returnPagePanel);
            panel_main.addTab("Profile", new ImageIcon(getClass().getResource("/images/setting.png")), profilePagePanel);
        } catch (Exception e) {
            panel_main.addTab("Home", homePagePanel);
            panel_main.addTab("Search", searchPagePanel);
            panel_main.addTab("Return", returnPagePanel);
            panel_main.addTab("Profile", profilePagePanel);
        }

        add(panel_main, BorderLayout.CENTER);
    }

    private void btn_LogoutActionPerformed() {
        LoginGUI login = new LoginGUI();
        login.setVisible(true);
        try {
            if (controller != null) controller.exit();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.dispose();
    }
}
