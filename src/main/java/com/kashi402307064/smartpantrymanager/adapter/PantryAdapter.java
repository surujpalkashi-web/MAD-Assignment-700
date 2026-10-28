package com.kashi402307064.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kashi402307064.smartpantrymanager.R;
import com.kashi402307064.smartpantrymanager.model.PantryItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.Holder> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvQty;
        private final TextView tvExpiry;
        private final ImageButton btnDelete;

        Holder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQty = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(PantryItem item) {
            tvName.setText(item.getName());
            String qtyText = formatQty(item.getQuantity()) + " " + item.getUnit();
            tvQty.setText(qtyText);

            if (item.getExpiryDate() != null) {
                tvExpiry.setVisibility(View.VISIBLE);
                tvExpiry.setText("Expires: " + dateFormat.format(new Date(item.getExpiryDate())));
            } else {
                tvExpiry.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(item);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(item);
                }
            });
        }

        private String formatQty(double qty) {
            if (qty == Math.floor(qty)) {
                return String.valueOf((long) qty);
            }
            return String.valueOf(qty);
        }
    }
}
