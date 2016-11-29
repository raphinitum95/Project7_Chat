package assignment7;

import java.util.ArrayList;
import java.util.Observable;

public class Chat extends Observable{
    public ArrayList<String> ChatUsers = new ArrayList<>();

    Chat(String[] users){
        for(int i = 0; i < users.length; i++){
            ChatUsers.add(users[i]);
        }
    }

    public void changed(){
        setChanged();
    }

    public void unChanged(){
        clearChanged();
    }
}
