package fall2018.csc2017.slidingtiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import fall2018.csc2017.slidingtiles.database.DatabaseHelper;

/**
 * The game activity.
 */
public class MineMainActivity extends AppCompatActivity implements IObserver {
    DatabaseHelper myDB;
    /**
     * The per-user scoreboard
     */
    private ScoreBoard personalScoreBoard;

    /**
     * The per-game scoreboard
     */
    private ScoreBoard globalScoreBoard;

    /**
     * If a game is paused.
     */
    private boolean isPaused;

    /**
     * The MineBoard manager.
     */
    private MineBoardManager boardManager;

    /**
     * The buttons to display.
     */
    private ArrayList<Button> tileButtons;

    /**
     * Grid View and calculated column height and width based on device size.
     */
    private GestureDetectGridView gridView;
    private static int columnWidth, columnHeight;

    /**
     * Time count.
     */
    private int count = 0;

    /**
     * Time count for autosave purpose.
     */
    private int tempCount = 0;

    /**
     * UserAccount associated to the game.
     */
    private UserAccount user;

    /**
     * Dimension of the MineBoard.
     */
    private int dimension;

    private int mine;

    private Thread t;

    private ImageButton face;

    /**
     * UserAccountManager associated to the UserAccount.
     */
    private UserAccountManager users;
    private String currentUser;

    /**
     * Mute the slidingTiles in grid view
     */
    private boolean isMutted;

    /**
     * Update the backgrounds on the buttons to match the slidingTiles.
     */
    private void updateTileButtons() {
        MineBoard board = boardManager.getBoard();
        int nextPos = 0;
        for (Button b : tileButtons) {
            b.setBackgroundResource(board.getTile(nextPos).getBackground());
            nextPos++;
        }
    }

    void updateMineLeft(){
        final TextView mineLeft = findViewById(R.id.mineLeft);
        mineLeft.setText("Mine: " + String.valueOf(boardManager.getBoard().getMineLeft()));
    }

    /**
     * Set up the background image for each button based on the master list
     * of positions, and then call the adapter to set the view.
     */
    public void display() {
        updateTileButtons();
        updateMineLeft();
        gridView.setAdapter(new CustomAdapter(tileButtons, columnWidth, columnHeight));
    }

