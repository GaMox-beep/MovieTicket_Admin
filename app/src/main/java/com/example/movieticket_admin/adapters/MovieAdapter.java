package com.example.movieticket_admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieticket_admin.R;
import com.example.movieticket_admin.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
        void onMovieEdit(Movie movie);
        void onMovieDelete(Movie movie);
    }

    public MovieAdapter(List<Movie> movieList, OnMovieClickListener listener) {
        this.movieList = movieList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_admin, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.bind(movie, listener);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void updateMovies(List<Movie> newMovies) {
        this.movieList.clear();
        this.movieList.addAll(newMovies);
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPoster;
        private TextView tvTitle;
        private TextView tvGenre;
        private TextView tvDuration;
        private TextView tvStatus;
        private ImageView ivMore;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvGenre = itemView.findViewById(R.id.tv_genre);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvStatus = itemView.findViewById(R.id.tv_status);
            ivMore = itemView.findViewById(R.id.iv_more);
        }

        public void bind(Movie movie, OnMovieClickListener listener) {
            // Set movie title
            tvTitle.setText(movie.getTitle());

            // Set genre
            tvGenre.setText(movie.getGenre());

            // Set duration (e.g. "120 phút")
            tvDuration.setText(movie.getDurationFormatted());

            // Set status
            tvStatus.setText(movie.getStatus());

            // Set background color or drawable theo status (ví dụ)


            // Load poster image với Glide
            if (movie.hasPoster()) {
                Glide.with(itemView.getContext())
                        .load(movie.getPosterUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(ivPoster);
            } else {
                ivPoster.setImageResource(R.drawable.ic_image_placeholder);
            }

            ivPoster.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMovieEdit(movie);
                }
            });

            // Click vào nút 3 chấm mở menu popup
            ivMore.setOnClickListener(v -> showOptionsMenu(movie, listener));
        }

        private void showOptionsMenu(Movie movie, OnMovieClickListener listener) {
            androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(
                    itemView.getContext(), ivMore);

            popup.getMenuInflater().inflate(R.menu.movie_item_menu, popup.getMenu());  // menu chỉ còn Delete

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_delete) {
                    if (listener != null) {
                        listener.onMovieDelete(movie);
                    }
                    return true;
                }
                return false;
            });

            popup.show();
        }

    }
}
