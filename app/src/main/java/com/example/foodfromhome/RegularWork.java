package com.example.foodfromhome;

/**
 * <h1>RegularWork</h1>
 * This class implements a backend task for keeping
 * track of regular deliveries.
 * <p>
 *
 * @author  Naren Surampudi
 * @version 1.0
 * @since   2020-3-3
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RegularWork extends Worker{

    private SQLiteDatabaseHandler db;
    String orderReceipt;
    Meal selectedMeal;

    public RegularWork(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        db = new SQLiteDatabaseHandler(getApplicationContext());
        List<Meal> meals = db.allMeals();
        if (meals != null) {
            for (int i = 0; i < meals.size(); i++) {
                if(meals.get(i).getFrequency().equals("Demand"))
                    continue;
                else {
                    float timer = Integer.parseInt(meals.get(i).getFrequency());
                    timer -= 1;
                    if(timer==0 || timer==0.5) {
                        // Assign new delivery and reset timer to 24
                        // generate otp and send to user. Also update otp in database
                        selectedMeal = meals.get(i);
                        String userEmail = selectedMeal.getReceiver();
                        int otp = ThreadLocalRandom.current().nextInt(10000, 99999 + 1);
                        float cost = meals.get(i).getCost();
                        if(timer==0)
                            cost -= 0.2*cost;
                        selectedMeal.setCost(cost);
                        selectedMeal.setFrequency("24.5");
                        selectedMeal.setOTP(otp);
                        // Create new rider and assign for delivery (only for demo)
                        User newUser = new User("sample" + otp + "@sample.com", "abc123", "sampleDelivery", "1234567890", "sampleCommunity");
                        db.addUser(newUser);
                        selectedMeal.setDelivery(newUser.getEmail());
                        db.updateMeal(selectedMeal);

                        // send delivery information email to user
                        orderReceipt = "ID: " + selectedMeal.getId() + "\n" + "Recipe: " + selectedMeal.getRecipe() + "\n" + "Size: " + selectedMeal.getPacket() + "\n" +"Deliver to: " + selectedMeal.getToLocation() + "\n" + "OTP: " + otp;

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
                                    InternetAddress.parse(selectedMeal.getReceiver()));
                            message.setSubject("Food From Home: Order Receipt");
                            message.setText("Your Order Summary: \n\n" + orderReceipt);

                            Transport.send(message);

                            System.out.println("Done");

                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        Meal meal = meals.get(i);
                        meal.setFrequency(Float.toString(timer));
                        db.updateMeal(meal);
                    }
                }
            }
        }
        System.out.println("THIS HERE IS THE WORKER!!!");
        return Result.success();
    }
}
