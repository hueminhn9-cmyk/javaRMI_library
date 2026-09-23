package admin.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class CenteredTableCellRenderer extends DefaultTableCellRenderer {
    public CenteredTableCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }
}

