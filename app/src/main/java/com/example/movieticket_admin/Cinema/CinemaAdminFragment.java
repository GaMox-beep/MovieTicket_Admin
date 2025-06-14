package com.example.movieticket_admin.Cinema;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.movieticket_admin.R;
import com.example.movieticket_admin.adapters.CinemaAdapter;
import com.example.movieticket_admin.models.Cinema;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CinemaAdminFragment extends Fragment implements CinemaAdapter.OnCinemaClickListener {

    private RecyclerView recyclerViewCinemas;
    private CinemaAdapter cinemaAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddCinemas;
    private List<Cinema> cinemaList;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> addCinemaLauncher;
    private ActivityResultLauncher<Intent> editCinemaLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Activity Result Launchers
        addCinemaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        // Refresh danh sách sau khi thêm cinema
                        loadCinemas();
                    }
                }
        );

        editCinemaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        // Refresh danh sách sau khi edit/delete cinema
                        loadCinemas();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cinema_admin, container, false);

        initViews(view);
        setupRecyclerView();
        setupFab();
        setupSwipeRefresh();
        loadCinemas();

        return view;
    }

    private void initViews(View view) {
        recyclerViewCinemas = view.findViewById(R.id.recycler_view_cinemas);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        fabAddCinemas = view.findViewById(R.id.fab_add_cinemas);

        cinemaList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        // Setup GridLayoutManager với 2 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewCinemas.setLayoutManager(gridLayoutManager);

        // Setup adapter
        cinemaAdapter = new CinemaAdapter(getContext(), cinemaList);
        cinemaAdapter.setOnCinemaClickListener(this);
        recyclerViewCinemas.setAdapter(cinemaAdapter);
    }

    private void setupFab() {
        fabAddCinemas.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCinemaActivity.class);
            addCinemaLauncher.launch(intent);
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadCinemas);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary_color
        );
    }

    private void loadCinemas() {
        showLoading(true);

        // TODO: Implement Firebase/API call to load cinemas
        // Tạm thời tạo dữ liệu mẫu
        loadSampleData();
    }

    private void loadSampleData() {
        // Dữ liệu mẫu cho test
        List<Cinema> sampleCinemas = new ArrayList<>();

        sampleCinemas.add(new Cinema("1", "CGV Vincom Center", "Hồ Chí Minh",
                "Tầng 4, Vincom Center, 72 Lê Thánh Tôn, Quận 1",
                "https://example.com/cgv1.jpg",
                "Rạp chiếu phim hiện đại với công nghệ âm thanh Dolby Atmos"));

        sampleCinemas.add(new Cinema("2", "Lotte