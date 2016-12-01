package assignment7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import assignment7.Controller.ClientRunnable;

import static javafx.application.Application.launch;


public class ClientMain {
    private static ArrayList<ClientRunnable> threads = new ArrayList<>();
    private BufferedReader reader;
    private PrintWriter writer;


    public void setUpNetworking(String IP, String name) throws Exception{
        @SuppressWarnings("resource")
        Socket sock = new Socket(IP, 4242);
        network(sock, name);
    }

    private void network(Socket sock, String name) throws Exception {
        InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(sock.getOutputStream());
        writer.println("@" + name);
        writer.flush();
        System.out.println("networking established");
        ClientRunnable a = new Controller().new ClientRunnable(name, sock);
        Thread readerThread = new Thread(a);
        readerThread.start();
        threads.add(a);
    }

    public static void main(String[] args) {
        launch(Main.class, args);
    }
}
