package patron;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIStyleHelper {
    // Rich Blue & Soft Light Background Palette Theme
    public static final Color COLOR_NAVBAR_BG = new Color(30, 58, 138);       // #1E3A8A Rich Deep Navy Blue
    public static final Color COLOR_NAV_HOVER = new Color(37, 99, 235);       // #2563EB Royal Blue Hover
    public static final Color COLOR_NAV_ACTIVE_BG = new Color(29, 78, 216);   // #1D4ED8 Deep Blue Active Item
    public static final Color COLOR_NAV_ACCENT = new Color(96, 165, 250);     // #60A5FA Bright Sky Blue Accent Bar

    public static final Color COLOR_HEADER_BG = new Color(239, 246, 255);    // #EFF6FF Light Blue Top Bar
    public static final Color COLOR_BG_MAIN = new Color(241, 245, 249);      // #F1F5F9 Soft Light Blue/Slate Background
    public static final Color COLOR_BG_LIGHT = new Color(248, 250, 252);     // #F8FAFC Card Light BG

    // Section Header Banners
    public static final Color COLOR_TEAL_HEADER = new Color(37, 99, 235);    // #2563EB Royal Blue Section Bar
    public static final Color COLOR_ORANGE_HEADER = new Color(59, 130, 246); // #3B82F6 Light Sky Blue Section Bar

    // Notice Box Banner matching Soft Blue Alert
    public static final Color COLOR_NOTICE_BG = new Color(239, 246, 255);    // #EFF6FF Soft Light Blue Notice Box
    public static final Color COLOR_NOTICE_BORDER = new Color(191, 219, 254); // #BFDBFE Blue Border
    public static final Color COLOR_NOTICE_TEXT = new Color(30, 58, 138);    // #1E3A8A Dark Blue Text

    // Standard Controls & Tables
    public static final Color COLOR_PRIMARY_BG = new Color(37, 99, 235);    // #2563EB Royal Blue Primary
    public static final Color COLOR_PRIMARY_BORDER = new Color(29, 78, 216); // Darker Blue Border
    public static final Color COLOR_SUCCESS_BG = new Color(16, 185, 129);    // Emerald Green Button
    public static final Color COLOR_SUCCESS_BORDER = new Color(5, 150, 105); // Dark Green Border
    public static final Color COLOR_DANGER_BG = new Color(239, 68, 68);      // Crimson Red Button
    public static final Color COLOR_DANGER_BORDER = new Color(185, 28, 28);  // Dark Red Border

    public static final Color COLOR_BORDER = new Color(219, 234, 254);       // #DBEAFE Light Blue Border
    public static final Color COLOR_TEXT_MAIN = new Color(30, 41, 59);      // #1E293B Text Main
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);   // #64748B Text Muted
    public static final Color COLOR_TABLE_HEADER = new Color(30, 64, 175);   // #1E40AF Deep Blue Table Header

    // Typography (Segoe UI / Clean Sans-Serif)
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    public static void styleTable(JTable table, JScrollPane scrollPane) {
        table.setRowHeight(38);
        table.setFont(FONT_BODY);
        table.setForeground(COLOR_TEXT_MAIN);
        table.setGridColor(new Color(219, 234, 254));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(191, 219, 254)); // #BFDBFE Light Blue Selection
        table.setSelectionForeground(new Color(30, 58, 138));   // #1E3A8A Dark Blue Selection Text

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
        header.setBackground(COLOR_TABLE_HEADER);
        header.setForeground(Color.WHITE);
        header.setFont(FONT_BODY_BOLD);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                l.setBackground(COLOR_TABLE_HEADER);
                l.setForeground(Color.WHITE);
                l.setFont(FONT_BODY_BOLD);
                l.setHorizontalAlignment(SwingConstants.LEFT);
                l.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return l;
            }
        };
        table.getTableHeader().setDefaultRenderer(headerRenderer);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 246, 255));
                    c.setForeground(COLOR_TEXT_MAIN);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
            scrollPane.getViewport().setBackground(Color.WHITE);
        }
    }

    public static void styleButton(JButton button, Color bgColor, Color borderColor) {
        styleButton(button, bgColor, borderColor, Color.WHITE);
    }

    public static void styleButton(JButton button, Color bgColor, Color borderColor, Color fgColor) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setOpaque(true);
        button.setFont(FONT_BODY_BOLD);
        button.setBackground(bgColor);
        button.setForeground(fgColor != null ? fgColor : Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor != null ? borderColor : bgColor.darker(), 1, true),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
    }

    // Button Color Styles for Internal Pages
    public static final Color COLOR_BLUE_BTN = new Color(37, 99, 235);    // #2563EB Royal Blue
    public static final Color COLOR_TEAL_BTN = new Color(59, 130, 246);   // #3B82F6 Sky Blue
    public static final Color COLOR_GREEN_BTN = new Color(16, 185, 129);  // #10B981 Emerald Green
    public static final Color COLOR_RED_BTN = new Color(220, 38, 38);     // #DC2626 Crimson Red
    public static final Color COLOR_SLATE_BTN = new Color(100, 116, 139); // #64748B Slate Gray

    public static JButton createBlueButton(String text, Icon icon) {
        return createStyledButton(text, icon, COLOR_BLUE_BTN, new Color(29, 78, 216));
    }

    public static JButton createTealButton(String text, Icon icon) {
        return createStyledButton(text, icon, COLOR_TEAL_BTN, new Color(37, 99, 235));
    }

    public static JButton createGreenButton(String text, Icon icon) {
        return createStyledButton(text, icon, COLOR_GREEN_BTN, new Color(5, 150, 105));
    }

    public static JButton createRedButton(String text, Icon icon) {
        return createStyledButton(text, icon, COLOR_RED_BTN, new Color(185, 28, 28));
    }

    public static JButton createSlateButton(String text, Icon icon) {
        return createStyledButton(text, icon, COLOR_SLATE_BTN, new Color(71, 85, 105));
    }

    public static JButton createStyledButton(String text, Icon icon, Color bg, Color borderColor) {
        JButton btn = new JButton(text);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setOpaque(true);
        if (icon != null) btn.setIcon(icon);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor != null ? borderColor : bg.darker(), 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        return btn;
    }

    // Light Blue Pill Button (e.g. "Xem lịch trình" / "Lưu thay đổi" button style)
    public static JButton createTealPillButton(String text, Icon icon) {
        return createStyledButton(text, icon, COLOR_TEAL_BTN, new Color(37, 99, 235));
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_BODY);
        textField.setForeground(COLOR_TEXT_MAIN);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }

    public static JPanel createCardPanel(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        if (title != null && !title.isEmpty()) {
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(FONT_TITLE);
            lblTitle.setForeground(COLOR_TEXT_MAIN);
            lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            card.add(lblTitle, BorderLayout.NORTH);
        }
        return card;
    }

    // Create Section Header Bar
    public static JPanel createSectionHeaderBar(String title, Color barColor) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(barColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        return headerPanel;
    }

    // Create Notice Banner
    public static JPanel createNoticeBanner(String noticeText) {
        JPanel noticePanel = new JPanel(new BorderLayout());
        noticePanel.setBackground(COLOR_NOTICE_BG);
        noticePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_NOTICE_BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblNotice = new JLabel("<html><b style='color:#1E3A8A;'>Lưu ý:</b> " + noticeText + "</html>");
        lblNotice.setFont(FONT_BODY);
        lblNotice.setForeground(COLOR_NOTICE_TEXT);

        noticePanel.add(lblNotice, BorderLayout.CENTER);
        return noticePanel;
    }

    public static ImageIcon getIcon(String path, int width, int height) {
        try {
            java.net.URL imgURL = UIStyleHelper.class.getResource(path);
            if (imgURL != null) {
                ImageIcon original = new ImageIcon(imgURL);
                Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception ignored) {}
        return null;
    }

    // Custom painted Sidebar Navigation Button with High-Contrast White Text on Deep Blue Theme
    public static class SidebarNavButton extends JButton {
        private boolean active = false;
        private Color bgNormal = new Color(30, 58, 138);     // #1E3A8A Rich Deep Navy Blue
        private Color bgHover = new Color(37, 99, 235);      // #2563EB Royal Blue Hover
        private Color bgActive = new Color(29, 78, 216);     // #1D4ED8 Deep Blue Active
        private Color accentColor = new Color(96, 165, 250); // #60A5FA Sky Blue Accent Bar

        public SidebarNavButton(String text, Icon icon) {
            super(text);
            if (icon != null) setIcon(icon);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE); // High contrast bold white text
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setIconTextGap(12);
            setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 12));
        }

        public void setActive(boolean active) {
            this.active = active;
            setForeground(Color.WHITE); // Crisp white text for both states!
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (active) {
                g2.setColor(bgActive);
                g2.fillRect(0, 0, w, h);
                g2.setColor(accentColor);
                g2.fillRect(0, 0, 4, h);
            } else if (getModel().isRollover()) {
                g2.setColor(bgHover);
                g2.fillRect(0, 0, w, h);
            } else {
                g2.setColor(bgNormal);
                g2.fillRect(0, 0, w, h);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}


