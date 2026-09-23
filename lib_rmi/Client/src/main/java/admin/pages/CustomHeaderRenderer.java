package admin.pages;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomHeaderRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setBackground(new Color(241, 245, 249)); // Slate 100
        label.setForeground(new Color(71, 85, 105));   // Slate 600
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 1, new Color(226, 232, 240)),
            new EmptyBorder(8, 6, 8, 6)
        ));
        return label;
    }
}


