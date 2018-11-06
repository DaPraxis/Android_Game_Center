package fall2018.csc2017.slidingtiles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

public class ScoreBoardActivity extends AppCompatActivity {
    private UserAccount user;
    private UserAccountManager users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        getUsers();
        getBoard();
        addBackToHomePageButton();
    }

    private void getUsers(){
        Intent intentExtras = getIntent();
        Bundle extra = intentExtras.getExtras();
        this.user=(UserAccount) extra.getSerializable("user");
        this.users = (UserAccountManager) extra.getSerializable("allUsers");
    }

    private void getBoard() {
        Intent intentExtras = getIntent();
        Bundle extra = intentExtras.getExtras();
        ScoreBoard personalScoreBoard = (ScoreBoard) extra.getSerializable("personalScoreBoard");
        ScoreBoard globalScoreBoard = (ScoreBoard) extra.getSerializable("globalScoreBoard");
        setPersonalBoard((ArrayList<Object[]>) personalScoreBoard.getScoreList());
//        setGlobalBoard((ArrayList<Object[]>) globalScoreBoard.getScoreList());
    }

    private void addBackToHomePageButton() {
        Button startButton = findViewById(R.id.homePageButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToHomePage();
            }
        });
    }

    private void switchToHomePage() {
        Intent tmp = new Intent(this, GameCenterActivity.class);
        Bundle pass = new Bundle();
        pass.putSerializable("user",this.user);
        pass.putSerializable("allUsers", this.users);

        tmp.putExtras(pass);
        startActivity(tmp);
    }

    private void setPersonalBoard(ArrayList<Object[]> scoreList) {

//        int[] name_list = {
//                R.id.Name0,
//                R.id.Name1,
//                R.id.Name2,
//                R.id.Name3,
//                R.id.Name4,
//                R.id.Name5,
//                R.id.Name6,
//                R.id.Name7,
//                R.id.Name8,
//                R.id.Name9,
//                R.id.Name10
//        };
//
//        int[] score_list = {
//                R.id.Score0,
//                R.id.Score1,
//                R.id.Score2,
//                R.id.Score3,
//                R.id.Score4,
//                R.id.Score5,
//                R.id.Score6,
//                R.id.Score7,
//                R.id.Score8,
//                R.id.Score9,
//                R.id.Score10
//        };

        for (int i = 0; i < 11; i++) {
            String name = "Name" + i;
            String score = "Score" + i;
            TextView t1 = findViewById
                    (getResources().getIdentifier(name, "id", getPackageName()));
            TextView t2 = findViewById
                    (getResources().getIdentifier(score, "id", getPackageName()));

            if (i < scoreList.size()) {
            t1.setText((String) scoreList.get(i)[0]);
            t2.setText(String.valueOf(scoreList.get(i)[1]));
            }

            else {
                t1.setText("");
                t2.setText("");
            }
        }
    }
}


