package com.voldaa.addressbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.RVViewHolder>{

    private ArrayList<RecyclerViewModel2> items;
    private OnRv2Listener mOnRv2Listener;
    static int row_index = -1;

    public RecyclerViewAdapter2(ArrayList<RecyclerViewModel2> items, OnRv2Listener onRv2Listener) {
        this.items = items;
        this.mOnRv2Listener = onRv2Listener;
    }

    @NonNull
    @Override
    public RVViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view2_items,parent,false);
        RecyclerViewAdapter2.RVViewHolder viewHolder = new RecyclerViewAdapter2.RVViewHolder(view,mOnRv2Listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter2.RVViewHolder holder, int position) {

        RecyclerViewModel2 currentItem = items.get(position);
        holder.textView.setText(currentItem.getText());
        holder.imageView.setImageResource(currentItem.getImage());

        if(row_index == position){
            holder.linearLayout.setBackgroundResource(R.drawable.recycler_view_item_bg_2);
        }else{
            holder.linearLayout.setBackgroundResource(R.drawable.recycler_view_item_selected2);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        OnRv2Listener onRv2Listener;
        ImageView imageView;
        LinearLayout linearLayout;

        public RVViewHolder(@NonNull View itemView, OnRv2Listener onRv2Listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rv2Image);
            textView = itemView.findViewById(R.id.rv2Text);
            linearLayout = itemView.findViewById(R.id.rv2linearlayout);
            this.onRv2Listener = onRv2Listener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onRv2Listener.onRv2ItemClick(getAdapterPosition());
            row_index = getAdapterPosition();
        }
    }
    public interface OnRv2Listener{
        void onRv2ItemClick(int position);
    }
}
