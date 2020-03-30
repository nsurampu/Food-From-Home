package com.example.foodfromhome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class UserProfile extends AppCompatActivity {

    private SQLiteDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        db = new SQLiteDatabaseHandler(this);
    }

    // Called when user finishes profile
    public void finishProfile(View view) {
        String callingCLass = getCallingActivity().getClassName();
        System.out.println(callingCLass);
        if(callingCLass.equals("com.example.foodfromhome.UserRegistration")) {
            EditText editText;
            editText = findViewById(R.id.nameRegister);
            String name = editText.getText().toString();
            editText = findViewById(R.id.phoneRegister);
            String mobile = editText.getText().toString();
            editText = findViewById(R.id.communityRegister);
            String community = editText.getText().toString();
            Bundle bundle = getIntent().getExtras();
            String email = bundle.getString("email");
            String pass = bundle.getString("pass");
            User user = new User(email, pass, name, mobile, community);
            db.addUser(user);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            EditText editText;
            editText = findViewById(R.id.nameRegister);
            String name = editText.getText().toString();
            editText = findViewById(R.id.phoneRegister);
            String mobile = editText.getText().toString();
            editText = findViewById(R.id.communityRegister);
            String community = editText.getText().toString();
            Bundle bundle = getIntent().getExtras();
            String email = bundle.getString("email");
            String pass = db.getUser(email).getPassword();
            User user = new User(email, pass, name, mobile, community);
            db.deleteOne(user);
            db.addUser(user);
            Bundle newBundle = new Bundle();
            newBundle.putString("email", email);
            Intent intent = new Intent(this, UserHome.class);
            intent.putExtras(newBundle);
            startActivity(intent);
        }
    }
}
