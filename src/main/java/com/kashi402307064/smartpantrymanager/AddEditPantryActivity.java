package com.kashi402307064.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kashi402307064.smartpantrymanager.db.PantryDataSource;
import com.kashi402307064.smartpantrymanager.model.PantryItem;

public class AddEditPantryActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private EditText etName;
    private EditText etQuantity;
    private EditText etUnit;
    private PantryDataSource dataSource;
    private long itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_pantry);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        Button btnSave = findViewById(R.id.btnSave);

        dataSource = new PantryDataSource(this);
        dataSource.open();

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            setTitle(R.string.edit_item);
            loadItem(itemId);
        } else {
            setTitle(R.string.add_item);
        }

        btnSave.setOnClickListener(v -> saveItem());
    }

    private void loadItem(long id) {
        for (PantryItem item : dataSource.getAllPantryItems()) {
            if (item.getId() == id) {
                etName.setText(item.getName());
                etQuantity.setText(String.valueOf(item.getQuantity()));
                etUnit.setText(item.getUnit());
                break;
            }
        }
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyText = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_required));
            return;
        }
        if (qtyText.isEmpty()) {
            etQuantity.setError(getString(R.string.error_required));
            return;
        }
        if (unit.isEmpty()) {
            etUnit.setError(getString(R.string.error_required));
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyText);
        } catch (NumberFormatException e) {
            etQuantity.setError(getString(R.string.error_number));
            return;
        }
        if (quantity <= 0) {
            etQuantity.setError(getString(R.string.error_positive));
            return;
        }

        String normalized = name.toLowerCase().trim();

        if (itemId == -1) {
            PantryItem item = new PantryItem(name, normalized, quantity, unit, null);
            dataSource.insertPantryItem(item);
            Toast.makeText(this, R.string.item_added, Toast.LENGTH_SHORT).show();
        } else {
            PantryItem item = new PantryItem(name, normalized, quantity, unit, null);
            item.setId(itemId);
            dataSource.updatePantryItem(item);
            Toast.makeText(this, R.string.item_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }
}
