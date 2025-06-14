package com.example.movieticket_admin.Cinema;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.movieticket_admin.R;
import com.example.movieticket_admin.adapters.CinemaAdapter;
import com.example.movieticket_admin.models.Cinema;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CinemaAdminFragment extends Fragment {
    private RecyclerView recyclerViewCinemas;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private FloatingActionButton fabAddCinema;
    private CinemaAdapter cinemaAdapter;
    private List<Cinema> cinemaList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cinema_admin, container, false);

        // Khởi tạo các view
        recyclerViewCinemas = view.findViewById(R.id.recycler_view_cinemas);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyState = view.findViewById(R.id.tv_empty_state);
        fabAddCinema = view.findViewById(R.id.fab_add_cinema);

        // Cấu hình RecyclerView
        recyclerViewCinemas.setLayoutManager(new LinearLayoutManager(getContext()));
        cinemaList = new ArrayList<>();
        cinemaAdapter = new CinemaAdapter(getContext(), cinemaList);
        recyclerViewCinemas.setAdapter(cinemaAdapter);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Xử lý làm mới
        swipeRefreshLayout.setOnRefreshListener(this::fetchCinemas);

        // Xử lý FloatingActionButton
        fabAddCinema.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCinemaActivity.class);
            startActivity(intent);
        });

        // Tải dữ liệu ban đầu
        fetchCinemas();

        return view;
    }

    private void fetchCinemas() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        recyclerViewCinemas.setVisibility(View.GONE);

        db.collection("cinemas")
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        cinemaList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Cinema cinema = document.toObject(Cinema.class);
                            cinema.setId(document.getId());
                            cinemaList.add(cinema);
                        }
                        cinemaAdapter.notifyDataSetChanged();

                        // Cập nhật giao diện
                        if (cinemaList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            recyclerViewCinemas.setVisibility(View.GONE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                            recyclerViewCinemas.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e("AdminCinemaFragment", "Error getting documents: ", task.getException());
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerViewCinemas.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCinemas(); // Làm mới dữ liệu khi fragment được hiển thị lại
    }
}