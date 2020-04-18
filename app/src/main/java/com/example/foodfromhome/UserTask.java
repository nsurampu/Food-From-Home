package com.example.foodfromhome;

/**
 * <h1>UserTask</h1>
 * This class implements the activity for creating a
 * delivery task. A delivery task can be used to refer
 * to either delivering a meal or ordering a meal. The
 * various details like OTP, location and size based
 * cost calculation etc. are performed here. This activity
 * also uses the Google APIs for distance tracking. Enter the
 * API key value in the public KEY field for using the application
 * <p>
 *
 * @author  Naren Surampudi
 * @version 1.0
 * @since   2020-3-3
 */


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UserTask extends AppCompatActivity {

    String KEY = "AIzaSyC0E4xnQWncCl-jUyXVDscJ5Z2M2myIRGs";

    private SQLiteDatabaseHandler db;
    List<String> itemsNames;
    Meal selectedMeal;
    String userEmail;
    String taskType;
    String buffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        regular = getSharedPreferences("freq", MODE_PRIVATE);
//        Switch aSwitch = findViewById(R.id.regularDelivery);
//        if(regular.getString("freq", null)!=null) {
//            if(regular.getString("freq", null).equals("true"))
//                aSwitch.setChecked(true);
//            else
//                aSwitch.setChecked(false);
//        }

        itemsNames = new ArrayList<>();
        buffer = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_task);
        db = new SQLiteDatabaseHandler(this);
        selectedMeal = null;
        taskType = null;
        Bundle bundle = getIntent().getExtras();
        userEmail = bundle.getString("email");

        itemsNames.add("Choose an option to either get or deliver a meal");

        ListView list = findViewById(R.id.availableMeals);
        list.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));
    }

    public void setFrequency(View view) {
//        regular = getSharedPreferences("freq", MODE_PRIVATE);
//        regularEdit = regular.edit();
        Switch aSwitch = findViewById(R.id.regularDelivery);
        String freq;
        if(aSwitch.isChecked())
            freq = "true";
        else
            freq = "false";
//        regularEdit.putString("freq", freq);
//        regularEdit.commit();
    }

    public void removeRegulars(View view) {
        List<Meal> meals = db.allMeals();

        if(meals!=null) {
            for (int i=0; i < meals.size(); i++) {
                if (meals.get(i).getReceiver().equals(userEmail) && !meals.get(i).getFrequency().equals("Demand"))
                    db.deleteMeal(meals.get(i));
            }
        }
    }

    public void getMeals(View view) {
        findViewById(R.id.regularDelivery).setVisibility(View.VISIBLE);
        findViewById(R.id.removeRegs).setVisibility(View.VISIBLE);
        itemsNames = new ArrayList<>();
        List<Meal> meals = db.allMeals();
        boolean counter = true;
        if (meals != null) {
            for (int i = 0; i < meals.size(); i++) {
                if (!meals.get(i).getUploader().equals(userEmail) && !meals.get(i).getDelivery().equals("None Assigned") && meals.get(i).getOTP()==-1) {
                    System.out.println(meals.get(i).toString());
                    itemsNames.add(meals.get(i).toString());
                    counter = false;
                }
            }
        }
        if(counter) {
            itemsNames.add("No Tasks available at this time. Check back later");
        }

        ListView list = findViewById(R.id.availableMeals);
        list.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String[] str = itemsNames.get(position).split("\n");
                String mealID = str[0].split(": ")[1];
                selectedMeal = db.getMeal(mealID);
                String prompt = null;
                if(selectedMeal.getOTP()!=-1) {
                    prompt = itemsNames.get(position);
                    taskType = "invalid";
                }
                else if(!selectedMeal.getDelivery().equals("None Assigned") && selectedMeal.getOTP()==-1) {
                    prompt = itemsNames.get(position);
                    taskType = "get";
                }
                else if(selectedMeal.getDelivery().equals("None Assigned")) {
                    prompt = itemsNames.get(position);
                    taskType = "deliver";
                }

                Snackbar snackbar = Snackbar.make(findViewById(R.id.deliveryTasks), prompt, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    public void getDeliveries(View view) {
        findViewById(R.id.regularDelivery).setVisibility(View.INVISIBLE);
        findViewById(R.id.removeRegs).setVisibility(View.INVISIBLE);
        itemsNames = new ArrayList<>();
        List<Meal> meals = db.allMeals();
        boolean counter = true;if (meals != null) {
            for (int i = 0; i < meals.size(); i++) {
                if (!meals.get(i).getUploader().equals(userEmail) && meals.get(i).getDelivery().equals("None Assigned") && meals.get(i).getFrequency().equals("Demand")) {
                    System.out.println(meals.get(i).toString());
                    itemsNames.add(meals.get(i).toString());
                    counter = false;
                }
            }
        }
        if(counter) {
            itemsNames.add("No Tasks available at this time. Check back later");
        }

        ListView list = findViewById(R.id.availableMeals);
        list.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String[] str = itemsNames.get(position).split("\n");
                String mealID = str[0].split(": ")[1];
                selectedMeal = db.getMeal(mealID);
                String prompt = null;
                if(selectedMeal.getOTP()!=-1) {
                    prompt = itemsNames.get(position);
                    taskType = "invalid";
                }
                else if(!selectedMeal.getDelivery().equals("None Assigned") && selectedMeal.getOTP()==-1) {
                    prompt = itemsNames.get(position);
                    taskType = "get";
                }
                else if(selectedMeal.getDelivery().equals("None Assigned")) {
                    prompt = itemsNames.get(position);
                    taskType = "deliver";
                }

                Snackbar snackbar = Snackbar.make(findViewById(R.id.deliveryTasks), prompt, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    public void getNearby(View view) {
        itemsNames = new ArrayList<>();
        List<Meal> meals = db.allMeals();
        boolean counter = true;

        if (meals != null) {
            for (int i = 0; i < meals.size(); i++) {
                if (!meals.get(i).getUploader().equals(userEmail) && meals.get(i).getFromLocation().equalsIgnoreCase(db.getUser(userEmail).getCommunity())) {
                    itemsNames.add(meals.get(i).toString());
                    System.out.println(meals.get(i).toString());
                    counter = false;
                }
            }
        }
        if(counter) {
            itemsNames.add("No Tasks available at this time. Check back later");
        }


        ListView list = findViewById(R.id.availableMeals);
        list.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String[] str = itemsNames.get(position).split("\n");
                String mealID = str[0].split(": ")[1];
                selectedMeal = db.getMeal(mealID);
                String prompt = null;
                if(selectedMeal.getOTP()!=-1) {
                    prompt = itemsNames.get(position);
                    taskType = "invalid";
                }
                else if(!selectedMeal.getDelivery().equals("None Assigned") && selectedMeal.getOTP()==-1) {
                    prompt = itemsNames.get(position);
                    taskType = "get";
                }
                else if(selectedMeal.getDelivery().equals("None Assigned")) {
                    prompt = itemsNames.get(position);
                    taskType = "deliver";
                }

                Snackbar snackbar = Snackbar.make(findViewById(R.id.deliveryTasks), prompt, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    // Called when user finishes creating task
    public void taskCreated(View view) {
        float cost = -1;

        Bundle bundle = new Bundle();
        bundle.putString("email", userEmail);
        Intent intent = new Intent(this, UserHome.class);
        if(selectedMeal==null){
            Snackbar prompt = Snackbar.make(findViewById(R.id.deliveryTasks), "Choose a meal to either get or deliver!", Snackbar.LENGTH_LONG);
            prompt.show();
        }
        else if(taskType.equals("deliver")) {
            db.deleteMeal(selectedMeal);
            Meal meal = new Meal(selectedMeal.getId(), selectedMeal.getRecipe(), selectedMeal.getFromLocation(), selectedMeal.getToLocation(), selectedMeal.getPacket(), selectedMeal.getUploader(), userEmail, selectedMeal.getReceiver(), selectedMeal.getOTP(), selectedMeal.getFrequency(), 0);
            db.addMeal(meal);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else if(taskType.equals("get")){
            // generate otp and send to user. Also update otp in database
            int otp = ThreadLocalRandom.current().nextInt(10000, 99999 + 1);
            Switch aSwitch = findViewById(R.id.regularDelivery);
            boolean checked = aSwitch.isChecked();
            String frequency;
            if(checked)
                frequency = "24";
            else
                frequency = "Demand";
            String toLocation = db.getUser(userEmail).getCommunity();
            System.out.println(selectedMeal.getFromLocation() + " - " + toLocation);
            Meal meal = new Meal(selectedMeal.getId(), selectedMeal.getRecipe(), selectedMeal.getFromLocation(), toLocation, selectedMeal.getPacket(), selectedMeal.getUploader(), selectedMeal.getDelivery(), userEmail, otp, frequency, -1);
            cost = getCost(meal);
            meal.setCost(cost);
            db.updateMeal(meal);
            History history = new History(userEmail, selectedMeal.getRecipe(), cost);
            history.setCost(cost);
            db.addHistory(history);

            // send delivery information email to user
            String orderReceipt;
            orderReceipt = "ID: " + selectedMeal.getId() + "\nRecipe: " + selectedMeal.getRecipe() + "\nSize: " + selectedMeal.getPacket() + "\nDelivery Executive: " + db.getUser(selectedMeal.getDelivery()).getName() + "\nDelivery Executive Contact: " + db.getUser(selectedMeal.getDelivery()).getMobile() +"\nDeliver to: " + toLocation + "\nOTP: " + otp;

            bundle.putString("sendEmail", "sendEmail");
            bundle.putString("receipt", orderReceipt);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else {
            Snackbar prompt = Snackbar.make(findViewById(R.id.deliveryTasks), "Error creating task. Try again by selecting a task.", Snackbar.LENGTH_LONG);
            prompt.show();
        }
    }

    public void backHome(View view) {
        Intent intent = new Intent(this, UserHome.class);
        Bundle bundle = getIntent().getExtras();
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public float getCost(Meal meal) {
        String toLocation = meal.getToLocation();
        String fromLocation = meal.getFromLocation();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String someText = getDistance("https://maps.googleapis.com/maps/api/directions/json?key=" + KEY + "&origin=" + fromLocation +"&destination=" + toLocation +"&sensor=false");
        float distance = Float.parseFloat(someText.split(": ")[1].split(",")[0].replaceAll("^\"|\"$", "").split(" ")[0]);

        float cost = 0;

        cost += distance*30;

        if(meal.getPacket().equals("Small"))
            cost *= 1;
        else if(meal.getPacket().equals("Medium"))
            cost *= 1.25;
        else
            cost *= 1.5;

        return cost;
    }

    public String getDistance(String... params) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                if(line.contains("km")) {
                    buffer.append(line + "-");
                    break;
                }
            }

            return buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
