package fall2018.csc2017.slidingtiles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import fall2018.csc2017.slidingtiles.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    EditText confirm;
    EditText email;
    EditText age;
    Button bRegister;
    UserAccountManager userAccountManager;
    UserAccount newUser;
    String usernameS;
    String passwordS;
    String confirmPWS;
    String emailS;
    String ageS;
    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDB = new DatabaseHelper(this);
        super.onCreate(savedInstanceState);
        userAccountManager = myDB.selectAccountManager();
        setContentView(R.layout.activity_register);
        updateInfo();

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
                if(registerButtonPushed()) {
                    userAccountManager.addUser(newUser.getName());
                    myDB.updateAccountManager(userAccountManager);
                    myDB.updateUser(newUser.getName(),newUser);
                    Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(registerIntent);
                }
            }
        });
    }

    private void updateInfo(){
        username = findViewById(R.id.username);
        usernameS = this.username.getText().toString();
        password = findViewById(R.id.password);
        passwordS = this.password.getText().toString();
        confirm = findViewById(R.id.confirmpw);
        confirmPWS = confirm.getText().toString();
        email = findViewById(R.id.email);
        emailS = email.getText().toString();
        age = findViewById(R.id.age);
        ageS = age.getText().toString();
        bRegister = findViewById(R.id.bRegister);
    }

    /**
     * Return true when a new account is registered, return false when a username is taken or
     * the confirming password does not match.
     * @return a boolean showing whether an account is successfully registered
     */
    //TODO: age and email imput can be every thing, write a regex, logic is wrong SUMMER!

    private boolean registerButtonPushed() {
        newUser = new UserAccount(usernameS, passwordS);
        if(!validateInfo(usernameS, "^[a-z]{3,7}$")){
            Toasty.error(getApplicationContext(), "Illegal input of username.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        if(!(validateInfo(passwordS, "^[a-z0-9]{1,9}$"))){
            Toasty.error(getApplicationContext(), "Illegal input of password.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        if (!passwordS.equals(confirmPWS)) {
            Toasty.error(getApplicationContext(), "password doesn't match.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        if (!myDB.createAndInsertNew(usernameS, newUser)){
            Toasty.error(getApplicationContext(), "User Name Exists", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        // check for optional settings

        if(!validateInfo(ageS,"^[1-9][0-9]?$")&&!validateInfo(ageS,"^$")){
            Toasty.error(getApplicationContext(), "Illegal input of age.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        if(!validateInfo(emailS, "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")
                &&!validateInfo(ageS,"^$")){
            Toasty.error(getApplicationContext(), "Illegal input of  email address.", Toast.LENGTH_SHORT, true).show();
            return false;
        }

        if (!ageS.isEmpty()){
            newUser.setAge(Integer.parseInt(ageS));}

        if (!emailS.isEmpty()){
            newUser.setEmail(emailS);
        }

        return true;
    }

    private boolean validateInfo(String info, String regex){
        String infoRegex = regex;
        Pattern regexP = Pattern.compile(infoRegex);
        Matcher matcher = regexP.matcher(info);
        return matcher.matches();
    }

}
