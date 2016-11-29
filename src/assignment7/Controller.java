package assignment7;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class Controller{
    public static ArrayList<String> names = new ArrayList<>();
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

    }

    @FXML
    private void quitChat(){
        System.exit(0);
    }
    @FXML
    private void createClient(){
        String IP = "localhost";
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
        //if(IPAddress.getText().isEmpty()) IP = "localhost";
        //else IP = IPAddress.getText();


        /*names.add(Name.getText());
        IPLabel.setVisible(false);
        IPLabel.setManaged(false);
        NameLabel.setVisible(false);
        NameLabel.setManaged(false);*/
        try {
            new ClientMain().setUpNetworking(IP, Name.getText());
            Name.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
