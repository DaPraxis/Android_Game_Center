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

import es.dmoral.toasty.Toasty;
import fall2018.csc2017.slidingtiles.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper myDB = new DatabaseHelper(this);
    EditText username;
    EditText password;
    EditText confirm;
    EditText email;
    EditText age;
    Button bRegister;
    UserAccountManager userAccountManager;
    UserAccount newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFromFile();
        if (userAccountManager == null){
            userAccountManager = new UserAccountManager();
        }
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirmpw);
        email = findViewById(R.id.email);
        age = findViewById(R.id.age);
        bRegister = findViewById(R.id.bRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(registerButtonPushed()) {
                    boolean update = myDB.createAndInsertNew(newUser.getName(), myDB.convertToJson(newUser));
                    if (update) {
                        Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        RegisterActivity.this.startActivity(registerIntent);
                    }
                    else {
                        Toasty.success(getApplicationContext(), "Please Try Again", Toast.LENGTH_SHORT, true).show();
                    }
                }
            }
        });
    }

    /**
     * Return true when a new account is registered, return false when a username is taken or
     * the confirming password does not match.
     * @return a boolean showing whether an account is successfully registered
     */
    private boolean registerButtonPushed() {
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        String confirmPW = confirm.getText().toString();
        String emails = email.getText().toString();
        ArrayList<String> userList = userAccountManager.getUserList();
        newUser = new UserAccount(username, password);
        TextView textView = findViewById(R.id.textView7);
        if(!(age.getText().toString().equals(""))){
            Integer agei = Integer.parseInt(age.getText().toString());
            newUser.setAge(agei);
        }
        if (!email.equals("")){
            newUser.setEmail(emails);
        }
        if (!userList.isEmpty()){
            for (String account : userList) {
                if (account.equals(username)) {
                    textView.setText("this name is taken");
                    return false;
                }
                //tell the user the username already exists.
        }}
        if (!password.equals(confirmPW)) {
            textView.setText("password doesn't match");
            return false;
        }
//        Tell the user the password and confirmed password are not the same
        userAccountManager.AddUser(newUser.getName());
        saveToFile(UserAccountManager.USERS);
        return true;
    }

    /**
     * Load from pre-saved .ser file.
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

    /**
     * Save to file fileName.
     * @param fileName the file to save
     */
    public void saveToFile(String fileName) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    this.openFileOutput(fileName, MODE_PRIVATE));
            outputStream.writeObject(userAccountManager);
            outputStream.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


}
