package com.example.shivang.icecreaminventory.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shivang on 19/01/18.
 */

public class Employee {
    String name;
    int qty;
    Map<String,Object> items;

    public Employee() {
    }

    public Employee(String name, int qty) {
        this.name = name;
        this.qty = qty;
        items = new HashMap<>();
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
