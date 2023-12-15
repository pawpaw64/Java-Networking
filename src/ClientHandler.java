import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername ;
    static int countClient =1;


    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = "Client "+countClient++;
            clientHandlers.add(this);


        } catch (Exception e) {
            closeEverything();
            e.printStackTrace();
        }
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public void closeEverything() {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void broadcastMsg(String msgToSent, String senderUsername) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(senderUsername)) {
                    clientHandler.bufferedWriter.write(senderUsername + ": " + msgToSent);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (Exception e) {
                closeEverything();
            }
        }

        saveMessageToFile(senderUsername + ": " + msgToSent);
    }


    private void saveMessageToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Server.MESSAGE_FILE, true))) {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        System.out.println("Server: " + clientUsername + " has left the chat!");
    }

    public void sendMsg(String msg) {
        try {
            bufferedWriter.write(msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        String msgFromClient;

        while (socket.isConnected()) {
            try {
                msgFromClient = bufferedReader.readLine();
                broadcastMsg(msgFromClient,clientUsername);
            } catch (Exception e) {
                closeEverything();
                break;
            }
        }
    }
}
