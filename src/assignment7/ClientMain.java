package assignment7;

/**
 * Created by Raphael on 11/27/16.
 */
public class ClientMain {
    public static void main(String[] args){
        Client client = new Client();
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.runme();
            }
        }).start();
    }
}
