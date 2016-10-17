package com.codepwn.samir.logimaster.products;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.codepwn.samir.logimaster.AddProductActivity;
import com.codepwn.samir.logimaster.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Async task koji dodaje novi proizvod ili vraca listu proizvoda zavisno od parametra action
 */
public class ProductAsyncTask extends AsyncTask <String, Product, String>{

    Context context;
    Activity activity;

    RecyclerView rv_productsList;
    public ProductRecycleAdapter productAdapter;
    RecyclerView.LayoutManager layoutManager;

    String id, old_barcode, barcode, name, unit;
    Double price;

    List<Product> products = new ArrayList<>();

    public ProductAsyncTask(Context context) {
        this.context = context;
        activity = (Activity) context;
    }

    @Override
    protected String doInBackground(String... params) {

        ProductDbHelper productDbHelper = new ProductDbHelper(context);
        String action = params[0];


        switch (action) {
            case "add_product": {

                id = params[1];
                barcode = params[2];
                name = params[3];
                unit = params[4];
                price = Double.parseDouble(params[5]);

                SQLiteDatabase db = productDbHelper.getWritableDatabase();
                productDbHelper.addProduct(db, id, barcode, name, unit, price);
                db.close();

                return "New product " + name + " saved in your database ...";

            }
            case "update_product": {

                old_barcode = params[1];
                barcode = params[2];
                name = params[3];
                unit = params[4];
                price = Double.parseDouble(params[5]);

                SQLiteDatabase db = productDbHelper.getWritableDatabase();
                productDbHelper.updateProduct(db, old_barcode, barcode, name, unit, price);
                db.close();

                return "Product updated ...";

            }
            case "get_products": {

                SQLiteDatabase db = productDbHelper.getReadableDatabase();
                Cursor cursor = productDbHelper.getProducts(db);

                while (cursor.moveToNext()) {

                    id = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.ID));
                    barcode = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.BARCODE));
                    name = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.NAME));
                    unit = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.UNIT));
                    price = cursor.getDouble(cursor.getColumnIndex(ProductContract.ProductEntry.PRICE));

                    Product product = new Product(id, barcode, name, unit, price);
                    publishProgress(product);
                }

                cursor.close();
                db.close();

                return "get_products";

            }
            case "check_product": {

                barcode = params[1];

                SQLiteDatabase db = productDbHelper.getReadableDatabase();
                Cursor cursor = productDbHelper.hasProduct(db, barcode);

                boolean hasProduct = (cursor.getCount() > 0);

                cursor.close();
                db.close();

                Log.d("AsyncTask", "TRIGGERED!" + String.valueOf(hasProduct));

                return String.valueOf(hasProduct);
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Product... values) {
        products.add(values[0]);
    }

    @Override
    protected void onPostExecute(String result) {

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) activity.findViewById(R.id.add_product_coordinator);

        switch (result) {

            case "get_products":

                rv_productsList = (RecyclerView) activity.findViewById(R.id.productsRecycler);

                layoutManager = new LinearLayoutManager(context);
                rv_productsList.setLayoutManager(layoutManager);

                productAdapter = new ProductRecycleAdapter(context, products, new ProductRecycleAdapter.OnProductClickListener() {
                    @Override
                    public void onItemClick(Product product) {
                        Intent editProduct = new Intent(context, AddProductActivity.class);
                        editProduct.putExtra("action", "update_product");
                        editProduct.putExtra("productId", product.getProductID());
                        editProduct.putExtra("productBarcode", product.getProductBarcode());
                        editProduct.putExtra("productName", product.getProductName());
                        editProduct.putExtra("productPrice", product.getProductPrice());
                        editProduct.putExtra("productUnit", product.getProductUnit());
                        context.startActivity(editProduct);
                    }
                });
                productAdapter.setUndoOn(true);
                rv_productsList.setAdapter(productAdapter);

                ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ProductTouchHelper(0, ItemTouchHelper.LEFT, context, rv_productsList));
                mItemTouchHelper.attachToRecyclerView(rv_productsList);

                rv_productsList.addItemDecoration(new ProductItemDecoration());

                break;

            case "true": {

                Snackbar.make(
                        coordinatorLayout,
                        "Product with same barcode already exists in your products database!",
                        Snackbar.LENGTH_LONG
                ).show();

                break;
            }

            case "false":

                // do nothing
                break;

            default: {

                Snackbar.make(
                        coordinatorLayout,
                        result,
                        Snackbar.LENGTH_SHORT
                ).show();

                break;
            }
        }
    }

}
