package com.example.dealsfindr;

import java.io.Serializable;

public class AutoCompleteItems implements Serializable {

    //For getting lender's requests
    //private String Token;
    private String ItemName;
    private int Price;





    public AutoCompleteItems (String itemName)

    {
        // this.Token = token;
        this.ItemName = itemName;

    }

    public AutoCompleteItems(){
    }

    public String toString() {

        return ItemName;
    }
}
