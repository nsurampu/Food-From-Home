package com.example.foodfromhome;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UserTask extends AppCompatActivity {

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
            // db.deleteMeal(selectedMeal);
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
            float cost=-1;
//            while(cost<0)
//                cost = getCost(meal);;
            cost = 123;
            meal.setCost(cost);
            db.updateMeal(meal);

            // send delivery information email to user
            String orderReceipt;
            orderReceipt = "ID: " + selectedMeal.getId() + "\n" + "Recipe: " + selectedMeal.getRecipe() + "\n" + "Size: " + selectedMeal.getPacket() + "\n" +"Deliver to: " + toLocation + "\n" + "OTP: " + otp;

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

        GeocodingLocation toLocationAddress = new GeocodingLocation();
        GeocodingLocation fromLocationAddress = new GeocodingLocation();
        while(buffer==null) {
            toLocationAddress.getAddressFromLocation(toLocation,
                    getApplicationContext(), new GeocoderHandler());
        }
        System.out.println(buffer);
        toLocation = buffer;
        buffer = null;
        while(buffer==null) {
            fromLocationAddress.getAddressFromLocation(fromLocation,
                    getApplicationContext(), new GeocoderHandler());
        }
        fromLocation = buffer;
        System.out.println(buffer);
        buffer = null;
        Location toLoc = new Location("Receiver");
        toLoc.setLatitude(Double.parseDouble(toLocation.split(":")[0]));
        toLoc.setLongitude(Double.parseDouble(toLocation.split(":")[1]));

        Location fromLoc = new Location("Provider");
        fromLoc.setLatitude(Double.parseDouble(fromLocation.split(":")[0]));
        fromLoc.setLongitude(Double.parseDouble(fromLocation.split(":")[1]));

        float distance = fromLoc.distanceTo(toLoc);
//        float distance = 125;

        float cost = 0;

        cost += distance*1.5;

        if(meal.getPacket().equals("Small"))
            cost *= 1;
        else if(meal.getPacket().equals("Medium"))
            cost *= 1.25;
        else
            cost *= 1.5;

        return cost;
    }

    private class GeocoderHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            String locationAddress = null;
            switch(message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    System.out.println(locationAddress);
                    break;
            }
            buffer = locationAddress;
        }
    }
}
