package admin;

import admin.pages.*;
import common.*;

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
        }
    }

    private ManagerController controller;

    private BookManagePanel bookManagePanel;
    private BookCopyManagePanel bookCopyManagePanel;
    private AuthorManagePanel authorManagePanel;
    private CategoryManagePanel categoryManagePanel;
    private PublisherManagePanel publisherManagePanel;
    private PatronManagePanel patronManagePanel;
    private CheckoutManagePanel checkoutManagePanel;
    private HoldManagePanel holdManagePanel;
    private NotifyManagePanel notifyManagePanel;
    private HistoryLogPanel historyLogPanel;

    private JTabbedPane mainTabPane;

    public ManageGUI() {
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

        // Header Top Panel with Banner like library_manager_rmi
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel bannerLabel = new JLabel();
        ImageIcon bannerIcon = getResourceIcon("/images/banner.png");
        if (bannerIcon != null) {
            bannerLabel.setIcon(bannerIcon);
            bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(new Color(30, 41, 59));
        headerBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("  VKU LIBRARY MANAGEMENT SYSTEM - ADMIN DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerBar.add(titleLabel, BorderLayout.WEST);

        topPanel.add(bannerLabel, BorderLayout.NORTH);
        topPanel.add(headerBar, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Initialize Admin Panels
        bookManagePanel = new BookManagePanel(controller);
        bookCopyManagePanel = new BookCopyManagePanel(controller);
        authorManagePanel = new AuthorManagePanel(controller);
        categoryManagePanel = new CategoryManagePanel(controller);
        publisherManagePanel = new PublisherManagePanel(controller);
        patronManagePanel = new PatronManagePanel(controller);
        checkoutManagePanel = new CheckoutManagePanel(controller);
        holdManagePanel = new HoldManagePanel(controller);
        notifyManagePanel = new NotifyManagePanel(controller);
        historyLogPanel = new HistoryLogPanel(controller);

        // Main Tabbed Pane at TOP
        mainTabPane = new JTabbedPane();
        mainTabPane.setTabPlacement(JTabbedPane.TOP);
        mainTabPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        mainTabPane.addTab("Sách (Book)", getResourceIcon("/images/book.png"), bookManagePanel);
        mainTabPane.addTab("Bản Sao Sách", getResourceIcon("/images/stack-of-books.png"), bookCopyManagePanel);
        mainTabPane.addTab("Tác Giả (Author)", getResourceIcon("/images/writer.png"), authorManagePanel);
        mainTabPane.addTab("Thể Loại (Category)", getResourceIcon("/images/tag.png"), categoryManagePanel);
        mainTabPane.addTab("Nhà Xuất Bản", getResourceIcon("/images/online-library.png"), publisherManagePanel);
        mainTabPane.addTab("Độc Giả (Patrons)", getResourceIcon("/images/user (1).png"), patronManagePanel);
        mainTabPane.addTab("Mượn / Trả", getResourceIcon("/images/checked.png"), checkoutManagePanel);
        mainTabPane.addTab("Đặt Giữ Sách", getResourceIcon("/images/reading_24.png"), holdManagePanel);
        mainTabPane.addTab("Thông Báo", getResourceIcon("/images/notification.png"), notifyManagePanel);
        mainTabPane.addTab("Lịch Sử Log", getResourceIcon("/images/history.png"), historyLogPanel);

        add(mainTabPane, BorderLayout.CENTER);
    }

    private ImageIcon getResourceIcon(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) return new ImageIcon(url);
        } catch (Exception ignored) {}
        return null;
    }
}
