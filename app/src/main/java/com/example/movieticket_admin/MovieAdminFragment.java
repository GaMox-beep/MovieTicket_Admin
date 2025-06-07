package com.example.movieticket_admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;



public class MovieAdminFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho MovieAdminFragment
        View view = inflater.inflate(R.layout.fragment_movie_admin, container, false);

        // Tìm FAB trong layout
        FloatingActionButton fabAddMovie = view.findViewById(R.id.fab_add_movie);

        // Xử lý sự kiện click FAB
        fabAddMovie.setOnClickListener(v -> {
            // Chuyển hướng đến AddMovieActivity
            Intent intent = new Intent(getActivity(), AddMovieActivity.class);
            startActivity(intent);
        });

        return view;
    }
}