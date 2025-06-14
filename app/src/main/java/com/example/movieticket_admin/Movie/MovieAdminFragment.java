package com.example.movieticket_admin.Movie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.movieticket_admin.R;
import com.example.movieticket_admin.adapters.MovieAdapter;
import com.example.movieticket_admin.models.Movie;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MovieAdminFragment extends Fragment {

    private static final String TAG = "MovieAdminFragment";
    private static final String COLLECTION_MOVIES = "movies";
    private static final int REQUEST_ADD_MOVIE = 1001;

    // UI Components
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddMovie;

    // Data
    private List<Movie> movieList;
    private MovieAdapter movieAdapter;
    private FirebaseFirestore firestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        movieList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        setupFab();
        loadMovies();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_movies);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        fabAddMovie = view.findViewById(R.id.fab_add_movie);
    }

    private void setupRecyclerView() {
        // Setup GridLayoutManager with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        // Setup adapter
        movieAdapter = new MovieAdapter(movieList, new MovieAdapter.OnMovieClickListener() {
            @Override
            public void onMovieClick(Movie movie) {
                // Handle movie item click - navigate to movie detail/edit
                Toast.makeText(getContext(), "Clicked: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMovieEdit(Movie movie) {
                Intent intent = new Intent(getContext(), EditMovieActivity.class);
                intent.putExtra("MOVIE_ID", movie.getId());
                startActivity(intent);
            }


            @Override
            public void onMovieDelete(Movie movie) {
                // Handle delete movie
                deleteMovie(movie);
            }
        });


        recyclerView.setAdapter(movieAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadMovies);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    private void setupFab() {
        fabAddMovie.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMovieActivity.class);
            startActivityForResult(intent, REQUEST_ADD_MOVIE);
        });
    }

    private void loadMovies() {
        showLoading(true);

        firestore.collection(COLLECTION_MOVIES)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    movieList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Movie movie = document.toObject(Movie.class);
                            if (movie != null) {
                                movieList.add(movie);
                                movie.setId(document.getId()); // Bắt buộc phải set Id document Firestore cho movie

                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error converting document to Movie", e);
                        }
                    }

                    updateUI();
                    showLoading(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading movies", e);
                    Toast.makeText(getContext(), "Lỗi khi tải danh sách phim: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    showLoading(false);
                });
    }

    private void deleteMovie(Movie movie) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa phim \"" + movie.getTitle() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    performDeleteMovie(movie);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performDeleteMovie(Movie movie) {
        firestore.collection(COLLECTION_MOVIES)
                .document(movie.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    movieList.remove(movie);
                    movieAdapter.notifyDataSetChanged();
                    updateUI();
                    Toast.makeText(getContext(), "Đã xóa phim: " + movie.getTitle(),
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting movie", e);
                    Toast.makeText(getContext(), "Lỗi khi xóa phim: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showLoading(boolean show) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateUI() {
        if (movieList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            movieAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_MOVIE && resultCode == getActivity().RESULT_OK) {
            // Reload movies when a new movie is added
            loadMovies();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        loadMovies();
    }
}