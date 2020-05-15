package com.example.dealsfindr;

import java.io.Serializable;

public class AutoCompleteItemsPromotions implements Serializable {

    //For getting lender's requests
    //private String Token;
    private String ItemName;


    public AutoCompleteItemsPromotions (String itemName)

    {
        // this.Token = token;
        this.ItemName = itemName;

    }

    public AutoCompleteItemsPromotions(){
    }

    public String toString() {

        return ItemName;
    }
}

