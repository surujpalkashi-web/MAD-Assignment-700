package com.kashi402307064.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kashi402307064.smartpantrymanager.db.PantryDataSource;

/**
 * App settings: expiry highlight toggle, pantry sort order, clear pantry.
 */
public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "smart_pantry_prefs";
    public static final String KEY_HIGHLIGHT_EXPIRING = "highlight_expiring";
    public static final String KEY_SORT_BY = "sort_by";
    public static final String SORT_NAME = "name";
    public static final String SORT_EXPIRY = "expiry";

    private SharedPreferences prefs;
    private PantryDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        dataSource = new PantryDataSource(this);
        dataSource.open();

        SwitchMaterial switchHighlight = findViewById(R.id.switchHighlight);
        RadioGroup radioSort = findViewById(R.id.radioSort);
        RadioButton radioName = findViewById(R.id.radioName);
        RadioButton radioExpiry = findViewById(R.id.radioExpiry);
        MaterialButton btnClear = findViewById(R.id.btnClearPantry);

        switchHighlight.setChecked(prefs.getBoolean(KEY_HIGHLIGHT_EXPIRING, true));

        String sortBy = prefs.getString(KEY_SORT_BY, SORT_NAME);
        if (SORT_EXPIRY.equals(sortBy)) {
            radioExpiry.setChecked(true);
        } else {
            radioName.setChecked(true);
        }

        switchHighlight.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_HIGHLIGHT_EXPIRING, isChecked).apply());

        radioSort.setOnCheckedChangeListener((group, checkedId) -> {
            String value = checkedId == R.id.radioExpiry ? SORT_EXPIRY : SORT_NAME;
            prefs.edit().putString(KEY_SORT_BY, value).apply();
        });

        btnClear.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Clear pantry")
                .setMessage("Remove all pantry items? This cannot be undone.")
                .setPositiveButton("Clear", (dialog, which) -> {
                    dataSource.clearPantry();
                    Toast.makeText(this, "Pantry cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show());
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }
}
