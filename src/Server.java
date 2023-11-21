import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Server {
    private ServerSocket serverSocket;
    private Hashtable<String, ClientHandler> clientHandlerMap;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clientHandlerMap = new Hashtable<>();
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new Client has connected:-");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlerMap.put(clientHandler.getClientUsername(), clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();

                // Bonus 2: Send previous messages to the new client
                sendPreviousMessages(clientHandler);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPreviousMessages(ClientHandler newClientHandler) {
        for (ClientHandler clientHandler : clientHandlerMap.values()) {
            newClientHandler.sendMsg("Server: " + clientHandler.getClientUsername() + " has entered the chat!");
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
