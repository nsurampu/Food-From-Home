package com.example.foodfromhome;

/**
 * <h1>UserRegistration</h1>
 * This class implements the activity for user registration
 * at the time of first time account creation
 * <p>
 *
 * @author  Naren Surampudi
 * @version 1.0
 * @since   2020-3-3
 */


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class UserRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
    }

    // Called when user completes registration
    public void newProfile(View view) {
        Intent intent = new Intent(this, UserProfile.class);
        EditText editText;
        editText = findViewById(R.id.registerEmail);
        String email = editText.getText().toString();
        editText = findViewById(R.id.registerPass);
        String pass = editText.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("pass", pass);
        bundle.putString("page", "register");
        intent.putExtras(bundle);
        startActivityForResult(intent, 9000);
    }
}
