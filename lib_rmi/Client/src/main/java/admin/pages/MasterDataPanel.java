package admin.pages;

import admin.ManagerController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class MasterDataPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private JButton btnAuthor;
    private JButton btnCategory;
    private JButton btnPublisher;

    private AuthorManagePanel authorPanel;
    private CategoryManagePanel categoryPanel;
    private PublisherManagePanel publisherPanel;

    public MasterDataPanel(ManagerController controller, AuthorManagePanel authorPanel, CategoryManagePanel categoryPanel, PublisherManagePanel publisherPanel) {
        this.authorPanel = authorPanel;
        this.categoryPanel = categoryPanel;
        this.publisherPanel = publisherPanel;

        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

        // Sub-navigation bar at top
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        navBar.setBackground(Color.WHITE);
        navBar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            new EmptyBorder(4, 15, 4, 15)
        ));

        JLabel lblGroup = new JLabel("Danh Mục Quản Lý: ");
        lblGroup.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblGroup.setForeground(new Color(30, 41, 59));
        navBar.add(lblGroup);

        btnAuthor = createSubNavButton("✍️ Quản Lý Tác Giả", true);
        btnCategory = createSubNavButton("🏷️ Quản Lý Thể Loại", false);
        btnPublisher = createSubNavButton("🏢 Quản Lý Nhà Xuất Bản", false);

        btnAuthor.addActionListener(e -> switchSubTab("AUTHOR"));
        btnCategory.addActionListener(e -> switchSubTab("CATEGORY"));
        btnPublisher.addActionListener(e -> switchSubTab("PUBLISHER"));

        navBar.add(btnAuthor);
        navBar.add(btnCategory);
        navBar.add(btnPublisher);

        add(navBar, BorderLayout.NORTH);

        // Center CardLayout Container
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(authorPanel, "AUTHOR");
        cardPanel.add(categoryPanel, "CATEGORY");
        cardPanel.add(publisherPanel, "PUBLISHER");

        add(cardPanel, BorderLayout.CENTER);
    }

    private JButton createSubNavButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 12));
        btn.setBackground(active ? new Color(224, 242, 254) : Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(active ? new Color(37, 99, 235) : new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void switchSubTab(String tabKey) {
        setButtonState(btnAuthor, "AUTHOR".equals(tabKey));
        setButtonState(btnCategory, "CATEGORY".equals(tabKey));
        setButtonState(btnPublisher, "PUBLISHER".equals(tabKey));

        cardLayout.show(cardPanel, tabKey);
    }

    private void setButtonState(JButton btn, boolean active) {
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 12));
        btn.setBackground(active ? new Color(224, 242, 254) : Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(active ? new Color(37, 99, 235) : new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
    }
}
