package com.example.dealsfindr.Consumer;

import java.io.Serializable;

public class PromotedItems implements Serializable {

    //For getting lender's requests
    //private String Token;
    private String ItemName;
    private String SupplierName;
    private int Price;





    public PromotedItems (String itemName, String supplierName, int price)

    {
        // this.Token = token;
        this.ItemName = itemName;
        this.SupplierName = supplierName;
        this.Price = price;

    }

    public PromotedItems(){
    }

    public String getItemName(){return ItemName;}

    public String toString() {

        return "ItemActivity: " + ItemName + "\n" + "From: " + SupplierName + "\n" + "Price: " + Price;
    }
}
