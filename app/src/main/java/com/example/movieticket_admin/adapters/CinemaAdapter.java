package com.example.movieticket_admin.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.movieticket_admin.R;
import com.example.movieticket_admin.models.Cinema;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.CinemaViewHolder> {

    private Context context;
    private List<Cinema> cinemaList;
    private OnCinemaClickListener listener;

    public interface OnCinemaClickListener {
        void onCinemaClick(Cinema cinema);
        void onCinemaEdit(Cinema cinema);
    }

    public CinemaAdapter(Context context, List<Cinema> cinemaList) {
        this.context = context;
        this.cinemaList = cinemaList;
    }

    public void setOnCinemaClickListener(OnCinemaClickListener listener) {
        this.listener = listener;
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
        holder.bind(cinema);
    }

    @Override
    public int getItemCount() {
        return cinemaList != null ? cinemaList.size() : 0;
    }

    public void updateCinemas(List<Cinema> newCinemas) {
        this.cinemaList = newCinemas;
        notifyDataSetChanged();
    }

    public void addCinema(Cinema cinema) {
        if (cinemaList != null) {
            cinemaList.add(0, cinema);
            notifyItemInserted(0);
        }
    }

    public void updateCinema(Cinema updatedCinema) {
        if (cinemaList != null) {
            for (int i = 0; i < cinemaList.size(); i++) {
                if (cinemaList.get(i).getId().equals(updatedCinema.getId())) {
                    cinemaList.set(i, updatedCinema);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void removeCinema(String cinemaId) {
        if (cinemaList != null) {
            for (int i = 0; i < cinemaList.size(); i++) {
                if (cinemaList.get(i).getId().equals(cinemaId)) {
                    cinemaList.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }
    }

    class CinemaViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivCinemaImage;
        private TextView tvCinemaName;
        private TextView tvCinemaCity;
        private TextView tvCinemaAddress;
        private TextView tvStatus;
        private ImageButton btnEdit;

        public CinemaViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCinemaImage = itemView.findViewById(R.id.iv_cinema_image);
            tvCinemaName = itemView.findViewById(R.id.tv_cinema_name);
            tvCinemaCity = itemView.findViewById(R.id.tv_cinema_city);
            tvCinemaAddress = itemView.findViewById(R.id.tv_cinema_address);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }

        public void bind(Cinema cinema) {
            // Set cinema name
            tvCinemaName.setText(cinema.getName());

            // Set city
            tvCinemaCity.setText(cinema.getCity());

            // Set address
            tvCinemaAddress.setText(cinema.getAddress());

            // Set status


            // Load image
            if (cinema.getImageUrl() != null && !cinema.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(cinema.getImageUrl())
                        .transform(new RoundedCorners(16))
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivCinemaImage);
            } else {
                ivCinemaImage.setImageResource(R.drawable.ic_launcher_background);
            }

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCinemaClick(cinema);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCinemaEdit(cinema);
                }
            });
        }
    }
}