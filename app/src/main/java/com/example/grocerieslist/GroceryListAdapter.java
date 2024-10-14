package com.example.grocerieslist;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public final class GroceryListAdapter extends ArrayAdapter<Product> {
    private final Context context;
    private ArrayList<Product> items;
    private boolean isComplete = false;
    public static final String ITEM_COUNT = "item_count";
    public static final String TOTAL_AMOUNT = "total_amount";
    public static final String TOTAL_PRICE = "total_price";

    public GroceryListAdapter(@NonNull Context context, ArrayList<Product> items) {
        super(context, 0, items);

        this.context = context;
        this.items = items;
    }

    public GroceryListAdapter(@NonNull Context context) {
        this(context, new ArrayList<>());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context)
                                        .inflate(R.layout.product_layout, parent, false);
        }

        Product product = getItem(position);

        TextView productNameTextView = convertView.findViewById(R.id.product_name);
        TextView productAmountTextView = convertView.findViewById(R.id.product_amount);
        TextView productPriceTextView = convertView.findViewById(R.id.product_price);
        Button removeProductButton = convertView.findViewById(R.id.remove_product);

        assert product != null;
        productNameTextView.setText(product.getName());
        productAmountTextView.setText(String.valueOf(product.getAmount()));
        productPriceTextView.setText(String.valueOf(product.getPrice()));

        removeProductButton.setOnClickListener(
                view -> this.deleteProductAt(position)
        );

        convertView.setOnLongClickListener(
                view -> this.deleteProductAt(position)
        );

        return convertView;
    }

    /**
     * Get all items.
     *
     * @return ArrayList
     */
    public ArrayList<Product> getItems() {
        return this.items;
    }

    /**
     * Try to delete product at certain position (index).
     *
     * @param position Position/index of item to be removed.
     * @return boolean
     */
    private boolean deleteProductAt(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.grocery_list_remove_item_title)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    try {
                        this.items.remove(position);

                        this.notifyDataSetChanged();
                    } catch (ListCompleteException e) {
                        showErrorDialog(R.string.grocery_list_complete);
                    } catch (Exception e) {
                        showErrorDialog(R.string.unexpected_error);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create()
                .show();

        return true;
    }

    /**
     * Display alert error message.
     *
     * @param message Message to be displayed.
     */
    private void showErrorDialog(@StringRes int message) {
        new androidx.appcompat.app.AlertDialog.Builder(this.getContext())
                .setTitle(R.string.invalid_input)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    /**
     * Clear item list.
     */
    public void clearGroceryList() {
        this.isComplete = false;

        for (Product product : this.items) {
            this.remove(product);
            this.items.remove(product);
        }

        this.notifyDataSetChanged();
    }

    /**
     * Start new list.
     */
    public void newList() {
        this.clearGroceryList();
    }

    /**
     * Analyze the items data.
     *
     * @return Map
     */
    @NonNull
    public LinkedHashMap<String, String> analyzeData() {
        LinkedHashMap<String, String> summary = new LinkedHashMap<>();

        summary.put(ITEM_COUNT, String.valueOf(this.items.size()));
        summary.put(TOTAL_AMOUNT, String.valueOf(this.totalAmount()));
        summary.put(TOTAL_PRICE, String.valueOf(this.totalPrice()));

        return summary;
    }

    /**
     * Calculate the sum of item amounts.
     *
     * @return int
     */
    private int totalAmount() {
        return this.items.stream()
                         .mapToInt(Product::getAmount)
                         .sum();
    }

    /**
     * Calculate the sum of item prices.
     *
     * @return double
     */
    private double totalPrice() {
        return this.items.stream()
                         .mapToDouble(Product::getTotalPrice)
                         .sum();
    }

    /**
     * Add item to the list.
     *
     * @param item Item to be added to the list.
     * @throws ListCompleteException If the list is already complete.
     */
    @Override
    public void add(Product item) {
        validateCompleteness();

        this.items.add(item);
    }

    /**
     * Add item <span style="font-style: italic;color:gray">(Product)</span> to the list.
     *
     * @param name   Product name
     * @param amount Product amount
     * @param price  Product price
     * @throws ListCompleteException If the list is already complete.
     * @throws InvalidInputException If any of the input fields is invalid.
     */
    public void add(String name, int amount, double price) {
        validateCompleteness();

        this.validate(name, amount, price);

        this.items.add(
                new Product(name, amount, price)
        );
    }

    /**
     * Remove the item the list.
     *
     * @param item Item to be removed from the list.
     * @throws ListCompleteException If the list is already complete.
     */
    @Override
    public void remove(Product item) {
        validateCompleteness();

        this.items.remove(item);
    }

    /**
     * Remove the item the list.
     *
     * @param index Index of item to be removed from the list.
     * @throws ListCompleteException If the list is already complete.
     */
    public void remove(int index) {
        validateCompleteness();

        this.items.remove(index);
    }

    /**
     * Complete the list.
     *
     * @throws InvalidInputException When list is empty or already completed.
     */
    public void completeList() {
        if (this.items.isEmpty() || this.isComplete) {
            throw new InvalidInputException(this.context.getString(R.string.grocery_list_empty));
        }

        this.isComplete = true;
    }

    /**
     * Complete the list.
     */
    public boolean isComplete() {
        return this.isComplete;
    }

    /**
     * Validate input name, amount and price.
     *
     * @throws InvalidInputException If any of the input fields is invalid.
     */
    public void validate(String name, int amount, double price) {
        this.validateName(name);
        this.validateAmount(amount);
        this.validatePrice(price);
    }

    /**
     * Validate input name.
     *
     * @throws InvalidInputException If name is an empty string.
     */
    public void validateName(@NonNull String name) {
        if (name.isEmpty()) {
            throw new InvalidInputException(this.context.getString(R.string.invalid_product_name));
        }
    }

    /**
     * Validate input amount.
     *
     * @throws InvalidInputException If amount is not a positive number bigger than 0.
     */
    public void validateAmount(int amount) {
        if (amount < 1) {
            throw new InvalidInputException(this.context.getString(R.string.invalid_product_amount));
        }
    }

    /**
     * Validate input price.
     *
     * @throws InvalidInputException If price is not a positive number.
     */
    public void validatePrice(double price) {
        if (price <= 0) {
            throw new InvalidInputException(this.context.getString(R.string.invalid_product_price));
        }
    }

    /**
     * Validate list completeness.
     *
     * @throws ListCompleteException If the list is complete.
     */
    public void validateCompleteness() {
        if (this.isComplete) {
            throw new ListCompleteException(this.context.getString(R.string.grocery_list_complete));
        }
    }
}
