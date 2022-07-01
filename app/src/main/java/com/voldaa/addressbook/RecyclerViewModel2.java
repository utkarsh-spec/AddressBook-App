package com.voldaa.addressbook;

public class RecyclerViewModel2 {
    private int image;
    private String text;

    public RecyclerViewModel2(int image, String text) {
        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public String getText() {
        return text;
    }
}
