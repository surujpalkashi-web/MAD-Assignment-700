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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kashi402307064.smartpantrymanager.adapter.PantryAdapter;
import com.kashi402307064.smartpantrymanager.db.PantryDataSource;
import com.kashi402307064.smartpantrymanager.model.PantryItem;

import java.util.List;

/**
 * Home screen: pantry list with add/edit/delete and bottom navigation.
 */
public class MainActivity extends AppCompatActivity {

    private PantryDataSource dataSource;
    private PantryAdapter adapter;
    private TextView tvEmpty;
    private RecyclerView recyclerPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new PantryDataSource(this);
        dataSource.open();

        new RecipeSeeder(dataSource).seedIfNeeded();

        recyclerPantry = findViewById(R.id.recyclerPantry);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        adapter = new PantryAdapter(new PantryAdapter.Listener() {
            @Override
            public void onEdit(PantryItem item) {
                Intent intent = new Intent(MainActivity.this, AddEditPantryActivity.class);
                intent.putExtra(AddEditPantryActivity.EXTRA_ITEM_ID, item.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(PantryItem item) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete item")
                        .setMessage("Remove " + item.getName() + " from your pantry?")
                        .setPositiveButton("Delete", (d, w) -> {
                            dataSource.deletePantryItem(item.getId());
                            loadPantry();
                            Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        recyclerPantry.setAdapter(adapter);

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditPantryActivity.class)));

        bottomNav.setSelectedItemId(R.id.nav_pantry);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_suggested) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                return true;
            } else if (id == R.id.nav_calories) {
                startActivity(new Intent(this, CalorieTrackerActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataSource != null) {
            dataSource.open();
            loadPantry();
        }
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_pantry);
        }
    }

    private void loadPantry() {
        List<PantryItem> items = dataSource.getAllPantryItems();
        adapter.setItems(items);
        boolean empty = items == null || items.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerPantry.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (dataSource != null) {
            dataSource.close();
        }
        super.onDestroy();
    }
}
