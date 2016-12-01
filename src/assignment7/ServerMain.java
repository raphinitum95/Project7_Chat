package assignment7;

import javafx.scene.control.TextArea;

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
    public static ArrayList<Controller.ClientRunnable> clients = new ArrayList<>();
    private static HashMap<String, Controller> log = new HashMap<>();
    public static HashMap<String, ArrayList<String>> friend_list = new HashMap<>();
    public static HashMap<String, ArrayList<String>> friend_requests = new HashMap<>();
    public static HashMap<String, Integer> active_chat = new HashMap<>();
    public static HashMap<Integer, ArrayList<String>> chat_list = new HashMap<>();
    public static HashMap<Integer, String> chat_history = new HashMap<>();
    public static HashMap<String, TextArea> chat_area = new HashMap<>();
    private static Controller info;
    public static void main(String[] args) {
        try {
            info = new Controller();
            new ServerMain().setUpNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Controller.ClientRunnable> threads = new ArrayList<>();
    private static BufferedReader reader;
    private static PrintWriter writer;


    public static void setNetworking(String IP, String name) throws Exception{
        @SuppressWarnings("resource")
        Socket sock = new Socket(IP, 4242);
        network(sock, name);
    }

    private static void network(Socket sock, String name) throws Exception {
        InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(sock.getOutputStream());
        writer.println("@" + name);
        writer.flush();
        if(!log.containsKey(name)) {
            System.out.println("networking established");
            Controller.ClientRunnable a = new Controller().new ClientRunnable(name, sock);
            Thread readerThread = new Thread(a);
            readerThread.start();
            threads.add(a);
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
            clients = info.clients;
            log = info.log;
            friend_list = info.friend_list;
            friend_requests = info.friend_requests;
            active_chat = info.active_chat;
            chat_list = info.chat_list;
            chat_history = info.chat_history;
            chat_area = info.chat_area;
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
                            } else{
                                printClient.get(mes[0]).println("&" + mes[0].substring(1));
                                printClient.get(mes[0]).flush();
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
