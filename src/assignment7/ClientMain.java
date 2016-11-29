package assignment7;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import static javafx.application.Application.launch;


public class ClientMain {
    private static ArrayList<ClientRunnable> threads = new ArrayList<>();
    private boolean flag = false;
    private String rand_name = "A";
    private BufferedReader reader;
    private PrintWriter writer;
    private HashMap<String, ArrayList<ClientRunnable>> chats = new HashMap<>();

    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        Socket sock = new Socket("192.168.0.3", 4242);
        network(sock, rand_name);
        rand_name = Character.toString((char)((int)(rand_name.charAt(0)) + 1));
    }

    public void setUpNetworking(String IP, String name) throws Exception{
        @SuppressWarnings("resource")
        Socket sock = new Socket(IP, 4242);
        network(sock, name);
    }

    private void network(Socket sock, String name) throws Exception {
        InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(sock.getOutputStream());
        writer.println(name);
        writer.flush();
        System.out.println("networking established");
        ClientRunnable a = new ClientRunnable(name);
        Thread readerThread = new Thread(a);
        readerThread.start();
        threads.add(a);
    }

    public static void main(String[] args) {
        launch(Main.class, args);
        try {
            new ClientMain().setUpNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ClientRunnable implements Runnable{
        private Button send;
        private TextField input;
        public TextArea message;
        private String name;
        ClientRunnable(String name){
            this.name = name;
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setUp() throws Exception{
            Pane pane = new Pane();

            input = new TextField();
            input.setLayoutY(207);
            input.setLayoutY(303);

            message = new TextArea();
            message.setPrefSize(600, 285);
            message.setEditable(false);

            send = new Button("send");
            send.setLayoutX(464);
            send.setLayoutY(303);
            send.setOnAction(e -> {
                writer.println(input.getText());
                writer.flush();
                input.clear();
                input.requestFocus();
            });

            pane.getChildren().addAll(input, message, send);

            Stage client = new Stage();
            client.setTitle(name + "'s chat");
            Scene scene = new Scene(pane, 600, 500);
            client.setScene(scene);
            client.show();
        }
        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            String message;
            String[] m;
            try {
                while ((message = reader.readLine()) != null) {
                    m = message.split(":");

                    ArrayList<ClientRunnable> ch = chats.get(m[0]);
                    for(ClientRunnable client: ch){
                        client.message.appendText(message + "\n");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
