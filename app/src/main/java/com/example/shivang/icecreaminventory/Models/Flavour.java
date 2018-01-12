package com.example.shivang.icecreaminventory.Models;

/**
 * Created by shivang on 11/01/18.
 */
public class Flavour {
    String flName;
    String flDesc;
    int flQty;

    public Flavour() {
    }

    public Flavour(String flName, String flDesc) {
        this.flName = flName;
        this.flDesc = flDesc;
        this.flQty=0;
    }

    public String getFlName() {
        return flName;
    }

    public void setFlName(String flName) {
        this.flName = flName;
    }

    public String getFlDesc() {
        return flDesc;
    }

    public void setFlDesc(String flDesc) {
        this.flDesc = flDesc;
    }

    public int getFlQty() {
        return flQty;
    }

    public void setFlQty(int flQty) {
        this.flQty = flQty;
    }
}