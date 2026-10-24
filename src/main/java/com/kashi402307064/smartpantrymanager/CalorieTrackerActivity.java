package com.kashi402307064.smartpantrymanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kashi402307064.smartpantrymanager.db.PantryDataSource;
import com.kashi402307064.smartpantrymanager.model.CalorieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalorieTrackerActivity extends AppCompatActivity {

    private PantryDataSource dataSource;
    private CalorieAdapter adapter;
    private TextView tvTotal;
    private TextView tvEmpty;
    private RecyclerView recyclerView;
    private String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_tracker);

        setTitle(R.string.calorie_tracker);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        dataSource = new PantryDataSource(this);
        tvTotal = findViewById(R.id.tvTotalCalories);
        tvEmpty = findViewById(R.id.tvCalorieEmpty);
        recyclerView = findViewById(R.id.recyclerCalories);
        EditText etFood = findViewById(R.id.etFoodName);
        EditText etCalories = findViewById(R.id.etCalories);
        Button btnAdd = findViewById(R.id.btnAddCalorie);

        adapter = new CalorieAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String food = etFood.getText().toString().trim();
            String calText = etCalories.getText().toString().trim();

            if (food.isEmpty()) {
                etFood.setError(getString(R.string.error_required));
                return;
            }
            if (calText.isEmpty()) {
                etCalories.setError(getString(R.string.error_required));
                return;
            }

            int calories;
            try {
                calories = Integer.parseInt(calText);
            } catch (NumberFormatException e) {
                etCalories.setError(getString(R.string.error_number));
                return;
            }
            if (calories <= 0) {
                etCalories.setError(getString(R.string.error_positive));
                return;
            }

            dataSource.open();
            dataSource.insertCalorieEntry(new CalorieEntry(food, calories, today));
            etFood.setText("");
            etCalories.setText("");
            refresh();
            Toast.makeText(this, R.string.calorie_added, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        refresh();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void refresh() {
        List<CalorieEntry> entries = dataSource.getCalorieEntriesForDate(today);
        int total = dataSource.getTotalCaloriesForDate(today);
        tvTotal.setText(getString(R.string.total_calories, total));
        adapter.setEntries(entries);

        if (entries.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private class CalorieAdapter extends RecyclerView.Adapter<CalorieAdapter.Holder> {

        private final List<CalorieEntry> entries = new ArrayList<>();

        void setEntries(List<CalorieEntry> list) {
            entries.clear();
            if (list != null) {
                entries.addAll(list);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_calorie, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            CalorieEntry entry = entries.get(position);
            holder.tvFood.setText(entry.getFoodName());
            holder.tvCals.setText(entry.getCalories() + " kcal");
            holder.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(CalorieTrackerActivity.this)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.confirm_delete_calorie)
                        .setPositiveButton(R.string.delete, (d, w) -> {
                            dataSource.open();
                            dataSource.deleteCalorieEntry(entry.getId());
                            refresh();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView tvFood;
            TextView tvCals;

            Holder(View itemView) {
                super(itemView);
                tvFood = itemView.findViewById(R.id.tvFoodName);
                tvCals = itemView.findViewById(R.id.tvCalorieValue);
            }
        }
    }
}
