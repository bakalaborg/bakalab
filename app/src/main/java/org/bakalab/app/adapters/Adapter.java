package org.bakalab.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Object> dataSet;
    private int layout;

    public abstract void onItemClick(View v, int position);
    public abstract void onEveryItem(Object item, int position, View holder);


    Adapter(int layout, List<Object> dataSet) {
        this.dataSet = dataSet;
        this.layout = layout;
    }

    protected abstract class ViewHolder extends RecyclerView.ViewHolder {

        View holder;
        public abstract void setItem(Object item, int position);

        ViewHolder(View view) {
            super(view);
            this.holder = view;
            view.setOnClickListener(this::onClick);
        }

        void onClick(View v) {
            onItemClick(v, getAdapterPosition());
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolder(itemView) {
            @Override
            public void setItem(Object item, int position) {
                onEveryItem(item, position, holder);
            }
        };

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.setItem(dataSet.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}