package com.example.movieticket_admin.Cinema;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movieticket_admin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class CinemaAdminFragment extends Fragment {

    private FloatingActionButton fabAddCinema;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cinema_admin, container, false);

        // Initialize FAB
        fabAddCinema = view.findViewById(R.id.fab_add_cinemas); // Thay R.id.fabAddMovie bằng ID thực tế trong layout

        // Setup FAB click listener
        setupFab();

        return view;
    }

    private void setupFab() {
        fabAddCinema.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCinemaActivity.class);
            startActivity(intent); // Thêm dòng này để thực sự chuyển trang
        });
    }
}