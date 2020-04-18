package com.example.foodfromhome;

/**
 * <h1>History</h1>
 * This class implements the History object, used to
 * make entries into the database for tracking a user's
 * order history
 * <p>
 *
 * @author  Naren Surampudi
 * @version 1.0
 * @since   2020-3-3
 */

public class History {

    private String email;
    private String recipe;
    private float cost;

    History() {

    }

    History(String email, String recipe, float cost) {
        this.email = email;
        this.recipe = recipe;
        this.cost = cost;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getEmail() {
        return email;
    }

    public String getRecipe() {
        return recipe;
    }

    public float getCost() {
        return cost;
    }

    public String toString() {
        return "Recipe: " + recipe + "\nCost(Rs): " + cost;
    }
}
