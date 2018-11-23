package fall2018.csc2017.slidingtiles;

import android.content.Context;
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
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import fall2018.csc2017.slidingtiles.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    DatabaseHelper myDB;
    /**
     * EditText view for username
     */
    EditText etUsername;

    /**
     * EditText view for password
     */
    EditText etPassword;

    /**
     * Button for log in
     */
    Button bLogIn;

    /**
     * TextView for register, functions as a button
     */
    TextView registerLink;

    /**
     * UserAccountManager
     */
    UserAccountManager userAccountManager;

    /**
     * TextView to set messages.
     */
    TextView massage;

    /**
     * Initialize login in activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new DatabaseHelper(this);
        if (!myDB.ifAccountManagerExists()) {
            myDB.createUserAccountManager();
        }
        setContentView(R.layout.activity_login);
        etUsername= findViewById(R.id.etUsername);
        etPassword= findViewById(R.id.password);
        bLogIn= findViewById(R.id.bLogin);
        massage =findViewById(R.id.massage);
        registerLink = findViewById(R.id.registerLink);
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });


        loginButtonPushed();
    }

    /**
     *  Actions after login in button is triggered, jump to game center activity while passing info to
     *  game center activity.
     */
    private void loginButtonPushed(){
        bLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFromFile();
                if (userAccountManager ==null){
                    userAccountManager = new UserAccountManager();
                }
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                UserAccount user = checkUserId(username,password);
                if(user!=null){
                    Intent loginIntent = new Intent(LoginActivity.this, MainInfoPanelActivity.class);
                    Bundle pass = new Bundle();
//                    pass.putSerializable("user",user);
//                    pass.putSerializable("allUsers", userAccountManager);
                    loginIntent.putExtras(pass);
                    // pass user name to global context using application singleton
                    // so that all activity knows current user
                    DataHolder.getInstance().save("current user", user.getName());
                    LoginActivity.this.startActivity(loginIntent);
            }}
        });
    }

    /**
     * Check if tempted user has legit id and username
     * @param username the username to check
     * @param password the password to check
     * @return the userAccount if the username and password are correctly matching
     */
    private UserAccount checkUserId(String username, String password){
        Context context = getApplicationContext();
        UserAccount account = myDB.selectUser(username);
            if(account != null && account.getPassword().equals(password)){
                Toasty.success(context, "Welcome!", Toast.LENGTH_SHORT, true).show();
                return account;
            }
        Toasty.error(context, "Wrong Username or Password", Toast.LENGTH_SHORT, true).show();
        return null;}

    /**
     * load from pre-saved .ser file.
     */
    private void loadFromFile() {
        try {
            InputStream inputStream = this.openFileInput(UserAccountManager.USERS);
            if (inputStream != null) {
                ObjectInputStream input = new ObjectInputStream(inputStream);
                userAccountManager = (UserAccountManager) input.readObject();
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




