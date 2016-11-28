package assignment7;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class Controller{
    private static ArrayList<String> names = new ArrayList<>();
    private static ArrayList<Integer> ports = new ArrayList<>();
    private static int port = 1999;
    @FXML
    private Pane pane;
    @FXML
    private TextField IPAddress;
    @FXML
    private TextField Name;
    @FXML
    private Label IPLabel;
    @FXML
    private Label NameLabel;

    public void initialize(){
        IPLabel.setVisible(false);
        IPLabel.setManaged(false);
        NameLabel.setVisible(false);
        NameLabel.setManaged(false);
    }

    @FXML
    private void quitChat(){
        System.exit(0);
    }
    @FXML
    private void createClient(){
        port++;
        String IP = IPAddress.getText();
        if(Name.getText().isEmpty()){
            NameLabel.setText("Please Enter a Name");
            NameLabel.setVisible(true);
            NameLabel.setManaged(true);
            return;
        }
        else if(names.contains(Name.getText())){
            NameLabel.setText("Name is Taken, Please Try Again.");
            NameLabel.setVisible(true);
            NameLabel.setManaged(true);
            return;
        }
        if(IPAddress.getText().isEmpty()) IP = "localhost";
        else IP = IPAddress.getText();


        names.add(Name.getText());
        IPLabel.setVisible(false);
        IPLabel.setManaged(false);
        NameLabel.setVisible(false);
        NameLabel.setManaged(false);
        try {
            new ClientMain().setUpNetworking(IP, port, Name.getText());
            IPAddress.clear();
            Name.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
