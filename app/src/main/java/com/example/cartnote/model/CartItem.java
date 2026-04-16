package com.example.cartnote.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int id;
    private String name;
    private int quantity;
    private int price;
    private int isChecked; // 0 for false, 1 for true
    private String deadline;
    private String imageUri;
    private int categoryId;

    public CartItem() {
    }

    public CartItem(int id, String name, int quantity, int price, int isChecked, String deadline, String imageUri, int categoryId) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.isChecked = isChecked;
        this.deadline = deadline;
        this.imageUri = imageUri;
        this.categoryId = categoryId;
    }

    // Compatibility constructor
    public CartItem(int id, String name, int quantity, int price, int isChecked, String deadline) {
        this(id, name, quantity, price, isChecked, deadline, null, -1);
    }

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

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public boolean isCheckedBool() {
        return isChecked == 1;
    }
}