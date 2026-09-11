package common;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomHeaderRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setBackground(new Color(37, 99, 235)); // Màu nền xanh đậm
        label.setForeground(Color.WHITE); // Màu chữ trắng
        label.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Phông chữ Segoe UI tiếng Việt chuẩn
        label.setHorizontalAlignment(JLabel.CENTER); // Căn giữa tiêu đề
        return label;
    }
}
