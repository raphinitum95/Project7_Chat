package assignment7;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.PlatformLoggingMXBean;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller{
    private static ArrayList<String> names = new ArrayList<>();
    public static HashMap<String, ClientRunnable> runable = new HashMap<>();
    public static ArrayList<ClientRunnable> clients = new ArrayList<>();
    public static HashMap<String, Controller> log = new HashMap<>();
    public static HashMap<String, ArrayList<String>> friend_list = new HashMap<>();
    public static HashMap<String, ArrayList<String>> friend_requests = new HashMap<>();
    public static HashMap<String, Integer> active_chat = new HashMap<>();
    public static HashMap<Integer, ArrayList<String>> chat_list = new HashMap<>();
    public static HashMap<Integer, String> chat_history = new HashMap<>();
    public static HashMap<String, TextArea> chat_area = new HashMap<>();
    private String name;
    private String group_name;
    private int indx;
    private static int uniqueName = 0;
    private static int counter = 0;
    @FXML
    private TextField outgoing;
    @FXML
    public TextArea incoming;
    @FXML
    private Button send;
    @FXML
    private ListView listView;
    @FXML
    private Label participants;
    @FXML
    private SplitPane chatView;
    @FXML
    private TextField addNames;
    @FXML
    private Button add;
    @FXML
    private Button create;
    @FXML
    private Label noFriend;

    private static TextField text;
    private static TextArea board;
    private static Button sender;
    private static Label part;
    private static ListView list;


    @FXML
    private void initialize(){
        indx = counter;
        counter++;
        send.setDisable(true);
        outgoing.setDisable(true);
        participants.setVisible(false);
        noFriend.setManaged(false);
        noFriend.setVisible(false);
        chatView.setDividerPositions(0);

        log.put(names.get(indx), this);

        text = outgoing;
        board = incoming;
        sender = send;
        part = participants;
        list = listView;

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue == null) return;
                if(newValue.split(";").length != 1){
                    updateList(newValue);
                    return;
                }
                else if(friend_list.get(name).contains(newValue) && friend_list.get(newValue).contains(name)){
                    updateList(newValue);
                    return;
                }
                incoming.clear();
                participants.setVisible(false);
                send.setDisable(true);
                outgoing.setDisable(true);
                Stage friendRequest = new Stage();
                friendRequest.initModality(Modality.WINDOW_MODAL);
                friendRequest.setTitle("Friend Request");

                Pane pane = new Pane();
                Label request = null;
                if(!friend_requests.get(name).contains(newValue)) {
                    request = new Label("Do you want to send a friend Request to " + newValue + "?");
                } else{
                    request = new Label("Do you want to be friends with " + newValue + "?");
                }
                request.setLayoutX(125);
                request.setLayoutY(120);
                Button yes = new Button("yes");
                yes.setLayoutX(200);
                yes.setLayoutY(145);
                Button no = new Button("no");
                no.setLayoutX(280);
                no.setLayoutY(145);

                pane.getChildren().addAll(request, yes, no);

                yes.setOnAction(event -> {
                    if(!friend_requests.get(name).contains(newValue)) {
                        friend_requests.get(newValue).add(name);
                        //friend_requests.get(name).add(newValue);
                        friendRequest.close();
                        //addFriend();
                    } else{
                        int rem_indx = friend_requests.get(name).indexOf(newValue);
                        friend_requests.get(name).remove(rem_indx);
                        friend_list.get(name).add(newValue);
                        friend_list.get(newValue).add(name);
                        friendRequest.close();
                        //requests();
                    }
                });
                no.setOnAction(event -> {
                    if (!friend_requests.get(name).contains(newValue)) {
                        friendRequest.close();
                    } else {
                        int rem_indx = friend_requests.get(name).indexOf(newValue);
                        friend_requests.get(name).remove(rem_indx);
                        rem_indx = friend_requests.get(newValue).indexOf(name);
                        friend_requests.get(newValue).remove(rem_indx);
                        friendRequest.close();
                        //requests();
                    }
                });

                friendRequest.setScene(new Scene(pane, 500, 250));
                friendRequest.showAndWait();
            }
        });
    }
    private void updateList(String newValue){
        int index = 0;
        int size = 0;
        String[] temp = newValue.split(";");
        participants.setVisible(true);
        participants.setText("Chatting with " + newValue);
        send.setDisable(false);
        outgoing.setDisable(false);
        ArrayList<String> people = new ArrayList<String>();
        for(String s: temp) people.add(s);
        if(temp.length == 1) people.add(name);
        for(int i = 0; i < chat_list.size(); i++){
            for(String str: people){
                if(chat_list.get(i).contains(str)){
                    index++;
                } else break;
            }
            if(index == people.size()){
                size = index;
                index = i;
                break;
            }
            index = 0;
        }
        if(size != people.size()) {
            if(temp.length > 1) {
                for (String str : temp) {
                    for (int i = 0; i < clients.size(); i++) {
                        boolean a = clients.get(i).name.equals(str);
                        boolean b = !clients.get(i).chatnames.contains(group_name);
                        if (clients.get(i).name.equals(str) && !clients.get(i).chatnames.contains(group_name)) {
                            friend_list.get(clients.get(i).name).add(newValue);
                            break;
                        }
                    }
                }
                active_chat.put(newValue, uniqueName);
            }
            chat_history.put(uniqueName, incoming.getText());
            chat_list.put(uniqueName, people);
            active_chat.put(name, uniqueName);
            if(!active_chat.containsKey(newValue)) active_chat.put(newValue, -1);
            uniqueName++;
        } else{
            active_chat.put(name, index);
            incoming.clear();
            incoming.setText(chat_history.get(index));
        }
    }

    @FXML
    private void quit(){
        System.exit(0);
    }
    @FXML
    private void availableFriends(){
        chatView.setDividerPositions(0);
        noFriend.setDisable(true);
        noFriend.setVisible(false);
        noFriend.setManaged(false);
        listView.setDisable(false);
        create.setDisable(true);
        addNames.setDisable(true);
        add.setDisable(true);
        ClientRunnable cl = clients.get(indx);
        String name = cl.name;
        ArrayList<String> friend = friend_list.get(name);
        listView.getItems().clear();
        for(String str: friend){
            listView.getItems().add(str);
        }
        cl.chatnames = friend_list.get(name);
    }

    @FXML
    private void addFriend(){
        chatView.setDividerPositions(0);
        noFriend.setDisable(true);
        noFriend.setVisible(false);
        noFriend.setManaged(false);
        listView.setDisable(false);
        create.setDisable(true);
        addNames.setDisable(true);
        add.setDisable(true);
        ClientRunnable cl = clients.get(indx);
        name = cl.name;
        ArrayList<String> friend = friend_list.get(name);
        listView.getItems().clear();
        for(ClientRunnable c: clients){
            if(c.name != cl.name) {
                if(c.name != cl.name) {
                    if(!friend_requests.get(name).contains(c.name)) {
                        if (friend == null) {
                            listView.getItems().add(c.name);
                        } else if (!friend.contains(c.name)) {
                            listView.getItems().add(c.name);
                        }
                    }
                }
            }
        }
    }


    @FXML
    private void deleteFriend(){
        chatView.setDividerPositions(0);
        noFriend.setDisable(true);
        noFriend.setVisible(false);
        noFriend.setManaged(false);
        listView.setDisable(false);
        create.setDisable(true);
        addNames.setDisable(true);
        add.setDisable(true);

    }
    @FXML
    private void logout(){
        for(ClientRunnable a: clients){
            if(a.btn == this.send){
                a.client.close();
                break;
            }
        }

    }
    @FXML
    private void requests(){
        chatView.setDividerPositions(0);
        noFriend.setDisable(true);
        noFriend.setVisible(false);
        noFriend.setManaged(false);
        listView.setDisable(false);
        create.setDisable(true);
        addNames.setDisable(true);
        add.setDisable(true);
        ClientRunnable cl = clients.get(indx);
        name = cl.name;
        ArrayList<String> friend = friend_requests.get(name);
        listView.getItems().clear();
        for(String str: friend){
            listView.getItems().add(str);
        }
        cl.chatnames = friend_list.get(name);
    }
    @FXML
    private void groupCreator(){
        chatView.setDividerPositions(0.2964);
        noFriend.setDisable(true);
        noFriend.setVisible(false);
        noFriend.setManaged(false);
        listView.setDisable(true);
        listView.getItems().clear();
        create.setDisable(false);
        addNames.setDisable(false);
        add.setDisable(false);
        group_name = clients.get(indx).name;
        noFriend.setStyle("-fx-text-fill: red;");
    }
    @FXML
    private void addContact(){
        if(!addNames.getText().isEmpty()){
            if(friend_list == null || friend_list.size() == 0){
                noFriend.setText(addNames.getText() + " is not a friend");
                noFriend.setManaged(true);
                noFriend.setDisable(false);
                noFriend.setVisible(true);
            }
            else if(friend_list.get(clients.get(indx).name).contains(addNames.getText())){
                noFriend.setManaged(false);
                noFriend.setDisable(true);
                noFriend.setVisible(false);
                group_name = group_name + ";" + addNames.getText();
                listView.getItems().add(addNames.getText());
            } else{
                noFriend.setText(addNames.getText() + " is not a friend");
                noFriend.setManaged(true);
                noFriend.setDisable(false);
                noFriend.setVisible(true);

            }
            addNames.clear();
        }
    }
    @FXML
    private void createChat(){
        int counter = 0;
        boolean flag = false;
        String[] temp = group_name.split(";");
        for(String str: temp){
            for(int i = 0; i < clients.size(); i++){
                if(clients.get(i).name == str && clients.get(i).chatnames.contains(group_name)){
                    counter++;
                }
                if(counter == temp.length) {
                    flag = true;
                    break;
                }
            }
        }
        if(listView.getItems().size() == 0){
            noFriend.setText("No names available");
            noFriend.setManaged(true);
            noFriend.setDisable(false);
            noFriend.setVisible(true);
        } else if(listView.getItems().size() == 1 || flag){
            noFriend.setText("Chat exists");
            noFriend.setManaged(true);
            noFriend.setDisable(false);
            noFriend.setVisible(true);
        } else{
            listView.getItems().clear();
            noFriend.setText("Chat Created");
            noFriend.setStyle("-fx-text-fill: Green;");
            noFriend.setManaged(true);
            noFriend.setDisable(false);
            noFriend.setVisible(true);
            friend_list.get(clients.get(indx).name).add(group_name);
        }
    }

    public class ClientRunnable implements Runnable{
        private ArrayList<String> chatnames;
        private Socket sock;
        private TextField input;
        private TextArea message;
        private Button btn;
        public String name;
        private BufferedReader reader;
        private PrintWriter writer;
        private Label particip;
        private Stage client;
        ClientRunnable(String name, Socket sock){
            this.name = name;
            client = new Stage();
            names.add(this.name);
            try {
                InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(streamReader);
                writer = new PrintWriter(sock.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.sock = sock;
            try {
                setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setUp() throws Exception{
            Parent root = FXMLLoader.load(getClass().getResource("ClientCreator.fxml"));
            this.message = board;
            this.input = text;
            this.btn = sender;
            runable.put(name, this);
            this.particip = part;
            friend_list.put(name, new ArrayList<>());
            friend_requests.put(name, new ArrayList<>());
            chat_area.put(name, this.message);

            btn.setOnAction(event -> {
                if(!input.getText().isEmpty()) {
                    int index = -1;
                    String temp = "";
                    String[] a = particip.getText().substring(14).split(";");
                    ArrayList<String> peeps = new ArrayList<String>();
                    if(a.length == 1) peeps.add(name);

                    for(int k = 0; k < a.length; k++){
                        peeps.add(a[k]);
                    }

                    for (int i = 0; i < chat_list.size(); i++) {
                        if(chat_list.get(i).size() == peeps.size()) {
                            int cnt = 0;
                            for(String str: chat_list.get(i)){
                                if(!peeps.contains(str)){
                                    break;
                                }
                                cnt++;
                            }
                            if(cnt == peeps.size()){
                                index = i;
                                break;
                            }
                        }
                    }
                    for (String str : chat_list.get(index)) {
                        if (temp.isEmpty()) {
                            temp = str;
                        } else {
                            temp = temp + ";" + str;
                        }
                    }
                    this.writer.println("@" + Integer.toString(index) + ":" + name + ":" + temp + ":" + input.getText());
                    this.writer.flush();
                    this.input.clear();
                    this.input.requestFocus();
                }
            });

            client.setTitle(name + "'s chat");
            Scene scene = new Scene(root, 600, 500);
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
            String message;
            String[] m;
            try {
                while ((message = reader.readLine()) != null) {
                    if (message.charAt(0) == '&') {
                        String[] temp = message.split(";");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                log.get(temp[0].substring(1)).runable.get(temp[0].substring(1)).client.show();
                            }
                        });
                    } else {
                        Thread.sleep(0);
                        synchronized (sender) {
                            m = message.split(":", 4);
                            String chat_name = m[0].substring(1);
                            if (m[1].equals(name)) {
                                this.message.setText(chat_history.get(Integer.parseInt(chat_name)));
                                this.message.appendText("<" + m[1] + "> " + m[3] + "\n");
                                chat_history.put(Integer.parseInt(chat_name), this.message.getText());
                            }
                            updateMessage(m[2].split(";"));
                        }
                        System.out.print("");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        private synchronized void updateMessage(String[] recepients) {
            for (String str : recepients) {
                if(active_chat.containsKey(str)) {
                    int current_chat = active_chat.get(str);
                    if (current_chat != -1)
                        chat_area.get(str).setText(chat_history.get(current_chat));
                }
            }
        }
    }
}
