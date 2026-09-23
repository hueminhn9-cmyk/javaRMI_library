import admin.AdminApp;
import patron.PatronApp;

import javax.swing.*;

public class RMIClient {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nếu có tham số "admin", chạy AdminApp, ngược lại mặc định chạy PatronApp (LoginGUI)
        if (args.length > 0 && "admin".equalsIgnoreCase(args[0])) {
            AdminApp.main(args);
        } else {
            PatronApp.main(args);
        }
    }
}
