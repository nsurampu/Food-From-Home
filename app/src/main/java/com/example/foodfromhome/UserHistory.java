package com.example.foodfromhome;

/**
 * <h1>UserHistory</h1>
 * This class implements the activity for retrieving
 * and displaying the order history of the user
 * <p>
 *
 * @author  Naren Surampudi
 * @version 1.0
 * @since   2020-3-3
 */

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class UserHistory extends AppCompatActivity {

    List<String> userHistory;
    SQLiteDatabaseHandler db;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);
        
        db = new SQLiteDatabaseHandler(this);
        bundle = getIntent().getExtras();
        String userEmail = bundle.getString("email");

        userHistory = new ArrayList<>();
        List<History> histories = db.allHistory();
        boolean counter = true;
        if (histories != null) {
            for (int i = 0; i < histories.size(); i++) {
                if (histories.get(i).getEmail().equals(userEmail)) {
                    System.out.println(histories.get(i).toString());
                    userHistory.add(histories.get(i).toString());
                    counter = false;
                }
            }
        }
        if(counter) {
            userHistory.add("No History Yet");
        }

        ListView list = findViewById(R.id.availableHistory);
        list.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, userHistory));
    }
    
    public void closeHistory(View view) {
        bundle = getIntent().getExtras();
        Intent intent = new Intent(this, UserHome.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
