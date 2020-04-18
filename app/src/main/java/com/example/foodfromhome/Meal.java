package com.example.foodfromhome;

/**
 * <h1>Meal</h1>
 * This class implements the Meal object, used to
 * make entries into the database for tracking meal
 * to be delivered or received by users. Entries are
 * made when a user uploads a meal
 * <p>
 *
 * @author  Naren Surampudi
 * @version 1.0
 * @since   2020-3-3
 */

public class Meal {

    private String id;
    private String recipe;
    private String fromLocation;
    private String toLocation;
    private String packet;
    private String uploader;
    private String delivery;
    private String receiver;
    private int otp;
    private String frequency;
    private float cost;

    public Meal(){

    }

    public Meal(String id, String recipe, String fromLocation, String toLocation, String packet, String uploader, String delivery, String receiver, int otp, String frequency, float cost) {
        this.id = id;
        this.recipe = recipe;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.packet = packet;
        this.uploader = uploader;
        this.delivery = delivery;
        this.receiver = receiver;
        this.otp = otp;
        this.frequency = frequency;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public String getRecipe() {
        return recipe;
    }

    public String getPacket() {
        return packet;
    }

    public String getUploader() {
        return uploader;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public String getReceiver() {
        return receiver;
    }

    public int getOTP() { return otp; }

    public String getFrequency() {
        return frequency;
    }

    public float getCost() {
        return cost;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setOTP(int otp) { this.otp = otp; }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n" + "Recipe: " + recipe + "\n" + "Size: " + packet + "\n" + "Location: " + fromLocation;
    }

}

