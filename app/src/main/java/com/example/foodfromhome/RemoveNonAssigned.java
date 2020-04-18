package com.example.foodfromhome;

/**
 * <h1>RemoveNonAssigned</h1>
 * This class implements a backend task for keeping
 * track of and removing meals that have not been ordered
 * by anyone. Such meals are removed after 15min
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

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RemoveNonAssigned extends Worker{

    private SQLiteDatabaseHandler db;
    String orderReceipt;
    Meal selectedMeal;

    public RemoveNonAssigned(
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
                if(!meals.get(i).getDelivery().equals("None Assigned") && meals.get(i).getOTP()==-1)
                    db.deleteMeal(meals.get(i));
            }
        }
        System.out.println("THIS HERE IS ANOTHER WORKER!!!");
        return Result.success();
    }
}
