package com.example.foodfromhome;

/**
 * <h1>SQLiteDataBaseHandler</h1>
 * This class implements a SQLite database handler
 * fpr creating, inserting into, updating and
 * retrieving from tables for User credentials,
 * Meals and User history
 * <p>
 *
 * @author  Naren Surampudi
 * @version 1.0
 * @since   2020-3-3
 */

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "FFH";
    private static final String TABLE_USERS = "UserData";
    private static final String KEY_EMAIL_USERS = "email";
    private static final String KEY_PASSWORD_USERS = "password";
    private static final String KEY_NAME_USERS = "name";
    private static final String KEY_MOBILE_USERS = "mobile";
    private static final String KEY_COMMUNITY_USERS = "community";
    private static final String[] COLUMNS_USERS = { KEY_EMAIL_USERS, KEY_PASSWORD_USERS, KEY_NAME_USERS, KEY_MOBILE_USERS,
            KEY_COMMUNITY_USERS };

    private static final String TABLE_MEALS = "MealData";
    private static final String KEY_ID_MEALS = "id";
    private static final String KEY_RECIPE_MEALS = "recipe";
    private static final String KEY_FROM_MEALS = "fromLoc";
    private static final String KEY_TO_MEALS = "toLoc";
    private static final String KEY_PACKET_MEALS = "packet";
    private static final String KEY_UPLOADER_MEALS = "uploader";
    private static final String KEY_DELIVERY_MEALS = "delivery";
    private static final String KEY_RECEIVER_MEALS = "receiver";
    private static final String KEY_OTP = "otp";
    private static final String KEY_FREQUENCY = "frequency";
    private static final String KEY_COST = "cost";
    private static final String[] COLUMNS_MEALS = { KEY_ID_MEALS, KEY_RECIPE_MEALS, KEY_FROM_MEALS, KEY_TO_MEALS, KEY_PACKET_MEALS,
            KEY_UPLOADER_MEALS, KEY_DELIVERY_MEALS, KEY_RECEIVER_MEALS, KEY_OTP, KEY_FREQUENCY, KEY_COST };

    private static final String TABLE_HISTORY = "HistoryData";
    private static final String KEY_USER = "email";
    private static final String KEY_RECIPE = "recipe";
    private static final String KEY_HIST_COST = "cost";
    private static final String[] COLUMNS_HISTORY = { KEY_USER, KEY_RECIPE, KEY_HIST_COST };

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE_USERS = "CREATE TABLE  UserData( "
                + "email TEXT PRIMARY KEY, " + "password TEXT, "
                + "name TEXT, " + "mobile TEXT, "
                + "community TEXT )";

        String CREATION_TABLE_MEALS = "CREATE TABLE  MealData( "
                + "id TEXT PRIMARY KEY, " + "recipe TEXT, "
                + "fromLoc TEXT, " + "toLoc TEXT, " + "packet TEXT, "
                + "uploader TEXT, "
                + "delivery TEXT, " + "receiver TEXT, " + "otp INTEGER, " + "frequency TEXT, " + "cost TEXT )";

        String CREATION_TABLE_HISTORY = "CREATE TABLE  HistoryData( "
                + "email TEXT PRIMARY KEY, " + "recipe TEXT, " + "cost TEXT )";

        db.execSQL(CREATION_TABLE_USERS);
        db.execSQL(CREATION_TABLE_MEALS);
        db.execSQL(CREATION_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        this.onCreate(db);
    }

    public void deleteOne(User user) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, "email = ?", new String[] { String.valueOf(user.getEmail()) });
        db.close();
    }

    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, // a. table
                COLUMNS_USERS, // b. column names
                " email = ?", // c. selections
                new String[] { String.valueOf(email) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        User user = new User();
        user.setEmail(cursor.getString(0));
        user.setPassword(cursor.getString(1));
        user.setName(cursor.getString(2));
        user.setMobile(cursor.getString(3));
        user.setCommunity(cursor.getString(4));

        return user;
    }

    public List<User> allUsers() {

        List<User> users = new LinkedList<User>();
        String query = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        User user = null;

        if (cursor.moveToFirst()) {
            do {
                user = new User();
                user.setEmail(cursor.getString(0));
                user.setPassword(cursor.getString(1));
                user.setName(cursor.getString(2));
                user.setMobile(cursor.getString(3));
                user.setCommunity(cursor.getString(4));
                users.add(user);
            } while (cursor.moveToNext());
        }

        return users;
    }

    public void addHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER, history.getEmail());
        values.put(KEY_RECIPE, history.getRecipe());
        values.put(KEY_HIST_COST, history.getCost());

        // insert
        db.insert(TABLE_HISTORY,null, values);
        db.close();
    }

    public List<History> allHistory() {

        List<History> histories = new LinkedList<History>();
        String query = "SELECT  * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        History history = null;

        if (cursor.moveToFirst()) {
            do {
                history = new History();
                history.setEmail(cursor.getString(0));
                history.setRecipe(cursor.getString(1));
                history.setCost(Float.parseFloat(cursor.getString(2)));
                histories.add(history);
            } while (cursor.moveToNext());
        }

        return histories;
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL_USERS, user.getEmail());
        values.put(KEY_PASSWORD_USERS, user.getPassword());
        values.put(KEY_NAME_USERS, user.getName());
        values.put(KEY_MOBILE_USERS, user.getMobile());
        values.put(KEY_COMMUNITY_USERS, user.getCommunity());
        // insert
        db.insert(TABLE_USERS,null, values);
        db.close();
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL_USERS, user.getEmail());
        values.put(KEY_PASSWORD_USERS, user.getPassword());
        values.put(KEY_NAME_USERS, user.getName());
        values.put(KEY_MOBILE_USERS, user.getMobile());
        values.put(KEY_COMMUNITY_USERS, user.getCommunity());

        int i = db.update(TABLE_USERS, // table
                values, // column/value
                "email = ?", // selections
                new String[] { String.valueOf(user.getEmail()) });

        db.close();

        return i;
    }

    public void deleteMeal(Meal meal) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEALS, "id = ?", new String[] { String.valueOf(meal.getId()) });
        db.close();
    }

    public Meal getMeal(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEALS, // a. table
                COLUMNS_MEALS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Meal meal = new Meal();
        meal.setId(cursor.getString(0));
        meal.setRecipe(cursor.getString(1));
        meal.setFromLocation(cursor.getString(2));
        meal.setToLocation(cursor.getString(3));
        meal.setPacket(cursor.getString(4));
        meal.setUploader(cursor.getString(5));
        meal.setDelivery(cursor.getString(6));
        meal.setReceiver(cursor.getString(7));
        meal.setOTP(Integer.parseInt(cursor.getString(8)));
        meal.setFrequency(cursor.getString(9));
        meal.setCost(Float.parseFloat(cursor.getString(10)));

        return meal;
    }

    public List<Meal> allMeals() {

        List<Meal> meals = new LinkedList<Meal>();
        String query = "SELECT  * FROM " + TABLE_MEALS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Meal meal = null;

        if (cursor.moveToFirst()) {
            do {
                meal = new Meal();
                meal.setId(cursor.getString(0));
                meal.setRecipe(cursor.getString(1));
                meal.setFromLocation(cursor.getString(2));
                meal.setToLocation(cursor.getString(3));
                meal.setPacket(cursor.getString(4));
                meal.setUploader(cursor.getString(5));
                meal.setDelivery(cursor.getString(6));
                meal.setReceiver(cursor.getString(7));
                meal.setOTP(Integer.parseInt(cursor.getString(8)));
                meal.setFrequency(cursor.getString(9));
                meal.setCost(Float.parseFloat(cursor.getString(10)));
                meals.add(meal);
            } while (cursor.moveToNext());
        }

        return meals;
    }

    public void addMeal(Meal meal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_MEALS, meal.getId());
        values.put(KEY_RECIPE_MEALS, meal.getRecipe());
        values.put(KEY_FROM_MEALS, meal.getFromLocation());
        values.put(KEY_TO_MEALS, meal.getToLocation());
        values.put(KEY_PACKET_MEALS, meal.getPacket());
        values.put(KEY_UPLOADER_MEALS, meal.getUploader());
        values.put(KEY_DELIVERY_MEALS, meal.getDelivery());
        values.put(KEY_RECEIVER_MEALS, meal.getReceiver());
        values.put(KEY_OTP, meal.getOTP());
        values.put(KEY_FREQUENCY, meal.getFrequency());
        values.put(KEY_COST, meal.getCost());
        // insert
        db.insert(TABLE_MEALS,null, values);
        db.close();
    }

    public int updateMeal(Meal meal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_MEALS, meal.getId());
        values.put(KEY_RECIPE_MEALS, meal.getRecipe());
        values.put(KEY_FROM_MEALS, meal.getFromLocation());
        values.put(KEY_TO_MEALS, meal.getToLocation());
        values.put(KEY_PACKET_MEALS, meal.getPacket());
        values.put(KEY_UPLOADER_MEALS, meal.getUploader());
        values.put(KEY_DELIVERY_MEALS, meal.getDelivery());
        values.put(KEY_RECEIVER_MEALS, meal.getReceiver());
        values.put(KEY_OTP, meal.getOTP());
        values.put(KEY_FREQUENCY, meal.getFrequency());
        values.put(KEY_COST, meal.getCost());

        int i = db.update(TABLE_MEALS, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(meal.getId()) });

        db.close();

        return i;
    }
}

