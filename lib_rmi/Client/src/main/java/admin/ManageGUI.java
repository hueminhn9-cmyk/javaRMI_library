package admin;

import admin.pages.*;
import chat.ChatPanel;
import common.model.*;
import common.rmi.*;
import patron.UIStyleHelper;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ManageGUI extends JFrame {
    class ClientImpl extends UnicastRemoteObject implements ClientInterface {
        public ClientImpl() throws RemoteException {}

        @Override
        public void notify(NOTIFY notify) throws RemoteException {
            SwingUtilities.invokeLater(() -> {
                if (notify == NOTIFY.UPDATE_BOOK && bookManagePanel != null) {
                    bookManagePanel.showTableBook();
                    if (bookCopyManagePanel != null) bookCopyManagePanel.showDataComboBoxBooks();
                }
                if ((notify == NOTIFY.UPDATE_BOOK_COPY || notify == NOTIFY.UPDATE_CHECKOUT || notify == NOTIFY.CLIENT_UPDATE_CHECKOUT) && bookCopyManagePanel != null) {
                    bookCopyManagePanel.showTableBookCopy();
                }
                if ((notify == NOTIFY.UPDATE_CHECKOUT || notify == NOTIFY.CLIENT_UPDATE_CHECKOUT) && checkoutManagePanel != null) {
                    checkoutManagePanel.showTableCheckout();
                }
                if (notify == NOTIFY.UPDATE_AUTHOR && authorManagePanel != null) {
                    authorManagePanel.showTableAuthor();
                    if (bookManagePanel != null) bookManagePanel.showDataComboBoxAuthor();
                }
                if (notify == NOTIFY.UPDATE_CATEGORY && categoryManagePanel != null) {
                    categoryManagePanel.showTableCategory();
                    if (bookManagePanel != null) bookManagePanel.showDataComboBoxCategory();
                }
                if (notify == NOTIFY.UPDATE_PUBLISHED && publisherManagePanel != null) {
                    publisherManagePanel.showTablePublished();
                    if (bookCopyManagePanel != null) bookCopyManagePanel.showDataComboBoxPublished();
                }
                if (notify == NOTIFY.UPDATE_NOTIFICATION && notifyManagePanel != null) {
                    notifyManagePanel.showTableNotification();
                }
            });
        }
    }

    private ManagerController controller;
    private Patron loggedUser;
    private String customUsername;

    private BookManagePanel bookManagePanel;
    private BookCopyManagePanel bookCopyManagePanel;
    private AuthorManagePanel authorManagePanel;
    private CategoryManagePanel categoryManagePanel;
    private PublisherManagePanel publisherManagePanel;
    private MasterDataPanel masterDataPanel;
    private PatronManagePanel patronManagePanel;
    private CheckoutManagePanel checkoutManagePanel;
    private HoldManagePanel holdManagePanel;
    private NotifyManagePanel notifyManagePanel;
    private HistoryLogPanel historyLogPanel;
    private ChatPanel chatPanel;

    private JPanel mainCardContainer;
    private CardLayout cardLayout;

    private List<UIStyleHelper.SidebarNavButton> navButtons = new ArrayList<>();
    private JPanel sidebarNavPanel;
    private int currentSelectedIndex = -1;

    public ManageGUI() {
        this((Patron) null);
    }

    public ManageGUI(String customUsername) {
        this.customUsername = customUsername;
        initWindow();
    }

    public ManageGUI(Patron loggedUser) {
        this.loggedUser = loggedUser;
        initWindow();
    }

    private void initWindow() {
        setTitle("VKU Library Management System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            controller = new ManagerController(new ClientImpl());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

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

        initComponents();
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setResizable(true);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Dynamic User Profile calculation
        String nameDisplay = "NGUYỄN VĂN AN";
        String roleDisplay = "Thủ thư • QTV Kho Sách";

        if (loggedUser != null) {
            String firstName = loggedUser.getFirstName() != null ? loggedUser.getFirstName().trim() : "";
            String lastName = loggedUser.getLastName() != null ? loggedUser.getLastName().trim() : "";
            if (!firstName.isEmpty() || !lastName.isEmpty()) {
                nameDisplay = (firstName + " " + lastName).trim().toUpperCase();
            } else if (loggedUser.getEmail() != null && !loggedUser.getEmail().isEmpty()) {
                nameDisplay = loggedUser.getEmail().toUpperCase();
            }
            if (loggedUser.getRole() != null && !loggedUser.getRole().isEmpty()) {
                roleDisplay = loggedUser.getRole() + " • Thủ thư";
            }
        } else if (customUsername != null && !customUsername.trim().isEmpty()) {
            nameDisplay = customUsername.trim().toUpperCase();
        }

        // 1. LEFT SIDEBAR NAVIGATION (VKU Daotao Style)
        JPanel leftSidebar = new JPanel(new BorderLayout());
        leftSidebar.setPreferredSize(new Dimension(250, 0));
        leftSidebar.setBackground(UIStyleHelper.COLOR_NAVBAR_BG);

        // Sidebar Top: Brand Logo & Admin Greeting Card
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
        userGreetingCard.setBorder(BorderFactory.createEmptyBorder(6, 20, 12, 20));

        JLabel lblGreeting = new JLabel("Xin chào,");
        lblGreeting.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblGreeting.setForeground(new Color(148, 163, 184)); // Slate gray

        JLabel lblUserName = new JLabel(nameDisplay);
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserName.setForeground(Color.WHITE);

        JLabel lblStatus = new JLabel("● Online (" + roleDisplay + ")");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(34, 197, 94)); // Green status

        userGreetingCard.add(lblGreeting);
        userGreetingCard.add(Box.createVerticalStrut(3));
        userGreetingCard.add(lblUserName);
        userGreetingCard.add(Box.createVerticalStrut(3));
        userGreetingCard.add(lblStatus);

        sidebarTop.add(userGreetingCard);

        // Navigation Group Label
        JLabel navHeaderLabel = new JLabel("QUẢN TRỊ THƯ VIỆN");
        navHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navHeaderLabel.setForeground(new Color(148, 163, 184));
        navHeaderLabel.setBorder(BorderFactory.createEmptyBorder(8, 20, 6, 20));
        sidebarTop.add(navHeaderLabel);

        leftSidebar.add(sidebarTop, BorderLayout.NORTH);

        // Scrollable Sidebar Navigation Menu
        sidebarNavPanel = new JPanel();
        sidebarNavPanel.setLayout(new BoxLayout(sidebarNavPanel, BoxLayout.Y_AXIS));
        sidebarNavPanel.setOpaque(false);
        sidebarNavPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        // Create Navigation Items with Clean PNG Icons
        addNavButton("Đầu sách (Titles)", UIStyleHelper.getIcon("/images/book.png", 18, 18), 0);
        addNavButton("Bản sao sách", UIStyleHelper.getIcon("/images/stack-of-books.png", 18, 18), 1);
        addNavButton("Mượn / Trả (Circulation)", UIStyleHelper.getIcon("/images/checked.png", 18, 18), 2);
        addNavButton("Quản lý độc giả", UIStyleHelper.getIcon("/images/user (1).png", 18, 18), 3);
        addNavButton("Danh mục Tác giả/Thể loại", UIStyleHelper.getIcon("/images/online-library.png", 18, 18), 4);
        addNavButton("Đặt giữ sách (Holds)", UIStyleHelper.getIcon("/images/reading_24.png", 18, 18), 5);
        addNavButton("Thông báo hệ thống", UIStyleHelper.getIcon("/images/notification.png", 18, 18), 6);
        addNavButton("Lịch sử nhật ký", UIStyleHelper.getIcon("/images/history.png", 18, 18), 7);
        addNavButton("Chat & Share file", UIStyleHelper.getIcon("/images/paper-plane.png", 18, 18), 8);

        JScrollPane sidebarScroll = new JScrollPane(sidebarNavPanel);
        sidebarScroll.setOpaque(false);
        sidebarScroll.getViewport().setOpaque(false);
        sidebarScroll.setBorder(null);
        sidebarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        leftSidebar.add(sidebarScroll, BorderLayout.CENTER);

        // Sidebar Bottom Toolbar
        JPanel sidebarFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        sidebarFooter.setOpaque(false);
        sidebarFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(44, 62, 80)));

        JButton btnSettings = createIconButton("Cài đặt", UIStyleHelper.getIcon("/images/setting.png", 16, 16));
        JButton btnRefresh = createIconButton("Làm mới", UIStyleHelper.getIcon("/images/refresh.png", 16, 16));
        btnRefresh.addActionListener(e -> {
            if (bookCopyManagePanel != null) bookCopyManagePanel.showTableBookCopy();
            if (checkoutManagePanel != null) checkoutManagePanel.showTableCheckout();
        });

        JButton btnLogout = createIconButton("Đăng xuất", UIStyleHelper.getIcon("/images/exit.png", 16, 16));
        btnLogout.addActionListener(e -> {
            this.dispose();
            new patron.LoginGUI().setVisible(true);
        });

        sidebarFooter.add(btnSettings);
        sidebarFooter.add(btnRefresh);
        sidebarFooter.add(btnLogout);

        leftSidebar.add(sidebarFooter, BorderLayout.SOUTH);

        add(leftSidebar, BorderLayout.WEST);

        // 2. RIGHT CONTAINER (Top Header Bar + Main View Panels)
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(UIStyleHelper.COLOR_BG_MAIN);

        // Top Header Bar
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(UIStyleHelper.COLOR_HEADER_BG);
        headerBar.setPreferredSize(new Dimension(0, 52));
        headerBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // Header Left: Hamburger Toggle + System Title
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        headerLeft.setOpaque(false);

        JLabel btnToggleSidebar = new JLabel("☰");
        btnToggleSidebar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnToggleSidebar.setForeground(new Color(71, 85, 105));
        btnToggleSidebar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel("VKU LIBRARY MANAGEMENT SYSTEM - ADMIN DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(30, 41, 59));

        headerLeft.add(btnToggleSidebar);
        headerLeft.add(titleLabel);

        headerBar.add(headerLeft, BorderLayout.WEST);

        // Header Right: Status Badge & User Info
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        headerRight.setOpaque(false);

        JLabel badgeDb = new JLabel("● CSDL: Ready");
        badgeDb.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeDb.setForeground(new Color(22, 101, 52));
        badgeDb.setBackground(new Color(220, 252, 231));
        badgeDb.setOpaque(true);
        badgeDb.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        JLabel userBadge = new JLabel("👤 " + nameDisplay);
        userBadge.setFont(UIStyleHelper.FONT_BODY_BOLD);
        userBadge.setForeground(new Color(30, 41, 59));

        headerRight.add(badgeDb);
        headerRight.add(userBadge);

        headerBar.add(headerRight, BorderLayout.EAST);

        rightContainer.add(headerBar, BorderLayout.NORTH);

        // Initialize Admin Panels
        bookManagePanel = new BookManagePanel(controller);
        bookCopyManagePanel = new BookCopyManagePanel(controller);
        authorManagePanel = new AuthorManagePanel(controller);
        categoryManagePanel = new CategoryManagePanel(controller);
        publisherManagePanel = new PublisherManagePanel(controller);
        masterDataPanel = new MasterDataPanel(controller, authorManagePanel, categoryManagePanel, publisherManagePanel);
        patronManagePanel = new PatronManagePanel(controller);
        checkoutManagePanel = new CheckoutManagePanel(controller);
        holdManagePanel = new HoldManagePanel(controller);
        notifyManagePanel = new NotifyManagePanel(controller);
        historyLogPanel = new HistoryLogPanel(controller);
        chatPanel = new ChatPanel("Admin");

        cardLayout = new CardLayout();
        mainCardContainer = new JPanel(cardLayout);
        mainCardContainer.setBackground(UIStyleHelper.COLOR_BG_MAIN);

        mainCardContainer.add(bookManagePanel, "BOOK");
        mainCardContainer.add(bookCopyManagePanel, "BOOK_COPY");
        mainCardContainer.add(checkoutManagePanel, "CHECKOUT");
        mainCardContainer.add(patronManagePanel, "PATRON");
        mainCardContainer.add(masterDataPanel, "MASTER");
        mainCardContainer.add(holdManagePanel, "HOLD");
        mainCardContainer.add(notifyManagePanel, "NOTIFY");
        mainCardContainer.add(historyLogPanel, "HISTORY");
        mainCardContainer.add(chatPanel, "CHAT");

        rightContainer.add(mainCardContainer, BorderLayout.CENTER);

        // Footer Bar matching VKU Daotao
        JPanel footerBar = new JPanel(new BorderLayout());
        footerBar.setBackground(Color.WHITE);
        footerBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(6, 20, 6, 20)
        ));

        JLabel footerLeft = new JLabel("Trường Đại học Công nghệ Thông tin và Truyền thông Việt - Hàn (VKU)");
        footerLeft.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLeft.setForeground(new Color(100, 116, 139));

        JLabel footerRight = new JLabel("Phiên bản VKU Library 2026-2027 • Hỗ trợ kỹ thuật");
        footerRight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerRight.setForeground(new Color(100, 116, 139));

        footerBar.add(footerLeft, BorderLayout.WEST);
        footerBar.add(footerRight, BorderLayout.EAST);

        rightContainer.add(footerBar, BorderLayout.SOUTH);

        add(rightContainer, BorderLayout.CENTER);

        // Default open to "Bản sao sách" (index 1)
        selectNavTab(1);
    }

    private void addNavButton(String text, Icon icon, int index) {
        UIStyleHelper.SidebarNavButton btn = new UIStyleHelper.SidebarNavButton(text, icon);
        btn.setMaximumSize(new Dimension(250, 42));
        btn.setPreferredSize(new Dimension(250, 42));
        btn.addActionListener(e -> selectNavTab(index));

        navButtons.add(btn);
        sidebarNavPanel.add(btn);
        sidebarNavPanel.add(Box.createVerticalStrut(2));
    }

    private void selectNavTab(int index) {
        if (index < 0 || index >= navButtons.size()) return;
        currentSelectedIndex = index;

        for (int i = 0; i < navButtons.size(); i++) {
            UIStyleHelper.SidebarNavButton b = navButtons.get(i);
            b.setActive(i == index);
        }

        switch (index) {
            case 0 -> cardLayout.show(mainCardContainer, "BOOK");
            case 1 -> cardLayout.show(mainCardContainer, "BOOK_COPY");
            case 2 -> cardLayout.show(mainCardContainer, "CHECKOUT");
            case 3 -> cardLayout.show(mainCardContainer, "PATRON");
            case 4 -> cardLayout.show(mainCardContainer, "MASTER");
            case 5 -> cardLayout.show(mainCardContainer, "HOLD");
            case 6 -> cardLayout.show(mainCardContainer, "NOTIFY");
            case 7 -> cardLayout.show(mainCardContainer, "HISTORY");
            case 8 -> cardLayout.show(mainCardContainer, "CHAT");
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
}

