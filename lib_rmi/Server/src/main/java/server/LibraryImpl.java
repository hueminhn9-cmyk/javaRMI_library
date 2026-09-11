package server;

import common.*;

import javax.swing.table.DefaultTableModel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

public class LibraryImpl extends UnicastRemoteObject implements LibraryRemote {
    private InetAddress localhost;
    private String ipAddress;
    private Connection conn;
    private Statement stm;
    private PreparedStatement pst;
    private ResultSet rst;
    private Vector vTitle = new Vector();
    private Vector vData = new Vector();
    private Vector clientList;

    public LibraryImpl() throws SQLException, RemoteException, UnknownHostException {
        super();
        try {
            localhost = InetAddress.getLocalHost();
            ipAddress = localhost.getHostAddress();
        } catch (Exception e) {
            ipAddress = "127.0.0.1";
        }
        conn = new DBConnection().getConnect();
        if (conn != null) {
            stm = conn.createStatement();
        }
        clientList = new Vector();
    }

    private void checkConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = new DBConnection().getConnect();
            if (conn != null) {
                stm = conn.createStatement();
            }
        }
    }

    @Override
    public synchronized void registerForCallback(ClientInterface callbackClientObject) throws RemoteException {
        if (!(clientList.contains(callbackClientObject))) {
            clientList.addElement(callbackClientObject);
            System.out.println(">> Registered new RMI Client callback successfully.");
        }
    }

    @Override
    public synchronized void unregisterForCallback(ClientInterface callbackClientObject) throws RemoteException {
        if (clientList.removeElement(callbackClientObject)) {
            System.out.println(">> Unregistered RMI Client callback successfully.");
        } else {
            System.out.println(">> Unregister warning: Client callback was not registered.");
        }
    }

    private synchronized void doCallbacks(NOTIFY notify) throws RemoteException {
        System.out.println(">> Initiating real-time callbacks: " + notify.toString());
        deleteAllLog(true);
        for (int i = 0; i < clientList.size(); i++) {
            try {
                ClientInterface nextClient = (ClientInterface) clientList.elementAt(i);
                nextClient.notify(notify);
                System.out.println("   [Callback Sent -> Client " + (i + 1) + "]: " + notify.toString());
            } catch (Exception e) {
                System.err.println("   Failed to deliver callback to client " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    // ========================================================
    // Manage Views / Table Models
    // ========================================================

    @Override
    public Response getBooks() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT book.id, book.title, category.name AS category, author.name AS author " +
                    "FROM book " +
                    "LEFT JOIN book_author ON book.id = book_author.book_id " +
                    "LEFT JOIN author ON book_author.author_id = author.id " +
                    "LEFT JOIN category ON book.category_id = category.id ORDER BY book.id";

            rst = stm.executeQuery(query);

            String[] title = new String[]{"Book ID", "Book Title", "Category", "Author"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getString("title"));
                row.add(rst.getString("category"));
                row.add(rst.getString("author"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getBooks: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response getBooksForSearch() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT book_copy.id, book.title, category.name AS 'category', " +
                    "author.name AS 'author', published.name AS 'published', book_copy.year_published " +
                    "FROM book " +
                    "LEFT JOIN book_author ON book.id = book_author.book_id " +
                    "LEFT JOIN author ON book_author.author_id = author.id " +
                    "LEFT JOIN category ON book.category_id = category.id " +
                    "INNER JOIN book_copy ON book.id = book_copy.book_id " +
                    "INNER JOIN published ON book_copy.published_id = published.id";

            rst = stm.executeQuery(query);

            String[] title = new String[]{"ID", "Book Title", "Category", "Author", "Published", "Year"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getString("title"));
                row.add(rst.getString("category"));
                row.add(rst.getString("author"));
                row.add(rst.getString("published"));
                row.add(rst.getString("year_published"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getBooksForSearch: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response getCheckoutsClient(int patron_id) throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT c.id, b.title AS book_title, c.start_time AS borrowed_date, " +
                    "c.end_time AS returned_date, c.is_returned AS borrow_status " +
                    "FROM checkout c " +
                    "INNER JOIN book_copy bc ON c.book_copy_id = bc.id " +
                    "INNER JOIN book b ON bc.book_id = b.id " +
                    "WHERE c.patron_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, patron_id);
            rst = pst.executeQuery();

            String[] title = new String[]{"ID", "Book Title", "Borrowed Date", "Returned Date", "Status"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getString("book_title"));
                row.add(rst.getString("borrowed_date"));
                row.add(rst.getString("returned_date"));
                row.add(rst.getBoolean("borrow_status") ? "Returned" : "Borrowing");
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getCheckoutsClient: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response getAuthors() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT a.id, a.name FROM author a";
            rst = stm.executeQuery(query);

            String[] title = new String[]{"Author ID", "Author Name"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getString("name"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getAuthors: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response getCategories() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT c.id, c.name FROM category c";
            rst = stm.executeQuery(query);

            String[] title = new String[]{"Category ID", "Category Name"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getString("name"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getCategories: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response getPublished() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT p.id, p.name FROM published p";
            rst = stm.executeQuery(query);

            String[] title = new String[]{"Published ID", "Published Name"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getString("name"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getPublished: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response getBooksCopy() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT bc.id, b.title, bc.year_published, p.name " +
                    "FROM book_copy bc " +
                    "INNER JOIN book b ON bc.book_id = b.id " +
                    "INNER JOIN published p ON bc.published_id = p.id";

            rst = stm.executeQuery(query);

            String[] title = new String[]{"Book Copy ID", "Book Title", "Year Public", "Published Name"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getString("title"));
                row.add(rst.getInt("year_published"));
                row.add(rst.getString("name"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getBooksCopy: " + e);
            return new Response(100, e.toString());
        }
    }

    @Override
    public Response getHolds() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT h.id, pa.email, CONCAT(pa.first_name, ' ', pa.last_name) AS fullname, " +
                    "b.title, h.start_time, h.end_time " +
                    "FROM hold AS h " +
                    "INNER JOIN patron_account AS pa ON pa.id = h.patron_id " +
                    "INNER JOIN book_copy AS bc ON bc.id = h.book_copy_id " +
                    "INNER JOIN book AS b ON b.id = bc.book_id " +
                    "ORDER BY h.id DESC";

            rst = stm.executeQuery(query);

            String[] title = new String[]{"Hold ID", "Patron Email", "Patron Name", "Book Title", "Time start", "Time end"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getString("email"));
                row.add(rst.getString("fullname"));
                row.add(rst.getString("title"));
                row.add(rst.getTimestamp("start_time"));
                row.add(rst.getTimestamp("end_time"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getHolds: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response getCheckouts() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT c.id, pa.email, CONCAT(pa.first_name, ' ', pa.last_name) AS fullname, " +
                    "b.title, c.start_time, c.end_time, c.is_returned " +
                    "FROM checkout AS c " +
                    "INNER JOIN patron_account AS pa ON pa.id = c.patron_id " +
                    "INNER JOIN book_copy AS bc ON bc.id = c.book_copy_id " +
                    "INNER JOIN book AS b ON b.id = bc.book_id " +
                    "ORDER BY c.id DESC";

            rst = stm.executeQuery(query);

            String[] title = new String[]{"ID", "Patron Email", "Patron Name", "Book Title", "Time start", "Time end", "Approved"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getString("email"));
                row.add(rst.getString("fullname"));
                row.add(rst.getString("title"));
                row.add(rst.getTimestamp("start_time"));
                row.add(rst.getTimestamp("end_time"));
                row.add(rst.getBoolean("is_returned") ? "Yes" : "No");
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getCheckouts: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response getNotifications() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT n.id, n.sent_at, n.message, pa.email " +
                    "FROM notification AS n " +
                    "INNER JOIN patron_account AS pa ON pa.id = n.patron_id ORDER BY id DESC";

            rst = stm.executeQuery(query);

            String[] title = new String[]{"Notification ID", "Sent At", "Message", "Patron Email"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getTimestamp("sent_at"));
                row.add(rst.getString("message"));
                row.add(rst.getString("email"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getNotifications: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response getPatrons() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT * FROM patron_account p";
            rst = stm.executeQuery(query);

            String[] title = new String[]{"Patron ID", "First Name", "Last Name", "Email", "Status", "Role"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getInt("id"));
                row.add(rst.getString("first_name"));
                row.add(rst.getString("last_name"));
                row.add(rst.getString("email"));
                row.add(rst.getBoolean("status") ? "Available" : "Unavailable");
                row.add(rst.getString("role"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getPatrons: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response getHistory() throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT * FROM db_log ORDER BY id DESC";
            rst = stm.executeQuery(query);

            String[] title = new String[]{"Table Name", "Action", "Time"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getString("table_name"));
                row.add(rst.getString("action"));
                row.add(rst.getTime("timestamp"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getHistory: " + e);
            return new Response(100, null);
        }
    }

    // ========================================================
    // CRUD Book Operations
    // ========================================================

    @Override
    public Response createBook(Book book, int author_id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String insertBookQuery = "INSERT INTO book (title, category_id) VALUES (?, ?)";
            PreparedStatement insertBookStatement = conn.prepareStatement(insertBookQuery, Statement.RETURN_GENERATED_KEYS);
            insertBookStatement.setString(1, book.getTitle());
            insertBookStatement.setInt(2, book.getCategory_id());
            insertBookStatement.executeUpdate();

            ResultSet generatedKeys = insertBookStatement.getGeneratedKeys();
            int bookId;
            if (generatedKeys.next()) {
                bookId = generatedKeys.getInt(1);
            } else {
                return new Response(100, "Failed to obtain generated book ID.");
            }

            String insertBookAuthorQuery = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
            PreparedStatement insertBookAuthorStatement = conn.prepareStatement(insertBookAuthorQuery);
            insertBookAuthorStatement.setInt(1, bookId);
            insertBookAuthorStatement.setInt(2, author_id);
            insertBookAuthorStatement.executeUpdate();

            doCallbacks(NOTIFY.UPDATE_BOOK);
            return new Response(200, "Created new book successfully!");
        } catch (Exception e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response getBook(int id) throws RemoteException {
        return null;
    }

    @Override
    public Response updateBook(Book book, int author_id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String updateBookQuery = "UPDATE book SET title = ?, category_id = ? WHERE id = ?";
            PreparedStatement updateBookStatement = conn.prepareStatement(updateBookQuery);
            updateBookStatement.setString(1, book.getTitle());
            updateBookStatement.setInt(2, book.getCategory_id());
            updateBookStatement.setInt(3, book.getId());
            updateBookStatement.executeUpdate();

            String updateBookAuthorQuery = "UPDATE book_author SET author_id = ? WHERE book_id = ?";
            PreparedStatement updateBookAuthorStatement = conn.prepareStatement(updateBookAuthorQuery);
            updateBookAuthorStatement.setInt(1, author_id);
            updateBookAuthorStatement.setInt(2, book.getId());
            updateBookAuthorStatement.executeUpdate();

            doCallbacks(NOTIFY.UPDATE_BOOK);
            return new Response(200, "Updated book successfully!");
        } catch (Exception e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response deleteBook(int id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String deleteBookCopyQuery = "DELETE FROM book_copy WHERE book_id = ?";
            PreparedStatement deleteBookCopyStatement = conn.prepareStatement(deleteBookCopyQuery);
            deleteBookCopyStatement.setInt(1, id);
            deleteBookCopyStatement.executeUpdate();

            String deleteBookAuthorQuery = "DELETE FROM book_author WHERE book_id = ?";
            PreparedStatement deleteBookAuthorStatement = conn.prepareStatement(deleteBookAuthorQuery);
            deleteBookAuthorStatement.setInt(1, id);
            deleteBookAuthorStatement.executeUpdate();

            String deleteBookQuery = "DELETE FROM book WHERE id = ?";
            PreparedStatement deleteBookStatement = conn.prepareStatement(deleteBookQuery);
            deleteBookStatement.setInt(1, id);
            int rowsAffected = deleteBookStatement.executeUpdate();

            if (rowsAffected > 0) {
                doCallbacks(NOTIFY.UPDATE_BOOK);
                return new Response(200, "Deleted book successfully!");
            } else {
                return new Response(100, "Book ID " + id + " not found.");
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    // ========================================================
    // CRUD Author Operations
    // ========================================================

    @Override
    public Response createAuthor(Author author, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String insertAuthorQuery = "INSERT INTO author (name) VALUES (?)";
            PreparedStatement insertAuthorStatement = conn.prepareStatement(insertAuthorQuery);
            insertAuthorStatement.setString(1, author.getName());
            insertAuthorStatement.executeUpdate();
            doCallbacks(NOTIFY.UPDATE_AUTHOR);
            return new Response(200, "Created new author successfully!");
        } catch (Exception e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response getAuthor(int id) throws RemoteException {
        return null;
    }

    @Override
    public Response updateAuthor(Author author, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String updateAuthorQuery = "UPDATE author SET name = ? WHERE id = ?";
            PreparedStatement updateAuthorStatement = conn.prepareStatement(updateAuthorQuery);
            updateAuthorStatement.setString(1, author.getName());
            updateAuthorStatement.setInt(2, author.getId());
            updateAuthorStatement.executeUpdate();
            doCallbacks(NOTIFY.UPDATE_AUTHOR);
            return new Response(200, "Updated author successfully!");
        } catch (Exception e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response deleteAuthor(int id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String deleteAuthorQuery = "DELETE FROM author WHERE id = ?";
            PreparedStatement deleteAuthorStatement = conn.prepareStatement(deleteAuthorQuery);
            deleteAuthorStatement.setInt(1, id);
            int rowsAffected = deleteAuthorStatement.executeUpdate();

            if (rowsAffected > 0) {
                doCallbacks(NOTIFY.UPDATE_AUTHOR);
                return new Response(200, "Deleted author successfully!");
            } else {
                return new Response(100, "Author ID " + id + " not found.");
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    // ========================================================
    // CRUD Category Operations
    // ========================================================

    @Override
    public Response createCategory(Category category, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String createCategoryQuery = "INSERT INTO category (name) VALUES (?)";
            PreparedStatement createCategoryStatement = conn.prepareStatement(createCategoryQuery);
            createCategoryStatement.setString(1, category.getName());
            createCategoryStatement.executeUpdate();
            doCallbacks(NOTIFY.UPDATE_CATEGORY);
            return new Response(200, "Created new Category successfully!");
        } catch (Exception e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response getCategory(int id) throws RemoteException {
        return null;
    }

    @Override
    public Response updateCategory(Category category, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String updateCategoryQuery = "UPDATE category SET name = ? WHERE id = ?";
            PreparedStatement updateCategoryStatement = conn.prepareStatement(updateCategoryQuery);
            updateCategoryStatement.setString(1, category.getName());
            updateCategoryStatement.setInt(2, category.getId());
            updateCategoryStatement.executeUpdate();
            doCallbacks(NOTIFY.UPDATE_CATEGORY);
            return new Response(200, "Updated Category successfully!");
        } catch (Exception e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response deleteCategory(int id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String deleteCategoryQuery = "DELETE FROM category WHERE id = ?";
            PreparedStatement deleteCategoryStatement = conn.prepareStatement(deleteCategoryQuery);
            deleteCategoryStatement.setInt(1, id);
            int rowsAffected = deleteCategoryStatement.executeUpdate();

            if (rowsAffected > 0) {
                doCallbacks(NOTIFY.UPDATE_CATEGORY);
                return new Response(200, "Deleted category successfully!");
            } else {
                return new Response(100, "Category ID " + id + " not found.");
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    // ========================================================
    // CRUD Published Operations
    // ========================================================

    @Override
    public Response createPublished(Published published, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "INSERT INTO published (name) VALUES (?)";
            pst = conn.prepareStatement(query);
            pst.setString(1, published.getName());
            pst.executeUpdate();
            doCallbacks(NOTIFY.UPDATE_PUBLISHED);
            return new Response(200, "Created Publisher successfully!");
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response getPublished(int id) throws RemoteException {
        return null;
    }

    @Override
    public Response updatePublished(Published published, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "UPDATE published SET name = ? WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, published.getName());
            pst.setInt(2, published.getId());
            pst.executeUpdate();
            doCallbacks(NOTIFY.UPDATE_PUBLISHED);
            return new Response(200, "Updated Publisher successfully!");
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response deletePublished(int id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "DELETE FROM published WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                doCallbacks(NOTIFY.UPDATE_PUBLISHED);
                return new Response(200, "Deleted Publisher successfully!");
            } else {
                return new Response(100, "Publisher ID " + id + " not found.");
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    // ========================================================
    // CRUD Patron Operations
    // ========================================================

    @Override
    public Response createPatron(Patron patron, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String roleToInsert = (patron.getRole() != null && !patron.getRole().isEmpty()) ? patron.getRole() : "PATRON";
            String query = "INSERT INTO patron_account (first_name, last_name, email, password, status, role) VALUES (?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(query);
            pst.setString(1, patron.getFirstName());
            pst.setString(2, patron.getLastName());
            pst.setString(3, patron.getEmail());
            pst.setString(4, patron.getPassword());
            pst.setBoolean(5, patron.isStatus());
            pst.setString(6, roleToInsert);

            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                return new Response(200, "Create new patron account successfully");
            } else {
                return new Response(100, "Failed to create new patron account.");
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response getPatron(int id) throws RemoteException {
        return null;
    }

    @Override
    public Response updatePatron(Patron patron, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "UPDATE patron_account SET first_name = ?, last_name = ?, email = ? WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, patron.getFirstName());
            pst.setString(2, patron.getLastName());
            pst.setString(3, patron.getEmail());
            pst.setInt(4, patron.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                return new Response(200, "Update patron account successfully");
            } else {
                return new Response(100, "Failed to update patron account.");
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response deletePatron(int id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "DELETE FROM patron_account WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                return new Response(200, "Successfully deleted patron.");
            } else {
                return new Response(100, "No patron found with ID " + id);
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    // ========================================================
    // CRUD BookCopy Operations
    // ========================================================

    @Override
    public Response createBookCopy(BookCopy bookCopy, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "INSERT INTO book_copy (year_published, book_id, published_id) VALUES (?, ?, ?)";
            pst = conn.prepareStatement(query);
            pst.setInt(1, bookCopy.getYear_published());
            pst.setInt(2, bookCopy.getBook_id());
            pst.setInt(3, bookCopy.getPublished_id());
            pst.executeUpdate();

            doCallbacks(NOTIFY.UPDATE_BOOK_COPY);
            return new Response(200, "Create book copy successfully");
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response getBookCopy(int id) throws RemoteException {
        return null;
    }

    @Override
    public Response updateBookCopy(BookCopy bookCopy, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "UPDATE book_copy SET year_published = ?, book_id = ?, published_id = ? WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, bookCopy.getYear_published());
            pst.setInt(2, bookCopy.getBook_id());
            pst.setInt(3, bookCopy.getPublished_id());
            pst.setInt(4, bookCopy.getId());
            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                doCallbacks(NOTIFY.UPDATE_BOOK_COPY);
                return new Response(200, "Updated book copy successfully!");
            } else {
                return new Response(100, "Book copy ID " + bookCopy.getId() + " not found.");
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response deleteBookCopy(int id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "DELETE FROM book_copy WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();

            if (rowsDeleted > 0) {
                doCallbacks(NOTIFY.UPDATE_BOOK_COPY);
                return new Response(200, "Deleted book copy successfully!");
            } else {
                return new Response(100, "Book copy ID " + id + " not found.");
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    // ========================================================
    // CRUD Hold Operations
    // ========================================================

    @Override
    public Response createHold(Hold hold, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "INSERT INTO hold (start_time, end_time, patron_id, book_copy_id) VALUES (?, ?, ?, ?)";
            pst = conn.prepareStatement(query);
            pst.setTimestamp(1, hold.getStart_time());
            pst.setTimestamp(2, hold.getEnd_time());
            pst.setInt(3, hold.getPatron_id());
            pst.setInt(4, hold.getBook_copy_id());
            pst.executeUpdate();

            doCallbacks(NOTIFY.UPDATE_HOLD);
            return new Response(200, "Create hold successfully");
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response getHold(int id) throws RemoteException {
        return null;
    }

    @Override
    public Response updateHold(Hold hold, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "UPDATE hold SET start_time = ?, end_time = ?, patron_id = ?, book_copy_id = ? WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setTimestamp(1, hold.getStart_time());
            pst.setTimestamp(2, hold.getEnd_time());
            pst.setInt(3, hold.getPatron_id());
            pst.setInt(4, hold.getBook_copy_id());
            pst.setInt(5, hold.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                doCallbacks(NOTIFY.UPDATE_HOLD);
                return new Response(200, "Hold updated successfully.");
            } else {
                return new Response(100, "No hold found with ID " + hold.getId());
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response deleteHold(int id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "DELETE FROM hold WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                doCallbacks(NOTIFY.UPDATE_HOLD);
                return new Response(200, "Hold deleted successfully.");
            } else {
                return new Response(100, "No hold found with ID " + id);
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    // ========================================================
    // CRUD Checkout Operations
    // ========================================================

    @Override
    public Response createCheckout(Checkout checkout, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "INSERT INTO checkout (start_time, end_time, is_returned, patron_id, book_copy_id) VALUES (?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(query);
            pst.setTimestamp(1, checkout.getStart_time());
            pst.setTimestamp(2, checkout.getEnd_time());
            pst.setBoolean(3, checkout.isIs_returned());
            pst.setInt(4, checkout.getPatron_id());
            pst.setInt(5, checkout.getBook_copy_id());
            pst.executeUpdate();

            doCallbacks(NOTIFY.CLIENT_UPDATE_CHECKOUT);
            doCallbacks(NOTIFY.UPDATE_CHECKOUT);

            return new Response(200, "Created checkout successfully");
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response getCheckout(int id) throws RemoteException {
        return null;
    }

    @Override
    public Response updateCheckout(Checkout checkout, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "UPDATE checkout SET start_time = ?, end_time = ?, is_returned = ?, patron_id = ?, book_copy_id = ? WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setTimestamp(1, checkout.getStart_time());
            pst.setTimestamp(2, checkout.getEnd_time());
            pst.setBoolean(3, checkout.isIs_returned());
            pst.setInt(4, checkout.getPatron_id());
            pst.setInt(5, checkout.getBook_copy_id());
            pst.setInt(6, checkout.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                if (checkout.isIs_returned()) {
                    Notification notification = new Notification();
                    Timestamp time_now = new Timestamp(new Date().getTime());
                    notification.setMessage("ID " + checkout.getId() + ": Thủ thư đã duyệt yêu cầu mượn sách của bạn!");
                    notification.setPatron_id(checkout.getPatron_id());
                    notification.setSend_at(time_now);

                    String insertNotifQuery = "INSERT INTO notification (sent_at, message, patron_id) VALUES (?, ?, ?)";
                    PreparedStatement notifStmt = conn.prepareStatement(insertNotifQuery);
                    notifStmt.setTimestamp(1, notification.getSend_at());
                    notifStmt.setString(2, notification.getMessage());
                    notifStmt.setInt(3, notification.getPatron_id());
                    notifStmt.executeUpdate();

                    doCallbacks(NOTIFY.UPDATE_NOTIFICATION);
                    doCallbacks(NOTIFY.CLIENT_UPDATE_NOTIFICATION);
                    doCallbacks(NOTIFY.CLIENT_UPDATE_CHECKOUT);
                }
                return new Response(200, "Updated checkout successfully.");
            } else {
                return new Response(100, "No checkout found with ID " + checkout.getId());
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response deleteCheckout(int id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String query = "DELETE FROM checkout WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                doCallbacks(NOTIFY.CLIENT_UPDATE_CHECKOUT);
                doCallbacks(NOTIFY.UPDATE_CHECKOUT);
                return new Response(200, "Deleted checkout successfully.");
            } else {
                return new Response(100, "No checkout found with ID " + id);
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    // ========================================================
    // CRUD Notification Operations
    // ========================================================

    @Override
    public Response createNotification(Notification notification, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String insertNotifQuery = "INSERT INTO notification (sent_at, message, patron_id) VALUES (?, ?, ?)";
            PreparedStatement notifStmt = conn.prepareStatement(insertNotifQuery);
            notifStmt.setTimestamp(1, notification.getSend_at());
            notifStmt.setString(2, notification.getMessage());
            notifStmt.setInt(3, notification.getPatron_id());
            notifStmt.executeUpdate();

            doCallbacks(NOTIFY.UPDATE_NOTIFICATION);
            doCallbacks(NOTIFY.CLIENT_UPDATE_NOTIFICATION);
            return new Response(200, "Created new notification successfully!");
        } catch (Exception e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response getNotification(int patron_id) throws RemoteException {
        try {
            checkConnection();
            vTitle.clear();
            vData.clear();

            String query = "SELECT sent_at, message FROM notification WHERE patron_id = ? ORDER BY id DESC";
            pst = conn.prepareStatement(query);
            pst.setInt(1, patron_id);
            rst = pst.executeQuery();

            String[] title = new String[]{"Time", "Message"};
            Collections.addAll(vTitle, title);
            while (rst.next()) {
                Vector row = new Vector();
                row.add(rst.getTimestamp("sent_at"));
                row.add(rst.getString("message"));
                vData.add(row);
            }
            rst.close();
            return new Response(200, new DefaultTableModel(vData, vTitle));
        } catch (SQLException e) {
            System.err.println("Error in getNotification: " + e);
            return new Response(100, null);
        }
    }

    @Override
    public Response updateNotification(Notification notification, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String updateNotifQuery = "UPDATE notification SET sent_at = ?, message = ?, patron_id = ? WHERE id = ?";
            PreparedStatement notifStmt = conn.prepareStatement(updateNotifQuery);
            notifStmt.setTimestamp(1, notification.getSend_at());
            notifStmt.setString(2, notification.getMessage());
            notifStmt.setInt(3, notification.getPatron_id());
            notifStmt.setInt(4, notification.getId());
            notifStmt.executeUpdate();

            doCallbacks(NOTIFY.UPDATE_NOTIFICATION);
            return new Response(200, "Updated notification successfully!");
        } catch (Exception e) {
            return new Response(100, e.getMessage());
        }
    }

    @Override
    public Response deleteNotification(int id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String deleteNotifQuery = "DELETE FROM notification WHERE id = ?";
            PreparedStatement notifStmt = conn.prepareStatement(deleteNotifQuery);
            notifStmt.setInt(1, id);
            int rowsAffected = notifStmt.executeUpdate();

            if (rowsAffected > 0) {
                doCallbacks(NOTIFY.UPDATE_NOTIFICATION);
                return new Response(200, "Deleted notification successfully!");
            } else {
                return new Response(100, "Notification ID " + id + " not found.");
            }
        } catch (SQLException e) {
            return new Response(100, e.getMessage());
        }
    }

    // ========================================================
    // CRUD Log Operations
    // ========================================================

    @Override
    public int createLog(Log log, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String createLogQuery = "INSERT INTO log (ip, username, table_name, col_id, time_start) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement createLogStatement = conn.prepareStatement(createLogQuery, Statement.RETURN_GENERATED_KEYS);
            createLogStatement.setString(1, log.getIp());
            createLogStatement.setString(2, log.getUsername());
            createLogStatement.setString(3, log.getTable_name());
            createLogStatement.setInt(4, log.getCol_id());
            createLogStatement.setTimestamp(5, log.getTime_start());
            createLogStatement.executeUpdate();

            ResultSet generatedKeys = createLogStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            return -1;
        } catch (Exception e) {
            System.err.println("Error in createLog: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public void updateLog(Log log, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String updateLogQuery = "UPDATE log SET ip = ?, username = ?, table_name = ?, col_id = ?, time_start = ? WHERE id = ?";
            PreparedStatement updateLogStatement = conn.prepareStatement(updateLogQuery);
            updateLogStatement.setString(1, log.getIp());
            updateLogStatement.setString(2, log.getUsername());
            updateLogStatement.setString(3, log.getTable_name());
            updateLogStatement.setInt(4, log.getCol_id());
            updateLogStatement.setTimestamp(5, log.getTime_start());
            updateLogStatement.setInt(6, log.getId());
            updateLogStatement.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error in updateLog: " + e.getMessage());
        }
    }

    @Override
    public void deleteLog(int id, boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String deleteLogQuery = "DELETE FROM log WHERE id = ?";
            PreparedStatement deleteLogStatement = conn.prepareStatement(deleteLogQuery);
            deleteLogStatement.setInt(1, id);
            deleteLogStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in deleteLog: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllLog(boolean isCallFromSever) throws RemoteException {
        try {
            checkConnection();
            String deleteLogQuery = "DELETE FROM log";
            PreparedStatement deleteLogStatement = conn.prepareStatement(deleteLogQuery);
            deleteLogStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in deleteAllLog: " + e.getMessage());
        }
    }

    @Override
    public boolean checkLog(String table_name, int col_id) throws RemoteException {
        try {
            checkConnection();
            String checkLogQuery = "SELECT COUNT(*) FROM log WHERE table_name = ? AND col_id = ?";
            PreparedStatement checkLogStatement = conn.prepareStatement(checkLogQuery);
            checkLogStatement.setString(1, table_name);
            checkLogStatement.setInt(2, col_id);
            ResultSet resultSet = checkLogStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error in checkLog: " + e.getMessage());
            return false;
        }
    }

    // ========================================================
    // Authentication & Registration
    // ========================================================

    @Override
    public Response loginClient(Patron patron) throws RemoteException {
        String email = patron.getEmail();
        String password = patron.getPassword();

        try {
            checkConnection();
            String query = "SELECT * FROM patron_account WHERE email = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, email);

            rst = pst.executeQuery();
            if (rst.next()) {
                String password_db = rst.getString("password");
                boolean passwordMatch = password_db.equals(password);

                if (passwordMatch) {
                    if (!rst.getBoolean("status")) {
                        return new Response(100, "Account has been suspended! Please contact Library Management.");
                    }
                    patron.setId(rst.getInt("id"));
                    patron.setFirstName(rst.getString("first_name"));
                    patron.setLastName(rst.getString("last_name"));
                    patron.setStatus(rst.getBoolean("status"));
                    patron.setRole(rst.getString("role"));
                    return new Response(200, patron);
                } else {
                    return new Response(100, "Invalid email or password!");
                }
            } else {
                return new Response(100, "User account not found!");
            }
        } catch (SQLException e) {
            return new Response(100, "Database error: " + e.getMessage());
        }
    }

    @Override
    public Response registerClient(Patron patron) throws RemoteException {
        try {
            checkConnection();
            String query = "INSERT INTO patron_account (first_name, last_name, email, password, status, role) VALUES (?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(query);
            pst.setString(1, patron.getFirstName());
            pst.setString(2, patron.getLastName());
            pst.setString(3, patron.getEmail());
            pst.setString(4, patron.getPassword());
            pst.setBoolean(5, true);
            pst.setString(6, "PATRON");

            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                return new Response(200, "Registration successful!");
            } else {
                return new Response(100, "Registration failed!");
            }
        } catch (SQLException e) {
            return new Response(100, "Database error: " + e.getMessage());
        }
    }
}
