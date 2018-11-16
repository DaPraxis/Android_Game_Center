package fall2018.csc2017.slidingtiles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represent a user's user account.
 */
public class UserAccount implements Serializable {

    /**
     * The username of the UserAccount
     */
    private String name;

    /**
     * The password of the UserAccount.
     */
    private String password;

    /**
     * The sliding tiles game history of the UserAccount.
     */
    private HashMap<String, BoardManager> history = new HashMap<>();

    /**
     * The sliding tiles score list of the UserAccount
     */
    private ArrayList<Integer> userScoreList = new ArrayList<>();

    /**
     * The archive of the game sliding tiles.
     */
    private HashMap<String,ScoreBoard> personalScoreBoard = new HashMap<>();

    /**
     * The UserAccount class constructor.
     * @param name the name of the UserAccount
     * @param password
     */
    public UserAccount(String name, String password){
        this.name=name;
        this.password=password;
        history.put("history3x3", null);
        history.put("history4x4", null);
        history.put("history5x5",null);
        history.put("resumeHistory", null);
        personalScoreBoard.put("history3x3", new ScoreBoard());
        personalScoreBoard.put("history4x4", new ScoreBoard());
        personalScoreBoard.put("history5x5", new ScoreBoard());

    }

    /**
     * Add a new game to the UserAccount.
     * @param name The name of the new game being added.
     */
    public void addGame(String name){
        history.put(name, null);
    }

    /**
     * Change the username of the UserAccount.
     * @param name the new name to be updated.
     */
    public void changeName(String name){
        this.name=name;
    }

    /**
     * Change the password of the UserAccount.
     * @param ps The new password to be updated.
     */
    public void changePassword(String ps){
        this.password=ps;
    }

    /**
     * The getter for the username of the UserAccount.
     * @return the username
     */
    public String getName(){
        return name;
    }

    /**
     * The getter for the password of the UserAccount.
     * @return the password
     */
    public String getPassword(){
        return password;
    }

    /**
     * The getter for the UserScoreList of the UserAccount.
     * @return the UserScoreList.
     */
    public ArrayList<Integer> getUserScoreList(){return this.userScoreList;}

    public boolean setHistory(String key, BoardManager item){
        if (history.get(key) == null) {
            history.put(key, item);
            return true;
        }
        else {
            history.replace(key, item);
            return false;
        }
    }

    /**
     * The getter for the History of the UserAccount.
     * @return the History
     */
    public HashMap<String, BoardManager> getHistory(){
        return history;
    }

    /**
     * Get the right size of ScoreBoard wanted.
     * @param tag the size of the ScoreBoard.
     * @return the ScoreBoard wanted
     */
    public ScoreBoard getScoreBoard(String tag) {
        return personalScoreBoard.get(tag);
    }
}
