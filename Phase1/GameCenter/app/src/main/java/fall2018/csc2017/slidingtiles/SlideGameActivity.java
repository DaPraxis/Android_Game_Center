package fall2018.csc2017.slidingtiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class SlideGameActivity extends AppCompatActivity {
    /**
     * user that is operating system
     */
    private UserAccount user;

    /**
     * all users from database
     */
    private UserAccountManager users;

    /**
     * scoreboard of user
     */
    private ScoreBoard scoreBoard;

    /**
     * boardManager in user
     */
    private BoardManager boardManager;

    /**
     * board type judgement
     */
    private String boardType = "Personal";


    /**
     * Initialize all buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUsers();
        setContentView(R.layout.activity_slide_game);
        addStartButtonListener();
        addLoadButtonListener();
        addResumeButtonListener();
        addPersonalScoreBoardListener();
        addGlobalScoreBoardListener();
}

    /**
     * add global scoreboard button
     */
    private void addGlobalScoreBoardListener() {
        Button globalRank = findViewById(R.id.button6);
        globalRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardType = "Global";
                choseLevel();
            }
        });
    }

    /**
     * add personal scoreboard button
     */
    private void addPersonalScoreBoardListener() {
        Button rank = findViewById(R.id.button5);
        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardType = "Personal";
                choseLevel();
            }
        });
    }

    /**
     * choose game difficulty to view scoreboard
     */
    private void choseLevel() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SlideGameActivity.this);
        builder.setTitle("Choose an level");
        // add a list
        String[] memoryList = {"3x3", "4x4","5x5"};
        builder.setItems(memoryList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // 3x3
                        if (boardType.equals("Personal")) {
                            scoreBoard = user.getScoreBoard("history3x3");
                        }
                        else {
                            scoreBoard = users.getSlideTilesGlobalScoreBoard("history3x3");
                        }
                        break;
                    case 1: // 4x4
                        if (boardType.equals("Personal")) {
                            scoreBoard = user.getScoreBoard("history4x4");
                        }
                        else {
                            scoreBoard = users.getSlideTilesGlobalScoreBoard("history4x4");
                        }
                        break;
                    case 2: // 5x5
                        if (boardType.equals("Personal")) {
                            scoreBoard = user.getScoreBoard("history5x5");
                        }
                        else {
                            scoreBoard = users.getSlideTilesGlobalScoreBoard("history5x5");
                        }
                        break;
                }
                if(scoreBoard != null){
                    switchToScoreBoard();
                }
                else{
                    builder.setMessage("No record :)");
                    AlertDialog d = builder.create();
                    d.show();
                }

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * start scoreboard activity
     */
    private void switchToScoreBoard() {
        Intent tmp = new Intent(this, ScoreBoardActivity.class);
        Bundle pass = new Bundle();
        pass.putSerializable("user",this.user);
        pass.putSerializable("allUsers", this.users);
        pass.putSerializable("scoreBoard", this.scoreBoard);
        tmp.putExtras(pass);
        startActivity(tmp);
    }

    /**
     * get user info and data from last class
     */
    private void getUsers(){
        Intent intentExtras = getIntent();
        Bundle extra = intentExtras.getExtras();
        assert extra != null;
        this.user=(UserAccount) extra.getSerializable("user");
        this.users = (UserAccountManager) extra.getSerializable("allUsers");
    }


    /**
     * add start button
     */
    private void addStartButtonListener() {
        Button startButton = findViewById(R.id.NewGameButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToDifficulty();
            }
        });
    }

    /**
     * add resume button
     */
    private void addResumeButtonListener() {
        Button startButton = findViewById(R.id.ResumeButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardManager = user.getHistory().get("resumeHistory");
                if (boardManager != null) {
                    try {
                        boardManager = (BoardManager) user.getHistory().get("resumeHistory").clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    user.getHistory().put("resumeHistory", null);
                    switchToGame();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder
                            (SlideGameActivity.this);
                    builder.setMessage("History not found");
                    AlertDialog d = builder.create();
                    d.show();
                }
            }
        });
    }

    /**
     * add load button
     */
    private void addLoadButtonListener() {
        Button startButton = findViewById(R.id.LoadButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
    }

    /**
     * start SlideDifficultActivity and pass useful info and data to next activity
     */
    private void switchToDifficulty(){
        Intent tmp = new Intent(this, SlideDifficultyActivity.class);
        Bundle pass = new Bundle();
        pass.putSerializable("user",this.user);
        pass.putSerializable("allUsers", this.users);
        tmp.putExtras(pass);
        startActivity(tmp);}

    /**
     * start playing game with MainSlideActivity
      */
    private void switchToGame() {
        Intent tmp = new Intent(this, MainSlideActivity.class);
        Bundle pass = new Bundle();
        pass.putSerializable("user",this.user);
        pass.putSerializable("allUsers", this.users);
        pass.putSerializable("boardManager", this.boardManager);
        tmp.putExtras(pass);
        startActivity(tmp);
    }

    /**
     * get correct user info
     */
    private void updateUser(){
        loadFromFile(UserAccountManager.USERS);
        for (UserAccount u: users.getUserList())
            if (u.getName().equals(user.getName())) {
                user = u;
            }
    }

    /**
     * load game
     */
    private void load(){
        updateUser();
        final AlertDialog.Builder builder = new AlertDialog.Builder(SlideGameActivity.this);
        builder.setTitle("Choose an memory");
        // add a list
        String[] memoryList = {"3x3", "4x4","5x5"};
        builder.setItems(memoryList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // 3x3
                            boardManager = user.getHistory().get("history3x3");
                        break;
                    case 1: // 4x4
                            boardManager = user.getHistory().get("history4x4");
                        break;
                    case 2: // 5x5
                            boardManager = user.getHistory().get("history5x5");
                        break;
                }
                if(boardManager!=null){
                    try {
                        boardManager = (BoardManager) boardManager.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    switchToGame();
                }
                else{
                    builder.setMessage("History not found");
                    AlertDialog d = builder.create();
                    d.show();
                }

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * load from saved database
     * @param fileName name of database
     */
    private void loadFromFile(String fileName) {

        try {
            InputStream inputStream = this.openFileInput(fileName);
            if (inputStream != null) {
                ObjectInputStream input = new ObjectInputStream(inputStream);
                users = (UserAccountManager) input.readObject();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } catch (ClassNotFoundException e) {
            Log.e("login activity", "File contained unexpected data type: " + e.toString());
        }
    }
}






