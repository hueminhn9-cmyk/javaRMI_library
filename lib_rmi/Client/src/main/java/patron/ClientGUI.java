package patron;

import chat.ChatPanel;
import common.model.*;
import common.rmi.*;
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
            SwingUtilities.invokeLater(() -> {
                if (notify == NOTIFY.CLIENT_UPDATE_NOTIFICATION && homePagePanel != null) {
                    homePagePanel.showNotification();
                }
                if ((notify == NOTIFY.CLIENT_UPDATE_CHECKOUT || notify == NOTIFY.UPDATE_CHECKOUT) && returnPagePanel != null) {
                    returnPagePanel.showCheckouts();
                }
                if ((notify == NOTIFY.UPDATE_BOOK || notify == NOTIFY.UPDATE_BOOK_COPY || notify == NOTIFY.CLIENT_UPDATE_CHECKOUT || notify == NOTIFY.UPDATE_CHECKOUT) && searchPagePanel != null) {
                    searchPagePanel.showBookForSearch();
                }
            });
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

        // Header Navigation Bar (No Image Banner, Sleek Modern Dark Slate Bar)
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(UIStyleHelper.COLOR_NAVBAR_BG);
        headerBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // Brand Title Container (Left)
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel appTitle = new JLabel("THƯ VIỆN VKU");
        appTitle.setFont(UIStyleHelper.FONT_HEADER);
        appTitle.setForeground(Color.WHITE);
        brandPanel.add(appTitle);

        JLabel portalBadge = new JLabel("CỔNG ĐỘC GIẢ");
        portalBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        portalBadge.setForeground(new Color(148, 163, 184)); // #94A3B8
        portalBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        brandPanel.add(portalBadge);

        headerBar.add(brandPanel, BorderLayout.WEST);

        // Right Actions (User Email + Logout Button)
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightActions.setOpaque(false);

        String userEmail = patron != null && patron.getEmail() != null ? patron.getEmail() : "Độc giả";
        JLabel userLabel = new JLabel("Độc giả: " + userEmail);
        userLabel.setFont(UIStyleHelper.FONT_BODY_BOLD);
        userLabel.setForeground(new Color(226, 232, 240));
        ImageIcon userIcon = UIStyleHelper.getIcon("/images/user (1).png", 18, 18);
        if (userIcon != null) userLabel.setIcon(userIcon);
        rightActions.add(userLabel);

        JButton btnLogout = new JButton("Đăng xuất");
        ImageIcon logoutIcon = UIStyleHelper.getIcon("/images/exit.png", 16, 16);
        if (logoutIcon != null) btnLogout.setIcon(logoutIcon);
        UIStyleHelper.styleButton(btnLogout, UIStyleHelper.COLOR_DANGER_BG, UIStyleHelper.COLOR_DANGER_BORDER, Color.BLACK);
        btnLogout.addActionListener(e -> btn_LogoutActionPerformed());
        rightActions.add(btnLogout);

        headerBar.add(rightActions, BorderLayout.EAST);

        add(headerBar, BorderLayout.NORTH);

        // Initialize Tabbed Panels
        homePagePanel = new HomePagePanel(patron, controller);
        searchPagePanel = new SearchPagePanel(patron, controller);
        returnPagePanel = new ReturnPagePanel(patron, controller);
        profilePagePanel = new ProfilePagePanel(patron, controller);
        ChatPanel chatPanel = new ChatPanel(patron != null ? patron.getEmail() : "Patron");

        panel_main = new JTabbedPane();
        panel_main.setFont(UIStyleHelper.FONT_BODY_BOLD);
        panel_main.setBackground(UIStyleHelper.COLOR_BG_LIGHT);
        panel_main.setTabPlacement(JTabbedPane.TOP);

        panel_main.addTab("Thông báo", UIStyleHelper.getIcon("/images/notification.png", 18, 18), homePagePanel);
        panel_main.addTab("Tra cứu & Mượn sách", UIStyleHelper.getIcon("/images/search.png", 18, 18), searchPagePanel);
        panel_main.addTab("Trả sách", UIStyleHelper.getIcon("/images/reading_24.png", 18, 18), returnPagePanel);
        panel_main.addTab("Hồ sơ cá nhân", UIStyleHelper.getIcon("/images/setting.png", 18, 18), profilePagePanel);
        panel_main.addTab("Trò chuyện & Truyền file", UIStyleHelper.getIcon("/images/chat.png", 18, 18), chatPanel);

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
