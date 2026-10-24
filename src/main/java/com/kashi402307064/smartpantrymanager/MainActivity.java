package com.kashi402307064.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kashi402307064.smartpantrymanager.adapter.PantryAdapter;
import com.kashi402307064.smartpantrymanager.db.PantryDataSource;
import com.kashi402307064.smartpantrymanager.model.PantryItem;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PantryAdapter.OnPantryActionListener {

    private PantryDataSource dataSource;
    private PantryAdapter adapter;
    private TextView tvEmpty;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new PantryDataSource(this);

        recyclerView = findViewById(R.id.recyclerPantry);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        View btnCalories = findViewById(R.id.btnCalories);

        adapter = new PantryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditPantryActivity.class)));

        btnCalories.setOnClickListener(v ->
                startActivity(new Intent(this, CalorieTrackerActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        refreshList();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    private void refreshList() {
        List<PantryItem> items = dataSource.getAllPantryItems();
        adapter.setItems(items);
        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditPantryActivity.class);
        intent.putExtra(AddEditPantryActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(getString(R.string.confirm_delete, item.getName()))
                .setPositiveButton(R.string.delete, (d, w) -> {
                    dataSource.open();
                    dataSource.deletePantryItem(item.getId());
                    refreshList();
                    Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
