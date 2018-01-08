package com.example.shivang.icecreaminventory;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shivang.icecreaminventory.Models.Item;

import java.util.List;

/**
 * Created by shivang on 08/01/18.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    private Item mItem;

    public ItemAdapter(Item item) {
        mItem = item;
    }
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.MyViewHolder holder, int position) {
        Item.flavour item = mItem.getFlavours().get(position);
        holder.tvTitle.setText(item.getFlName());


    }

    @Override
    public int getItemCount() {
        return mItem.getFlavours().size();
    }


    static ViewGroup parent;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imgItem;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            imgItem = itemView.findViewById(R.id.imgItem);

        }
    }
}