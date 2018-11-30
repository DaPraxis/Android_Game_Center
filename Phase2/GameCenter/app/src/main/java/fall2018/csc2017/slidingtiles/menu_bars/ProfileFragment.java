package fall2018.csc2017.slidingtiles.menu_bars;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import fall2018.csc2017.slidingtiles.ChangePasswordActivity;
import fall2018.csc2017.slidingtiles.DataHolder;
import fall2018.csc2017.slidingtiles.R;
import fall2018.csc2017.slidingtiles.UserAccount;
import fall2018.csc2017.slidingtiles.UserAccountManager;
import fall2018.csc2017.slidingtiles.database.DatabaseHelper;

public class ProfileFragment extends Fragment {
    DatabaseHelper myDB;
    private View view;
    private TextView username;
    private TextView age;
    private TextView email;
    private UserAccount user;
    private boolean isEnablbed = false;
    private Button editProfile;
    private Button changePs;
    private String currentUser;
    private UserAccountManager users;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        myDB = new DatabaseHelper(this.getContext());
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        getAllComponents();
        setEnabled(isEnablbed);
        getAllUsers();
        setContents();
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllComponents();
                editProfileButtonPushed();
            }
        });
        changePs = view.findViewById(R.id.changePs);
        changePs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmp = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(tmp);
            }
        });

        return view;
    }

    private void getAllComponents() {
        username = view.findViewById(R.id.username);
        age = view.findViewById(R.id.age);
        email = view.findViewById(R.id.email);
        editProfile = view.findViewById(R.id.editButton);
        changePs = view.findViewById(R.id.changePs);
    }

    private void getAllUsers() {
        currentUser = (String) DataHolder.getInstance().retrieve("current user");
        assert user != null;
        user = myDB.selectUser(currentUser);
        users = myDB.selectAccountManager();
    }

    private void setContents() {
        username.setText(user.getName());
        if (user.getAge() != null) {
            age.setText(user.getAge().toString());
        }
        email.setText(user.getEmail());

    }

    private void setEnabled(boolean isEnabled) {
        username.setEnabled(isEnabled);
        age.setEnabled(isEnabled);
        email.setEnabled(isEnabled);
    }

    private void editProfileButtonPushed() {
        if (!isEnablbed) {
            isEnablbed = true;
            setEnabled(true);
        } else {
            isEnablbed = false;
            email = view.findViewById(R.id.email);
            String emailS = email.getText().toString();
            username = view.findViewById(R.id.username);
            String usernameS = username.getText().toString();
            age = view.findViewById(R.id.age);
            String newAge = age.getText().toString();
            if (!validateInfo(newAge, "^[1-9][0-9]?$") && !validateInfo(newAge, "^$")) {
                Toasty.error(getContext(), "Illegal input of age.", Toast.LENGTH_SHORT, true).show();
                return;
            } else {
                user.setAge(Integer.getInteger(newAge));
            }
            if (!validateInfo(emailS, "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")
                    && !validateInfo(email.toString(), "^$")) {
                Toasty.error(getContext(), "Illegal input of  email address.", Toast.LENGTH_SHORT, true).show();
                return;
            } else {
                user.setEmail(emailS);
            }
            if (users.getUserList().contains(usernameS) && !usernameS.equals(user.getName())) {
                Toasty.error(getContext(), "Username already exists", Toast.LENGTH_SHORT, true).show();
                return;
            }
            if (!validateInfo(usernameS, "^[a-z]{3,7}$")) {
                Toasty.error(getContext(), "Illegal input of username.", Toast.LENGTH_SHORT, true).show();
                return;
            }

            String oldName = user.getName();
            users.getUserList().remove(user.getName());
            user.setName(usernameS);
            users.addUser(usernameS);
            myDB.deleteAndInsertUser(oldName, usernameS, user);
            myDB.updateAccountManager(users);
            DataHolder.getInstance().save("current user", usernameS);
            setEnabled(false);
            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            TextView t = headerView.findViewById(R.id.primary_username);
            t.setText(usernameS);
        }
    }

    private boolean validateInfo(String info, String regex) {
        Pattern regexP = Pattern.compile(regex);
        Matcher matcher = regexP.matcher(info);
        return matcher.matches();
    }

}

