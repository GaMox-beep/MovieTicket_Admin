package com.example.movieticket_admin.Seat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticket_admin.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.movieticket_admin.adapters.SeatLayoutAdapter;
import com.example.movieticket_admin.models.SeatLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

public class SeatLayoutManagerFragment extends Fragment {

    private FirebaseFirestore db;
    private EditText editTextId, editTextName, editTextOriginalLayout, editTextCurrentLayout;
    private Button buttonCreate, buttonUpdate, buttonDelete;
    private RecyclerView recyclerViewSeatLayouts;
    private SeatLayoutAdapter adapter;
    private ListenerRegistration firestoreListener;
    private static final String COLLECTION_NAME = "seat_layout";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seat_layout_manager, container, false);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các view
        editTextId = view.findViewById(R.id.editTextId);
        editTextName = view.findViewById(R.id.editTextName);
        editTextOriginalLayout = view.findViewById(R.id.editTextOriginalLayout);
        editTextCurrentLayout = view.findViewById(R.id.editTextCurrentLayout);
        buttonCreate = view.findViewById(R.id.buttonCreate);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        recyclerViewSeatLayouts = view.findViewById(R.id.recyclerViewSeatLayouts);

        // Ngăn chỉnh sửa trực tiếp trên editTextId
        editTextId.setKeyListener(null);

        // Thiết lập RecyclerView
        adapter = new SeatLayoutAdapter();
        recyclerViewSeatLayouts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSeatLayouts.setAdapter(adapter);

        // Thiết lập sự kiện click cho item trong RecyclerView
        adapter.setOnItemClickListener((seatLayout, documentId) -> {
            editTextId.setText(documentId);
            // Điền các trường khác để hỗ trợ chỉnh sửa
            editTextName.setText(seatLayout.getName());
            editTextOriginalLayout.setText(seatLayout.getOriginalLayout());
            editTextCurrentLayout.setText(seatLayout.getCurrentLayout());
        });

        // Thiết lập sự kiện cho các nút
        buttonCreate.setOnClickListener(v -> createSeatLayout());
        buttonUpdate.setOnClickListener(v -> updateSeatLayout());
        buttonDelete.setOnClickListener(v -> deleteSeatLayout());

        // Lắng nghe dữ liệu từ Firestore
        setupFirestoreListener();

        return view;
    }

    private void createSeatLayout() {
        String name = editTextName.getText().toString().trim();
        String originalLayout = editTextOriginalLayout.getText().toString().trim();
        String currentLayout = editTextCurrentLayout.getText().toString().trim();

        if (name.isEmpty() || originalLayout.isEmpty() || currentLayout.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng SeatLayout
        SeatLayout seatLayout = new SeatLayout(name, originalLayout, currentLayout);

        // Thêm vào Firestore
        db.collection(COLLECTION_NAME)
                .add(seatLayout)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Tạo bố cục thành công, ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    clearInputs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateSeatLayout() {
        String documentId = editTextId.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String originalLayout = editTextOriginalLayout.getText().toString().trim();
        String currentLayout = editTextCurrentLayout.getText().toString().trim();

        if (documentId.isEmpty() || name.isEmpty() || originalLayout.isEmpty() || currentLayout.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin, bao gồm ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật document
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(documentId);
        SeatLayout seatLayout = new SeatLayout(name, originalLayout, currentLayout);
        docRef.set(seatLayout)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Cập nhật bố cục thành công", Toast.LENGTH_SHORT).show();
                    clearInputs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteSeatLayout() {
        String documentId = editTextId.getText().toString().trim();

        if (documentId.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập ID để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xóa document
        db.collection(COLLECTION_NAME).document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Xóa bố cục thành công", Toast.LENGTH_SHORT).show();
                    clearInputs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupFirestoreListener() {
        firestoreListener = db.collection(COLLECTION_NAME)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Lỗi khi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<SeatLayout> seatLayouts = new ArrayList<>();
                    List<String> documentIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        SeatLayout seatLayout = document.toObject(SeatLayout.class);
                        seatLayouts.add(seatLayout);
                        documentIds.add(document.getId());
                    }
                    adapter.setSeatLayouts(seatLayouts, documentIds);
                });
    }

    private void clearInputs() {
        editTextId.setText("");
        editTextName.setText("");
        editTextOriginalLayout.setText("");
        editTextCurrentLayout.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy listener để tránh rò rỉ bộ nhớ
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }
}