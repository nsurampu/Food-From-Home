package com.example.foodfromhome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabaseHandler db;
    SharedPreferences spEmail;
    SharedPreferences.Editor editorEmail;
    public static CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new SQLiteDatabaseHandler(this);
        spEmail = getSharedPreferences("email", MODE_PRIVATE);
        editorEmail = spEmail.edit();

        final Intent intent = new Intent(this, UserHome.class);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String accessToken = loginResult.getAccessToken().getToken();
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.i("LoginActivity", response.toString());
                                Bundle fbData = getFacebookData(object);
                                EditText editText = findViewById(R.id.emailField);
                                Bundle bundle = new Bundle();
                                bundle.putString("email", editText.getText().toString());
                                intent.putExtras(bundle);
                                String sql ="SELECT * FROM UserData";
                                Cursor cursor= db.getReadableDatabase().rawQuery(sql, null);
                                if (cursor.moveToFirst()) {
                                    while (!cursor.isAfterLast()) {
                                        String userEmail = cursor.getString(cursor.getColumnIndex("email"));
                                        if(userEmail.equals(editText.getText().toString())) {
                                            cursor.close();
                                            editorEmail.putString("email", editText.getText().toString());
                                            editorEmail.commit();
                                            startActivity(intent);
                                        }
                                        cursor.moveToNext();
                                    }
                                }
                                User user = new User(editText.getText().toString(), fbData.getString("id"), fbData.getString("first_name"), "N/A", "N/A");
                                db.addUser(user);
                                startActivity(intent);
                                cursor.close();
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("Login Cancelled");
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("Login Failed");
                    }
                });

        LoginManager.getInstance().logOut();

        if(spEmail.getString("email", null)!=null) {
            String email = spEmail.getString("email", null);
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        }
        catch(JSONException e) {
           System.out.println("Error parsing JSON");
        }
        return null;
    }
}
