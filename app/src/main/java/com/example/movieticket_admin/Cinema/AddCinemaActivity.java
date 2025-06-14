package com.example.movieticket_admin.Cinema;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import com.example.movieticket_admin.R;

import java.util.HashMap;
import java.util.Map;

public class AddCinemaActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private TextInputLayout tilCinemaName, tilCity, tilAddress, tilImageUrl, tilDescription;
    private TextInputEditText etCinemaName, etAddress, etImageUrl, etDescription;
    private AutoCompleteTextView spinnerCity;
    private LinearLayout layoutImagePreview;
    private ImageView ivImagePreview;
    private Button btnCancel, btnSave;

    // Firebase
    private FirebaseFirestore db;

    private String[] cities = {
            "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ",
            "Biên Hòa", "Nha Trang", "Huế", "Vũng Tàu", "Buôn Ma Thuột",
            "Quy Nhon", "Thái Nguyên", "Nam Định", "Phan Thiết", "Long Xuyên"
    };

    // Loading state
    private boolean isSaving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cinema);

        initFirebase();
        initViews();
        setupListeners();
        setupCitySpinner();
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);

        tilCinemaName = findViewById(R.id.til_cinema_name);
        tilCity = findViewById(R.id.til_city);
        tilAddress = findViewById(R.id.til_address);
        tilImageUrl = findViewById(R.id.til_image_url);
        tilDescription = findViewById(R.id.til_description);

        etCinemaName = findViewById(R.id.et_cinema_name);
        etAddress = findViewById(R.id.et_address);
        etImageUrl = findViewById(R.id.et_image_url);
        etDescription = findViewById(R.id.et_description);

        spinnerCity = findViewById(R.id.spinner_city);
        layoutImagePreview = findViewById(R.id.layout_image_preview);
        ivImagePreview = findViewById(R.id.iv_image_preview);

        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Cancel button
        btnCancel.setOnClickListener(v -> onBackPressed());

        // Save button
        btnSave.setOnClickListener(v -> {
            if (!isSaving) {
                saveCinema();
            }
        });

        // Image URL text watcher for preview
        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String imageUrl = s.toString().trim();
                if (!TextUtils.isEmpty(imageUrl) && isValidUrl(imageUrl)) {
                    showImagePreview(imageUrl);
                } else {
                    hideImagePreview();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupCitySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                cities
        );
        spinnerCity.setAdapter(adapter);
    }

    private void showImagePreview(String imageUrl) {
        layoutImagePreview.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .into(ivImagePreview);
    }

    private void hideImagePreview() {
        layoutImagePreview.setVisibility(View.GONE);
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void saveCinema() {
        if (validateInput()) {
            isSaving = true;
            btnSave.setText("Đang lưu...");
            btnSave.setEnabled(false);

            Map<String, Object> cinema = createCinemaData();

            db.collection("cinemas")
                    .add(cinema)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Thêm rạp phim thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        resetSaveButton();
                    });
        }
    }

    private void resetSaveButton() {
        isSaving = false;
        btnSave.setText("Lưu Rạp");
        btnSave.setEnabled(true);
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Validate cinema name
        String cinemaName = etCinemaName.getText().toString().trim();
        if (TextUtils.isEmpty(cinemaName)) {
            tilCinemaName.setError("Vui lòng nhập tên rạp");
            isValid = false;
        } else if (cinemaName.length() < 3) {
            tilCinemaName.setError("Tên rạp phải có ít nhất 3 ký tự");
            isValid = false;
        } else {
            tilCinemaName.setError(null);
        }

        // Validate city
        String city = spinnerCity.getText().toString().trim();
        if (TextUtils.isEmpty(city)) {
            tilCity.setError("Vui lòng chọn thành phố");
            isValid = false;
        } else {
            tilCity.setError(null);
        }

        // Validate address
        String address = etAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            tilAddress.setError("Vui lòng nhập địa chỉ");
            isValid = false;
        } else if (address.length() < 10) {
            tilAddress.setError("Địa chỉ phải có ít nhất 10 ký tự");
            isValid = false;
        } else {
            tilAddress.setError(null);
        }

        // Validate image URL (optional but if provided must be valid)
        String imageUrl = etImageUrl.getText().toString().trim();
        if (!TextUtils.isEmpty(imageUrl) && !isValidUrl(imageUrl)) {
            tilImageUrl.setError("URL không hợp lệ");
            isValid = false;
        } else {
            tilImageUrl.setError(null);
        }

        // Validate description length
        String description = etDescription.getText().toString().trim();
        if (description.length() > 500) {
            tilDescription.setError("Mô tả không được vượt quá 500 ký tự");
            isValid = false;
        } else {
            tilDescription.setError(null);
        }

        return isValid;
    }

    private Map<String, Object> createCinemaData() {
        String name = etCinemaName.getText().toString().trim();
        String city = spinnerCity.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        Map<String, Object> cinema = new HashMap<>();
        cinema.put("name", name);
        cinema.put("city", city);
        cinema.put("address", address);
        cinema.put("imageUrl", TextUtils.isEmpty(imageUrl) ? null : imageUrl);
        cinema.put("description", TextUtils.isEmpty(description) ? null : description);
        cinema.put("createdAt", Timestamp.now());
        cinema.put("updatedAt", Timestamp.now());
        cinema.put("isActive", true); // Trạng thái hoạt động

        return cinema;
    }

    @Override
    public void onBackPressed() {
        if (isSaving) {
            Toast.makeText(this, "Đang lưu dữ liệu, vui lòng đợi...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user has entered any data
        if (hasUserEnteredData()) {
            // Show confirmation dialog
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có muốn thoát? Dữ liệu chưa lưu sẽ bị mất.")
                    .setPositiveButton("Thoát", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("Hủy", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasUserEnteredData() {
        return !TextUtils.isEmpty(etCinemaName.getText()) ||
                !TextUtils.isEmpty(spinnerCity.getText()) ||
                !TextUtils.isEmpty(etAddress.getText()) ||
                !TextUtils.isEmpty(etImageUrl.getText()) ||
                !TextUtils.isEmpty(etDescription.getText());
    }
}