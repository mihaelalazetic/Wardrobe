package com.lazetic.wardrobe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lazetic.wardrobe.R;
import com.lazetic.wardrobe.models.Wardrobe;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder  {
    ImageView coverPhoto;
    TextView description;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        coverPhoto = itemView.findViewById(R.id.coverPhoto);
        description = itemView.findViewById(R.id.description);
    }

}

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<Wardrobe> wardrobes;
    private final RecyclerviewOnClickListener listener;

    public MyAdapter(List<Wardrobe> wardrobes, Context context , RecyclerviewOnClickListener listener) {
        this.listener = listener;
        this.context = context;
        this.wardrobes = wardrobes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.coverPhoto.setImageBitmap(wardrobes.get(position).getImage());
        holder.description.setText(wardrobes.get(position).getName());

        holder.itemView.setOnClickListener(view -> listener.recyclerviewClick(wardrobes.get(position)));
    }

    @Override
    public int getItemCount() {
        return wardrobes.size();
    }

}