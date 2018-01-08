package com.example.shivang.icecreaminventory.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shivang on 08/01/18.
 */

public class Item implements Serializable {
    static int ctr=0;
    int id;
    String name;
    public class flavour {
        String flName;
        double price;
        int qty;

        public flavour(String flName, double price, int qty) {
            this.flName = flName;
            this.price = price;
            this.qty = qty;
        }

        public String getFlName() {
            return flName;
        }

        public void setFlName(String flName) {
            this.flName = flName;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public double getAmt() {
            return amt;
        }

        public void setAmt(double amt) {
            this.amt = amt;
        }

        double amt = price*qty;
    }
    ArrayList<flavour> flavours;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = ctr++;
        this.id = id;
        this.flavours = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Item(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<flavour> getFlavours() {
        return flavours;
    }

    public void setFlavours(ArrayList<flavour> flavours) {
        this.flavours = flavours;
    }
}
