package com.kashi402307064.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kashi402307064.smartpantrymanager.db.PantryDataSource;
import com.kashi402307064.smartpantrymanager.model.PantryItem;

import java.util.List;

/**
 * Simple test screen so you can Run the app and verify SQLite works.
 * Student: Kashi Surujpal 402307064
 */
public class MainActivity extends AppCompatActivity {

    private PantryDataSource dataSource;
    private TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textStatus = findViewById(R.id.textStatus);
        Button btnAdd = findViewById(R.id.btnAddTest);
        Button btnList = findViewById(R.id.btnListItems);

        dataSource = new PantryDataSource(this);
        dataSource.open();

        btnAdd.setOnClickListener(v -> addTestItem());
        btnList.setOnClickListener(v -> listItems());

        textStatus.setText("SQLite ready. Tap buttons to test.\nStudent: Kashi Surujpal 402307064");
    }

    private void addTestItem() {
        PantryItem item = new PantryItem(
                "Milk",
                "milk",
                1.0,
                "litre",
                null
        );
        long id = dataSource.insertPantryItem(item);
        Toast.makeText(this, "Inserted item id=" + id, Toast.LENGTH_SHORT).show();
        listItems();
    }

    private void listItems() {
        List<PantryItem> items = dataSource.getAllPantryItems();
        StringBuilder sb = new StringBuilder();
        sb.append("Pantry items (").append(items.size()).append("):\n\n");
        for (PantryItem i : items) {
            sb.append("- ").append(i.getName())
                    .append(" ").append(i.getQuantity())
                    .append(" ").append(i.getUnit())
                    .append("\n");
        }
        if (items.isEmpty()) {
            sb.append("(empty — tap Add Test Item)");
        }
        textStatus.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }
}
