package com.example.dealsfindr.Consumer;

import java.io.Serializable;

public class Categories implements Serializable {

    private int CategroyId;
    private String CategroyName;
    //private String Suppliername;
    //private String ItemName;
    //private int Price;




    public Categories (int categoryId, String categroyName)

    {
        // this.Token = token;
        this.CategroyId = categoryId;
        this.CategroyName = categroyName;
        //this.ItemName = itemName;
        //this.Price = price;

    }

    public Categories(){
    }

    public int getCategoryId()
    {
        return CategroyId;
    }



    public String toString() {


        return CategroyName;
    }
}
