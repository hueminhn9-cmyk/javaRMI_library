package patron;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIStyleHelper {
    public static final Color COLOR_NAVBAR_BG = new Color(15, 23, 42);      // #0F172A
    
    // Light Background Tints for High-Contrast Black Text
    public static final Color COLOR_PRIMARY_BG = new Color(224, 242, 254);   // #E0F2FE Light Sky Blue
    public static final Color COLOR_PRIMARY_BORDER = new Color(37, 99, 235); // #2563EB Royal Blue

    public static final Color COLOR_SUCCESS_BG = new Color(220, 252, 231);   // #DCFCE7 Light Mint Green
    public static final Color COLOR_SUCCESS_BORDER = new Color(16, 185, 129); // #10B981 Emerald Green

    public static final Color COLOR_DANGER_BG = new Color(254, 226, 226);    // #FEE2E2 Light Soft Red
    public static final Color COLOR_DANGER_BORDER = new Color(239, 68, 68);  // #EF4444 Bright Red

    public static final Color COLOR_BG_LIGHT = new Color(248, 250, 252);     // #F8FAFC
    public static final Color COLOR_BORDER = new Color(226, 232, 240);       // #E2E8F0
    public static final Color COLOR_TEXT_MAIN = new Color(30, 41, 59);      // #1E293B
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);   // #64748B
    public static final Color COLOR_TABLE_HEADER = new Color(30, 41, 59);   // #1E293B

    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    public static void styleTable(JTable table, JScrollPane scrollPane) {
        table.setRowHeight(38);
        table.setFont(FONT_BODY);
        table.setForeground(COLOR_TEXT_MAIN);
        table.setGridColor(COLOR_BORDER);
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(219, 234, 254)); // #DBEAFE
        table.setSelectionForeground(new Color(30, 64, 175));   // #1E40AF

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
                l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return l;
            }
        };
        table.getTableHeader().setDefaultRenderer(headerRenderer);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_BG_LIGHT);
                    c.setForeground(COLOR_TEXT_MAIN);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
            scrollPane.getViewport().setBackground(Color.WHITE);
        }
    }

    public static void styleButton(JButton button, Color bgColor, Color borderColor) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setFont(FONT_BODY_BOLD);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); // Explicit black text for clear readability
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor != null ? borderColor : bgColor.darker(), 2, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
    }

    public static void styleButton(JButton button, Color bgColor, Color borderColor, Color fgColor) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setFont(FONT_BODY_BOLD);
        button.setBackground(bgColor);
        button.setForeground(fgColor != null ? fgColor : Color.BLACK);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor != null ? borderColor : bgColor.darker(), 2, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
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
}
