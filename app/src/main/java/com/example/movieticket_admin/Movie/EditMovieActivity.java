package com.example.movieticket_admin.Movie;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.movieticket_admin.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditMovieActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDuration, etReleaseDate, etPosterUrl, etTrailerUrl, etDescription;
    private AutoCompleteTextView spinnerGenre, spinnerStatus;
    private LinearLayout layoutPosterPreview, layoutTrailerPreview;
    private ImageView ivPosterPreview, ivTrailerPreview;
    private Button btnCancel, btnSave;
    private ImageButton btnBack;

    private String movieId; // id phim cần chỉnh sửa
    private FirebaseFirestore db;

    private String[] genreOptions = {"Hành động", "Kinh dị", "Tình cảm", "Hài", "Phiêu lưu"}; // ví dụ
    private String[] statusOptions = {"Sắp chiếu", "Đang chiếu", "Đã kết thúc"}; // ví dụ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);

        db = FirebaseFirestore.getInstance();

        // Ánh xạ view
        etTitle = findViewById(R.id.et_title);
        etDuration = findViewById(R.id.et_duration);
        etReleaseDate = findViewById(R.id.et_release_date);
        etPosterUrl = findViewById(R.id.et_poster_url);
        etTrailerUrl = findViewById(R.id.et_trailer_url);
        etDescription = findViewById(R.id.et_description);
        spinnerGenre = findViewById(R.id.spinner_genre);
        spinnerStatus = findViewById(R.id.spinner_status);
        layoutPosterPreview = findViewById(R.id.layout_poster_preview);
        layoutTrailerPreview = findViewById(R.id.layout_trailer_preview);
        ivPosterPreview = findViewById(R.id.iv_poster_preview);
        ivTrailerPreview = findViewById(R.id.iv_trailer_preview);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        // Thiết lập adapter cho spinner

        // Lấy movieId từ Intent
        movieId = getIntent().getStringExtra("MOVIE_ID");
        if (movieId == null) {
            Toast.makeText(this, "Không tìm thấy phim để chỉnh sửa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load dữ liệu movie từ Firestore
        loadMovieData();

        // Bắt sự kiện nút back
        btnBack.setOnClickListener(v -> finish());

        // Bắt sự kiện nút cancel
        btnCancel.setOnClickListener(v -> finish());

        // Bắt sự kiện nút lưu
        btnSave.setOnClickListener(v -> updateMovie());

        // Click chọn ngày khởi chiếu
        etReleaseDate.setOnClickListener(v -> showDatePicker());

        // Khi nhập URL Poster, hiển thị preview
        etPosterUrl.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    layoutPosterPreview.setVisibility(View.VISIBLE);
                    Glide.with(EditMovieActivity.this)
                            .load(url)
                            .placeholder(R.drawable.bg_image_placeholder)
                            .error(R.drawable.bg_image_placeholder)
                            .into(ivPosterPreview);
                } else {
                    layoutPosterPreview.setVisibility(View.GONE);
                }
            }
        });
        setupSpinners();

        // Trailer preview - bạn có thể thêm xử lý tương tự nếu muốn
    }

    private void loadMovieData() {
        db.collection("movies").document(movieId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        etTitle.setText(documentSnapshot.getString("title"));
                        etDuration.setText(String.valueOf(documentSnapshot.getLong("duration")));
                        etReleaseDate.setText(documentSnapshot.getString("releaseDate"));
                        etPosterUrl.setText(documentSnapshot.getString("posterUrl"));
                        etTrailerUrl.setText(documentSnapshot.getString("trailerUrl"));
                        etDescription.setText(documentSnapshot.getString("description"));

                        String genre = documentSnapshot.getString("genre");
                        if (genre != null) {
                            int pos = Arrays.asList(genreOptions).indexOf(genre);
                            if (pos >= 0) spinnerGenre.setText(genreOptions[pos], false);
                        }

                        String status = documentSnapshot.getString("status");
                        if (status != null) {
                            int pos = Arrays.asList(statusOptions).indexOf(status);
                            if (pos >= 0) spinnerStatus.setText(statusOptions[pos], false);
                        }

                        // Hiển thị poster preview nếu có url
                        String posterUrl = documentSnapshot.getString("posterUrl");
                        if (posterUrl != null && !posterUrl.isEmpty()) {
                            layoutPosterPreview.setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .load(posterUrl)
                                    .placeholder(R.drawable.bg_image_placeholder)
                                    .error(R.drawable.bg_image_placeholder)
                                    .into(ivPosterPreview);
                        }

                        // Tương tự trailer preview nếu cần
                    } else {
                        Toast.makeText(this, "Phim không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etReleaseDate.setText(dateStr);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    private void updateMovie() {
        String title = etTitle.getText().toString().trim();
        String genre = spinnerGenre.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String releaseDate = etReleaseDate.getText().toString().trim();
        String status = spinnerStatus.getText().toString().trim();
        String posterUrl = etPosterUrl.getText().toString().trim();
        String trailerUrl = etTrailerUrl.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Kiểm tra bắt buộc
        if (title.isEmpty() || genre.isEmpty() || durationStr.isEmpty() || releaseDate.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ các trường bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Thời lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> movieData = new HashMap<>();
        movieData.put("title", title);
        movieData.put("genre", genre);
        movieData.put("duration", duration);
        movieData.put("releaseDate", releaseDate);
        movieData.put("status", status);
        movieData.put("posterUrl", posterUrl);
        movieData.put("trailerUrl", trailerUrl);
        movieData.put("description", description);

        db.collection("movies").document(movieId)
                .update(movieData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditMovieActivity.this, "Cập nhật phim thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditMovieActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
}
