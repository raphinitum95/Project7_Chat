package assignment7;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.HashMap;

public class LogInController {
    private static HashMap<String, String> userLog = new HashMap<>();
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label error;
    @FXML
    private TextField IP;
    @FXML
    private void logIn(){
        if(username.getText().isEmpty() || password.getText().isEmpty()){
            error.setStyle("-fx-text-fill: red;");
            error.setText("Please fill all the required fields");
            error.setVisible(true);
            error.setManaged(true);
            return;
        }
        if(!userLog.containsKey(username.getText())){
            error.setStyle("-fx-text-fill: red;");
            error.setText("Username does not exist");
            error.setVisible(true);
            error.setManaged(true);
        } else{
            if(!password.getText().equals(userLog.get(username.getText()))){
                error.setStyle("-fx-text-fill: red;");
                error.setText("Password does not match that on record");
                error.setVisible(true);
                error.setManaged(true);
            } else{
                error.setVisible(false);
                error.setManaged(false);
                String address;
                if(IP.getText().isEmpty()) address = "localhost";
                else address = IP.getText();
                try {
                    new ClientMain().setUpNetworking(address, username.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            password.clear();
            username.clear();
            IP.clear();
        }
    }
    @FXML
    private void createAccount(){
        if(userLog.containsKey(username.getText()) || username.getText().isEmpty()){
            error.setStyle("-fx-text-fill: red;");
            error.setText("Username is not Available");
            error.setVisible(true);
            error.setManaged(true);
        } else if(password.getText().isEmpty()){
            error.setStyle("-fx-text-fill: red;");
            error.setText("Please provide a Password");
            error.setVisible(true);
            error.setManaged(true);
        } else if(username.getText().contains("@") || username.getText().contains(":") || username.getText().contains(";")){
            error.setStyle("-fx-text-fill: red;");
            error.setText("Username cannot contain '@', ':' or ';'");
            error.setVisible(true);
            error.setManaged(true);
        } else if(username.getText().contains(" ")){
            error.setStyle("-fx-text-fill: red;");
            error.setText("Username cannot contain spaces");
            error.setVisible(true);
            error.setManaged(true);
        } else{
            userLog.put(username.getText(), password.getText());
            error.setStyle("-fx-text-fill: green;");
            error.setText("Account was created Successfully. Please Click the Log In button to continue");
            error.setVisible(true);
            error.setManaged(true);
        }
    }
}
