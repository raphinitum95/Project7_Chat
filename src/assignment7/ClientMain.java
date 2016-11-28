package assignment7;

import java.net.Socket;
import java.util.ArrayList;
import javafx.scene.control.TextField;


public class ClientMain {
    private static ArrayList<ClientRunnable> threads = new ArrayList<>();
    private boolean flag = false;
    private String rand_name = "A";

    public void go() throws Exception {
        setUpNetworking();
    }

    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        Socket sock = new Socket("192.168.0.3", 4242);
        network(sock, rand_name);
        rand_name = Character.toString((char)((int)(rand_name.charAt(0)) + 1));
    }

    public void setUpNetworking(String IP, int port, String name) throws Exception{
        @SuppressWarnings("resource")
        Socket sock = new Socket(IP, port);
        network(sock, name);
    }

    private void network(Socket sock, String name) throws Exception {
        System.out.println("networking established");
        ClientRunnable a = new ClientRunnable(sock, sock, name, name);
        Thread readerThread = new Thread(a);
        readerThread.start();
        threads.add(a);
    }

    public static void main(String[] args) {
        try {
            new ClientMain().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
