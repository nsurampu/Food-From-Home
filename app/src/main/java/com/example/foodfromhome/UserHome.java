package com.example.foodfromhome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class UserHome extends AppCompatActivity {

    private SQLiteDatabaseHandler db;
    SharedPreferences spEmail;
    SharedPreferences.Editor editorEmail;
    boolean isTask, isDeliver;
    Meal meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        db = new SQLiteDatabaseHandler(this);
        spEmail = getSharedPreferences("email", MODE_PRIVATE);
        editorEmail = spEmail.edit();
        isTask = false;
        isDeliver = false;
        meal = null;

        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("email");
        TextView textView = findViewById(R.id.welcome);
        textView.setText("Welcome " + db.getUser(email).getName() + "!");

        List<Meal> meals = db.allMeals();

        if (meals != null) {
            for (int i = 0; i < meals.size(); i++) {
                if (meals.get(i).getReceiver().equals(email) || meals.get(i).getDelivery().equals(email)) {
                    isTask = true;
                    if(meals.get(i).getDelivery().equals(email))
                        isDeliver = true;
                    meal = meals.get(i);
                    break;
                }
            }
        }

        if(isTask) {
            findViewById(R.id.newTask).setEnabled(false);
            findViewById(R.id.newMeal).setEnabled(false);

            TextView sampleTextView;
            String deliveryEmail, deliveryContact;

            if(isDeliver) {
                findViewById(R.id.taskFinish).setVisibility(View.VISIBLE);
                findViewById(R.id.otpText).setVisibility(View.VISIBLE);
                findViewById(R.id.otpView).setVisibility(View.VISIBLE);
                sampleTextView = findViewById(R.id.deliveryText);
                deliveryEmail = meal.getReceiver();
                if(!meal.getReceiver().equals("None Assigned")) {
                    sampleTextView.setText(db.getUser(deliveryEmail).getName());
                    deliveryContact = db.getUser(deliveryEmail).getMobile();
                    sampleTextView = findViewById(R.id.deliveryContact);
                    sampleTextView.setText(deliveryContact);
                }
            }
            else {
                findViewById(R.id.taskFinish).setVisibility(View.INVISIBLE);
                findViewById(R.id.otpText).setVisibility(View.INVISIBLE);
                findViewById(R.id.otpView).setVisibility(View.INVISIBLE);
                sampleTextView = findViewById(R.id.deliveryText);
                deliveryEmail = meal.getDelivery();
                sampleTextView.setText(db.getUser(deliveryEmail).getName());
                deliveryContact = db.getUser(deliveryEmail).getMobile();
                sampleTextView = findViewById(R.id.deliveryContact);
                sampleTextView.setText(deliveryContact);
            }

            sampleTextView = findViewById(R.id.recipeText);
            sampleTextView.setText(meal.getRecipe());
            sampleTextView = findViewById(R.id.locationText);
            sampleTextView.setText(meal.getToLocation());
            sampleTextView = findViewById(R.id.costText);
            sampleTextView.setText(String.valueOf(meal.getCost()));
        }
        else {
            findViewById(R.id.newTask).setEnabled(true);
            findViewById(R.id.newMeal).setEnabled(true);

            findViewById(R.id.taskFinish).setVisibility(View.INVISIBLE);
            findViewById(R.id.otpText).setVisibility(View.INVISIBLE);
            findViewById(R.id.otpView).setVisibility(View.INVISIBLE);

            TextView sampleTextView;
            sampleTextView = findViewById(R.id.recipeText);
            sampleTextView.setText("-");
            sampleTextView = findViewById(R.id.deliveryText);
            sampleTextView.setText("-");
            sampleTextView = findViewById(R.id.locationText);
            sampleTextView.setText("-");
            sampleTextView = findViewById(R.id.costText);
            sampleTextView.setText("-");
        }
    }

    // Called to finish task using OTP
    public void taskOTP(View view) {
        Bundle bundle = getIntent().getExtras();
        TextView textView = findViewById(R.id.otpText);
        int userOtp = Integer.parseInt(textView.getText().toString());
        if(meal.getOTP()==userOtp) {
            db.deleteMeal(meal);
            Snackbar prompt = Snackbar.make(findViewById(R.id.homePage), "Meal delivery successful", Snackbar.LENGTH_LONG);
            prompt.show();
        }
        else {
            Snackbar warning = Snackbar.make(findViewById(R.id.homePage), "Incorrect OTP", Snackbar.LENGTH_LONG);
            warning.show();
        }
    }

    // Called when user starts new delivery task
    public void deliveryTask(View view) {
        Bundle bundle = getIntent().getExtras();
        Intent intent = new Intent(this, UserTask.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //Called when user wants to upload meal
    public void mealUpload(View view) {
        Bundle bundle = getIntent().getExtras();
        Intent intent = new Intent(this, UserMeal.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // Called when user wants to edit profile
    public void editProfile(View view) {
        Bundle bundle = getIntent().getExtras();
        Intent intent = new Intent(this, UserProfile.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 8050);
    }

    // Called when user signs out
    public void signOut(View view) {
        editorEmail.putString("email", null);
        editorEmail.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
