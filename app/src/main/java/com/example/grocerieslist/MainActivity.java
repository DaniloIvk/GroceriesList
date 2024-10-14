package com.example.grocerieslist;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {
    private ListView groceryListView;
    private GroceryListAdapter groceryListAdapter;
    private TextInputEditText itemNameTextInput;
    private TextInputEditText itemAmountTextInput;
    private TextInputEditText itemPriceTextInput;
    private final String defaultItemName = "";
    private final String defaultItemAmount = "1";
    private final String defaultItemPrice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.initialize();
    }

    private void initialize() {
        // Grocery form fields
        this.initializeItemNameTextInput();
        this.initializeItemAmountTextInput();
        this.initializeItemPriceTextInput();

        // Grocery form actions
        this.initializeReduceAmountButton();
        this.initializeIncreaseAmountButton();
        this.initializeAddGroceryButton();
        this.initializeResetFormFieldsButton();

        // Grocery list
        this.initializeGroceryListView();

        // Grocery list actions
        this.initializeEndGroceryListButton();
        this.initializeNewGroceryListButton();
    }

    private void initializeItemNameTextInput() {
        this.itemNameTextInput = findViewById(R.id.item_name_input);

        this.itemNameTextInput.setHint("Banana");
        this.itemNameTextInput.setText(this.defaultItemName);
    }

    private void initializeItemAmountTextInput() {
        this.itemAmountTextInput = findViewById(R.id.item_amount_input);

        this.itemAmountTextInput.setText(this.defaultItemAmount);
    }

    private void initializeItemPriceTextInput() {
        this.itemPriceTextInput = findViewById(R.id.item_price_input);

        this.itemPriceTextInput.setText(this.defaultItemPrice);
    }

    private void initializeReduceAmountButton() {
        Button reduceAmountButton = findViewById(R.id.reduce_item_amount_button);

        reduceAmountButton.setOnClickListener(view -> {
            int amount = Integer.parseInt(this.itemAmountTextInput.getEditableText().toString());

            if (--amount < 0) {
                this.displayInvalidInputDialog(getString(R.string.invalid_product_amount));
            } else {
                this.itemAmountTextInput.setText(String.valueOf(amount));
            }
        });
    }

    private void initializeIncreaseAmountButton() {
        Button increaseAmountButton = findViewById(R.id.increase_item_amount_button);

        increaseAmountButton.setOnClickListener(view -> {
            int amount = Integer.parseInt(this.itemAmountTextInput.getEditableText().toString());

            if (++amount > 9999) {
                this.displayInvalidInputDialog(getString(R.string.invalid_product_amount));
            } else {
                this.itemAmountTextInput.setText(String.valueOf(amount));
            }
        });
    }

    private void initializeAddGroceryButton() {
        Button addGroceryButton = findViewById(R.id.add_grocery_button);

        addGroceryButton.setOnClickListener(view -> {
            try {
                this.groceryListAdapter.add(
                        this.itemNameTextInput.getEditableText().toString(),
                        Integer.parseInt(this.itemAmountTextInput.getEditableText().toString()),
                        Double.parseDouble(this.itemPriceTextInput.getEditableText().toString())
                );

                this.groceryListAdapter.notifyDataSetChanged();

                this.resetGroceryForm();
            } catch (InvalidInputException e) {
                this.displayInvalidInputDialog(e.getMessage());
            } catch (Exception e) {
                this.displayUnexpectedErrorDialog();
            }
        });
    }

    private void initializeResetFormFieldsButton() {
        Button resetFormFieldsButton = findViewById(R.id.reset_form_fields_button);

        resetFormFieldsButton.setOnClickListener(view -> this.resetGroceryForm());
    }

    private void initializeGroceryListView() {
        this.groceryListView = findViewById(R.id.groceries_list);

        this.groceryListAdapter = new GroceryListAdapter(this);

        this.groceryListView.setAdapter(this.groceryListAdapter);
    }

    private void initializeEndGroceryListButton() {
        Button endGroceryListButton = findViewById(R.id.end_grocery_list_button);

        endGroceryListButton.setOnClickListener(view -> {
            Dialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.grocery_list_summary_title)
                    .setMessage(this.formatGrocerySummary())
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        try {
                            this.groceryListAdapter.completeList();
                        } catch (InvalidInputException | ListCompleteException e) {
                            this.displayInvalidInputDialog(e.getMessage());
                        } catch (Exception e) {
                            this.displayUnexpectedErrorDialog();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();

            alertDialog.setOnShowListener(dialog -> {
                try {
                    if (this.groceryListAdapter.isComplete()) {
                        throw new ListCompleteException(getString(R.string.grocery_list_complete));
                    }
                    if (this.groceryListAdapter.getItems().isEmpty()) {
                        throw new InvalidInputException(getString(R.string.grocery_list_empty));
                    }
                } catch (InvalidInputException | ListCompleteException e) {
                    dialog.dismiss();
                    this.displayInvalidInputDialog(e.getMessage());
                } catch (Exception e) {
                    dialog.dismiss();
                    this.displayUnexpectedErrorDialog();
                }
            });

            alertDialog.show();
        });
    }

    private void initializeNewGroceryListButton() {
        Button newGroceryListButton = findViewById(R.id.new_grocery_list_button);

        newGroceryListButton.setOnClickListener(view -> {
            this.groceryListAdapter.newList();
            this.resetGroceryForm();
        });
    }

    private void displayInvalidInputDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.invalid_input)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    private void displayUnexpectedErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.invalid_input)
                .setMessage(R.string.unexpected_error)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    private void resetGroceryForm() {
        this.itemNameTextInput.setText(this.defaultItemName);
        this.itemAmountTextInput.setText(this.defaultItemAmount);
        this.itemPriceTextInput.setText(this.defaultItemPrice);
    }

    private String formatGrocerySummary() {
        LinkedHashMap<String, String> summary = this.groceryListAdapter.analyzeData();

        String item_count = summary.get(GroceryListAdapter.ITEM_COUNT);
        String total_amount = summary.get(GroceryListAdapter.TOTAL_AMOUNT);
        String total_price = summary.get(GroceryListAdapter.TOTAL_PRICE);

        return String.format(
                getString(R.string.grocery_list_summary_format),
                item_count,
                total_amount,
                total_price
        );
    }
}
