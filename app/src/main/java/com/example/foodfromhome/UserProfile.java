package com.example.foodfromhome;

/**
 * <h1>UserProfile</h1>
 * This class implements the activity for displaying
 * and editing the profile of a user. Includes user's
 * name, contact number and address
 * <p>
 *
 * @author  Naren Surampudi
 * @version 1.0
 * @since   2020-3-3
 */


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserProfile extends AppCompatActivity {

    private SQLiteDatabaseHandler db;
    private FusedLocationProviderClient fusedLocationClient;
    public String address;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        db = new SQLiteDatabaseHandler(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("page").equals("home")) {
            String email = bundle.getString("email");
            editText = findViewById(R.id.nameRegister);
            editText.setText(db.getUser(email).getName());
            editText = findViewById(R.id.phoneRegister);
            editText.setText(db.getUser(email).getMobile());
            editText = findViewById(R.id.communityRegister);
            editText.setText(db.getUser(email).getCommunity());
        }
    }

    // Called to detect location
    public void detectLocation(View view) {
        editText = findViewById(R.id.communityRegister);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        List<Address> addresses;
                        if (location != null) {
                            System.out.println(location);
                            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                            try {
                                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addresses.size() > 0)
                                    address = addresses.get(0).getThoroughfare();
                            } catch (IOException e) {
                                System.out.println(e);
                            }
                        }
                    }
                });
        editText.setText(address);
    }

    // Called when user finishes profile
    public void finishProfile(View view) {
        String callingCLass = getCallingActivity().getClassName();
        System.out.println(callingCLass);
        if(callingCLass.equals("com.example.foodfromhome.UserRegistration")) {
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
