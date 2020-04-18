package com.example.foodfromhome;

/**
 * <h1>UserMeal</h1>
 * This class implements the activity for uploading
 * a meal by the user
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.UUID;

public class UserMeal extends AppCompatActivity {

    SQLiteDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_user_meal);
        db = new SQLiteDatabaseHandler(this);
        EditText editText = findViewById(R.id.mealCommunity);
        editText.setText(db.getUser(bundle.getString("email")).getCommunity());
    }

    // Called when user finishes uploading meal
    public void finishUpload(View view) {
        Bundle bundle = getIntent().getExtras();
        EditText editText;
        editText = findViewById(R.id.mealRecipe);
        String recipe = editText.getText().toString();
        editText = findViewById(R.id.mealCommunity);
        String fromLocation = editText.getText().toString();
        RadioGroup radioGroup;
        radioGroup = findViewById(R.id.packetGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String packet = radioButton.getText().toString();
        String delivery = "None Assigned";
        String receiver = "None Assigned";
        String toLocation = "None Assigned";
        int otp = -1;
        String frequency = "Demand";
        String id = UUID.randomUUID().toString();
        Meal meal = new Meal(id, recipe, fromLocation, toLocation, packet, bundle.getString("email"), delivery, receiver ,otp, frequency, -1);
        db.addMeal(meal);
        Intent intent = new Intent(this, UserHome.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
