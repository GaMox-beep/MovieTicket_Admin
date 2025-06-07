package com.example.movieticket_admin;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieticket_admin.R;
import com.example.movieticket_admin.models.Movie;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddMovieActivity extends AppCompatActivity {

    private static final String TAG = "AddMovieActivity";
    private static final String COLLECTION_MOVIES = "movies";
    private static final String STORAGE_PATH_POSTERS = "movie_posters/";
    private static final String STORAGE_PATH_TRAILERS = "movie_trailers/";

    // UI Components
    private ImageButton btnBack;
    private TextInputEditText etTitle, etDuration, etReleaseDate, etDescription;
    private TextInputLayout tilTitle, tilGenre, tilDuration, tilReleaseDate, tilStatus, tilDescription;
    private AutoCompleteTextView spinnerGenre, spinnerStatus;
    private ImageView ivPosterPreview, ivTrailerPreview;
    private Button btnSelectPoster, btnSelectTrailer, btnCancel, btnSave;
    private TextView tvPosterName, tvTrailerName;

    // Firebase
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // Data
    private Uri selectedPosterUri, selectedTrailerUri;
    private String posterDownloadUrl, trailerDownloadUrl;
    private ProgressDialog progressDialog;

    // Date formatter
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();

    // Activity Result Launchers
    private ActivityResultLauncher<String> posterPickerLauncher;
    private ActivityResultLauncher<String> trailerPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        initFirebase();
        initViews();
        setupSpinners();
        setupDatePicker();
        setupFilePickerLaunchers();
        setupClickListeners();
        initProgressDialog();
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    private void initViews() {
        // Header
        btnBack = findViewById(R.id.btn_back);

        // Input fields
        etTitle = findViewById(R.id.et_title);
        etDuration = findViewById(R.id.et_duration);
        etReleaseDate = findViewById(R.id.et_release_date);
        etDescription = findViewById(R.id.et_description);

        // Text Input Layouts
        tilTitle = findViewById(R.id.til_title);
        tilGenre = findViewById(R.id.til_genre);
        tilDuration = findViewById(R.id.til_duration);
        tilReleaseDate = findViewById(R.id.til_release_date);
        tilStatus = findViewById(R.id.til_status);
        tilDescription = findViewById(R.id.til_description);

        // Spinners
        spinnerGenre = findViewById(R.id.spinner_genre);
        spinnerStatus = findViewById(R.id.spinner_status);

        // Media components
        ivPosterPreview = findViewById(R.id.iv_poster_preview);
        ivTrailerPreview = findViewById(R.id.iv_trailer_preview);
        btnSelectPoster = findViewById(R.id.btn_select_poster);
        btnSelectTrailer = findViewById(R.id.btn_select_trailer);
        tvPosterName = findViewById(R.id.tv_poster_name);
        tvTrailerName = findViewById(R.id.tv_trailer_name);

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

    private void setupFilePickerLaunchers() {
        // Poster picker
        posterPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedPosterUri = uri;
                        displayPosterPreview(uri);
                        tvPosterName.setText(getFileName(uri));
                    }
                }
        );

        // Trailer picker
        trailerPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedTrailerUri = uri;
                        displayTrailerPreview(uri);
                        tvTrailerName.setText(getFileName(uri));
                    }
                }
        );
    }

    private void displayPosterPreview(Uri uri) {
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.ic_image_placeholder)
                .into(ivPosterPreview);
    }

    private void displayTrailerPreview(Uri uri) {
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.ic_play_circle)
                .into(ivTrailerPreview);
    }

    private String getFileName(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            return path.substring(path.lastIndexOf('/') + 1);
        }
        return "Unknown file";
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnSelectPoster.setOnClickListener(v -> posterPickerLauncher.launch("image/*"));

        btnSelectTrailer.setOnClickListener(v -> trailerPickerLauncher.launch("video/*"));

        btnCancel.setOnClickListener(v -> {
            // Show confirmation dialog
            showCancelConfirmationDialog();
        });

        btnSave.setOnClickListener(v -> validateAndSaveMovie());
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

        if (selectedPosterUri == null) {
            Toast.makeText(this, "Vui lòng chọn poster cho phim", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid) {
            // Create movie object
            Movie movie = new Movie(title, genre, Integer.parseInt(durationStr),
                    releaseDate, status, description);

            // Start upload process
            uploadMovieWithMedia(movie);
        }
    }

    private void clearErrors() {
        tilTitle.setError(null);
        tilGenre.setError(null);
        tilDuration.setError(null);
        tilReleaseDate.setError(null);
        tilStatus.setError(null);
    }

    private void uploadMovieWithMedia(Movie movie) {
        progressDialog.show();

        // Upload poster first (required)
        uploadPoster(movie, () -> {
            // Upload trailer if selected
            if (selectedTrailerUri != null) {
                uploadTrailer(movie, () -> saveMovieToFirestore(movie));
            } else {
                saveMovieToFirestore(movie);
            }
        });
    }

    private void uploadPoster(Movie movie, Runnable onSuccess) {
        String fileName = "poster_" + UUID.randomUUID().toString() + ".jpg";
        StorageReference posterRef = storageRef.child(STORAGE_PATH_POSTERS + fileName);

        posterRef.putFile(selectedPosterUri)
                .addOnSuccessListener(taskSnapshot -> {
                    posterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        posterDownloadUrl = uri.toString();
                        movie.setPosterUrl(posterDownloadUrl);
                        onSuccess.run();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Log.e(TAG, "Failed to get poster download URL", e);
                        Toast.makeText(this, "Lỗi khi tải poster: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Failed to upload poster", e);
                    Toast.makeText(this, "Lỗi khi tải poster: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadTrailer(Movie movie, Runnable onSuccess) {
        String fileName = "trailer_" + UUID.randomUUID().toString() + ".mp4";
        StorageReference trailerRef = storageRef.child(STORAGE_PATH_TRAILERS + fileName);

        trailerRef.putFile(selectedTrailerUri)
                .addOnSuccessListener(taskSnapshot -> {
                    trailerRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        trailerDownloadUrl = uri.toString();
                        movie.setTrailerUrl(trailerDownloadUrl);
                        onSuccess.run();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Log.e(TAG, "Failed to get trailer download URL", e);
                        Toast.makeText(this, "Lỗi khi tải trailer: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Failed to upload trailer", e);
                    Toast.makeText(this, "Lỗi khi tải trailer: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void saveMovieToFirestore(Movie movie) {
        firestore.collection(COLLECTION_MOVIES)
                .add(movie)
                .addOnSuccessListener(documentReference -> {
                    // Update movie with the generated ID
                    String movieId = documentReference.getId();
                    movie.setId(movieId);

                    // Update the document with the ID
                    documentReference.update("id", movieId)
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Thêm phim thành công!", Toast.LENGTH_SHORT).show();

                                // Return result and finish
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("new_movie", movie);
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
                selectedPosterUri != null ||
                selectedTrailerUri != null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}