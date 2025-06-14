package com.example.movieticket_admin.Cinema;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieticket_admin.R;
import com.example.movieticket_admin.models.Cinema;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditCinemaActivity extends AppCompatActivity {
    private TextInputEditText etCinemaName, etAddress, etImageUrl, etDescription;
    private AutoCompleteTextView spinnerCity;
    private ImageView ivImagePreview;
    private LinearLayout layoutImagePreview;
    private Button btnSave, btnCancel,btnDelete;
    private ImageButton btnBack;
    private FirebaseFirestore db;
    private String cinemaId;
    private Cinema currentCinema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cinema);

        // Khởi tạo các view
        etCinemaName = findViewById(R.id.et_cinema_name);
        etAddress = findViewById(R.id.et_address);
        etImageUrl = findViewById(R.id.et_image_url);
        etDescription = findViewById(R.id.et_description);
        spinnerCity = findViewById(R.id.spinner_city);
        ivImagePreview = findViewById(R.id.iv_image_preview);
        layoutImagePreview = findViewById(R.id.layout_image_preview);
        btnSave = findViewById(R.id.btn_update);
        btnCancel = findViewById(R.id.btn_cancel);
        btnDelete= findViewById(R.id.btn_delete);
        btnBack = findViewById(R.id.btn_back);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Lấy cinemaId từ Intent
        cinemaId = getIntent().getStringExtra("cinemaId");

        // Cấu hình spinner cho thành phố
        setupCitySpinner();

        // Xử lý sự kiện thay đổi URL hình ảnh
        etImageUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                updateImagePreview();
            }
        });

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Hủy
        btnCancel.setOnClickListener(v -> finish());

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> saveCinema());
//Xử lý nút xóa
        if (cinemaId != null) {
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
        } else {
            btnDelete.setVisibility(View.GONE); // Ẩn nút xóa khi thêm mới
        }
        // Tải dữ liệu rạp nếu có cinemaId
        if (cinemaId != null) {
            loadCinemaData();
        } else {
            // Nếu không có cinemaId, cập nhật tiêu đề cho trường hợp thêm mới
            findViewById(android.R.id.text1).setVisibility(View.GONE); // Ẩn tiêu đề mặc định
        }
    }

    private void setupCitySpinner() {
        String[] cities = getResources().getStringArray(R.array.major_CitiesVN);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        spinnerCity.setAdapter(adapter);
    }

    private void loadCinemaData() {
        db.collection("cinemas").document(cinemaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentCinema = documentSnapshot.toObject(Cinema.class);
                        if (currentCinema != null) {
                            // Hiển thị dữ liệu
                            etCinemaName.setText(currentCinema.getName());
                            spinnerCity.setText(currentCinema.getCity(), false);
                            etAddress.setText(currentCinema.getAddress());
                            etDescription.setText(currentCinema.getDescription());
                            etImageUrl.setText(currentCinema.getImageUrl());
                            updateImagePreview();
                        }
                    } else {
                        Toast.makeText(this, "Rạp không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EditCinemaActivity", "Error loading cinema: ", e);
                    Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Rạp Phim")
                .setMessage("Bạn có chắc muốn xóa rạp này? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCinema())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCinema() {
        if (cinemaId == null) return;

        db.collection("cinemas").document(cinemaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Xóa rạp thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("EditCinemaActivity", "Error deleting cinema: ", e);
                    Toast.makeText(this, "Lỗi khi xóa rạp", Toast.LENGTH_SHORT).show();
                });
    }
    private void updateImagePreview() {
        String imageUrl = etImageUrl.getText() != null ? etImageUrl.getText().toString().trim() : "";
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivImagePreview);
            layoutImagePreview.setVisibility(View.VISIBLE);
        } else {
            layoutImagePreview.setVisibility(View.GONE);
        }
    }

    private void saveCinema() {
        String name = etCinemaName.getText() != null ? etCinemaName.getText().toString().trim() : "";
        String city = spinnerCity.getText() != null ? spinnerCity.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String imageUrl = etImageUrl.getText() != null ? etImageUrl.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        // Kiểm tra dữ liệu đầu vào
        if (TextUtils.isEmpty(name)) {
            etCinemaName.setError("Vui lòng nhập tên rạp");
            return;
        }
        if (TextUtils.isEmpty(city)) {
            spinnerCity.setError("Vui lòng chọn thành phố");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Vui lòng nhập địa chỉ");
            return;
        }

        // Tạo hoặc cập nhật document
        Map<String, Object> cinemaData = new HashMap<>();
        cinemaData.put("name", name);
        cinemaData.put("city", city);
        cinemaData.put("address", address);
        cinemaData.put("imageUrl", imageUrl);
        cinemaData.put("description", description);
        cinemaData.put("isActive", currentCinema != null ? currentCinema.isActive() : true); // Giữ hoặc mặc định true
        cinemaData.put("updatedAt", Timestamp.now());
        if (cinemaId == null) {
            cinemaData.put("createdAt", Timestamp.now());
        } else {
            cinemaData.put("createdAt", currentCinema != null ? currentCinema.getCreatedAt() : Timestamp.now());
        }

        // Lưu vào Firestore
        if (cinemaId == null) {
            // Thêm mới
            db.collection("cinemas")
                    .add(cinemaData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Thêm rạp thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("EditCinemaActivity", "Error adding cinema: ", e);
                        Toast.makeText(this, "Lỗi khi thêm rạp", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Cập nhật
            db.collection("cinemas").document(cinemaId)
                    .set(cinemaData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật rạp thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("EditCinemaActivity", "Error updating cinema: ", e);
                        Toast.makeText(this, "Lỗi khi cập nhật rạp", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}