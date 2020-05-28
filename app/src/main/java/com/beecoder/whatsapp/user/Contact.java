package com.beecoder.whatsapp.user;

public class Contact {
    private String name;
    private String number;

    public Contact(String name, String number, String uid) {
        this.name = name;
        this.number = number;
        Uid = uid;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    private String Uid;
    private int image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Contact(String name, String number, int image) {
        this.name = name;
        this.number = number;
        this.image = image;
    }

    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }
}
