package com.codepwn.samir.logimaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    // Otvori listu proizvoda
    public void openProductList(View v) {
        startActivity(new Intent(this, ProductListActivity.class));
    }

    // Otvori barcode skener
    public void startScanner(View v) {
        startActivity(new Intent(this, ScanProductActivity.class));
    }

}
