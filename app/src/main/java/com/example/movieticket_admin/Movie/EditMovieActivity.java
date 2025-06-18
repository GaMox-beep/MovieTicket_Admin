package com.example.movieticket_admin.Movie;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieticket_admin.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditMovieActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDuration, etReleaseDate, etPosterUrl, etTrailerUrl, etDescription;
    private AutoCompleteTextView spinnerGenre, spinnerStatus;
    private LinearLayout layoutPosterPreview, layoutTrailerPreview;
    private ImageView ivPosterPreview, ivTrailerPreview;
    private Button btnCancel, btnSave, btnAddShowtime;
    private ImageButton btnBack;

    private String movieId;
    private FirebaseFirestore db;
    private List<String> seatLayoutNames = new ArrayList<>();
    private Map<String, String> seatLayoutIdMap = new HashMap<>();
    private List<String> cinemaNames = new ArrayList<>();
    private Map<String, String> cinemaIdMap = new HashMap<>();
    private String[] genreOptions = {"Hành động", "Kinh dị", "Tình cảm", "Hài", "Phiêu lưu"};
    private String[] statusOptions = {"Sắp chiếu", "Đang chiếu", "Đã kết thúc"};
    private boolean seatLayoutsLoaded = false;

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
        btnAddShowtime = findViewById(R.id.btn_add_showtime);
        btnBack = findViewById(R.id.btn_back);

        // Lấy movieId từ Intent
        movieId = getIntent().getStringExtra("MOVIE_ID");
        if (movieId == null) {
            Toast.makeText(this, "Không tìm thấy phim để chỉnh sửa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tải danh sách layout ghế và rạp
        loadSeatLayoutNames();
        loadCinemaNames();

        // Thiết lập spinners
        setupSpinners();

        // Load dữ liệu movie từ Firestore
        loadMovieData();

        // Bắt sự kiện nút back
        btnBack.setOnClickListener(v -> finish());

        // Bắt sự kiện nút cancel
        btnCancel.setOnClickListener(v -> finish());

        // Bắt sự kiện nút lưu
        btnSave.setOnClickListener(v -> updateMovie());

        // Bắt sự kiện nút thêm suất chiếu
        btnAddShowtime.setOnClickListener(v -> showAddShowtimeDialog());

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
    }

    private void loadSeatLayoutNames() {
        db.collection("seat_layout")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    seatLayoutNames.clear();
                    seatLayoutIdMap.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String id = document.getId();
                        if (name != null && !name.trim().isEmpty()) {
                            String normalizedName = name.trim();
                            seatLayoutNames.add(normalizedName);
                            seatLayoutIdMap.put(normalizedName, id);
                        }
                    }
                    seatLayoutsLoaded = true;
                    Log.d("EditMovieActivity", "Loaded seat layouts: " + seatLayoutNames + ", IDs: " + seatLayoutIdMap);
                })
                .addOnFailureListener(e -> {
                    seatLayoutsLoaded = false;
                    Log.e("EditMovieActivity", "Error loading seat layouts: ", e);
                    Toast.makeText(this, "Lỗi khi tải layout ghế: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCinemaNames() {
        db.collection("cinemas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cinemaNames.clear();
                    cinemaIdMap.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String id = document.getId();
                        if (name != null) {
                            cinemaNames.add(name);
                            cinemaIdMap.put(name, id);
                        }
                    }
                    Log.d("EditMovieActivity", "Loaded cinemas: " + cinemaNames);
                })
                .addOnFailureListener(e -> {
                    Log.e("EditMovieActivity", "Error loading cinemas: ", e);
                    Toast.makeText(this, "Lỗi khi tải danh sách rạp: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showAddShowtimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_showtime, null);
        builder.setView(dialogView);

        // Ánh xạ các view trong dialog
        Spinner spinnerCinema = dialogView.findViewById(R.id.spinner_cinema);
        Spinner spinnerSeatLayout = dialogView.findViewById(R.id.spinner_seat_layout);
        TextInputEditText etDate = dialogView.findViewById(R.id.et_date);
        TextInputEditText etTime = dialogView.findViewById(R.id.et_time);
        Button btnCancelDialog = dialogView.findViewById(R.id.btn_cancel_dialog);
        Button btnSaveDialog = dialogView.findViewById(R.id.btn_save_dialog);

        // Cấu hình spinner rạp chiếu
        ArrayAdapter<String> cinemaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cinemaNames);
        cinemaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCinema.setAdapter(cinemaAdapter);

        // Cấu hình spinner layout ghế
        ArrayAdapter<String> seatLayoutAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, seatLayoutNames);
        seatLayoutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeatLayout.setAdapter(seatLayoutAdapter);

        // Vô hiệu hóa nút Lưu nếu chưa tải xong seat layouts
        btnSaveDialog.setEnabled(seatLayoutsLoaded);

        // Cấu hình chọn ngày
        Calendar calendar = Calendar.getInstance();
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditMovieActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        etDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Cấu hình chọn giờ
        etTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    EditMovieActivity.this,
                    (view, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        etTime.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        });

        AlertDialog dialog = builder.create();

        // Xử lý nút Hủy trong dialog
        btnCancelDialog.setOnClickListener(v -> dialog.dismiss());

        // Xử lý nút Lưu trong dialog
        btnSaveDialog.setOnClickListener(v -> {
            String cinemaName = spinnerCinema.getSelectedItem() != null ? spinnerCinema.getSelectedItem().toString() : "";
            String seatLayoutName = spinnerSeatLayout.getSelectedItem() != null ? spinnerSeatLayout.getSelectedItem().toString() : "";
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            if (cinemaName.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn rạp chiếu", Toast.LENGTH_SHORT).show();
                return;
            }
            if (seatLayoutName.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn layout ghế", Toast.LENGTH_SHORT).show();
                return;
            }
            if (date.isEmpty()) {
                etDate.setError("Vui lòng chọn ngày");
                return;
            }
            if (time.isEmpty()) {
                etTime.setError("Vui lòng chọn giờ");
                return;
            }

            String cinemaId = cinemaIdMap.get(cinemaName);
            String seatLayoutId = seatLayoutIdMap.get(seatLayoutName);
            Log.d("EditMovieActivity", "Saving showtime with seatLayoutName: " + seatLayoutName + ", seatLayoutId: " + seatLayoutId);
            if (seatLayoutId == null || seatLayoutId.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy ID layout ghế", Toast.LENGTH_SHORT).show();
                return;
            }
            saveShowtime(cinemaId, seatLayoutId, date, time, dialog);
        });

        dialog.show();
    }

    private void saveShowtime(String cinemaId, String seatLayoutId, String date, String time, AlertDialog dialog) {
        db.collection("seat_layout").document(seatLayoutId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String originalLayoutStr = documentSnapshot.getString("originalLayout");
                        if (originalLayoutStr == null || originalLayoutStr.isEmpty()) {
                            Toast.makeText(this, "Layout ghế không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Phân tách chuỗi thành List<String> bằng dấu |
                        List<String> originalLayout = Arrays.asList(originalLayoutStr.split("\\|"));

                        if (originalLayout.isEmpty()) {
                            Toast.makeText(this, "Layout ghế không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Lưu showtime với currentLayout và seatLayoutId
                        Map<String, Object> showtimeData = new HashMap<>();
                        showtimeData.put("movieId", movieId);
                        showtimeData.put("cinemaId", cinemaId);
                        showtimeData.put("currentLayout", originalLayout);
                        showtimeData.put("seatLayoutId", seatLayoutId);
                        showtimeData.put("date", date);
                        showtimeData.put("time", time);
                        showtimeData.put("createdAt", Timestamp.now());
                        showtimeData.put("updatedAt", Timestamp.now());

                        Log.d("EditMovieActivity", "Saving showtime data: " + showtimeData);

                        db.collection("showtimes")
                                .add(showtimeData)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Thêm suất chiếu thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("EditMovieActivity", "Error adding showtime: ", e);
                                    Toast.makeText(this, "Lỗi khi thêm suất chiếu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.e("EditMovieActivity", "Seat layout document does not exist: " + seatLayoutId);
                        Toast.makeText(this, "Layout ghế không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EditMovieActivity", "Error fetching seat layout: ", e);
                    Toast.makeText(this, "Lỗi khi lấy layout ghế: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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

                        String posterUrl = documentSnapshot.getString("posterUrl");
                        if (posterUrl != null && !posterUrl.isEmpty()) {
                            layoutPosterPreview.setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .load(posterUrl)
                                    .placeholder(R.drawable.bg_image_placeholder)
                                    .error(R.drawable.bg_image_placeholder)
                                    .into(ivPosterPreview);
                        }
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
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, genreOptions);
        spinnerGenre.setAdapter(genreAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, statusOptions);
        spinnerStatus.setAdapter(statusAdapter);
    }
}