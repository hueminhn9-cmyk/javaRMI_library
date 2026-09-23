package admin;

import admin.pages.*;
import chat.ChatPanel;
import common.model.*;
import common.rmi.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
                if (notify == NOTIFY.UPDATE_BOOK_COPY && bookCopyManagePanel != null) {
                    bookCopyManagePanel.showTableBookCopy();
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

    private JTabbedPane mainTabPane;

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
        String nameDisplay = "Nguyễn Văn An";
        String initials = "VA";
        String roleDisplay = "Thủ thư • QTV Kho Sách";

        if (loggedUser != null) {
            String firstName = loggedUser.getFirstName() != null ? loggedUser.getFirstName().trim() : "";
            String lastName = loggedUser.getLastName() != null ? loggedUser.getLastName().trim() : "";
            if (!firstName.isEmpty() || !lastName.isEmpty()) {
                nameDisplay = (firstName + " " + lastName).trim();
            } else if (loggedUser.getEmail() != null && !loggedUser.getEmail().isEmpty()) {
                nameDisplay = loggedUser.getEmail();
            }
            if (loggedUser.getRole() != null && !loggedUser.getRole().isEmpty()) {
                roleDisplay = loggedUser.getRole() + " • Thủ thư";
            }
        } else if (customUsername != null && !customUsername.trim().isEmpty()) {
            nameDisplay = customUsername.trim();
        }

        // Calculate Initials for Avatar
        String[] nameParts = nameDisplay.split("\\s+");
        if (nameParts.length >= 2) {
            initials = ("" + nameParts[0].charAt(0) + nameParts[nameParts.length - 1].charAt(0)).toUpperCase();
        } else if (nameParts.length == 1 && nameParts[0].length() >= 1) {
            initials = nameParts[0].substring(0, Math.min(2, nameParts[0].length())).toUpperCase();
        }

        // Header Top Panel matching mock-up
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // Header Bar Top Row
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(Color.WHITE);
        headerBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Left Section: App Logo + Title + Badges
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerLeft.setOpaque(false);

        JLabel logoLabel = new JLabel("📚");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("VKU LIBRARY MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(new Color(30, 41, 59));

        JLabel badgeVer = new JLabel("Enterprise v2.4");
        badgeVer.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeVer.setForeground(new Color(37, 99, 235));
        badgeVer.setBackground(new Color(224, 242, 254));
        badgeVer.setOpaque(true);
        badgeVer.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        JLabel badgeDb = new JLabel("● CSDL: Sẵn sàng");
        badgeDb.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeDb.setForeground(new Color(22, 101, 52));
        badgeDb.setBackground(new Color(220, 252, 231));
        badgeDb.setOpaque(true);
        badgeDb.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        headerLeft.add(logoLabel);
        headerLeft.add(titleLabel);
        headerLeft.add(badgeVer);
        headerLeft.add(badgeDb);

        // Right Section: Dynamic User Profile Badge
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        JLabel userAvatar = new JLabel(initials);
        userAvatar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userAvatar.setForeground(new Color(37, 99, 235));
        userAvatar.setBackground(new Color(224, 242, 254));
        userAvatar.setOpaque(true);
        userAvatar.setPreferredSize(new Dimension(32, 32));
        userAvatar.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        JLabel userName = new JLabel(nameDisplay);
        userName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userName.setForeground(new Color(30, 41, 59));

        JLabel userRole = new JLabel(roleDisplay);
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userRole.setForeground(new Color(100, 116, 139));

        userInfo.add(userName);
        userInfo.add(userRole);

        headerRight.add(userAvatar);
        headerRight.add(userInfo);

        headerBar.add(headerLeft, BorderLayout.WEST);
        headerBar.add(headerRight, BorderLayout.EAST);

        topPanel.add(headerBar, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

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
        ChatPanel chatPanel = new ChatPanel("Admin");

        // Main Tabbed Pane at TOP
        mainTabPane = new JTabbedPane();
        mainTabPane.setTabPlacement(JTabbedPane.TOP);
        mainTabPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainTabPane.setBackground(Color.WHITE);

        mainTabPane.addTab("Đầu Sách (Titles)", getResourceIcon("/images/book.png"), bookManagePanel);
        mainTabPane.addTab("Bản Sao Sách", getResourceIcon("/images/stack-of-books.png"), bookCopyManagePanel);
        mainTabPane.addTab("Mượn / Trả (Circulation)", getResourceIcon("/images/checked.png"), checkoutManagePanel);
        mainTabPane.addTab("Độc Giả (Patrons)", getResourceIcon("/images/user (1).png"), patronManagePanel);
        mainTabPane.addTab("Danh Mục (Tác Giả / Thể Loại / NXB)", getResourceIcon("/images/online-library.png"), masterDataPanel);
        mainTabPane.addTab("Đặt Giữ Sách", getResourceIcon("/images/reading_24.png"), holdManagePanel);
        mainTabPane.addTab("Thông Báo System", getResourceIcon("/images/notification.png"), notifyManagePanel);
        mainTabPane.addTab("Lịch Sử Log", getResourceIcon("/images/history.png"), historyLogPanel);
        mainTabPane.addTab("Chat & Share File TCP", getResourceIcon("/images/paper-plane.png"), chatPanel);

        // Default open to "Bản Sao Sách" tab as requested
        mainTabPane.setSelectedIndex(1);

        add(mainTabPane, BorderLayout.CENTER);

        // Footer Bar
        JPanel footerBar = new JPanel(new BorderLayout());
        footerBar.setBackground(Color.WHITE);
        footerBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(6, 20, 6, 20)
        ));

        JLabel footerLeft = new JLabel("Trường Đại học Công nghệ Thông tin và Truyền thông Việt - Hàn (VKU)");
        footerLeft.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLeft.setForeground(new Color(100, 116, 139));

        JLabel footerRight = new JLabel("Phiên bản Desktop Web 2.4.0 • Hỗ trợ kỹ thuật");
        footerRight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerRight.setForeground(new Color(100, 116, 139));

        footerBar.add(footerLeft, BorderLayout.WEST);
        footerBar.add(footerRight, BorderLayout.EAST);

        add(footerBar, BorderLayout.SOUTH);
    }

    private ImageIcon getResourceIcon(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) return new ImageIcon(url);
        } catch (Exception ignored) {}
        return null;
    }
}
