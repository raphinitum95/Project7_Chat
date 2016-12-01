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
    private static HashMap<String, ClientObserver> client_w = new HashMap<>();
    private static HashMap<String, PrintWriter> printClient = new HashMap<>();
    private static HashMap<String, Chat> chats = new HashMap<>();
    private static ArrayList<String> usernameList = new ArrayList<>();
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
            Thread t = new Thread(new ClientHandler(clientSocket));
            t.start();
            this.addObserver(writer);
            System.out.println("got a connection");
        }
    }
    class ClientHandler implements Runnable {
        private BufferedReader reader;
        private ClientObserver writer;
        private PrintWriter print;

        public ClientHandler(Socket clientSocket) {
            Socket sock = clientSocket;

            try {
                writer = new ClientObserver(clientSocket.getOutputStream());
                print = new PrintWriter(clientSocket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String message;
            String[] mes;
            String temp_name;
            boolean flag;
            try {
                while ((message = reader.readLine()) != null) {
                    flag = true;
                    mes = message.split(":", 4);
                    if(mes[0].charAt(0) == '@') {
                        try{
                            Integer.parseInt(mes[0].substring(1));
                        }catch(Exception e){
                            if (printClient.get(mes[0]) == null) {
                                printClient.put(mes[0], print);
                                client_w.put(mes[0], writer);
                                System.out.println(mes[0]);
                                usernameList.add(mes[0]);
                            }
                            flag = false;
                        }
                        if(flag) {
                            System.out.println("Server read: " + message);


                            if (!chats.containsKey(mes[0])) {
                                String[] temp = mes[2].split(";");
                                temp_name = mes[1];

                                Chat current = new Chat(temp);
                                chats.put(mes[0], current);

                                for (String str : temp) {
                                    current.addObserver(client_w.get("@" + str));
                                }
                                current.changed();
                                current.notifyObservers(mes[0] + ":" + temp_name + ":" + mes[2] + ":" + mes[3]);

                            } else {
                                Chat current = chats.get(mes[0]);
                                current.changed();
                                current.notifyObservers(mes[0] + ":" + mes[1] + ":" + mes[2] + ":" + mes[3]);
                                current.unChanged();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
