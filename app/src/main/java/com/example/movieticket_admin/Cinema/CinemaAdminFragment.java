package com.example.movieticket_admin.Cinema;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movieticket_admin.Movie.AddMovieActivity;
import com.example.movieticket_admin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class CinemaAdminFragment extends Fragment {

    private FloatingActionButton fabAddMovie;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cinema_admin, container, false);
    }
    private void setupFab() {
        fabAddMovie.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCinemaActivity.class);
        });
    }
}