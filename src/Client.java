import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

public class Client {
    private static int count = 1;
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (Exception e) {
            closeEverything();
        }
    }

    private void closeEverything() {
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

    public void sendMsg(String msg) {
        try {
           bufferedWriter.write(username + msg);


            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            closeEverything();
        }
    }

    public void listenForMsg() {
        new Thread(() -> {
            String msgFromGroupChat;
            while (socket.isConnected()) {
                try {
                    msgFromGroupChat = bufferedReader.readLine();
                    System.out.println(msgFromGroupChat);
                } catch (Exception e) {
                    closeEverything();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        //String username = "Client "+count++;
        int clientId = count++;
        String username = "";
        Socket socket = new Socket("127.0.0.1", 8080);
        Client client = new Client(socket, username);
        client.listenForMsg();


        while (true) {
            String message = sc.nextLine();
            if (message.equalsIgnoreCase("exit")) {
                client.sendMsg("has left the chat!");
                break;
            } else {
                client.sendMsg(message);
            }
        }
    }
}
