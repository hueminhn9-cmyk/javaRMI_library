package admin;

import common.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author nguye
 */
public class ManageGUI extends javax.swing.JFrame {
    class ClientImpl extends UnicastRemoteObject implements ClientInterface {

        public ClientImpl() throws RemoteException {
        }

        @Override
        public void notify(NOTIFY notify) throws RemoteException {
            log = null;
            if (notify == NOTIFY.UPDATE_BOOK) showTableBook();
            if (notify == NOTIFY.UPDATE_AUTHOR) showTableAuthor();
            if (notify == NOTIFY.UPDATE_CATEGORY) showTableCategory();
            if (notify == NOTIFY.UPDATE_NOTIFICATION) showTableNotification();

        }
    }

    ManagerController controller;
    TableRowSorter sorter;
    Log log;
    InetAddress ipAddress;

    {
        try {
            ipAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    String ip = ipAddress.getHostAddress().isEmpty() ? "Unknown" : ipAddress.getHostAddress();
    String username = "manage";
    String table_name;
    String col_id;

    /**
     * Creates new form LibraryGUI
     */
    public ManageGUI() {
        initComponents();
        try {
            controller = new ManagerController(new ClientImpl());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        // Bắt sự kiện đóng cửa sổ để xóa log
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                try {
                    controller.exit();
                    if (log != null)
                        controller.deleteLog(log.getId());
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // load data
        showTableBook();
        showTableAuthor();
        showTableCategory();
        showTablePublished();
        showTableBookCopy();
        showTableHold();
        showTableCheckout();
        showTableNotification();
        showTablePatrons();
        showTableHistory();

        showDataComboBoxCategory();
        showDataComboBoxPublished();
        showDataComboBoxAuthor();
        showDataComboBoxBooks();
        showDataComboBoxBookCopy();
        showDataComboBoxPatron();
        //
    }


    public synchronized void showTableBook() {
        try {
            Response response = controller.getBooksController();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getStatus());
            }
            sorter = new TableRowSorter<>((DefaultTableModel) response.getData());
            tbl_Book.setRowSorter(sorter);
            tf_search_Book.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search(tf_search_Book.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search(tf_search_Book.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search(tf_search_Book.getText());
                }

                public void search(String str) {
                    if (str.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }
                }
            });

            tbl_Book.setModel((DefaultTableModel) response.getData());
            tbl_Book.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
            tbl_Book.setRowHeight(30);
            tbl_Book.setDefaultEditor(Object.class, null);
            TableColumn indexColumn = tbl_Book.getColumnModel().getColumn(0);
            indexColumn.setCellRenderer(new CenteredTableCellRenderer());
            indexColumn.setMaxWidth(80);
            sp_Book.setViewportView(tbl_Book);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public synchronized void showTableAuthor() {
        try {
            Response response = controller.getAuthorsController();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getStatus());
            }
            sorter = new TableRowSorter<>((DefaultTableModel) response.getData());
            tbl_Author.setRowSorter(sorter);
            tf_search_Author.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search(tf_search_Author.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search(tf_search_Author.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search(tf_search_Author.getText());
                }

                public void search(String str) {
                    if (str.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }
                }
            });

            tbl_Author.setModel((DefaultTableModel) response.getData());
            tbl_Author.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
            tbl_Author.setRowHeight(30);
            tbl_Author.setDefaultEditor(Object.class, null);
            TableColumn indexColumn = tbl_Author.getColumnModel().getColumn(0);
            indexColumn.setCellRenderer(new CenteredTableCellRenderer());
            indexColumn.setMaxWidth(80);
            sp_Author.setViewportView(tbl_Author);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public synchronized void showTableCategory() {
        try {
            Response response = controller.getCategoriesController();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getStatus());
            }
            sorter = new TableRowSorter<>((DefaultTableModel) response.getData());
            tbl_Category.setRowSorter(sorter);
            tf_search_Category.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search(tf_search_Category.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search(tf_search_Category.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search(tf_search_Category.getText());
                }

                public void search(String str) {
                    if (str.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }
                }
            });

            tbl_Category.setModel((DefaultTableModel) response.getData());
            tbl_Category.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
            tbl_Category.setRowHeight(30);
            tbl_Category.setDefaultEditor(Object.class, null);
            TableColumn indexColumn = tbl_Category.getColumnModel().getColumn(0);
            indexColumn.setCellRenderer(new CenteredTableCellRenderer());
            indexColumn.setMaxWidth(80);
            sp_Category.setViewportView(tbl_Category);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public synchronized void showTablePublished() {
        try {
            Response response = controller.getPublishedController();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getStatus());
            }
            sorter = new TableRowSorter<>((DefaultTableModel) response.getData());
            tbl_Published.setRowSorter(sorter);
            tf_search_Published.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search(tf_search_Published.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search(tf_search_Published.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search(tf_search_Published.getText());
                }

                public void search(String str) {
                    if (str.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }
                }
            });

