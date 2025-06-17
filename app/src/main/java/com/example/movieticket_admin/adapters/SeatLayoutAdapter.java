package com.example.movieticket_admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieticket_admin.models.SeatLayout;
import com.example.movieticket_admin.R;

import java.util.ArrayList;
import java.util.List;

public class SeatLayoutAdapter extends RecyclerView.Adapter<SeatLayoutAdapter.SeatLayoutViewHolder> {

    private List<SeatLayout> seatLayouts;
    private List<String> documentIds; // Lưu danh sách document IDs
    private OnItemClickListener onItemClickListener;

    // Interface cho sự kiện click
    public interface OnItemClickListener {
        void onItemClick(SeatLayout seatLayout, String documentId);
    }

    public SeatLayoutAdapter() {
        this.seatLayouts = new ArrayList<>();
        this.documentIds = new ArrayList<>();
    }

    public void setSeatLayouts(List<SeatLayout> seatLayouts, List<String> documentIds) {
        this.seatLayouts = seatLayouts;
        this.documentIds = documentIds;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public SeatLayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seat_layout, parent, false);
        return new SeatLayoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatLayoutViewHolder holder, int position) {
        SeatLayout seatLayout = seatLayouts.get(position);
        String documentId = documentIds.get(position);
        holder.textViewId.setText("ID: " + documentId);
        holder.textViewName.setText("Tên: " + seatLayout.getName());

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(seatLayout, documentId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return seatLayouts.size();
    }

    static class SeatLayoutViewHolder extends RecyclerView.ViewHolder {
        TextView textViewId, textViewName;

        public SeatLayoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewId = itemView.findViewById(R.id.textViewId);
            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }
}