package assignment7;

/**
 * Created by Raphael on 11/27/16.
 */
public class ServerMain {
    public static void main(String[] args){
        Server server = new Server();
        // Thread not necessary, since there is only one thread.
        new Thread(new Runnable () {
            @Override public void run() {
                server.runme();
            }
        }).start();
    }
}
