package com.example.shivang.icecreaminventory.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shivang on 19/01/18.
 */

public class Employee {
    String name;
    int qty;
    String pass;
    Map<String,Object> items;

    public Employee() {
    }

    public Employee(String name, int qty,String pass) {
        this.name = name;
        this.qty = qty;
        this.pass = pass;
        items = new HashMap<>();
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Map<String, Object> getItems() {
        return items;
    }

    public void setItems(Map<String, Object> items) {
        this.items = items;
    }
}
