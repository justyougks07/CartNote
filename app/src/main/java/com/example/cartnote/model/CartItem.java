package com.example.cartnote.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int id;
    private String name;
    private int quantity;
    private int price;
    private int isChecked; // 0 for false, 1 for true
    private String deadline;
    private int imageResource;
    private int categoryId;

    public CartItem() {}

    public CartItem(int id, String name, int quantity, int price, int isChecked, String deadline, int imageResource, int categoryId) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.isChecked = isChecked;
        this.deadline = deadline;
        this.imageResource = imageResource;
        this.categoryId = categoryId;
    }

    public CartItem(int i, String s, int i1, int i2, int i3, String date) {
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getIsChecked() { return isChecked; }
    public void setIsChecked(int isChecked) { this.isChecked = isChecked; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public int getImageResource() { return imageResource; }
    public void setImageResource(int imageResource) { this.imageResource = imageResource; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public boolean isCheckedBool() {
        return isChecked == 1;
    }
}
