package com.voldaa.addressbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StaticRVadapter extends RecyclerView.Adapter<StaticRVadapter.staticRViewHolder>  {

    private ArrayList<com.voldaa.addressbook.StaticRVModel> items;
    private OnplaceListener mOnplaceListener;
    private OnLongClickListener mOnLongClickListner;
    static int row_index = -1;

    public StaticRVadapter(ArrayList<com.voldaa.addressbook.StaticRVModel> items, OnplaceListener onplaceListener, OnLongClickListener onLongClickListener) {
        this.items = items;
        this.mOnplaceListener = onplaceListener;
        this.mOnLongClickListner = onLongClickListener;
    }

    @NonNull
    @Override
    public staticRViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_items,parent,false);
        staticRViewHolder staticRViewHolder = new staticRViewHolder(view, mOnplaceListener, mOnLongClickListner);
        return staticRViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull  staticRViewHolder holder, int position) {
        com.voldaa.addressbook.StaticRVModel currentItem = items.get(position);
        holder.textView.setText(currentItem.getText());
        holder.linearLayout.setBackgroundResource(R.drawable.static_rv_bg);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class staticRViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView textView;
        OnplaceListener onplaceListener;
        OnLongClickListener onLongClickListener;
        LinearLayout linearLayout;
        public staticRViewHolder(@NonNull  View itemView, OnplaceListener onplaceListener, OnLongClickListener onLongClickListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemtextView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            this.onplaceListener = onplaceListener;
            this.onLongClickListener = onLongClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onplaceListener.onPlaceClick(getAdapterPosition());
//            row_index = getAdapterPosition();

        }

        @Override
        public boolean onLongClick(View v) {
            onLongClickListener.onLongClick(getAdapterPosition());
            row_index = getAdapterPosition();
            return true;
        }
    }

    public interface OnplaceListener{
        void onPlaceClick(int position);

    }

    public interface OnLongClickListener{
        void onLongClick(int position);
    }
}
