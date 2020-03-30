package com.example.foodfromhome;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UserTask extends AppCompatActivity {

    private SQLiteDatabaseHandler db;
    String[] itemsNames;
    Meal selectedMeal;
    String userEmail;
    String taskType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_task);
        db = new SQLiteDatabaseHandler(this);
        selectedMeal = null;
        taskType = null;
        itemsNames = new String[1];
        Bundle bundle = getIntent().getExtras();
        userEmail = bundle.getString("email");
//        itemsNames[0] = "Select an option to see the respective lists";
        List<Meal> meals = db.allMeals();
        itemsNames = new String[0];
        boolean counter = true;

        if (meals != null) {
            itemsNames = new String[meals.size()];

            for (int i = 0; i < meals.size(); i++) {
                if (!meals.get(i).getUploader().equals(userEmail)) {
                    itemsNames[i] = meals.get(i).toString();
                    System.out.println(meals.get(i).toString());
                    counter = false;
                }
            }
        }
        if(counter) {
            itemsNames = new String[1];
            itemsNames[0] = "No Tasks available at this time. Check back later";
        }

        ListView list = findViewById(R.id.availableMeals);
        list.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String[] str = itemsNames[position].split("\n");
                String mealID = str[0].split(": ")[1];
                selectedMeal = db.getMeal(mealID);
                String prompt = null;
                if(selectedMeal.getOTP()!=-1) {
                    prompt = "Already assigned for delivery. Please try ordering a different meal";
                    taskType = "invalid";
                }
                else if(!selectedMeal.getDelivery().equals("None Assigned") && selectedMeal.getOTP()==-1) {
                    prompt = "You can ORDER this meal. Create task to get meal delivered";
                    taskType = "get";
                }
                else if(selectedMeal.getDelivery().equals("None Assigned")) {
                    prompt = "You can DELIVER this meal. Create task to deliver meal";
                    taskType = "deliver";
                }

                Snackbar snackbar = Snackbar.make(findViewById(R.id.deliveryTasks), prompt, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

//    public void getDeliveries(View view) {
//        List<Meal> meals = db.allMeals();
//
//        itemsNames = new String[0];
//        boolean counter = true;
//
//        if (meals != null) {
//            itemsNames = new String[meals.size()];
//
//            for (int i = 0; i < meals.size(); i++) {
//                if (meals.get(i).getDelivery().equals("None Assigned") && !meals.get(i).getUploader().equals(userEmail)) {
//                    itemsNames[i] = meals.get(i).toString();
//                    System.out.println(meals.get(i).toString());
//                    counter = false;
//                }
//            }
//        }
//
//        if(counter) {
//            itemsNames = new String[1];
//            itemsNames[0] = "No Deliveries available at this time. Check back later";
//        }
//
//        ListView list = findViewById(R.id.availableMeals);
//        list.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));
//    }
//
//    public void getMeals(View view) {
//        List<Meal> meals = db.allMeals();
//
//        itemsNames = new String[0];
//        boolean counter = true;
//
//        if (meals != null) {
//            itemsNames = new String[meals.size()];
//
//            for (int i = 0; i < meals.size(); i++) {
//                if(!meals.get(i).getUploader().equals(userEmail)) {
//                    itemsNames[i] = meals.get(i).toString();
//                    System.out.println(meals.get(i).toString());
//                    counter = false;
//                }
//            }
//        }
//
//        if(counter) {
//            itemsNames = new String[1];
//            itemsNames[0] = "No Meals available at this time. Check back later";
//        }
//
//        ListView list = findViewById(R.id.availableMeals);
//        list.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));
//    }

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
            Meal meal = new Meal(selectedMeal.getId(), selectedMeal.getRecipe(), selectedMeal.getCommunity(), selectedMeal.getPacket(), selectedMeal.getUploader(), userEmail, selectedMeal.getReceiver(), selectedMeal.getOTP());
            db.addMeal(meal);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else if(taskType.equals("get")){
            // generate otp and send to user. Also update otp in database
            int otp = ThreadLocalRandom.current().nextInt(10000, 99999 + 1);
            db.deleteMeal(selectedMeal);
            Meal meal = new Meal(selectedMeal.getId(), selectedMeal.getRecipe(), selectedMeal.getCommunity(), selectedMeal.getPacket(), selectedMeal.getUploader(), selectedMeal.getDelivery(), userEmail, otp);
            db.addMeal(meal);
            // send delivery information email to user
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
}
