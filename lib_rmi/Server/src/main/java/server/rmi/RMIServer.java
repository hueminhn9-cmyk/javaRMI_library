package server.rmi;

import common.rmi.*;
import server.db.ChatDAO;
import server.tcp.TCPChatServer;
import server.tcp.TCPFileServer;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;


public class RMIServer {
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", Config.IP_SERVER);
            LibraryImpl server = new LibraryImpl();
            LocateRegistry.createRegistry(Config.PORT_SERVER);

            // Đăng ký đối tượng này với rmiregistry
            Naming.rebind("rmi://" + Config.IP_SERVER + ":" + Config.PORT_SERVER + "/api", server);


            System.out.println(">>>>> INFO: RMI Server started !");
            System.out.println("rmi://" + Config.IP_SERVER + ":" + Config.PORT_SERVER + "/api");

            // Auto-initialize MySQL chat_history table
            ChatDAO.createTableIfNotExists();

            // Start Multi-Machine TCP Chat Server (Port 7777) & File Server (Port 8888)
            TCPChatServer.startServer();
            TCPFileServer.startServer();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
