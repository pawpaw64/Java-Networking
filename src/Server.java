import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Server {
    private ServerSocket serverSocket;
    private Hashtable<String, ClientHandler> clientHandlerMap;
    static final String MESSAGE_FILE = "C:\\Users\\Computer Care\\Downloads\\Java-Networking\\src\\messages.txt";


    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clientHandlerMap = new Hashtable<>();
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Server has started...");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlerMap.put(clientHandler.getClientUsername(), clientHandler);
                System.out.println(clientHandler.getClientUsername()+" is connected...");
                Thread thread = new Thread(clientHandler);
                thread.start();

                previousMessages(clientHandler);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void previousMessages(ClientHandler newClientHandler) {
    for (ClientHandler clientHandler : clientHandlerMap.values()) {
        if (!clientHandler.equals(newClientHandler)) {
            clientHandler.sendMsg(newClientHandler.getClientUsername() + " has entered the chat!");
        }
    }
    newClientHandler.sendMsg("Welcome, " + newClientHandler.getClientUsername() + "! You are connected.");
    sendOldMessages(newClientHandler);
}

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        Server server = new Server(serverSocket);
        server.startServer();
    }

    private void sendOldMessages(ClientHandler clientHandler) {
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                clientHandler.sendMsg(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
