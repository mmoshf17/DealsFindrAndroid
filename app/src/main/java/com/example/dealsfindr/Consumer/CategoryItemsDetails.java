package com.example.dealsfindr.Consumer;

import java.io.Serializable;

public class CategoryItemsDetails implements Serializable {


    private String ItemName;
    private int Price;
    private String SupplierName;

    public CategoryItemsDetails (String itemName, int price,String supplierName)

    {
        // this.Token = token;

        this.ItemName = itemName;
        this.Price = price;
        this.SupplierName = supplierName;

    }

    public CategoryItemsDetails(){
    }

    public String getItemName()
    {
        return ItemName;
    }
    public int getPrice()
    {
        return Price;
    }
    public String getSupplierName()
    {
        return SupplierName;
    }


    public String toString() {


        return "ItemActivity: " + ItemName + "\n" +  "Price: " + Price +  "\n" + "From: " + SupplierName;
    }
}
