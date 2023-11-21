import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private  String username;

    public Client(Socket socket ,String username) {
        try{
            this.socket = socket;
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username=username;
        }catch (Exception e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }

    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public void sendMsg(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner sc=new Scanner(System.in);
            while (socket.isConnected()){
                String msgTosend=sc.nextLine();
                bufferedWriter.write(username+": "+msgTosend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (Exception e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void listenForMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while(socket.isConnected()){
                    try{
                        msgFromGroupChat=bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    }catch (Exception e){
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Scanner sc=new Scanner(System.in);
        System.out.println("enter your username to connect the group chat");
        String username=sc.nextLine();
        Socket socket=new Socket("localhost",123);
        Client client=new Client(socket,username);
        client.listenForMsg();
        client.sendMsg();
    }

}
