package com.codepwn.samir.logimaster.products;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.codepwn.samir.logimaster.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by samir on 6/23/16.
 */
public class ProductRecycleAdapter extends RecyclerView.Adapter<ProductRecycleAdapter.ProductViewHolder> implements Filterable {

    Context context;
    private final OnProductClickListener listener;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    List<Product> products = new ArrayList<>();
    List<Product> filteredProducts = new ArrayList<>();
    List<Product> itemsPendingRemoval = new ArrayList<>();

    int lastInsertedIndex; // so we can add some more items for testing purposes
    boolean undoOn; // is undo on, you can turn it on from the toolbar menu

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Product, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    public interface OnProductClickListener {
        void onItemClick(Product item);
    }

    public ProductRecycleAdapter(Context context, List<Product> products, OnProductClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.products = products;
        this.filteredProducts = products;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_products_list, parent, false);
        return new ProductViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        final Product product = filteredProducts.get(position);

            holder.bind(product, listener);

        if (itemsPendingRemoval.contains(product)) {

            // we need to show the "undo" state of the row
            holder.itemView.setBackgroundColor(Color.RED);
            holder.tv_name.setVisibility(View.GONE);
            holder.tv_barcode.setVisibility(View.GONE);
            holder.tv_price.setVisibility(View.GONE);

            holder.undoButton.setVisibility(View.VISIBLE);
            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(product);
                    pendingRunnables.remove(product);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(product);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(filteredProducts.indexOf(product));
                }
            });

        } else {

            // we need to show the "normal" state
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);

            holder.tv_name.setVisibility(View.VISIBLE);
            holder.tv_name.setText(product.getProductName());

            holder.tv_barcode.setVisibility(View.VISIBLE);
            holder.tv_barcode.setText(product.getProductBarcode());

            holder.tv_price.setVisibility(View.VISIBLE);
            holder.tv_price.setText(String.format(Locale.getDefault(), "%.2f", product.getProductPrice()));

            holder.undoButton.setVisibility(View.GONE);
            holder.undoButton.setOnClickListener(null);

        }
    }

    @Override
    public int getItemCount() {
        return filteredProducts.size();
    }

    @Override
    public long getItemId(int position) {
        return filteredProducts.indexOf(position);
    }

    public void addProducts(List<Product> filteredProducts) {
        products = filteredProducts;
        notifyDataSetChanged();
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        final Product product = filteredProducts.get(position);
        if (!itemsPendingRemoval.contains(product)) {
            itemsPendingRemoval.add(product);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {

                    remove(filteredProducts.indexOf(product));

                }
            };

            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(product, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {

        Product product = filteredProducts.get(position);

        ProductDbHelper productDbHelper = new ProductDbHelper(context);
        SQLiteDatabase db = productDbHelper.getWritableDatabase();
        productDbHelper.deleteProduct(db, product.getProductBarcode(), product.getProductName());

        if (itemsPendingRemoval.contains(product)) {
            itemsPendingRemoval.remove(product);
        }
        if (filteredProducts.contains(product)) {
            filteredProducts.remove(position);
            notifyItemRemoved(position);
        }
        if (products.contains(product)) {
            products.remove(product);
        }

    }

    public boolean isPendingRemoval(int position) {

        Product product = filteredProducts.get(position);
        return itemsPendingRemoval.contains(product);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filteredProducts = (List<Product>) results.values;

                if (results.count > 0) {
                    addProducts(filteredProducts);
                } else {
                    notifyDataSetChanged();
                }

            }


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<Product> filteredResults;

                if (constraint.length() == 0) {
                    filteredResults = products;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }
        };
    }

    protected List<Product> getFilteredResults(String constraint) {
        List<Product> results = new ArrayList<>();

        for (Product item : products) {

            if (item.getProductName().toLowerCase().contains(constraint)) {
                results.add(item);
            }

            if (item.getProductBarcode().toLowerCase().contains(constraint)) {
                results.add(item);
            }

        }
        return results;
    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_barcode, tv_price;
        Button undoButton;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tw_plistName);
            tv_barcode = (TextView) itemView.findViewById(R.id.tw_plistBarcode);
            tv_price = (TextView) itemView.findViewById(R.id.tw_plistPrice);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);

        }

        public void bind(final Product product, final OnProductClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(product);
                }
            });

        }
    }
}

