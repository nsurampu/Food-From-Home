package com.example.foodfromhome;

/**
 * <h1>UserHome</h1>
 * This class implements the activity for the user's
 * home dashboard. The dashboard gives navigation
 * controls and displays details about the current
 * order.
 * <p>
 *
 * @author  Naren Surampudi
 * @version 1.0
 * @since   2020-3-3
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class UserHome extends AppCompatActivity {

    private SQLiteDatabaseHandler db;
    SharedPreferences spEmail;
    SharedPreferences.Editor editorEmail;
    boolean isTask, isDeliver;
    Meal meal;
    Bundle bundle;
    SharedPreferences wkManager;
    SharedPreferences.Editor wkManagerStatus;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                finish();
                startActivity(getIntent());
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        wkManager = getSharedPreferences("status", MODE_PRIVATE);
        wkManagerStatus = wkManager.edit();
        System.out.println(wkManager.getString("status", null));
        if(wkManager.getString("status", null)==null) {
            PeriodicWorkRequest periodicWorkRequest1 = new PeriodicWorkRequest.Builder(RegularWork.class, 60, TimeUnit.MINUTES)
                    .build();
            PeriodicWorkRequest periodicWorkRequest2 = new PeriodicWorkRequest.Builder(RemoveNonAssigned.class, 15, TimeUnit.MINUTES)
                    .build();
            WorkManager.getInstance().enqueue(periodicWorkRequest1);
            WorkManager.getInstance().enqueue(periodicWorkRequest2);
            wkManagerStatus.putString("status", "running");
            wkManagerStatus.commit();

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        db = new SQLiteDatabaseHandler(this);
        spEmail = getSharedPreferences("email", MODE_PRIVATE);
        editorEmail = spEmail.edit();
        isTask = false;
        isDeliver = false;
        meal = null;

        bundle = getIntent().getExtras();
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

        if(!(bundle.getString("sendEmail")==null)) {
            final String username = "foodfromhome20@gmail.com";
            final String password = "";   // removed for privacy and security
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("foodfromhome20@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(meal.getReceiver()));
                message.setSubject("Food From Home: Order Receipt");
                message.setText("Your Order Summary: \n\n" + bundle.getString("receipt") + "\n\n Provide the above OTP to the assigned delivery executive to complete order");

                Transport.send(message);

                System.out.println("Done");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }

        if(isTask) {
            TextView sampleTextView;
            String deliveryEmail, deliveryContact;

            if(isDeliver) {
                findViewById(R.id.taskFinish).setVisibility(View.VISIBLE);
                findViewById(R.id.otpText).setVisibility(View.VISIBLE);
                findViewById(R.id.otpView).setVisibility(View.VISIBLE);
                sampleTextView = findViewById(R.id.deliveryText);
                deliveryEmail = meal.getReceiver();
                findViewById(R.id.newTask).setEnabled(false);
                findViewById(R.id.newMeal).setEnabled(false);
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
                if(meal.getOTP()==-1) {
                    findViewById(R.id.newTask).setEnabled(true);
                    findViewById(R.id.newMeal).setEnabled(true);
                }
                else {
                    findViewById(R.id.newTask).setEnabled(false);
                    findViewById(R.id.newMeal).setEnabled(false);
                    sampleTextView = findViewById(R.id.deliveryText);
                    deliveryEmail = meal.getDelivery();
                    sampleTextView.setText(db.getUser(deliveryEmail).getName());
                    deliveryContact = db.getUser(deliveryEmail).getMobile();
                    sampleTextView = findViewById(R.id.deliveryContact);
                    sampleTextView.setText(deliveryContact);
                }
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

    public void menuAccess(View view) {
        Button button;
        button = findViewById(R.id.newTask);
        // VISIBLE is 0
        int visible = button.getVisibility();
        if(visible==0) {
            findViewById(R.id.newTask).setVisibility(View.INVISIBLE);
            findViewById(R.id.newMeal).setVisibility(View.INVISIBLE);
            findViewById(R.id.profileButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.historyButton).setVisibility(View.INVISIBLE);
        }
        else {
            findViewById(R.id.newTask).setVisibility(View.VISIBLE);
            findViewById(R.id.newMeal).setVisibility(View.VISIBLE);
            findViewById(R.id.profileButton).setVisibility(View.VISIBLE);
            findViewById(R.id.historyButton).setVisibility(View.VISIBLE);
        }
    }

    public void getHistory(View view) {
        bundle = getIntent().getExtras();
        Intent intent = new Intent(this, UserHistory.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // Called to finish task using OTP
    public void taskOTP(View view) {
        bundle = getIntent().getExtras();
        TextView textView = findViewById(R.id.otpText);
        int userOtp = Integer.parseInt(textView.getText().toString());
        if(meal.getOTP()==userOtp) {
            if(meal.getFrequency().equals("Demand"))
                db.deleteMeal(meal);
            else {
                meal.setDelivery("None Assigned");
                meal.setOTP(-1);
                db.updateMeal(meal);
            }
            Snackbar prompt = Snackbar.make(findViewById(R.id.homePage), "Meal delivery successful", Snackbar.LENGTH_LONG);
            prompt.show();
            finish();
            startActivity(getIntent());
        }
        else {
            Snackbar warning = Snackbar.make(findViewById(R.id.homePage), "Incorrect OTP", Snackbar.LENGTH_LONG);
            warning.show();
        }
    }

    // Called when user starts new delivery task
    public void deliveryTask(View view) {
        bundle = getIntent().getExtras();
        Intent intent = new Intent(this, UserTask.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //Called when user wants to upload meal
    public void mealUpload(View view) {
        bundle = getIntent().getExtras();
        Intent intent = new Intent(this, UserMeal.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // Called when user wants to edit profile
    public void editProfile(View view) {
        bundle = getIntent().getExtras();
        bundle.putString("page", "home");
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


