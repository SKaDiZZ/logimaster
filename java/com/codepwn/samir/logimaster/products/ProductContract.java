package com.codepwn.samir.logimaster.products;

import android.provider.BaseColumns;

/**
 * Definisi tabelu sa proizvodima u bazi podataka
 */
public final  class ProductContract {

    public ProductContract() {}

    public static abstract class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "products_table";
        public static final String ID = "id";
        public static final String BARCODE = "barcode";
        public static final String NAME = "name";
        public static final String UNIT = "unit";
        public static final String PRICE = "price";

    }

}
