package com.example.movieticket_admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieticket_admin.Cinema.EditCinemaActivity;
import com.example.movieticket_admin.R;
import com.example.movieticket_admin.models.Cinema;

import java.util.List;

public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.CinemaViewHolder> {
    private List<Cinema> cinemaList;
    private Context context;

    public CinemaAdapter(Context context, List<Cinema> cinemaList) {
        this.context = context;
        this.cinemaList = cinemaList;
    }

    @NonNull
    @Override
    public CinemaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cinema, parent, false);
        return new CinemaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CinemaViewHolder holder, int position) {
        Cinema cinema = cinemaList.get(position);
        holder.textCinemaName.setText(cinema.getName());

        // Tải hình ảnh bằng Glide
        if (cinema.getImageUrl() != null && !cinema.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(cinema.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imageCinema);
        } else {
            holder.imageCinema.setImageResource(R.drawable.ic_launcher_background); // Hình ảnh mặc định
        }

        // Xử lý sự kiện nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditCinemaActivity.class);
            intent.putExtra("cinemaId", cinema.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cinemaList.size();
    }

    public static class CinemaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCinema;
        TextView textCinemaName;

        public CinemaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCinema = itemView.findViewById(R.id.image_cinema);
            textCinemaName = itemView.findViewById(R.id.text_cinema_name);
        }
    }
}