            tbl_Published.setModel((DefaultTableModel) response.getData());
            tbl_Published.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
            tbl_Published.setRowHeight(30);
            tbl_Published.setDefaultEditor(Object.class, null);
            TableColumn indexColumn = tbl_Published.getColumnModel().getColumn(0);
            indexColumn.setCellRenderer(new CenteredTableCellRenderer());
            indexColumn.setMaxWidth(80);
            sp_Published.setViewportView(tbl_Published);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public synchronized void showTableBookCopy() {
        try {
            Response response = controller.getBooksCopyController();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getStatus());
            }
            sorter = new TableRowSorter<>((DefaultTableModel) response.getData());
            tbl_BookCopy.setRowSorter(sorter);
            tf_search_BookCopy.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search(tf_search_BookCopy.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search(tf_search_BookCopy.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search(tf_search_BookCopy.getText());
                }

                public void search(String str) {
                    if (str.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }
                }
            });

            tbl_BookCopy.setModel((DefaultTableModel) response.getData());
            tbl_BookCopy.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
            tbl_BookCopy.setRowHeight(30);
            tbl_BookCopy.setDefaultEditor(Object.class, null);
            TableColumn indexColumn = tbl_BookCopy.getColumnModel().getColumn(0);
            indexColumn.setCellRenderer(new CenteredTableCellRenderer());
            indexColumn.setMaxWidth(80);
            sp_BookCopy.setViewportView(tbl_BookCopy);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }
    public synchronized void showTableHold() {
        try {
            Response response = controller.getHoldsController();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            sorter = new TableRowSorter<>((DefaultTableModel) response.getData());
            tbl_Hold.setRowSorter(sorter);
            tf_search_Hold.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search(tf_search_Hold.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search(tf_search_Hold.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search(tf_search_Hold.getText());
                }

                public void search(String str) {
                    if (str.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }
                }
            });

            tbl_Hold.setModel((DefaultTableModel) response.getData());
            tbl_Hold.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
            tbl_Hold.setRowHeight(30);
            tbl_Hold.setDefaultEditor(Object.class, null);
            TableColumn indexColumn = tbl_Hold.getColumnModel().getColumn(0);
            indexColumn.setCellRenderer(new CenteredTableCellRenderer());
            indexColumn.setMaxWidth(80);
            sp_Hold.setViewportView(tbl_Hold);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public synchronized void showTableCheckout() {
        try {
            Response response = controller.getCheckoutsController();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            sorter = new TableRowSorter<>((DefaultTableModel) response.getData());
            tbl_Checkout.setRowSorter(sorter);
            tf_search_Checkout.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search(tf_search_Checkout.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search(tf_search_Checkout.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search(tf_search_Checkout.getText());
                }

                public void search(String str) {
                    if (str.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }
                }
            });

            tbl_Checkout.setModel((DefaultTableModel) response.getData());
            tbl_Checkout.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
            tbl_Checkout.setRowHeight(30);
            tbl_Checkout.setDefaultEditor(Object.class, null);
            TableColumn indexColumn = tbl_Checkout.getColumnModel().getColumn(0);
            indexColumn.setCellRenderer(new CenteredTableCellRenderer());
            indexColumn.setMaxWidth(80);
            sp_Checkout.setViewportView(tbl_Checkout);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public synchronized void showTableNotification() {
        try {
            Response response = controller.getNotificationsController();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            sorter = new TableRowSorter<>((DefaultTableModel) response.getData());
            tbl_Notification.setRowSorter(sorter);
            tf_search_Notification.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search(tf_search_Notification.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search(tf_search_Notification.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search(tf_search_Notification.getText());
                }

                public void search(String str) {
                    if (str.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }
                }
            });

            tbl_Notification.setModel((DefaultTableModel) response.getData());
            tbl_Notification.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
            tbl_Notification.setRowHeight(30);
            tbl_Notification.setDefaultEditor(Object.class, null);
            TableColumn indexColumn = tbl_Notification.getColumnModel().getColumn(0);
            indexColumn.setCellRenderer(new CenteredTableCellRenderer());
            indexColumn.setMaxWidth(80);
            sp_Notification.setViewportView(tbl_Notification);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public synchronized void showTablePatrons() {
        try {
            Response response = controller.getPatronsController();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            sorter = new TableRowSorter<>((DefaultTableModel) response.getData());
            tbl_Patron.setRowSorter(sorter);
            tf_search_Patron.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search(tf_search_Patron.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search(tf_search_Patron.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search(tf_search_Patron.getText());
                }

                public void search(String str) {
                    if (str.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }
                }
            });

            tbl_Patron.setModel((DefaultTableModel) response.getData());
            tbl_Patron.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
            tbl_Patron.setRowHeight(30);
            tbl_Patron.setDefaultEditor(Object.class, null);
            TableColumn indexColumn = tbl_Patron.getColumnModel().getColumn(0);
            indexColumn.setCellRenderer(new CenteredTableCellRenderer());
            indexColumn.setMaxWidth(80);
            sp_Patron.setViewportView(tbl_Patron);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public synchronized void showTableHistory() {
        try {
            Response response = controller.getHistoryController();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            sorter = new TableRowSorter<>((DefaultTableModel) response.getData());
            tbl_History.setRowSorter(sorter);
            tf_search_History.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    search(tf_search_History.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    search(tf_search_History.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    search(tf_search_History.getText());
                }

                public void search(String str) {
                    if (str.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }
                }
            });

            tbl_History.setModel((DefaultTableModel) response.getData());
            tbl_History.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
            tbl_History.setRowHeight(30);
            tbl_History.setDefaultEditor(Object.class, null);
            TableColumn indexColumn = tbl_History.getColumnModel().getColumn(0);
            indexColumn.setCellRenderer(new CenteredTableCellRenderer());
            indexColumn.setMaxWidth(80);
            sp_History.setViewportView(tbl_History);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    // ------------------------------------------------------------------
    public synchronized void showDataComboBoxCategory() {
        try {
            Response response = controller.getDataComboBoxCategories();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            cb_category_Book.removeAllItems();
            List<Category> categoryList = (List<Category>) response.getData();
            for (Category i :
                    categoryList) {
                cb_category_Book.addItem(i);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void showDataComboBoxPublished() {
        try {
            Response response = controller.getDataComboBoxPublished();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            cb_published_BookCopy.removeAllItems();
            List<Published> publishedList = (List<Published>) response.getData();
            for (Published i :
                    publishedList) {
                cb_published_BookCopy.addItem(i);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void showDataComboBoxAuthor() {
        try {
            Response response = controller.getDataComboBoxAuthors();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            cb_author_Book.removeAllItems();
            List<Author> authorList = (List<Author>) response.getData();
            for (Author i : authorList) {
                cb_author_Book.addItem(i);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void showDataComboBoxBookCopy() {
        try {
            Response response = controller.getDataComboBoxBookCopies();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            cb_book_Hold.removeAllItems();
            cb_book_Checkout.removeAllItems();
            List<BookCopy> bookCopyList = (List<BookCopy>) response.getData();
            for (BookCopy i : bookCopyList) {
                cb_book_Hold.addItem(i);
                cb_book_Checkout.addItem(i);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void showDataComboBoxPatron() {
        try {
            Response response = controller.getDataComboBoxPatrons();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            cb_patron_Hold.removeAllItems();
            cb_patron_Checkout.removeAllItems();
            List<Patron> patronsList = (List<Patron>) response.getData();
            for (Patron i : patronsList) {
                cb_patron_Hold.addItem(i);
                cb_patron_Checkout.addItem(i);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public synchronized void showDataComboBoxBooks() {
        try {
            Response response = controller.getDataComboBoxBooks();
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            }
            cb_book_BookCopy.removeAllItems();

            List<Book> booksList = (List<Book>) response.getData();
            for (Book i : booksList) {
                cb_book_BookCopy.addItem(i);

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                
    public void initComponents() {
        setTitle("VKU Library Management System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        setPreferredSize(new Dimension(1100, 700));

        // Top Banner Panel
        jPanel1 = new JPanel(new BorderLayout());
        jPanel1.setBackground(new Color(30, 41, 59));
        jLabel1 = new JLabel();
        ImageIcon bannerIcon = getResourceIcon("/images/banner.png");
        if (bannerIcon != null) {
            jLabel1.setIcon(bannerIcon);
            jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            jLabel1.setText("  VKU LIBRARY MANAGEMENT SYSTEM - ADMIN DASHBOARD  ");
            jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 22));
            jLabel1.setForeground(Color.WHITE);
            jLabel1.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        }
        jPanel1.add(jLabel1, BorderLayout.CENTER);
        getContentPane().add(jPanel1, BorderLayout.NORTH);

        // Main Tabbed Pane
        main_panel = new JTabbedPane();
        main_panel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // 1. Tab Book
        initBookPanel();
        main_panel.addTab("Sách (Book)", getResourceIcon("/images/book.png"), panel_Book);

        // 2. Tab Book Copy
        initBookCopyPanel();
        main_panel.addTab("Bản Sao Sách (Copies)", getResourceIcon("/images/stack-of-books.png"), panel_BookCopy);

        // 3. Tab Author
        initAuthorPanel();
        main_panel.addTab("Tác Giả (Author)", getResourceIcon("/images/writer.png"), panel_Author);

        // 4. Tab Category
        initCategoryPanel();
        main_panel.addTab("Thể Loại (Category)", getResourceIcon("/images/tag.png"), panel_Category);

        // 5. Tab Published
        initPublishedPanel();
        main_panel.addTab("Nhà Xuất Bản (Publisher)", getResourceIcon("/images/online-library.png"), panel_Published);

        // 6. Tab Patron
        initPatronPanel();
        main_panel.addTab("Độc Giả (Patrons)", getResourceIcon("/images/user (1).png"), panel_Patron);

        // 7. Tab Checkout (Giai đoạn 2)
        initCheckoutPanel();
        main_panel.addTab("Mượn / Trả (Checkout)", getResourceIcon("/images/checked.png"), panel_Checkout);

        // 8. Tab Hold (Giai đoạn 2)
        initHoldPanel();
        main_panel.addTab("Đặt Giữ Sách (Hold)", getResourceIcon("/images/reading_24.png"), panel_Hold);

        // 9. Tab Notification
        initNotificationPanel();
        main_panel.addTab("Thông Báo (Notify)", getResourceIcon("/images/notification.png"), panel_Notification);

        // 10. Tab History Log
        initHistoryPanel();
        main_panel.addTab("Lịch Sử Log (Logs)", getResourceIcon("/images/history.png"), panel_History);

        getContentPane().add(main_panel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private ImageIcon getResourceIcon(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) return new ImageIcon(url);
        } catch (Exception e) {}
        return null;
    }

    private JButton createBtn(String text, String iconPath, Color bg) {
        JButton btn = new JButton(text);
        ImageIcon icon = getResourceIcon(iconPath);
        if (icon != null) btn.setIcon(icon);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return btn;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(200, 32));
    }

    // ------------------------------------------------------------------
    // Panel 1: Sách (Book)
    // ------------------------------------------------------------------
    private void initBookPanel() {
        panel_Book = new JPanel(new BorderLayout(15, 15));
        panel_Book.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form Panel (Left)
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Sách ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));
        formCard.setPreferredSize(new Dimension(380, 450));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tf_ID_Book = new JTextField();
        styleTextField(tf_ID_Book);
        tf_title_Book = new JTextField();
        styleTextField(tf_title_Book);
        cb_category_Book = new JComboBox<>();
        cb_category_Book.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb_category_Book.setPreferredSize(new Dimension(200, 32));
        cb_author_Book = new JComboBox<>();
        cb_author_Book.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb_author_Book.setPreferredSize(new Dimension(200, 32));

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(new JLabel("Mã Sách (ID):"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_ID_Book, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(new JLabel("Tên Sách:"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_title_Book, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(new JLabel("Thể Loại:"), gbc);
        gbc.gridx = 1;
        formCard.add(cb_category_Book, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(new JLabel("Tác Giả:"), gbc);
        gbc.gridx = 1;
        formCard.add(cb_author_Book, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        btn_create_Book = createBtn("Thêm Mới", "/images/add.png", new Color(22, 163, 74));
        btn_create_Book.addActionListener(this::btn_create_BookActionPerformed);

        btn_update_Book = createBtn("Cập Nhật", "/images/refresh.png", new Color(37, 99, 235));
        btn_update_Book.addActionListener(this::btn_update_BookActionPerformed);

        btn_delete_Book = createBtn("Xóa", "/images/bin.png", new Color(220, 38, 38));
        btn_delete_Book.addActionListener(this::btn_delete_BookActionPerformed);

        btn_refresh_Book = createBtn("Làm Mới", "/images/reset.png", new Color(75, 85, 99));
        btn_refresh_Book.addActionListener(this::btn_refresh_BookActionPerformed);

        btnPanel.add(btn_create_Book);
        btnPanel.add(btn_update_Book);
        btnPanel.add(btn_delete_Book);
        btnPanel.add(btn_refresh_Book);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        panel_Book.add(formCard, BorderLayout.WEST);

        // Table Panel (Right)
        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel searchIcon = new JLabel("  Tìm Kiếm Sách: ");
        searchIcon.setIcon(getResourceIcon("/images/search_32.png"));
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf_search_Book = new JTextField(25);
        styleTextField(tf_search_Book);
        searchBar.add(searchIcon);
        searchBar.add(tf_search_Book);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_Book = new JTable();
        tbl_Book.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_BookMousePressed(evt);
            }
        });
        sp_Book = new JScrollPane(tbl_Book);
        tablePanel.add(sp_Book, BorderLayout.CENTER);

        panel_Book.add(tablePanel, BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    // Panel 2: Bản Sao Sách (Book Copy)
    // ------------------------------------------------------------------
    private void initBookCopyPanel() {
        panel_BookCopy = new JPanel(new BorderLayout(15, 15));
        panel_BookCopy.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Bản Sao Sách ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));
        formCard.setPreferredSize(new Dimension(380, 450));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tf_ID_BookCopy = new JTextField();
        styleTextField(tf_ID_BookCopy);
        cb_book_BookCopy = new JComboBox<>();
        cb_book_BookCopy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb_book_BookCopy.setPreferredSize(new Dimension(200, 32));

        cb_published_BookCopy = new JComboBox<>();
        cb_published_BookCopy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb_published_BookCopy.setPreferredSize(new Dimension(200, 32));

        tf_year_BookCopy = new JTextField();
        styleTextField(tf_year_BookCopy);

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(new JLabel("Mã Bản Sao (ID):"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_ID_BookCopy, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(new JLabel("Tên Sách:"), gbc);
        gbc.gridx = 1;
        formCard.add(cb_book_BookCopy, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(new JLabel("Nhà Xuất Bản:"), gbc);
        gbc.gridx = 1;
        formCard.add(cb_published_BookCopy, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(new JLabel("Năm Xuất Bản:"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_year_BookCopy, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        btn_create_BookCopy = createBtn("Thêm Mới", "/images/add.png", new Color(22, 163, 74));
        btn_create_BookCopy.addActionListener(this::btn_create_BookCopyActionPerformed);

        btn_update_BookCopy = createBtn("Cập Nhật", "/images/refresh.png", new Color(37, 99, 235));
        btn_update_BookCopy.addActionListener(this::btn_update_BookCopyActionPerformed);

        btn_delete_BookCopy = createBtn("Xóa", "/images/bin.png", new Color(220, 38, 38));
        btn_delete_BookCopy.addActionListener(this::btn_delete_BookCopyActionPerformed);

        btn_refresh_BookCopy = createBtn("Làm Mới", "/images/reset.png", new Color(75, 85, 99));
        btn_refresh_BookCopy.addActionListener(this::btn_refresh_BookCopyActionPerformed);

        btnPanel.add(btn_create_BookCopy);
        btnPanel.add(btn_update_BookCopy);
        btnPanel.add(btn_delete_BookCopy);
        btnPanel.add(btn_refresh_BookCopy);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        panel_BookCopy.add(formCard, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel searchIcon = new JLabel("  Tìm Kiếm Bản Sao: ");
        searchIcon.setIcon(getResourceIcon("/images/search_32.png"));
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf_search_BookCopy = new JTextField(25);
        styleTextField(tf_search_BookCopy);
        searchBar.add(searchIcon);
        searchBar.add(tf_search_BookCopy);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_BookCopy = new JTable();
        tbl_BookCopy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_BookCopyMousePressed(evt);
            }
        });
        sp_BookCopy = new JScrollPane(tbl_BookCopy);
        tablePanel.add(sp_BookCopy, BorderLayout.CENTER);

        panel_BookCopy.add(tablePanel, BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    // Panel 3: Tác Giả (Author)
    // ------------------------------------------------------------------
    private void initAuthorPanel() {
        panel_Author = new JPanel(new BorderLayout(15, 15));
        panel_Author.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Tác Giả ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));
        formCard.setPreferredSize(new Dimension(380, 450));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tf_ID_Author = new JTextField();
        styleTextField(tf_ID_Author);
        tf_name_Author = new JTextField();
        styleTextField(tf_name_Author);

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(new JLabel("Mã Tác Giả (ID):"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_ID_Author, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(new JLabel("Tên Tác Giả:"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_name_Author, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        btn_create_Author = createBtn("Thêm Mới", "/images/add.png", new Color(22, 163, 74));
        btn_create_Author.addActionListener(this::btn_create_AuthorActionPerformed);

        btn_update_Author = createBtn("Cập Nhật", "/images/refresh.png", new Color(37, 99, 235));
        btn_update_Author.addActionListener(this::btn_update_AuthorActionPerformed);

        btn_delete_Author = createBtn("Xóa", "/images/bin.png", new Color(220, 38, 38));
        btn_delete_Author.addActionListener(this::btn_delete_AuthorActionPerformed);

        btn_refresh_Author = createBtn("Làm Mới", "/images/reset.png", new Color(75, 85, 99));
        btn_refresh_Author.addActionListener(this::btn_refresh_AuthorActionPerformed);

        btnPanel.add(btn_create_Author);
        btnPanel.add(btn_update_Author);
        btnPanel.add(btn_delete_Author);
        btnPanel.add(btn_refresh_Author);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        panel_Author.add(formCard, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel searchIcon = new JLabel("  Tìm Kiếm Tác Giả: ");
        searchIcon.setIcon(getResourceIcon("/images/search_32.png"));
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf_search_Author = new JTextField(25);
        styleTextField(tf_search_Author);
        searchBar.add(searchIcon);
        searchBar.add(tf_search_Author);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_Author = new JTable();
        tbl_Author.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_AuthorMousePressed(evt);
            }
        });
        sp_Author = new JScrollPane(tbl_Author);
        tablePanel.add(sp_Author, BorderLayout.CENTER);

        panel_Author.add(tablePanel, BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    // Panel 4: Thể Loại (Category)
    // ------------------------------------------------------------------
    private void initCategoryPanel() {
        panel_Category = new JPanel(new BorderLayout(15, 15));
        panel_Category.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Thể Loại ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));
        formCard.setPreferredSize(new Dimension(380, 450));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tf_ID_Category = new JTextField();
        styleTextField(tf_ID_Category);
        tf_name_Category = new JTextField();
        styleTextField(tf_name_Category);

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(new JLabel("Mã Thể Loại (ID):"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_ID_Category, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(new JLabel("Tên Thể Loại:"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_name_Category, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        btn_create_Category = createBtn("Thêm Mới", "/images/add.png", new Color(22, 163, 74));
        btn_create_Category.addActionListener(this::btn_create_CategoryActionPerformed);

        btn_update_Category = createBtn("Cập Nhật", "/images/refresh.png", new Color(37, 99, 235));
        btn_update_Category.addActionListener(this::btn_update_CategoryActionPerformed);

        btn_delete_Category = createBtn("Xóa", "/images/bin.png", new Color(220, 38, 38));
        btn_delete_Category.addActionListener(this::btn_delete_CategoryActionPerformed);

        btn_refresh_Category = createBtn("Làm Mới", "/images/reset.png", new Color(75, 85, 99));
        btn_refresh_Category.addActionListener(this::btn_refresh_CategoryActionPerformed);

        btnPanel.add(btn_create_Category);
        btnPanel.add(btn_update_Category);
        btnPanel.add(btn_delete_Category);
        btnPanel.add(btn_refresh_Category);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        panel_Category.add(formCard, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel searchIcon = new JLabel("  Tìm Kiếm Thể Loại: ");
        searchIcon.setIcon(getResourceIcon("/images/search_32.png"));
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf_search_Category = new JTextField(25);
        styleTextField(tf_search_Category);
        searchBar.add(searchIcon);
        searchBar.add(tf_search_Category);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_Category = new JTable();
        tbl_Category.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_CategoryMousePressed(evt);
            }
        });
        sp_Category = new JScrollPane(tbl_Category);
        tablePanel.add(sp_Category, BorderLayout.CENTER);

        panel_Category.add(tablePanel, BorderLayout.CENTER);
    }    private void initPublishedPanel() {
        panel_Published = new JPanel(new BorderLayout(15, 15));
        panel_Published.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Nhà Xuất Bản ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));
        formCard.setPreferredSize(new Dimension(380, 450));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tf_ID_Published = new JTextField();
        styleTextField(tf_ID_Published);
        tf_name_Published = new JTextField();
        styleTextField(tf_name_Published);

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(new JLabel("Mã NXB (ID):"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_ID_Published, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(new JLabel("Tên NXB:"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_name_Published, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        btn_create_Published = createBtn("Thêm Mới", "/images/add.png", new Color(22, 163, 74));
        btn_create_Published.addActionListener(this::btn_create_PublishedActionPerformed);

        btn_update_Published = createBtn("Cập Nhật", "/images/refresh.png", new Color(37, 99, 235));
        btn_update_Published.addActionListener(this::btn_update_PublishedActionPerformed);

        btn_delete_Published = createBtn("Xóa", "/images/bin.png", new Color(220, 38, 38));
        btn_delete_Published.addActionListener(this::btn_delete_PublishedActionPerformed);

        btn_refresh_Published = createBtn("Làm Mới", "/images/reset.png", new Color(75, 85, 99));
        btn_refresh_Published.addActionListener(this::btn_refresh_PublishedActionPerformed);

        btnPanel.add(btn_create_Published);
        btnPanel.add(btn_update_Published);
        btnPanel.add(btn_delete_Published);
        btnPanel.add(btn_refresh_Published);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        panel_Published.add(formCard, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel searchIcon = new JLabel("  Tìm Kiếm NXB: ");
        searchIcon.setIcon(getResourceIcon("/images/search_32.png"));
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf_search_Published = new JTextField(25);
        styleTextField(tf_search_Published);
        searchBar.add(searchIcon);
        searchBar.add(tf_search_Published);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_Published = new JTable();
        tbl_Published.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_PublishedMousePressed(evt);
            }
        });
        sp_Published = new JScrollPane(tbl_Published);
        tablePanel.add(sp_Published, BorderLayout.CENTER);

        panel_Published.add(tablePanel, BorderLayout.CENTER);
    }

    private void initPatronPanel() {
        panel_Patron = new JPanel(new BorderLayout(15, 15));
        panel_Patron.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1), " Thông Tin Độc Giả ",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));
        formCard.setPreferredSize(new Dimension(380, 480));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tf_ID_Patron = new JTextField();
        styleTextField(tf_ID_Patron);
        tf_fname_Patron = new JTextField();
        styleTextField(tf_fname_Patron);
        tf_lname_Patron = new JTextField();
        styleTextField(tf_lname_Patron);
        tf_email_Patron = new JTextField();
        styleTextField(tf_email_Patron);
        tf_pass_Patron = new JTextField();
        styleTextField(tf_pass_Patron);
        checkBox_Patron = new JCheckBox("Kích hoạt / Hoạt động");
        checkBox_Patron.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(new JLabel("Mã Độc Giả:"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_ID_Patron, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(new JLabel("Họ:"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_fname_Patron, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(new JLabel("Tên:"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_lname_Patron, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_email_Patron, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formCard.add(new JLabel("Mật Khẩu:"), gbc);
        gbc.gridx = 1;
        formCard.add(tf_pass_Patron, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formCard.add(new JLabel("Trạng Thái:"), gbc);
        gbc.gridx = 1;
        formCard.add(checkBox_Patron, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btn_update_Patron = createBtn("Cập Nhật", "/images/refresh.png", new Color(37, 99, 235));
        btn_update_Patron.addActionListener(this::btn_update_PatronActionPerformed);

        btn_refresh_Patron = createBtn("Làm Mới", "/images/reset.png", new Color(75, 85, 99));
        btn_refresh_Patron.addActionListener(this::btn_refresh_PatronActionPerformed);

        btn_send = createBtn("Gửi Thông Báo", "/images/enter.png", new Color(139, 92, 246));
        btn_send.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_sendMouseClicked(evt);
            }
        });

        btnPanel.add(btn_update_Patron);
        btnPanel.add(btn_refresh_Patron);
        btnPanel.add(btn_send);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        panel_Patron.add(formCard, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel searchIcon = new JLabel("  Tìm Kiếm Độc Giả: ");
        searchIcon.setIcon(getResourceIcon("/images/search_32.png"));
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf_search_Patron = new JTextField(25);
        styleTextField(tf_search_Patron);
        searchBar.add(searchIcon);
        searchBar.add(tf_search_Patron);

        tablePanel.add(searchBar, BorderLayout.NORTH);

        tbl_Patron = new JTable();
        tbl_Patron.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_PatronMousePressed(evt);
            }
        });
        sp_Patron = new JScrollPane(tbl_Patron);
        tablePanel.add(sp_Patron, BorderLayout.CENTER);

        panel_Patron.add(tablePanel, BorderLayout.CENTER);
    }

    private void initCheckoutPanel() {
        panel_Checkout = new JPanel(new BorderLayout(15, 15));
        panel_Checkout.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel searchIcon = new JLabel("  Tìm Kiếm Mượn Trả: ");
        searchIcon.setIcon(getResourceIcon("/images/search_32.png"));
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf_search_Checkout = new JTextField(25);
        styleTextField(tf_search_Checkout);

        btn_refresh_Checkout = createBtn("Làm Mới", "/images/reset.png", new Color(75, 85, 99));
        btn_refresh_Checkout.addActionListener(this::btn_refresh_CheckoutActionPerformed);

        topBar.add(searchIcon);
        topBar.add(tf_search_Checkout);
        topBar.add(btn_refresh_Checkout);

        tablePanel.add(topBar, BorderLayout.NORTH);

        tbl_Checkout = new JTable();
        tbl_Checkout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_CheckoutMousePressed(evt);
            }
        });
        sp_Checkout = new JScrollPane(tbl_Checkout);
        tablePanel.add(sp_Checkout, BorderLayout.CENTER);

        panel_Checkout.add(tablePanel, BorderLayout.CENTER);
    }

    private void initHoldPanel() {
        panel_Hold = new JPanel(new BorderLayout(15, 15));
        panel_Hold.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel searchIcon = new JLabel("  Tìm Kiếm Đặt Giữ: ");
        searchIcon.setIcon(getResourceIcon("/images/search_32.png"));
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf_search_Hold = new JTextField(25);
        styleTextField(tf_search_Hold);

        btn_refresh_Hold = createBtn("Làm Mới", "/images/reset.png", new Color(75, 85, 99));
        btn_refresh_Hold.addActionListener(this::btn_refresh_HoldActionPerformed);

        topBar.add(searchIcon);
        topBar.add(tf_search_Hold);
        topBar.add(btn_refresh_Hold);

        tablePanel.add(topBar, BorderLayout.NORTH);

        tbl_Hold = new JTable();
        tbl_Hold.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_HoldMousePressed(evt);
            }
        });
        sp_Hold = new JScrollPane(tbl_Hold);
        tablePanel.add(sp_Hold, BorderLayout.CENTER);

        panel_Hold.add(tablePanel, BorderLayout.CENTER);
    }

    private void initNotificationPanel() {
        panel_Notification = new JPanel(new BorderLayout(15, 15));
        panel_Notification.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel searchIcon = new JLabel("  Tìm Kiếm Thông Báo: ");
        searchIcon.setIcon(getResourceIcon("/images/search_32.png"));
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf_search_Notification = new JTextField(25);
        styleTextField(tf_search_Notification);

        btn_refresh_Notification = createBtn("Làm Mới", "/images/reset.png", new Color(75, 85, 99));
        btn_refresh_Notification.addActionListener(this::btn_refresh_NotificationActionPerformed);

        topBar.add(searchIcon);
        topBar.add(tf_search_Notification);
        topBar.add(btn_refresh_Notification);

        tablePanel.add(topBar, BorderLayout.NORTH);

        tbl_Notification = new JTable();
        sp_Notification = new JScrollPane(tbl_Notification);
        tablePanel.add(sp_Notification, BorderLayout.CENTER);

        panel_Notification.add(tablePanel, BorderLayout.CENTER);
    }

    private void initHistoryPanel() {
        panel_History = new JPanel(new BorderLayout(15, 15));
        panel_History.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel searchIcon = new JLabel("  Tìm Kiếm Lịch Sử Log: ");
        searchIcon.setIcon(getResourceIcon("/images/search_32.png"));
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tf_search_History = new JTextField(25);
        styleTextField(tf_search_History);

        btn_refresh_History = createBtn("Làm Mới", "/images/reset.png", new Color(75, 85, 99));
        btn_refresh_History.addActionListener(this::btn_refresh_HistoryActionPerformed);

        topBar.add(searchIcon);
        topBar.add(tf_search_History);
        topBar.add(btn_refresh_History);

        tablePanel.add(topBar, BorderLayout.NORTH);

        tbl_History = new JTable();
        sp_History = new JScrollPane(tbl_History);
        tablePanel.add(sp_History, BorderLayout.CENTER);

        panel_History.add(tablePanel, BorderLayout.CENTER);
    }         


    // Send Notification
    public void btn_sendAllActionPerformed(ActionEvent evt) {
    }

    public void btn_sendMouseClicked(java.awt.event.MouseEvent evt) {
        int selectedRow = tbl_Patron.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "You need to choose who to send to!");
            return;
        }
        String m = JOptionPane.showInputDialog(this, "What notification do you want to send?",
                "Send Notification", JOptionPane.INFORMATION_MESSAGE);
        if (m == null) {
            return;
        }
        try {
            Notification notification = new Notification();

            Date date = new Date();
            Timestamp time_now = new Timestamp(date.getTime());

            notification.setMessage(m);
            notification.setPatron_id(Integer.parseInt(tf_ID_Patron.getText()));
            notification.setSend_at(time_now);

            Response response = controller.createNotificationController(notification);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                JOptionPane.showMessageDialog(this, "Send Notify to Patron Successfully!");
            }

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    // Block

    public boolean checkBlock(String table_name, int col_id) {
        try {
            return controller.checkLog(table_name, col_id);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    // Book - done

    public void tbl_BookMousePressed(java.awt.event.MouseEvent evt) {
        int selectedRow = tbl_Book.getSelectedRow();
        tf_ID_Book.setEditable(false);
        if (selectedRow != -1) {
            table_name = "book";
            // Lấy thông tin từ hàng dữ liệu được chọn
            int bookId = (int) tbl_Book.getValueAt(selectedRow, 0);
            String title = (String) tbl_Book.getValueAt(selectedRow, 1);
            String category = (String) tbl_Book.getValueAt(selectedRow, 2);
            String author = (String) tbl_Book.getValueAt(selectedRow, 3);


            // check block
            if (checkBlock(table_name, bookId)) {
                JOptionPane.showMessageDialog(this, "Bạn không thể thao tác với bản ghi này! Có người dùng khác đang sử dụng bản ghi này!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Set vào tf
            tf_ID_Book.setText(String.valueOf(bookId));
            tf_title_Book.setText(title);

            ComboBoxModel<Category> cb_model_category = cb_category_Book.getModel();
            for (int i = 0; i < cb_model_category.getSize(); i++) {
                if (cb_model_category.getElementAt(i).toString().equals(category)) {
                    cb_model_category.setSelectedItem(cb_model_category.getElementAt(i));
                    break;
                }
            }
            ComboBoxModel<Author> cb_model_author = cb_author_Book.getModel();
            for (int i = 0; i < cb_model_author.getSize(); i++) {
                if (cb_model_author.getElementAt(i).toString().equals(author)) {
                    cb_model_author.setSelectedItem(cb_model_author.getElementAt(i));
                    break;
                }
            }
            Date date = new Date();
            Timestamp time_now = new Timestamp(date.getTime());
            if (log == null) {
                log = new Log(ip, username, table_name, bookId, time_now);
                try {
                    log.setId(controller.createLog(log));
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else
                // TH: Click vào bảng khác
                if (log.getTable_name() != table_name) {

                    try {
                        controller.deleteLog(log.getId());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    // Lấy thời gian hiện tại

                    log = new Log(ip, username, table_name, bookId, time_now);
                    try {
                        log.setId(controller.createLog(log));
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }

                } else
                    // TH: Click vào cùng bảng
                    if (log.getTable_name().equals(table_name)) {
                        log.setCol_id(bookId);
                        try {
                            controller.updateLog(log);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
            System.out.println(" LOG: " + log.toString());

        }
    }

    public void btn_refresh_BookActionPerformed(java.awt.event.ActionEvent evt) {
        tf_ID_Book.setEditable(true);

        tf_ID_Book.setText("");
        tf_title_Book.setText("");
        showTableBook();
        showDataComboBoxAuthor();
        showDataComboBoxCategory();
    }

    public void btn_delete_BookActionPerformed(java.awt.event.ActionEvent evt) {

        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sách này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {


            int book_id = Integer.parseInt(tf_ID_Book.getText());

            try {
                Response res = controller.deleteBookController(book_id);
                if (res.getStatus() == 100) {
                    JOptionPane.showMessageDialog(this, res.getData());
                } else {
                    showTableBook();
                    JOptionPane.showMessageDialog(this, res.getData());
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
        btn_refresh_BookActionPerformed(null);
    }

    public void btn_update_BookActionPerformed(java.awt.event.ActionEvent evt) {
        if (tf_ID_Book.getText() == null || tf_ID_Book.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách cần cập nhật!");
            return;
        }
        Book book = new Book();
        int book_id = Integer.parseInt(tf_ID_Book.getText().trim());
        String book_title = tf_title_Book.getText();
        Category category = (Category) cb_category_Book.getSelectedItem();
        if (category == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại sách!");
            return;
        }
        int category_id = category.getId();
        Author author = (Author) cb_author_Book.getSelectedItem();
        if (author == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tác giả!");
            return;
        }
        int author_id = author.getId();

        book.setId(book_id);
        book.setTitle(book_title);
        book.setCategory_id(category_id);

        try {
            Response response = controller.updateBookController(book, author_id);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableBook();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        btn_refresh_BookActionPerformed(null);
    }

    public void btn_create_BookActionPerformed(java.awt.event.ActionEvent evt) {
        Book book = new Book();
        String book_title = tf_title_Book.getText();
        Category category = (Category) cb_category_Book.getSelectedItem();
        if (category == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thể loại sách!");
            return;
        }
        int category_id = category.getId();
        Author author = (Author) cb_author_Book.getSelectedItem();
        if (author == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tác giả!");
            return;
        }
        int author_id = author.getId();

        book.setTitle(book_title);
        book.setCategory_id(category_id);

        try {
            Response response = controller.createBookController(book, author_id);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableBook();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        btn_refresh_BookActionPerformed(null);
    }
    // Author

    public void tbl_AuthorMousePressed(java.awt.event.MouseEvent evt) {
        int selectedRow = tbl_Author.getSelectedRow();
        tf_ID_Author.setEditable(false);
        if (selectedRow != -1) {
            table_name = "author";
            // Lấy thông tin từ hàng dữ liệu được chọn
            int author_id = (int) tbl_Author.getValueAt(selectedRow, 0);
            String name = (String) tbl_Author.getValueAt(selectedRow, 1);


            // check block
            if (checkBlock(table_name, author_id)) {
                JOptionPane.showMessageDialog(this, "Bạn không thể thao tác với bản ghi này! Có người dùng khác đang sử dụng bản ghi này!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Set vào tf
            tf_ID_Author.setText(String.valueOf(author_id));
            tf_name_Author.setText(name);

            Date date = new Date();
            Timestamp time_now = new Timestamp(date.getTime());
            if (log == null) {
                log = new Log(ip, username, table_name, author_id, time_now);
                try {
                    log.setId(controller.createLog(log));
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else
                // TH: Click vào bảng khác
                if (log.getTable_name() != table_name) {

                    try {
                        controller.deleteLog(log.getId());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    // Lấy thời gian hiện tại

                    log = new Log(ip, username, table_name, author_id, time_now);
                    try {
                        log.setId(controller.createLog(log));
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }

                } else
                    // TH: Click vào cùng bảng
                    if (log.getTable_name().equals(table_name)) {
                        log.setCol_id(author_id);
                        try {
                            controller.updateLog(log);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
            System.out.println(" LOG: " + log.toString());

        }
    }

    public void btn_refresh_AuthorActionPerformed(java.awt.event.ActionEvent evt) {
        tf_ID_Author.setEditable(true);

        tf_ID_Author.setText("");
        tf_name_Author.setText("");
        showTableAuthor();
    }

    public void btn_delete_AuthorActionPerformed(java.awt.event.ActionEvent evt) {
        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {


            int author_id = Integer.parseInt(tf_ID_Author.getText());

            try {
                Response res = controller.deleteAuthorController(author_id);
                if (res.getStatus() == 100) {
                    JOptionPane.showMessageDialog(this, res.getData());
                } else {
                    showTableBook();
                    JOptionPane.showMessageDialog(this, res.getData());
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
        btn_refresh_BookActionPerformed(null);
    }

    public void btn_update_AuthorActionPerformed(java.awt.event.ActionEvent evt) {
        Author author = new Author();
        if (tf_ID_Author.getText() != null) {
            int author_id = Integer.parseInt(tf_ID_Author.getText());
            author.setId(author_id);
        }
        String author_name = tf_name_Author.getText();
        author.setName(author_name);

        try {
            Response response = controller.updateAuthorController(author);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableAuthor();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        btn_refresh_BookActionPerformed(null);
    }

    public void btn_create_AuthorActionPerformed(java.awt.event.ActionEvent evt) {
        Author author = new Author();
        if (!tf_ID_Author.getText().isEmpty()) {
            int author_id = Integer.parseInt(tf_ID_Author.getText());
            author.setId(author_id);
        }
        String name = tf_name_Author.getText();
        author.setName(name);

        try {
            Response response = controller.createAuthorController(author);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableAuthor();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        btn_refresh_AuthorActionPerformed(null);
    }
    // Category

    public void tbl_CategoryMousePressed(java.awt.event.MouseEvent evt) {
        int selectedRow = tbl_Category.getSelectedRow();
        tf_ID_Category.setEditable(false);
        if (selectedRow != -1) {
            table_name = "category";
            // Lấy thông tin từ hàng dữ liệu được chọn
            int category_id = (int) tbl_Category.getValueAt(selectedRow, 0);
            String name = (String) tbl_Category.getValueAt(selectedRow, 1);


            // check block
            if (checkBlock(table_name, category_id)) {
                JOptionPane.showMessageDialog(this, "Bạn không thể thao tác với bản ghi này! Có người dùng khác đang sử dụng bản ghi này!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Set vào tf
            tf_ID_Category.setText(String.valueOf(category_id));
            tf_name_Category.setText(name);

            Date date = new Date();
            Timestamp time_now = new Timestamp(date.getTime());
            if (log == null) {
                log = new Log(ip, username, table_name, category_id, time_now);
                try {
                    log.setId(controller.createLog(log));
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else
                // TH: Click vào bảng khác
                if (log.getTable_name() != table_name) {

                    try {
                        controller.deleteLog(log.getId());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    // Lấy thời gian hiện tại

                    log = new Log(ip, username, table_name, category_id, time_now);
                    try {
                        log.setId(controller.createLog(log));
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }

                } else
                    // TH: Click vào cùng bảng
                    if (log.getTable_name().equals(table_name)) {
                        log.setCol_id(category_id);
                        try {
                            controller.updateLog(log);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
            System.out.println(" LOG: " + log.toString());

        }
    }

    public void btn_refresh_CategoryActionPerformed(java.awt.event.ActionEvent evt) {
        tf_ID_Category.setEditable(true);

        tf_ID_Category.setText("");
        tf_name_Category.setText("");
        showTableCategory();
    }

    public void btn_delete_CategoryActionPerformed(java.awt.event.ActionEvent evt) {
        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {


            int category_id = Integer.parseInt(tf_ID_Category.getText());

            try {
                Response res = controller.deleteCategoryController(category_id);
                if (res.getStatus() == 100) {
                    JOptionPane.showMessageDialog(this, res.getData());
                } else {
                    showTableCategory();
                    JOptionPane.showMessageDialog(this, res.getData());
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
        btn_refresh_CategoryActionPerformed(null);
    }

    public void btn_update_CategoryActionPerformed(java.awt.event.ActionEvent evt) {
        Category category = new Category();
        if (tf_ID_Category.getText() != null) {
            int category_id = Integer.parseInt(tf_ID_Category.getText());
            category.setId(category_id);
        }
        String category_name = tf_name_Category.getText();
        category.setName(category_name);

        try {
            Response response = controller.updateCategoryController(category);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableCategory();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        btn_refresh_BookActionPerformed(null);
    }

    public void btn_create_CategoryActionPerformed(java.awt.event.ActionEvent evt) {
        Category category = new Category();
        if (!tf_ID_Category.getText().isEmpty()) {
            int category_id = Integer.parseInt(tf_ID_Category.getText());
            category.setId(category_id);
        }
        String name = tf_name_Category.getText();
        category.setName(name);

        try {
            Response response = controller.createCategoryController(category);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableCategory();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        btn_refresh_CategoryActionPerformed(null);
    }

    // Patron Account
    public void tbl_PatronMousePressed(java.awt.event.MouseEvent evt) {
        int selectedRow = tbl_Patron.getSelectedRow();
        tf_ID_Patron.setEditable(false);
        if (selectedRow != -1) {
            table_name = "patron_account";
            // Lấy thông tin từ hàng dữ liệu được chọn
            int author_id = (int) tbl_Patron.getValueAt(selectedRow, 0);
            String fname = (String) tbl_Patron.getValueAt(selectedRow, 1);
            String lname = (String) tbl_Patron.getValueAt(selectedRow, 2);
            String email = (String) tbl_Patron.getValueAt(selectedRow, 3);
            String status = (String) tbl_Patron.getValueAt(selectedRow, 4);


            // check block
            if (checkBlock(table_name, author_id)) {
                JOptionPane.showMessageDialog(this, "Bạn không thể thao tác với bản ghi này! Có người dùng khác đang sử dụng bản ghi này!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Set vào tf
            tf_ID_Patron.setText(String.valueOf(author_id));
            tf_fname_Patron.setText(fname);
            tf_lname_Patron.setText(lname);
            tf_email_Patron.setText(email);
            if (status.equals("Available")) {
                checkBox_Patron.setEnabled(true);
            } else {
                checkBox_Patron.setEnabled(false);
            }

            Date date = new Date();
            Timestamp time_now = new Timestamp(date.getTime());
            if (log == null) {
                log = new Log(ip, username, table_name, author_id, time_now);
                try {
                    log.setId(controller.createLog(log));
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else
                // TH: Click vào bảng khác
                if (log.getTable_name() != table_name) {

                    try {
                        controller.deleteLog(log.getId());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    // Lấy thời gian hiện tại

                    log = new Log(ip, username, table_name, author_id, time_now);
                    try {
                        log.setId(controller.createLog(log));
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }

                } else
                    // TH: Click vào cùng bảng
                    if (log.getTable_name().equals(table_name)) {
                        log.setCol_id(author_id);
                        try {
                            controller.updateLog(log);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
            System.out.println(" LOG: " + log.toString());

        }
    }

    public void btn_refresh_PatronActionPerformed(java.awt.event.ActionEvent evt) {
        tf_ID_Patron.setEditable(true);

        tf_ID_Patron.setText("");
        tf_fname_Patron.setText("");
        tf_lname_Patron.setText("");
        tf_email_Patron.setText("");
        tf_pass_Patron.setText("");

        showTableNotification();
    }

    public void btn_delete_PatronActionPerformed(java.awt.event.ActionEvent evt) {
        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {


            int patron_id = Integer.parseInt(tf_ID_Patron.getText());

            try {
                Response res = controller.deleteControllerPatron(patron_id);
                if (res.getStatus() == 100) {
                    JOptionPane.showMessageDialog(this, res.getData());
                } else {
                    showTablePatrons();
                    JOptionPane.showMessageDialog(this, res.getData());
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
        btn_refresh_PatronActionPerformed(null);
    }


    public void btn_update_PatronActionPerformed(java.awt.event.ActionEvent evt) {
        Patron patron = new Patron();
        int patron_id = Integer.parseInt(tf_ID_Patron.getText());
        String patron_fname = tf_fname_Patron.getText();
        String patron_lname =  tf_lname_Patron.getText();
        String patron_email = tf_email_Patron.getText();




        patron.setId(patron_id);
        patron.setFirstName(patron_fname);
        patron.setLastName(patron_lname);
        patron.setEmail(patron_email);




        try {
                Response response = controller.updatePatronController(patron);
                if (response.getStatus() == 100) {
                    JOptionPane.showMessageDialog(this, response.getData());
                } else {
                    showTablePatrons();
                    JOptionPane.showMessageDialog(this, response.getData());
                }

            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            btn_refresh_PatronActionPerformed(null);


    }

    public void btn_create_PatronActionPerformed(java.awt.event.ActionEvent evt) {
        Patron patron = new Patron();
        if (!tf_ID_Patron.getText().isEmpty()) {
            int patron_id = Integer.parseInt(tf_ID_Patron.getText());
            patron.setId(patron_id);
        }
        String patron_fname = tf_fname_Patron.getText();
        String patron_lname =  tf_lname_Patron.getText();
        String patron_email = tf_email_Patron.getText();
        String patron_pass  = tf_pass_Patron.getText();


        patron.setFirstName(patron_fname);
        patron.setLastName(patron_lname);
        patron.setEmail(patron_email);
        patron.setPassword(patron_pass);

        try {
            Response response = controller.createPatronController(patron);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTablePatrons();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        btn_refresh_PatronActionPerformed(null);
    }


    // Hold
    public void tbl_HoldMousePressed(java.awt.event.MouseEvent evt) {
        int selectedRow = tbl_Hold.getSelectedRow();
        tf_ID_Hold.setEditable(false);
        if (selectedRow != -1) {
            table_name = "hold";
            // Lấy thông tin từ hàng dữ liệu được chọn
            int hold_id = (int) tbl_Hold.getValueAt(selectedRow, 0);
            String patron = (String) tbl_Hold.getValueAt(selectedRow, 1);
            String book_copy = (String) tbl_Hold.getValueAt(selectedRow, 3);
            Timestamp time_start = (Timestamp) tbl_Hold.getValueAt(selectedRow, 4);
            Timestamp time_end = (Timestamp) tbl_Hold.getValueAt(selectedRow, 5);

            // check block
            if (checkBlock(table_name, hold_id)) {
                JOptionPane.showMessageDialog(this, "Bạn không thể thao tác với bản ghi này! Có người dùng khác đang sử dụng bản ghi này!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Set vào tf
            tf_ID_Hold.setText(String.valueOf(hold_id));
            tf_start_Hold.setText(String.valueOf(time_start));
            tf_end_Hold.setText(String.valueOf(time_end));

            ComboBoxModel<BookCopy> cb_model_bookcopy = cb_book_Hold.getModel();
            for (int i = 0; i < cb_model_bookcopy.getSize(); i++) {
                if (cb_model_bookcopy.getElementAt(i).toString().equals(book_copy)) {
                    cb_model_bookcopy.setSelectedItem(cb_model_bookcopy.getElementAt(i));
                    break;
                }
            }
            ComboBoxModel<Patron> cb_model_patron = cb_patron_Hold.getModel();
            for (int i = 0; i < cb_model_patron.getSize(); i++) {
                if (cb_model_patron.getElementAt(i).toString().equals(patron)) {
                    cb_model_patron.setSelectedItem(cb_model_patron.getElementAt(i));
                    break;
                }
            }

            Date date = new Date();
            Timestamp time_now = new Timestamp(date.getTime());
            if (log == null) {
                log = new Log(ip, username, table_name, hold_id, time_now);
                try {
                    log.setId(controller.createLog(log));
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else
                // TH: Click vào bảng khác
                if (log.getTable_name() != table_name) {

                    try {
                        controller.deleteLog(log.getId());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    // Lấy thời gian hiện tại

                    log = new Log(ip, username, table_name, hold_id, time_now);
                    try {
                        log.setId(controller.createLog(log));
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }

                } else
                    // TH: Click vào cùng bảng
                    if (log.getTable_name().equals(table_name)) {
                        log.setCol_id(hold_id);
                        try {
                            controller.updateLog(log);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
            System.out.println(" LOG: " + log.toString());

        }
    }

    public void btn_create_HoldActionPerformed(java.awt.event.ActionEvent evt) {
        Hold hold = new Hold();
        String start = tf_start_Hold.getText();
        Timestamp timestamp_start = Timestamp.valueOf(start);
        String end = tf_end_Hold.getText();
        Timestamp timestamp_end = Timestamp.valueOf(end);
        BookCopy bookCopy = (BookCopy) cb_book_Hold.getSelectedItem();
        int bookCopyId = bookCopy.getId();
        Patron patron = (Patron) cb_patron_Hold.getSelectedItem();
        int patronId = patron.getId();

        hold.setStart_time(timestamp_start);
        hold.setEnd_time(timestamp_end);
        hold.setBook_copy_id(bookCopyId);
        hold.setPatron_id(patronId);

        try {
            Response response = controller.createHoldController(hold);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableBook();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        btn_refresh_HoldActionPerformed(null);
    }

    public void btn_refresh_HoldActionPerformed(java.awt.event.ActionEvent evt) {
        tf_ID_Hold.setEditable(true);
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp now = new Timestamp(currentTimeMillis);
        tf_ID_Hold.setText("");
        tf_start_Hold.setText(now.toString());
        tf_end_Hold.setText(now.toString());
        showTableHold();
    }

    public void btn_delete_HoldActionPerformed(java.awt.event.ActionEvent evt) {
        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phiếu mượn này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {


            int holdId = Integer.parseInt(tf_ID_Hold.getText());

            try {
                Response res = controller.deleteHoldController(holdId);
                if (res.getStatus() == 100) {
                    JOptionPane.showMessageDialog(this, res.getData());
                } else {
                    showTableBook();
                    JOptionPane.showMessageDialog(this, res.getData());
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
        btn_refresh_HoldActionPerformed(null);
    }

    public void btn_update_HoldActionPerformed(java.awt.event.ActionEvent evt) {
        Hold hold = new Hold();
        int holdId = Integer.parseInt(tf_ID_Hold.getText());
        hold.setId(holdId);
        String start = tf_start_Hold.getText();
        Timestamp timestamp_start = Timestamp.valueOf(start);
        String end = tf_end_Hold.getText();
        Timestamp timestamp_end = Timestamp.valueOf(end);
        BookCopy bookCopy = (BookCopy) cb_book_Hold.getSelectedItem();
        int bookCopyId = bookCopy.getId();
        Patron patron = (Patron) cb_patron_Hold.getSelectedItem();
        int patronId = patron.getId();

        hold.setStart_time(timestamp_start);
        hold.setEnd_time(timestamp_end);
        hold.setBook_copy_id(bookCopyId);
        hold.setPatron_id(patronId);

        try {
            Response response = controller.updateHoldController(hold);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableBook();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        btn_refresh_HoldActionPerformed(null);
    }


    // Published
    public void tbl_PublishedMousePressed(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:

    }

    public void btn_refresh_PublishedActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    public void btn_delete_PublishedActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    public void btn_update_PublishedActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    public void btn_create_PublishedActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    // Checkout

    public void tbl_CheckoutMousePressed(java.awt.event.MouseEvent evt) {
        int selectedRow = tbl_Checkout.getSelectedRow();
        tf_ID_Checkout.setEditable(false);
        if (selectedRow != -1) {
            table_name = "checkout";
            // Lấy thông tin từ hàng dữ liệu được chọn
            int checkout_id = (int) tbl_Checkout.getValueAt(selectedRow, 0);
            String patron = (String) tbl_Checkout.getValueAt(selectedRow, 1);
            String book_copy = (String) tbl_Checkout.getValueAt(selectedRow, 3);
            Timestamp time_start = (Timestamp) tbl_Checkout.getValueAt(selectedRow, 4);
            Timestamp time_end = (Timestamp) tbl_Checkout.getValueAt(selectedRow, 5);
            String status = (String) tbl_Checkout.getValueAt(selectedRow, 6);

            if (status.equals("Yes")) {
                checkBox_Checkout.setSelected(true);
            }
            else {
                checkBox_Checkout.setSelected(false);
            }

            // check block
            if (checkBlock(table_name, checkout_id)) {
                JOptionPane.showMessageDialog(this, "Bạn không thể thao tác với bản ghi này! Có người dùng khác đang sử dụng bản ghi này!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Set vào tf
            tf_ID_Checkout.setText(String.valueOf(checkout_id));
            tf_start_Checkout.setText(String.valueOf(time_start));
            tf_end_Checkout.setText(String.valueOf(time_end));

            ComboBoxModel<BookCopy> cb_model_bookcopy = cb_book_Checkout.getModel();
            for (int i = 0; i < cb_model_bookcopy.getSize(); i++) {
                if (cb_model_bookcopy.getElementAt(i).toString().equals(book_copy)) {
                    cb_model_bookcopy.setSelectedItem(cb_model_bookcopy.getElementAt(i));
                    break;
                }
            }
            ComboBoxModel<Patron> cb_model_patron = cb_patron_Checkout.getModel();
            for (int i = 0; i < cb_model_patron.getSize(); i++) {
                if (cb_model_patron.getElementAt(i).toString().equals(patron)) {
                    cb_model_patron.setSelectedItem(cb_model_patron.getElementAt(i));
                    break;
                }
            }

            Date date = new Date();
            Timestamp time_now = new Timestamp(date.getTime());
            if (log == null) {
                log = new Log(ip, username, table_name, checkout_id, time_now);
                try {
                    log.setId(controller.createLog(log));
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else
                // TH: Click vào bảng khác
                if (log.getTable_name() != table_name) {

                    try {
                        controller.deleteLog(log.getId());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    // Lấy thời gian hiện tại

                    log = new Log(ip, username, table_name, checkout_id, time_now);
                    try {
                        log.setId(controller.createLog(log));
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }

                } else
                    // TH: Click vào cùng bảng
                    if (log.getTable_name().equals(table_name)) {
                        log.setCol_id(checkout_id);
                        try {
                            controller.updateLog(log);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
            System.out.println(" LOG: " + log.toString());

        }
    }

    public void btn_create_CheckoutActionPerformed(java.awt.event.ActionEvent evt) {
        Checkout checkout = new Checkout();
        String start = tf_start_Checkout.getText();
        Timestamp timestamp_start = Timestamp.valueOf(start);
        String end = tf_end_Checkout.getText();
        Timestamp timestamp_end = Timestamp.valueOf(end);
        BookCopy bookCopy = (BookCopy) cb_book_Checkout.getSelectedItem();
        int bookCopyId = bookCopy.getId();
        Patron patron = (Patron) cb_patron_Checkout.getSelectedItem();
        int patronId = patron.getId();

        checkout.setStart_time(timestamp_start);
        checkout.setEnd_time(timestamp_end);
        checkout.setBook_copy_id(bookCopyId);
        checkout.setPatron_id(patronId);

        try {
            Response response = controller.createCheckoutController(checkout);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableCheckout();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        btn_refresh_CheckoutActionPerformed(null);
    }

    public void btn_update_CheckoutActionPerformed(java.awt.event.ActionEvent evt) {
        Checkout checkout = new Checkout();
        int checkoutId = Integer.parseInt(tf_ID_Checkout.getText());
        checkout.setId(checkoutId);
        String start = tf_start_Checkout.getText();
        Timestamp timestamp_start = Timestamp.valueOf(start);
        String end = tf_end_Checkout.getText();
        Timestamp timestamp_end = Timestamp.valueOf(end);
        BookCopy bookCopy = (BookCopy) cb_book_Checkout.getSelectedItem();
        int bookCopyId = bookCopy.getId();
        Patron patron = (Patron) cb_patron_Checkout.getSelectedItem();
        int patronId = patron.getId();
        Boolean status = checkBox_Checkout.isSelected();


        checkout.setStart_time(timestamp_start);
        checkout.setEnd_time(timestamp_end);
        checkout.setBook_copy_id(bookCopyId);
        checkout.setPatron_id(patronId);
        checkout.setIs_returned(status);

        try {
            Response response = controller.updateCheckoutController(checkout);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableCheckout();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        btn_refresh_CheckoutActionPerformed(null);
    }

    public void btn_delete_CheckoutActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sách này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {


            int checkoutId = Integer.parseInt(tf_ID_Checkout.getText());

            try {
                Response res = controller.deleteCheckoutController(checkoutId);
                if (res.getStatus() == 100) {
                    JOptionPane.showMessageDialog(this, res.getData());
                } else {
                    showTableCheckout();
                    JOptionPane.showMessageDialog(this, res.getData());
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
        btn_refresh_CheckoutActionPerformed(null);
    }

    public void btn_refresh_CheckoutActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        tf_ID_Checkout.setEditable(true);
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp now = new Timestamp(currentTimeMillis);
        tf_ID_Checkout.setText("");
        tf_start_Checkout.setText(now.toString());
        tf_end_Checkout.setText(now.toString());
        showTableCheckout();
    }

    // Book Copy
    public void tbl_BookCopyMousePressed(java.awt.event.MouseEvent evt) {
        int selectedRow = tbl_BookCopy.getSelectedRow();
        tf_ID_BookCopy.setEditable(false);
        if (selectedRow != -1) {
            table_name = "book_copy";
            // Lấy thông tin từ hàng dữ liệu được chọn
            int book_copy_id = (int) tbl_BookCopy.getValueAt(selectedRow, 0);
            String book = (String) tbl_BookCopy.getValueAt(selectedRow, 1);
            int year_publish = (int) tbl_BookCopy.getValueAt(selectedRow, 2);
            String published = (String) tbl_BookCopy.getValueAt(selectedRow, 3);

            // check block
            if (checkBlock(table_name, book_copy_id)) {
                JOptionPane.showMessageDialog(this, "Bạn không thể thao tác với bản ghi này! Có người dùng khác đang sử dụng bản ghi này!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Set vào tf
            tf_ID_BookCopy.setText(String.valueOf(book_copy_id));
            tf_year_BookCopy.setText(String.valueOf(year_publish));

            ComboBoxModel<Book> cb_model_book = cb_book_BookCopy.getModel();
            for (int i = 0; i < cb_model_book.getSize(); i++) {
                if (cb_model_book.getElementAt(i).toString().equals(book)) {
                    cb_model_book.setSelectedItem(cb_model_book.getElementAt(i));
                    break;
                }
            }
            ComboBoxModel<Published> cb_model_published = cb_published_BookCopy.getModel();
            for (int i = 0; i < cb_model_published.getSize(); i++) {
                if (cb_model_published.getElementAt(i).toString().equals(published)) {
                    cb_model_published.setSelectedItem(cb_model_published.getElementAt(i));
                    break;
                }
            }

            Date date = new Date();
            Timestamp time_now = new Timestamp(date.getTime());
            if (log == null) {
                log = new Log(ip, username, table_name, book_copy_id, time_now);
                try {
                    log.setId(controller.createLog(log));
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else
                // TH: Click vào bảng khác
                if (log.getTable_name() != table_name) {

                    try {
                        controller.deleteLog(log.getId());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    // Lấy thời gian hiện tại

                    log = new Log(ip, username, table_name, book_copy_id, time_now);
                    try {
                        log.setId(controller.createLog(log));
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }

                } else
                    // TH: Click vào cùng bảng
                    if (log.getTable_name().equals(table_name)) {
                        log.setCol_id(book_copy_id);
                        try {
                            controller.updateLog(log);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
            System.out.println(" LOG: " + log.toString());

        }
    }

    public void btn_create_BookCopyActionPerformed(java.awt.event.ActionEvent evt) {
        Book book = (Book) cb_book_BookCopy.getSelectedItem();
        int book_id = book.getId();
        Published published = (Published) cb_published_BookCopy.getSelectedItem();
        int published_id = published.getId();
        int year = Integer.parseInt(tf_year_BookCopy.getText());
        BookCopy bookCopy = new BookCopy();
        bookCopy.setPublished_id(published_id);
        bookCopy.setBook_id(book_id);
        bookCopy.setYear_published(year);
        try {
            Response response = controller.createBookCopyController(bookCopy);
            if (response.getStatus() == 100) {
                JOptionPane.showMessageDialog(this, response.getData());
            } else {
                showTableBookCopy();
                JOptionPane.showMessageDialog(this, response.getData());
            }

        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        btn_refresh_BookActionPerformed(null);
    }

    public void btn_delete_BookCopyActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    public void btn_update_BookCopyActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    public void btn_refresh_BookCopyActionPerformed(java.awt.event.ActionEvent evt) {
        tf_ID_BookCopy.setEditable(true);
        tf_ID_BookCopy.setText("");
        tf_year_BookCopy.setText("");
        showTableBookCopy();
    }


    //
    public void btn_refresh_HistoryActionPerformed(ActionEvent evt) {
        showTableHistory();
    }

    public void btn_refresh_NotificationActionPerformed(ActionEvent evt) {
        showTableNotification();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ManageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btn_create_Author;
    private javax.swing.JButton btn_create_Book;
    private javax.swing.JButton btn_create_BookCopy;
    private javax.swing.JButton btn_create_Category;
    private javax.swing.JButton btn_create_Checkout;
    private javax.swing.JButton btn_create_Hold;
    private javax.swing.JButton btn_create_Patron;
    private javax.swing.JButton btn_create_Published;
    private javax.swing.JButton btn_delete_Author;
    private javax.swing.JButton btn_delete_Book;
    private javax.swing.JButton btn_delete_BookCopy;
    private javax.swing.JButton btn_delete_Category;
    private javax.swing.JButton btn_delete_Checkout;
    private javax.swing.JButton btn_delete_Hold;
    private javax.swing.JButton btn_delete_Patron;
    private javax.swing.JButton btn_delete_Published;
    private javax.swing.JButton btn_refresh_Author;
    private javax.swing.JButton btn_refresh_Book;
    private javax.swing.JButton btn_refresh_BookCopy;
    private javax.swing.JButton btn_refresh_Category;
    private javax.swing.JButton btn_refresh_Checkout;
    private javax.swing.JButton btn_refresh_History;
    private javax.swing.JButton btn_refresh_Hold;
    private javax.swing.JButton btn_refresh_Notification;
    private javax.swing.JButton btn_refresh_Patron;
    private javax.swing.JButton btn_refresh_Published;
    private javax.swing.JButton btn_send;
    private javax.swing.JButton btn_sendAll;
    private javax.swing.JButton btn_update_Author;
    private javax.swing.JButton btn_update_Book;
    private javax.swing.JButton btn_update_BookCopy;
    private javax.swing.JButton btn_update_Category;
    private javax.swing.JButton btn_update_Checkout;
    private javax.swing.JButton btn_update_Hold;
    private javax.swing.JButton btn_update_Patron;
    private javax.swing.JButton btn_update_Published;
    private javax.swing.JComboBox cb_author_Book;
    private javax.swing.JComboBox cb_book_BookCopy;
    private javax.swing.JComboBox cb_book_Checkout;
    private javax.swing.JComboBox cb_book_Hold;
    private javax.swing.JComboBox cb_category_Book;
    private javax.swing.JComboBox cb_patron_Checkout;
    private javax.swing.JComboBox cb_patron_Hold;
    private javax.swing.JComboBox cb_published_BookCopy;
    private javax.swing.JCheckBox checkBox_Checkout;
    private javax.swing.JCheckBox checkBox_Patron;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane main_panel;
    private javax.swing.JPanel panel_Author;
    private javax.swing.JPanel panel_Book;
    private javax.swing.JPanel panel_BookCopy;
    private javax.swing.JPanel panel_Category;
    private javax.swing.JPanel panel_Checkout;
    private javax.swing.JPanel panel_History;
    private javax.swing.JPanel panel_Hold;
    private javax.swing.JPanel panel_Notification;
    private javax.swing.JPanel panel_Patron;
    private javax.swing.JPanel panel_Published;
    private javax.swing.JPanel panel_Setting;
    private javax.swing.JPanel panel_Statistics;
    private javax.swing.JScrollPane sp_Author;
    private javax.swing.JScrollPane sp_Book;
    private javax.swing.JScrollPane sp_BookCopy;
    private javax.swing.JScrollPane sp_Category;
    private javax.swing.JScrollPane sp_Checkout;
    private javax.swing.JScrollPane sp_History;
    private javax.swing.JScrollPane sp_Hold;
    private javax.swing.JScrollPane sp_Notification;
    private javax.swing.JScrollPane sp_Patron;
    private javax.swing.JScrollPane sp_Published;
    private javax.swing.JTable tbl_Author;
    private javax.swing.JTable tbl_Book;
    private javax.swing.JTable tbl_BookCopy;
    private javax.swing.JTable tbl_Category;
    private javax.swing.JTable tbl_Checkout;
    private javax.swing.JTable tbl_History;
    private javax.swing.JTable tbl_Hold;
    private javax.swing.JTable tbl_Notification;
    private javax.swing.JTable tbl_Patron;
    private javax.swing.JTable tbl_Published;
    private javax.swing.JTextField tf_ID_Author;
    private javax.swing.JTextField tf_ID_Book;
    private javax.swing.JTextField tf_ID_BookCopy;
    private javax.swing.JTextField tf_ID_Category;
    private javax.swing.JTextField tf_ID_Checkout;
    private javax.swing.JTextField tf_ID_Hold;
    private javax.swing.JTextField tf_ID_Patron;
    private javax.swing.JTextField tf_ID_Published;
    private javax.swing.JTextField tf_email_Patron;
    private javax.swing.JTextField tf_end_Checkout;
    private javax.swing.JTextField tf_end_Hold;
    private javax.swing.JTextField tf_fname_Patron;
    private javax.swing.JTextField tf_lname_Patron;
    private javax.swing.JTextField tf_name_Author;
    private javax.swing.JTextField tf_name_Category;
    private javax.swing.JTextField tf_name_Published;
    private javax.swing.JTextField tf_pass_Patron;
    private javax.swing.JTextField tf_search_Author;
    private javax.swing.JTextField tf_search_Book;
    private javax.swing.JTextField tf_search_BookCopy;
    private javax.swing.JTextField tf_search_Category;
    private javax.swing.JTextField tf_search_Checkout;
    private javax.swing.JTextField tf_search_History;
    private javax.swing.JTextField tf_search_Hold;
    private javax.swing.JTextField tf_search_Notification;
    private javax.swing.JTextField tf_search_Patron;
    private javax.swing.JTextField tf_search_Published;
    private javax.swing.JTextField tf_start_Checkout;
    private javax.swing.JTextField tf_start_Hold;
    private javax.swing.JTextField tf_title_Book;
    private javax.swing.JTextField tf_year_BookCopy;


    // End of variables declaration                   
}
