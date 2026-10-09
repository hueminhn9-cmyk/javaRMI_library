package patron;

import chat.ChatPanel;
import common.model.*;
import common.rmi.*;
import patron.pages.HomePagePanel;
import patron.pages.ProfilePagePanel;
import patron.pages.ReturnPagePanel;
import patron.pages.SearchPagePanel;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI extends JFrame {
    private Patron patron;
    private ClientController controller;

    private HomePagePanel homePagePanel;
    private SearchPagePanel searchPagePanel;
    private ReturnPagePanel returnPagePanel;
    private ProfilePagePanel profilePagePanel;
    private ChatPanel chatPanel;

    private JPanel mainCardContainer;
    private CardLayout cardLayout;

    private List<UIStyleHelper.SidebarNavButton> navButtons = new ArrayList<>();
    private JPanel sidebarNavPanel;
    private int currentSelectedIndex = -1;

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
        setTitle("VKU Library Portal - " + (patron != null ? patron.getEmail() : ""));
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
        setSize(1200, 780);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 1. LEFT SIDEBAR NAVIGATION (VKU Daotao Style)
        JPanel leftSidebar = new JPanel(new BorderLayout());
        leftSidebar.setPreferredSize(new Dimension(240, 0));
        leftSidebar.setBackground(UIStyleHelper.COLOR_NAVBAR_BG);

        // Sidebar Top: Brand Logo & User Greeting Box
        JPanel sidebarTop = new JPanel();
        sidebarTop.setLayout(new BoxLayout(sidebarTop, BoxLayout.Y_AXIS));
        sidebarTop.setOpaque(false);

        // Brand Logo Box
        JPanel brandBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 15));
        brandBox.setOpaque(false);

        JLabel logoIcon = new JLabel();
        ImageIcon libIcon = UIStyleHelper.getIcon("/images/library.png", 26, 26);
        if (libIcon != null) logoIcon.setIcon(libIcon);
        else logoIcon.setText("🏛️");

        JLabel brandTitle = new JLabel("VKU!");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandTitle.setForeground(Color.WHITE);

        brandBox.add(logoIcon);
        brandBox.add(brandTitle);
        sidebarTop.add(brandBox);

        // User Greeting Card (matching VKU Daotao screenshot)
        JPanel userGreetingCard = new JPanel();
        userGreetingCard.setLayout(new BoxLayout(userGreetingCard, BoxLayout.Y_AXIS));
        userGreetingCard.setOpaque(false);
        userGreetingCard.setBorder(BorderFactory.createEmptyBorder(8, 20, 15, 20));

        JLabel lblGreeting = new JLabel("Xin chào,");
        lblGreeting.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblGreeting.setForeground(new Color(148, 163, 184)); // Light slate gray

        String fullName = "ĐỘC GIẢ VKU";
        if (patron != null) {
            String fname = patron.getFirstName() != null ? patron.getFirstName().trim() : "";
            String lname = patron.getLastName() != null ? patron.getLastName().trim() : "";
            if (!fname.isEmpty() || !lname.isEmpty()) {
                fullName = (fname + " " + lname).trim().toUpperCase();
            } else if (patron.getEmail() != null) {
                fullName = patron.getEmail().toUpperCase();
            }
        }

        JLabel lblUserName = new JLabel(fullName);
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserName.setForeground(Color.WHITE);

        JLabel lblStatus = new JLabel("● Trực tuyến");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(34, 197, 94)); // Green status text

        userGreetingCard.add(lblGreeting);
        userGreetingCard.add(Box.createVerticalStrut(3));
        userGreetingCard.add(lblUserName);
        userGreetingCard.add(Box.createVerticalStrut(4));
        userGreetingCard.add(lblStatus);

        sidebarTop.add(userGreetingCard);

        // Category Header Label
        JLabel navHeaderLabel = new JLabel("DANH MỤC THƯ VIỆN");
        navHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navHeaderLabel.setForeground(new Color(148, 163, 184));
        navHeaderLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 8, 20));
        sidebarTop.add(navHeaderLabel);

        leftSidebar.add(sidebarTop, BorderLayout.NORTH);

        // Sidebar Navigation Buttons List (Fixed order and locked positions)
        sidebarNavPanel = new JPanel();
        sidebarNavPanel.setLayout(new BoxLayout(sidebarNavPanel, BoxLayout.Y_AXIS));
        sidebarNavPanel.setOpaque(false);
        sidebarNavPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Create Navigation Items matching VKU Daotao layout
        addNavButton("Lịch thông báo", UIStyleHelper.getIcon("/images/notification.png", 18, 18), 0);
        addNavButton("Tra cứu & Mượn sách", UIStyleHelper.getIcon("/images/search.png", 18, 18), 1);
        addNavButton("Trả sách & Gia hạn", UIStyleHelper.getIcon("/images/reading_24.png", 18, 18), 2);
        addNavButton("Hồ sơ độc giả", UIStyleHelper.getIcon("/images/setting.png", 18, 18), 3);
        addNavButton("Trò chuyện & File", UIStyleHelper.getIcon("/images/chat.png", 18, 18), 4);

        leftSidebar.add(sidebarNavPanel, BorderLayout.CENTER);

        // Sidebar Bottom Toolbar (Settings, Profile, Logout)
        JPanel sidebarFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        sidebarFooter.setOpaque(false);
        sidebarFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(44, 62, 80)));

        JButton btnSettings = createIconButton("Cài đặt", UIStyleHelper.getIcon("/images/setting.png", 16, 16));
        JButton btnUser = createIconButton("Hồ sơ cá nhân", UIStyleHelper.getIcon("/images/user (1).png", 16, 16));
        btnUser.addActionListener(e -> selectNavTab(3));

        JButton btnLogout = createIconButton("Đăng xuất", UIStyleHelper.getIcon("/images/exit.png", 16, 16));
        btnLogout.addActionListener(e -> btn_LogoutActionPerformed());

        sidebarFooter.add(btnSettings);
        sidebarFooter.add(btnUser);
        sidebarFooter.add(btnLogout);

        leftSidebar.add(sidebarFooter, BorderLayout.SOUTH);

        add(leftSidebar, BorderLayout.WEST);

        // 2. RIGHT CONTAINER (Top Header Bar + Main View Area)
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(UIStyleHelper.COLOR_BG_MAIN);

        // Top Header Bar matching VKU Daotao
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(UIStyleHelper.COLOR_HEADER_BG);
        headerBar.setPreferredSize(new Dimension(0, 52));
        headerBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // Header Left: Hamburger Icon + Academic Semester Title
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        headerLeft.setOpaque(false);

        JLabel btnToggleSidebar = new JLabel("☰");
        btnToggleSidebar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnToggleSidebar.setForeground(new Color(71, 85, 105));
        btnToggleSidebar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel headerTitle = new JLabel("Các dịch vụ thư viện, năm học 2026 - 2027");
        headerTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        headerTitle.setForeground(new Color(51, 65, 85));

        headerLeft.add(btnToggleSidebar);
        headerLeft.add(headerTitle);

        headerBar.add(headerLeft, BorderLayout.WEST);

        // Header Right: Term Dropdown Selector + Logged User Name
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        headerRight.setOpaque(false);

        JComboBox<String> cbTerm = new JComboBox<>(new String[]{"Học kỳ 1 - 2026-2027", "Học kỳ 2 - 2026-2027"});
        cbTerm.setFont(UIStyleHelper.FONT_SMALL);
        cbTerm.setBackground(Color.WHITE);

        String userDisplay = (patron != null && patron.getEmail() != null) ? patron.getEmail() : "TRẦN THỊ NHƯ QUỲNH - 24ITB166";
        JLabel userBadge = new JLabel("👤 " + userDisplay);
        userBadge.setFont(UIStyleHelper.FONT_BODY_BOLD);
        userBadge.setForeground(new Color(30, 41, 59));

        headerRight.add(cbTerm);
        headerRight.add(userBadge);

        headerBar.add(headerRight, BorderLayout.EAST);

        rightContainer.add(headerBar, BorderLayout.NORTH);

        // Initialize Main Content Panels
        homePagePanel = new HomePagePanel(patron, controller);
        searchPagePanel = new SearchPagePanel(patron, controller);
        returnPagePanel = new ReturnPagePanel(patron, controller);
        profilePagePanel = new ProfilePagePanel(patron, controller);
        chatPanel = new ChatPanel(patron != null ? patron.getEmail() : "Patron");

        cardLayout = new CardLayout();
        mainCardContainer = new JPanel(cardLayout);
        mainCardContainer.setBackground(UIStyleHelper.COLOR_BG_MAIN);

        mainCardContainer.add(homePagePanel, "HOME");
        mainCardContainer.add(searchPagePanel, "SEARCH");
        mainCardContainer.add(returnPagePanel, "RETURN");
        mainCardContainer.add(profilePagePanel, "PROFILE");
        mainCardContainer.add(chatPanel, "CHAT");

        rightContainer.add(mainCardContainer, BorderLayout.CENTER);

        add(rightContainer, BorderLayout.CENTER);

        // Default select first tab ("HOME")
        selectNavTab(0);
    }

    private void addNavButton(String text, Icon icon, int index) {
        UIStyleHelper.SidebarNavButton btn = new UIStyleHelper.SidebarNavButton(text, icon);
        btn.setMaximumSize(new Dimension(240, 42));
        btn.setPreferredSize(new Dimension(240, 42));
        btn.addActionListener(e -> selectNavTab(index));

        navButtons.add(btn);
        sidebarNavPanel.add(btn);
        sidebarNavPanel.add(Box.createVerticalStrut(3));
    }

    private void selectNavTab(int index) {
        if (index < 0 || index >= navButtons.size()) return;
        currentSelectedIndex = index;

        for (int i = 0; i < navButtons.size(); i++) {
            UIStyleHelper.SidebarNavButton b = navButtons.get(i);
            b.setActive(i == index);
        }

        switch (index) {
            case 0 -> cardLayout.show(mainCardContainer, "HOME");
            case 1 -> cardLayout.show(mainCardContainer, "SEARCH");
            case 2 -> cardLayout.show(mainCardContainer, "RETURN");
            case 3 -> cardLayout.show(mainCardContainer, "PROFILE");
            case 4 -> cardLayout.show(mainCardContainer, "CHAT");
        }
    }

    private JButton createIconButton(String tooltip, Icon icon) {
        JButton btn = new JButton();
        if (icon != null) btn.setIcon(icon);
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(36, 36));
        return btn;
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

