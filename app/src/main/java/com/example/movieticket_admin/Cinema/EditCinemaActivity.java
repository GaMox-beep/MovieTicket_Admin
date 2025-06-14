package com.example.movieticket_admin.Cinema;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieticket_admin.R;
import com.example.movieticket_admin.models.Cinema;
import com.google.android.material.textfield.TextInputEditText;

public class EditCinemaActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ImageButton btnBack;
    private TextInputEditText etCinemaName, etAddress, etImageUrl, etDescription;
    private AutoCompleteTextView spinnerCity;
    private Button btnCancel, btnUpdate, btnDelete;
    private LinearLayout layoutImagePreview;
    private ImageView ivImagePreview;

    private Cinema currentCinema;
    public static final String EXTRA_CINEMA = "extra_cinema";

    private String[] cities = {
            "Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Hải Phòng", "Cần Thơ",
            "Biên Hòa", "Hạ Long", "Nha Trang", "Phan Thiết", "Long Xuyên",
            "Thái Nguyên", "Thanh Hóa", "Tuy Hòa", "Rạch Giá", "Cà Mau"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cinema);

        initViews();
        getCinemaData();
        setupCitySpinner();
        setupImagePreview();
        setupButtons();
        populateFields();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btn_back);
        etCinemaName = findViewById(R.id.et_cinema_name);
        etAddress = findViewById(R.id.et_address);
        etImageUrl = findViewById(R.id.et_image_url);
        etDescription = findViewById(R.id.et_description);
        spinnerCity = findViewById(R.id.spinner_city);
        btnCancel = findViewById(R.id.btn_cancel);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        layoutImagePreview = findViewById(R.id.layout_image_preview);
        ivImagePreview = findViewById(R.id.iv_image_preview);
    }

    private void getCinemaData() {
        currentCinema = (Cinema) getIntent().getSerializableExtra(EXTRA_CINEMA);
        if (currentCinema == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin rạp phim", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupCitySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                cities
        );
        spinnerCity.setAdapter(adapter);
    }

    private void setupImagePreview() {
        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    loadImagePreview(url);
                    layoutImagePreview.setVisibility(View.VISIBLE);
                } else {
                    layoutImagePreview.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadImagePreview(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivImagePreview);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());

        btnCancel.setOnClickListener(v -> finish());

        btnUpdate.setOnClickListener(v -> {
            if (validateInputs()) {
                updateCinema();
            }
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void populateFields() {
        if (currentCinema != null) {
            tvTitle.setText("Chỉnh sửa " + currentCinema.getName());
            etCinemaName.setText(currentCinema.getName());
            etAddress.setText(currentCinema.getAddress());
            etImageUrl.setText(currentCinema.getImageUrl());
            etDescription.setText(currentCinema.getDescription());
            spinnerCity.setText(currentCinema.getCity(), false);

            // Load image preview nếu có
            if (currentCinema.getImageUrl() != null && !currentCinema.getImageUrl().isEmpty()) {
                layoutImagePreview.setVisibility(View.VISIBLE);
                loadImagePreview(currentCinema.getImageUrl());
            }
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate cinema name
        String cinemaName = etCinemaName.getText().toString().trim();
        if (cinemaName.isEmpty()) {
            etCinemaName.setError("Vui lòng nhập tên rạp");
            isValid = false;
        }

        // Validate city
        String city = spinnerCity.getText().toString().trim();
        if (city.isEmpty()) {
            spinnerCity.setError("Vui lòng chọn thành phố");
            isValid = false;
        }

        // Validate address
        String address = etAddress.getText().toString().trim();
        if (address.isEmpty()) {
            etAddress.setError("Vui lòng nhập địa chỉ");
            isValid = false;
        }

        return isValid;
    }

    private void updateCinema() {
        // Lấy dữ liệu từ form
        String name = etCinemaName.getText().toString().trim();
        String city = spinnerCity.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Cập nhật thông tin cinema
        currentCinema.setName(name);
        currentCinema.setCity(city);
        currentCinema.setAddress(address);
        currentCinema.setImageUrl(imageUrl);
        currentCinema.setDescription(description);
        currentCinema.setUpdatedAt(System.currentTimeMillis());

        // TODO: Call API để update cinema
        performUpdateCinema(currentCinema);
    }

    private void performUpdateCinema(Cinema cinema) {
        // TODO: Implement Firebase/API call
        // Tạm thời chỉ show toast và finish
        Toast.makeText(this, "Đã cập nhật rạp phim: " + cinema.getName(), Toast.LENGTH_SHORT).show();

        // Trả kết quả về fragment
        setResult(RESULT_OK);
        finish();
    }

    private void showDeleteConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa rạp phim \"" + currentCinema.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCinema())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCinema() {
        // TODO: Implement Firebase/API call để xóa cinema
        performDeleteCinema(currentCinema.getId());
    }

    private void performDeleteCinema(String cinemaId) {
        // TODO: Implement Firebase/API call
        // Tạm thời chỉ show toast và finish
        Toast.makeText(this, "Đã xóa rạp phim: " + currentCinema.getName(), Toast.LENGTH_SHORT).show();

        // Trả kết quả về fragment
        setResult(RESULT_OK);
        finish();
    }
}