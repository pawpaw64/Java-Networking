import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String  clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            // character streams ends with writer bytes stream ends with stream
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername=bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMsg("Server"+clientUsername+"has entered the chat!");


        }catch (Exception e){
            closeEverything(socket,bufferedReader,bufferedWriter);
            e.printStackTrace();
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void broadcastMsg(String msgToSent) {
        for(ClientHandler clientHandler:clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals((clientUsername)));
                clientHandler.bufferedWriter.write(msgToSent);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
            } catch (Exception e){
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }

    }
    public void removeClientHandler(){
        clientHandlers.remove(this);
        System.out.println("Server"+clientUsername+"has left the chat!");
    }

    @Override
    public void run() {
        String msgFromClient;

        while(socket.isConnected()) {
            try {
                msgFromClient = bufferedReader.readLine();
                broadcastMsg(msgFromClient);
            } catch (Exception e) {
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }

    }

}
