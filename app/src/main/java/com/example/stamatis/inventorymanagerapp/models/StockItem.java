package com.example.stamatis.inventorymanagerapp.models;

/**
 * Created by Stamatis Stiliatis Togrou on 11/7/2017.
 */

public class StockItem {

    private final String productName;
    private final String price;
    private final int quantity;
    private final String supplierName;
    private final String supplierEmail;
    private final String image;

    public StockItem(String productName, String price, int quantity, String supplierName, String supplierEmail, String image) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.supplierName = supplierName;
        this.supplierEmail = supplierEmail;
        this.image = image;
    }

    public String getProductName() {
        return productName;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public String getImage() {
        return image;
    }

}
