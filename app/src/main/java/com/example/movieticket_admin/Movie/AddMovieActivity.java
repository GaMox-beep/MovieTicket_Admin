package com.example.movieticket_admin.Movie;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.movieticket_admin.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddMovieActivity extends AppCompatActivity {

    private static final String TAG = "AddMovieActivity";
    private static final String COLLECTION_MOVIES = "movies";

    // UI Components
    private ImageButton btnBack;
    private TextInputEditText etTitle, etDuration, etReleaseDate, etDescription, etPosterUrl, etTrailerUrl;
    private TextInputLayout tilTitle, tilGenre, tilDuration, tilReleaseDate, tilStatus, tilDescription, tilPosterUrl, tilTrailerUrl;
    private AutoCompleteTextView spinnerGenre, spinnerStatus;
    private Button btnCancel, btnSave;
    private ImageView ivPosterPreview, ivTrailerPreview;
    private LinearLayout layoutPosterPreview, layoutTrailerPreview;

    // Firebase
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;

    // Date formatter
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        initFirebase();
        initViews();
        setupSpinners();
        setupDatePicker();
        setupClickListeners();
        setupUrlPreview();
        initProgressDialog();
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initViews() {
        // Header
        btnBack = findViewById(R.id.btn_back);

        // Input fields
        etTitle = findViewById(R.id.et_title);
        etDuration = findViewById(R.id.et_duration);
        etReleaseDate = findViewById(R.id.et_release_date);
        etDescription = findViewById(R.id.et_description);
        etPosterUrl = findViewById(R.id.et_poster_url);
        etTrailerUrl = findViewById(R.id.et_trailer_url);

        // Text Input Layouts
        tilTitle = findViewById(R.id.til_title);
        tilGenre = findViewById(R.id.til_genre);
        tilDuration = findViewById(R.id.til_duration);
        tilReleaseDate = findViewById(R.id.til_release_date);
        tilStatus = findViewById(R.id.til_status);
        tilDescription = findViewById(R.id.til_description);
        tilPosterUrl = findViewById(R.id.til_poster_url);
        tilTrailerUrl = findViewById(R.id.til_trailer_url);

        // Spinners
        spinnerGenre = findViewById(R.id.spinner_genre);
        spinnerStatus = findViewById(R.id.spinner_status);

        // Preview components
        ivPosterPreview = findViewById(R.id.iv_poster_preview);
        ivTrailerPreview = findViewById(R.id.iv_trailer_preview);
        layoutPosterPreview = findViewById(R.id.layout_poster_preview);
        layoutTrailerPreview = findViewById(R.id.layout_trailer_preview);

        // Action buttons
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
    }

    private void setupSpinners() {
        // Genre Spinner
        String[] genres = getResources().getStringArray(R.array.movie_genres);
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, genres);
        spinnerGenre.setAdapter(genreAdapter);

        // Status Spinner
        String[] statuses = getResources().getStringArray(R.array.movie_status);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, statuses);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void setupDatePicker() {
        etReleaseDate.setOnClickListener(v -> showDatePicker());
        tilReleaseDate.setEndIconOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    etReleaseDate.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnCancel.setOnClickListener(v -> showCancelConfirmationDialog());
        btnSave.setOnClickListener(v -> validateAndSaveMovie());
    }

    private void setupUrlPreview() {
        // Setup poster URL preview
        etPosterUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString().trim();
                if (!TextUtils.isEmpty(url) && isValidUrl(url)) {
                    loadPosterPreview(url);
                    layoutPosterPreview.setVisibility(View.VISIBLE);
                } else {
                    layoutPosterPreview.setVisibility(View.GONE);
                }
            }
        });

        // Setup trailer URL preview
        etTrailerUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString().trim();
                if (!TextUtils.isEmpty(url) && isValidUrl(url)) {
                    loadTrailerPreview(url);
                    layoutTrailerPreview.setVisibility(View.VISIBLE);
                } else {
                    layoutTrailerPreview.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadPosterPreview(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivPosterPreview);
    }

    private void loadTrailerPreview(String url) {
        // For video URLs, we'll show a thumbnail or placeholder
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_play_circle)
                .error(R.drawable.ic_play_circle)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivTrailerPreview);
    }

    private void showCancelConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn hủy? Tất cả dữ liệu đã nhập sẽ bị mất.")
                .setPositiveButton("Có", (dialog, which) -> finish())
                .setNegativeButton("Không", null)
                .show();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu phim...");
        progressDialog.setCancelable(false);
    }

    private void validateAndSaveMovie() {
        // Clear previous errors
        clearErrors();

        String title = etTitle.getText().toString().trim();
        String genre = spinnerGenre.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String releaseDate = etReleaseDate.getText().toString().trim();
        String status = spinnerStatus.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String posterUrl = etPosterUrl.getText().toString().trim();
        String trailerUrl = etTrailerUrl.getText().toString().trim();

        boolean isValid = true;

        // Validate required fields
        if (TextUtils.isEmpty(title)) {
            tilTitle.setError("Vui lòng nhập tên phim");
            isValid = false;
        }

        if (TextUtils.isEmpty(genre)) {
            tilGenre.setError("Vui lòng chọn thể loại");
            isValid = false;
        }

        if (TextUtils.isEmpty(durationStr)) {
            tilDuration.setError("Vui lòng nhập thời lượng");
            isValid = false;
        } else {
            try {
                int duration = Integer.parseInt(durationStr);
                if (duration <= 0 || duration > 999) {
                    tilDuration.setError("Thời lượng phải từ 1-999 phút");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilDuration.setError("Thời lượng không hợp lệ");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(releaseDate)) {
            tilReleaseDate.setError("Vui lòng chọn ngày khởi chiếu");
            isValid = false;
        } else {
            // Validate date format
            try {
                dateFormatter.parse(releaseDate);
            } catch (ParseException e) {
                tilReleaseDate.setError("Định dạng ngày không hợp lệ");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(status)) {
            tilStatus.setError("Vui lòng chọn trạng thái");
            isValid = false;
        }

        // Poster URL is optional, but if provided, should be valid URL
        if (!TextUtils.isEmpty(posterUrl) && !isValidUrl(posterUrl)) {
            tilPosterUrl.setError("URL poster không hợp lệ");
            isValid = false;
        }

        // Trailer URL is optional, but if provided, should be valid URL
        if (!TextUtils.isEmpty(trailerUrl) && !isValidUrl(trailerUrl)) {
            tilTrailerUrl.setError("URL trailer không hợp lệ");
            isValid = false;
        }

        if (isValid) {
            // Save movie to Firestore
            saveMovieToFirestore(title, genre, Integer.parseInt(durationStr),
                    releaseDate, status, description, posterUrl, trailerUrl);
        }
    }

    private boolean isValidUrl(String url) {
        return url.matches("^(http|https)://.*");
    }

    private void clearErrors() {
        tilTitle.setError(null);
        tilGenre.setError(null);
        tilDuration.setError(null);
        tilReleaseDate.setError(null);
        tilStatus.setError(null);
        tilPosterUrl.setError(null);
        tilTrailerUrl.setError(null);
    }

    private void saveMovieToFirestore(String title, String genre, int duration,
                                      String releaseDate, String status, String description,
                                      String posterUrl, String trailerUrl) {
        progressDialog.show();

        // Create movie data map
        Map<String, Object> movieData = new HashMap<>();
        movieData.put("title", title);
        movieData.put("genre", genre);
        movieData.put("duration", duration);
        movieData.put("releaseDate", releaseDate);
        movieData.put("status", status);
        movieData.put("description", description);
        movieData.put("createdAt", System.currentTimeMillis());

        // Add URLs if provided
        if (!TextUtils.isEmpty(posterUrl)) {
            movieData.put("posterUrl", posterUrl);
        }
        if (!TextUtils.isEmpty(trailerUrl)) {
            movieData.put("trailerUrl", trailerUrl);
        }

        // Add to Firestore
        firestore.collection(COLLECTION_MOVIES)
                .add(movieData)
                .addOnSuccessListener(documentReference -> {
                    // Update document with its own ID
                    String movieId = documentReference.getId();
                    documentReference.update("id", movieId)
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Thêm phim thành công!", Toast.LENGTH_SHORT).show();

                                // Return result and finish
                                Intent resultIntent = new Intent();
                                movieData.put("id", movieId);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Log.e(TAG, "Failed to update movie ID", e);
                                Toast.makeText(this, "Lỗi khi cập nhật ID phim: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Failed to save movie to Firestore", e);
                    Toast.makeText(this, "Lỗi khi lưu phim: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        if (hasUnsavedChanges()) {
            showCancelConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasUnsavedChanges() {
        return !TextUtils.isEmpty(etTitle.getText()) ||
                !TextUtils.isEmpty(spinnerGenre.getText()) ||
                !TextUtils.isEmpty(etDuration.getText()) ||
                !TextUtils.isEmpty(etReleaseDate.getText()) ||
                !TextUtils.isEmpty(spinnerStatus.getText()) ||
                !TextUtils.isEmpty(etDescription.getText()) ||
                !TextUtils.isEmpty(etPosterUrl.getText()) ||
                !TextUtils.isEmpty(etTrailerUrl.getText());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}