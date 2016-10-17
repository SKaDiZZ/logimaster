package com.codepwn.samir.logimaster;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.codepwn.samir.logimaster.products.ProductAsyncTask;

import java.util.Locale;

public class AddProductActivity extends AppCompatActivity {

    String action = "add_product";
    String scannedCode;
    String oldBarcode;

    EditText et_productID;
    EditText et_productBarcode;
    EditText et_productName;
    EditText et_productPrice;
    RadioButton rb_productUnitPiece;
    RadioButton rb_productUnitKilo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        checkIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Product");

        et_productID = (EditText) findViewById(R.id.productID);
        et_productBarcode = (EditText) findViewById(R.id.productBarcode);
        et_productName = (EditText) findViewById(R.id.productName);
        et_productPrice = (EditText) findViewById(R.id.productPrice);
        rb_productUnitPiece = (RadioButton) findViewById(R.id.productUnitPiece);
        rb_productUnitKilo = (RadioButton) findViewById(R.id.productUnitKilo);

        et_productBarcode.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        if(editable.length() > 0) {

                            String barcode = et_productBarcode.getText().toString();
                            ProductAsyncTask asyncTask = new ProductAsyncTask(AddProductActivity.this);
                            asyncTask.execute("check_product", barcode);

                            Log.d("BARCODE", barcode);

                        }

                    }
        });
        

        if (scannedCode != null) {
            et_productBarcode.setText(scannedCode);
        }

        if (action.equals("update_product")) {

            getSupportActionBar().setTitle("Edit Product");

            oldBarcode = getIntent().getExtras().getString("productBarcode");

            et_productID.setText(getIntent().getExtras().getString("productId"));
            et_productBarcode.setText(getIntent().getExtras().getString("productBarcode"));
            et_productName.setText(getIntent().getExtras().getString("productName"));
            et_productPrice.setText(String.format(Locale.getDefault(), "%.2f", getIntent().getExtras().getDouble("productPrice")));

            if (getIntent().getExtras().getString("productUnit").equals("kilo")) {

                rb_productUnitKilo.setChecked(true);
                rb_productUnitPiece.setChecked(false);

            } else {

                rb_productUnitKilo.setChecked(false);
                rb_productUnitPiece.setChecked(true);

            }
        }

    }

    public void checkIntent() {

        if (getIntent().hasExtra("scannedCode")) {
            scannedCode = getIntent().getExtras().getString("scannedCode");
        }

        if (getIntent().hasExtra("action")) {

            if (getIntent().getExtras().getString("action").equals("update_product")) {

                action = "update_product";

            }

        }

    }

    public void scanCode(View view) {

        Intent startScanner = new Intent(this, ScanProductActivity.class);
        startScanner.putExtra("action", "return_code");
        startActivity(startScanner);

    }

    public void saveProduct(View view) {

        ProductAsyncTask productAsyncTask;

        String productID = et_productID.getText().toString();
        String productBarcode = et_productBarcode.getText().toString();
        String productName = et_productName.getText().toString();
        String productPrice = et_productPrice.getText().toString();
        String productUnit;

        if(rb_productUnitPiece.isChecked()) {
            productUnit = "piece";
        } else {
            productUnit = "kilo";
        }

        if(action.equals("add_product")) {

            productAsyncTask = new ProductAsyncTask(this);
            productAsyncTask.execute(action, productID, productBarcode, productName, productUnit, productPrice
            );

        } else if(action.equals("update_product")) {

            productAsyncTask = new ProductAsyncTask(this);
            productAsyncTask.execute(action, oldBarcode, productBarcode, productName, productUnit, productPrice);

        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), ProductListActivity.class));
            }
        }, 2000);

    }
}
