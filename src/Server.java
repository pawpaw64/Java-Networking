import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;


    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new Client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void classServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.isClosed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(123);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}