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
import com.lazetic.wardrobe.models.Posts;

import java.util.List;

class PostsViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView name, location, date;

    public PostsViewHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.icon);
        name = itemView.findViewById(R.id.name);
        location = itemView.findViewById(R.id.location);
        date = itemView.findViewById(R.id.date);
    }

}

public class PostsAdapter extends RecyclerView.Adapter<PostsViewHolder> {

    Context context;
    List<Posts> posts;
    private final PostsRecyclerviewOnClickListener listener;

    public PostsAdapter(List<Posts> posts, Context context, PostsRecyclerviewOnClickListener listener) {
        this.listener = listener;
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater.from(context).inflate(R.layout.posts_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        holder.icon.setImageBitmap(posts.get(position).getIcon());
        holder.name.setText(posts.get(position).getName());
        holder.location.setText(posts.get(position).getLocation());
        holder.date.setText(posts.get(position).getDate());

        holder.itemView.setOnClickListener(view -> listener.postsRecyclerviewClick(posts.get(position)));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}