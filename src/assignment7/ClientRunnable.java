package assignment7;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER;

public class ClientRunnable implements Runnable{
    private static ArrayList<ClientRunnable> clients = new ArrayList<>();
    private static Object a = new Object();
    private Socket senderSock;
    private Socket receiverSock;
    private String sender;
    private String receiver = null;
    private String[] names;
    private DataInputStream reader;
    private PrintWriter writer;
    private boolean group;

    private TextField input;
    private TextField message;
    private Button send;
    private String temp_message = null;

    ClientRunnable(Socket senderSock, Socket receiverSock, String sender, String receiver){
        this.senderSock = senderSock;
        this.receiverSock = receiverSock;
        this.sender = sender;
        this.receiver = receiver;
        group = false;
        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ClientRunnable(Socket senderSock, Socket receiverSock, String[] names){
        this.senderSock = senderSock;
        this.receiverSock = receiverSock;
        this.sender = names[0];
        this.names = names;
        group = true;
        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUp() throws Exception{
        reader = new DataInputStream(senderSock.getInputStream());
        writer = new PrintWriter(senderSock.getOutputStream());
        Pane pane = new Pane();
        ScrollPane textArea = new ScrollPane();
        textArea.setPrefSize(600, 286);
        textArea.setHbarPolicy(NEVER);

        input = new TextField();
        input.setLayoutY(207);
        input.setLayoutY(303);

        message = new TextField();
        message.setPrefSize(600, 285);
        message.setAlignment(Pos.TOP_LEFT);
        message.setEditable(false);

        send = new Button("send");
        send.setLayoutX(464);
        send.setLayoutY(303);
        send.setOnAction(e -> {
            if(!input.getText().isEmpty()) {
                send();
            }
        });

        textArea.setContent(message);
        pane.getChildren().addAll(input, textArea, send);

        Stage client = new Stage();
        client.setTitle(sender + "'s chat");
        Scene scene = new Scene(pane, 600, 500);
        client.setScene(scene);
        client.show();
        clients.add(this);
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
        while(true){
            if(temp_message != null) {
                if (group) {
                    synchronized (clients) {
                        temp_message = temp_message + "\n";
                        for (int i = 0; i < names.length; i++) {
                            for (ClientRunnable c : clients) {
                                if (c.sender.equals(sender) || names[i].equals(c.receiver))
                                    c.message.setText(c.message.getText() + temp_message);
                            }
                        }
                    }
                } else {
                    synchronized (a) {
                        temp_message = temp_message + "\n";
                        for (ClientRunnable c : clients) {
                            if (c.sender.equals(sender) || receiver.equals(c.receiver)) {
                                c.message.setText(c.message.getText() + temp_message);
                            }
                        }
                    }
                }
                temp_message = null;
            }
            Thread.yield();
        }

    }

    public void send() {
        temp_message = input.getText();
        input.clear();
        input.requestFocus();
    }
}
