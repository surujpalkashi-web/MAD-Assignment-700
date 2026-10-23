package com.kashi402307064.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kashi402307064.smartpantrymanager.db.PantryDataSource;

/**
 * Launcher screen. Opens the local SQLite database and shows basic counts.
 */
public class MainActivity extends AppCompatActivity {

    private PantryDataSource dataSource;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        dataSource = new PantryDataSource(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        tvStatus.setText(getString(R.string.status_counts,
                dataSource.getAllPantryItems().size(),
                dataSource.getRecipeCount()));
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }
}
