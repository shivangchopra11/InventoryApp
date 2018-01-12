package com.example.shivang.icecreaminventory.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shivang on 08/01/18.
 */

public class Item implements Serializable {
    static int ctr=0;
    String name;
    String desc;
    Map<String,Object> flavours;

    public Item() {
    }

    public Item(String name, String desc) {
        this.name = name;
        this.desc = desc;
        flavours = new HashMap<>();
    }

    public static int getCtr() {
        return ctr;
    }

    public static void setCtr(int ctr) {
        Item.ctr = ctr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Map<String, Object> getFlavours() {
        return flavours;
    }

    public void setFlavours(Map<String, Object> flavours) {
        this.flavours = flavours;
    }
}
