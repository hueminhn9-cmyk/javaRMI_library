package common;

import admin.ManageGUI;
import patron.LoginGUI;
import javax.swing.*;

public class RMIClient {
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LoginGUI().setVisible(true);
    }
}
