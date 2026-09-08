package common;

import patron.LoginGUI;
import javax.swing.*;

public class RMIClient {
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        new LoginGUI().setVisible(true);

    }


}
