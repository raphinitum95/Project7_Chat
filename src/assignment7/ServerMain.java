package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

public class ServerMain extends Observable {
    private HashMap<String, ClientObserver> client_w = new HashMap<>();
    private HashMap<String, PrintWriter> printClient = new HashMap<>();
    private HashMap<String, Chat> chats = new HashMap<>();
    private ArrayList<String> usernameList = new ArrayList<>();
    private String Username;
    public static void main(String[] args) {
        try {
            new ServerMain().setUpNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        ServerSocket serverSock = new ServerSocket(4242);
        while (true) {
            Socket clientSocket = serverSock.accept();
            ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
            PrintWriter print = new PrintWriter(clientSocket.getOutputStream());
            Thread t = new Thread(new ClientHandler(clientSocket));
            t.start();
            BufferedReader UsernameReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while((Username = UsernameReader.readLine())!= null) {
                printClient.put(Username, print);
                client_w.put(Username, writer);
                System.out.println(Username);
            }
            this.addObserver(writer);
            usernameList.add(Username);
            System.out.println("got a connection");
        }
    }
    class ClientHandler implements Runnable {
        private BufferedReader reader;

        public ClientHandler(Socket clientSocket) {
            Socket sock = clientSocket;
            try {
                reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String message;
            String[] mes;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("Server read: " + message);
                    mes = message.split(";");

                    if(mes[0].equals("1")){
                        int size = Integer.parseInt(mes[1]);
                        String[] temp = new String[size];
                        for(int i = 0; i < size; i++){
                            temp[i] = mes[i+2];
                        }
                        Chat current = new Chat(temp);
                        chats.put(mes[1], current);

                        for(String str: temp){
                            current.addObserver(client_w.get(str));
                        }
                        current.changed();
                        current.notifyObservers(mes[1] + ":" + mes[size + 2]);

                    }
                    else if(mes[0].equals("2")){
                        Chat current = chats.get(mes[1]);
                        current.changed();
                        current.notifyObservers(mes[1] + ":" + mes[2]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
