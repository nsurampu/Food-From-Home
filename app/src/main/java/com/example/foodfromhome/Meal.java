package com.example.foodfromhome;

public class Meal {

    private String id;
    private String recipe;
    private String community;
    private String packet;
    private String uploader;
    private String delivery;
    private String receiver;
    private int otp;

    public Meal(){

    }

    public Meal(String id, String recipe, String community, String packet, String uploader, String delivery, String receiver, int otp) {
        this.id = id;
        this.recipe = recipe;
        this.community = community;
        this.packet = packet;
        this.uploader = uploader;
        this.delivery = delivery;
        this.receiver = receiver;
        this.otp = otp;
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

    public String getCommunity() {
        return community;
    }

    public String getReceiver() {
        return receiver;
    }

    public int getOTP() { return otp; }

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

    public void setCommunity(String community) {
        this.community = community;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setOTP(int otp) { this.otp = otp; }

    @Override
    public String toString() {
        return "ID: " + id + "\n" + "Recipe: " + recipe + "\n" + "Size: " + packet + "\n" + "Location: " + community;
    }

}