    /**
     * Switch to GameCenterActivity.
     */
    private void switchToGameCenter() {
        Intent intent = new Intent(this, InfoPanelMainActivity.class);
        Bundle pass = new Bundle();
        myDB.updateAccountManager(users);
        myDB.updateUser(currentUser,user);
        pass.putString("fragment", "Mine");
        intent.putExtras(pass);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        isPaused = true;
        boardManager.setTime(count);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to save/override this game?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveHistory(dialog);
                        switchToGameCenter();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switchToGameCenter();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isPaused = false;
                dialog.cancel();
            }
        });
        alert.show();

    }

    /**
     * Return the score of the finished game.
     * @return the score of the finished game.
     */
    private int getScore(){
        int score;
        score = personalScoreBoard.calculateScore(boardManager);
        Object[] result = new Object[2];
        result[0] = user.getName();
        result[1] = score;
        personalScoreBoard.addAndSort(result);
        globalScoreBoard.addAndSort(result);
        myDB.updateAccountManager(users);
        myDB.updateUser(currentUser,user);
        return score;

    }

    /**
     * Save the game history.
     * @param dialog
     */
    private void saveHistory(DialogInterface dialog){
        user.setMineHistory("historyMine", boardManager);
        myDB.updateUser(currentUser,user);
        dialog.cancel();
    }

    /**
     * Automatically save the game for resuming.
     */
    private void autoSave(){
        boardManager.setTime(count);
        user.setMineHistory("resumeHistoryMine", boardManager);
        myDB.updateUser(currentUser,user);
    }


    /**
     * Initialize all buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new DatabaseHelper(this);
        getAllInfo(); // pass in all useful data from last activity, including boardManager
        setContentView(R.layout.activity_minesweeper);
        face = (ImageButton) findViewById(R.id.Thomas);
        face.setImageResource(R.drawable.normal);
        createTileButtons(this);
        t = time();
        t.start();

        // Add View to activity
        setGridView();
        addStartOver();
    }

    private void setGridView(){
        gridView = findViewById(R.id.mine_grid);
        if (gridView.isFrozen()){
            gridView.cloneAsThawed();
        }
        gridView.setNumColumns(boardManager.getBoard().getDimension ());
        gridView.setBoardBoardManager(boardManager);
        boardManager.getBoard().addObserver(this);
        // Observer sets up desired dimensions as well as calls our display function
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        gridView.getViewTreeObserver().removeOnGlobalLayoutListener(
                                this);
                        int displayWidth = gridView.getMeasuredWidth();
                        int displayHeight = gridView.getMeasuredHeight();

                        columnWidth = displayWidth / boardManager.getBoard().getDimension ();
                        columnHeight = displayHeight / boardManager.getBoard().getDimension ();

                        display();

                    }
                });
    }


    private void addStartOver(){
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused = false;
//                face.setImageResource(R.drawable.normal);
                boardManager = new MineBoardManager(dimension,mine);
                boardManager.setTime(-1);
                createTileButtons(getApplicationContext());
                setGridView();
            }
        });
    }

    /**
     * Alert when a puzzle is solved.
     */
    private void winAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MineMainActivity.this);
        int score = getScore();
        builder.setMessage("you got " + String.valueOf(score) + " !")

                .setPositiveButton("See my rank", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switchToScoreBoard();
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        user.setMineHistory("resumeHistoryMine", null);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Alert when a puzzle is solved.
     */
    private void loseAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MineMainActivity.this);
        int score = getScore();
        builder.setMessage("You have lost! you got " + String.valueOf(score) + " !")

                .setPositiveButton("See my rank", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switchToScoreBoard();
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        user.setMineHistory("resumeHistoryMine", null);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Switch to ScoreBoard activity/view.
     */
    private void switchToScoreBoard(){
        Intent tmp = new Intent(this, ScoreBoardTabLayoutActivity.class);
        Bundle pass = new Bundle();
        myDB.updateUser(currentUser, user);
        myDB.updateAccountManager(users);
        pass.putSerializable("personalScoreBoard",personalScoreBoard);
        pass.putSerializable("globalScoreBoard",globalScoreBoard);

        tmp.putExtras(pass);
        startActivity(tmp);
    }

    private Thread time(){
        final TextView time = findViewById(R.id.time);
        count = boardManager.getTime();
        isPaused = false;
        Thread t = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted()) {
                    if (!isPaused) {
                        try {
                            Thread.sleep(100);
                            if (boardManager.isWon()) {
                                this.interrupt();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (boardManager.isWon() && !isPaused) {
                                        boardManager.setTime(count);
                                        isPaused = true;
                                        user.setMineHistory("resumeHistoryMine", null);
                                        face.setImageResource(R.drawable.win);
                                        gridView.freeze();
                                        winAlert();
                                    }
                                    else if (boardManager.isLost() && !isPaused){
                                        boardManager.setTime(count);
                                        isPaused = true;
                                        user.setMineHistory("resumeHistoryMine", null);
                                        face.setImageResource(R.drawable.sad);
                                        gridView.freeze();
                                        loseAlert();
                                    }
                                    else {
                                        count = boardManager.getTime();
                                        count += 1;
                                        boardManager.setTime(count);
                                        if (tempCount < 2) {
                                            tempCount += 1;
                                        } else {
                                            autoSave();
                                        }
                                        time.setText("Time: " +String.format("%03d", count/10) + " s");
                                    }
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        return t;}

    /**
     * Receive all the info(User, Size, SlidingBoardManager, ScoreBoards)from previous activity/view.
     * @throws CloneNotSupportedException
     */
    private void getAllInfo() {
        Intent intentExtras = getIntent();
        Bundle extra = intentExtras.getExtras();

        currentUser = (String)DataHolder.getInstance().retrieve("current user");
        this.user = myDB.selectUser(currentUser);
        this.users = myDB.selectAccountManager();

        this.boardManager = (MineBoardManager) extra.getSerializable("boardManager");
        dimension = boardManager.getBoard().getDimension ();
        mine = boardManager.getBoard().getMine();
        assert this.boardManager != null;
        this.personalScoreBoard = user.getScoreBoard("Mine");
        this.globalScoreBoard = users.getGlobalScoreBoard("Mine");
    }

    /**
     * Create the buttons for displaying the slidingTiles.
     * @param context the context
     */
    private void createTileButtons(final Context context) {
        isMutted = false;
        MineBoard board = boardManager.getBoard();
        tileButtons = new ArrayList<>();
        for (int i = 0; i < dimension * dimension; i++) {
            Button tmp = new Button(context);
            tmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    face.setImageResource(R.drawable.surprise);
                }
            });
            tmp.setBackgroundResource(board.getTile(i).getBackground());
            this.tileButtons.add(tmp);
        }
    }


    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void update(IObservable o) {
        display();
        face.setImageResource(R.drawable.normal);
    }


}
