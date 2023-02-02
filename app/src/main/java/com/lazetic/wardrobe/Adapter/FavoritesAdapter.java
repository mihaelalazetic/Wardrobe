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
import com.lazetic.wardrobe.models.Combination;

import java.util.List;

class FavoritesViewHolder extends RecyclerView.ViewHolder {
    ImageView topFav, bottomFav, shoeFav, accessoryFav;
    TextView date;

    public FavoritesViewHolder(@NonNull View itemView) {
        super(itemView);
        topFav = itemView.findViewById(R.id.topFavs);
        bottomFav = itemView.findViewById(R.id.bottomFavs);
        shoeFav = itemView.findViewById(R.id.shoeFavs);
        accessoryFav = itemView.findViewById(R.id.accessoryFavs);
        date = itemView.findViewById(R.id.date);
    }

}

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    Context context;
    List<Combination> combinations;
    private final FavRecyclerviewOnClickListener listener;

    public FavoritesAdapter(List<Combination> combinations, Context context, FavRecyclerviewOnClickListener listener) {
        this.listener = listener;
        this.context = context;
        this.combinations = combinations;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoritesViewHolder(LayoutInflater.from(context).inflate(R.layout.favorite_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        holder.topFav.setImageBitmap(combinations.get(position).getTop());
        holder.bottomFav.setImageBitmap(combinations.get(position).getBottom());
        holder.shoeFav.setImageBitmap(combinations.get(position).getShoe());
        holder.accessoryFav.setImageBitmap(combinations.get(position).getAccessory());
        String title = null;
        if (combinations.get(position).getName() == null) {
            title = combinations.get(position).getDate();
        } else {
            title = combinations.get(position).getName() + ", " + combinations.get(position).getDate();
        }
        holder.date.setText(title);

        holder.itemView.setOnClickListener(view -> listener.favRecyclerviewClick(combinations.get(position)));
    }

    @Override
    public int getItemCount() {
        return combinations.size();
    }

}