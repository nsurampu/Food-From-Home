package com.example.foodfromhome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabaseHandler db;
    SharedPreferences spEmail;
    SharedPreferences.Editor editorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new SQLiteDatabaseHandler(this);
        spEmail = getSharedPreferences("email", MODE_PRIVATE);
        editorEmail = spEmail.edit();

        Intent intent = new Intent(this, UserHome.class);
        if(spEmail.getString("email", null)!=null) {
            String email = spEmail.getString("email", null);
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    // Called when user chooses to register
    public void newRegister(View view) {
        Intent intent = new Intent(this, UserRegistration.class);
        startActivity(intent);
    }

    // Called when user logs in
    public void userLogin(View view) throws IllegalStateException{
        Intent intent = new Intent(this, UserHome.class);
        String email, pass;
        EditText editText;
        editText = findViewById(R.id.emailField);
        email = editText.getText().toString();
        editText = findViewById(R.id.passField);
        pass = editText.getText().toString();
        Snackbar emptyWarning = Snackbar.make(findViewById(R.id.mainPage), "Required Fields Empty", Snackbar.LENGTH_LONG);
        Snackbar warning = Snackbar.make(findViewById(R.id.mainPage), "Login Failed", Snackbar.LENGTH_LONG);
        try {
            if (email.isEmpty() || pass.isEmpty())
                emptyWarning.show();
            else if (pass.equals(db.getUser(email).getPassword())) {
                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                bundle.putString("page", "login");
                intent.putExtras(bundle);
                System.out.println(pass.equals(db.getUser(email).getPassword()));
                editorEmail.putString("email", email);
                editorEmail.commit();
                startActivity(intent);
            } else {
                warning.show();
            }
        }
        catch(IllegalStateException e) {
            Snackbar illegalWarning = Snackbar.make(findViewById(R.id.mainPage), "Account does not exist. Please register to use app", Snackbar.LENGTH_LONG);
            illegalWarning.show();
        }
    }
}
