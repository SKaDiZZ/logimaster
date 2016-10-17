package com.codepwn.samir.logimaster.products;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Definisi operacije u bazi podataka
 */
public class ProductDbHelper extends SQLiteOpenHelper {

    // Verzija baze podataka
    private static final int DB_VERSION = 1;
    // Ime baze podataka
    private static final String DB_NAME = "products.db";

    // Query koji kreira novu tabelu za proizvode u bazi podataka
    private static final String SQL_CREATE_TABLE = "CREATE TABLE "
                                                    + ProductContract.ProductEntry.TABLE_NAME
                                                    + " ("
                                                    + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY, "
                                                    + ProductContract.ProductEntry.ID + " TEXT, "
                                                    + ProductContract.ProductEntry.BARCODE + " TEXT, "
                                                    + ProductContract.ProductEntry.NAME + " TEXT, "
                                                    + ProductContract.ProductEntry.UNIT + " TEXT, "
                                                    + ProductContract.ProductEntry.PRICE + " DOUBLE "
                                                    + ")";

    // Query koji brise podatke u tabeli sa proizvodima
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;

     public ProductDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
         Log.d("DATABASE " + DB_NAME +": ",  " created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        Log.d("DATABASE " + DB_NAME +": ", ProductContract.ProductEntry.TABLE_NAME + " created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        Log.d("DATABASE " + DB_NAME +": ", ProductContract.ProductEntry.TABLE_NAME + " deleted!");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Dodaj novi proizvod u bazu podataka
     * @param db SQLiteDatabase -> baza
     * @param id String -> productID
     * @param barcode String -> productBarcode
     * @param name String -> productName
     * @param unit String -> productUnit
     * @param price Double -> productPrice
     */
    public void addProduct(SQLiteDatabase db, String id, String barcode, String name, String unit, Double price) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.ProductEntry.ID, id);
        contentValues.put(ProductContract.ProductEntry.BARCODE, barcode);
        contentValues.put(ProductContract.ProductEntry.NAME, name);
        contentValues.put(ProductContract.ProductEntry.UNIT, unit);
        contentValues.put(ProductContract.ProductEntry.PRICE, price);

        db.insert(ProductContract.ProductEntry.TABLE_NAME, null, contentValues);

        Log.d("DATABASE " + DB_NAME +": ", name + " product inserted into " + ProductContract.ProductEntry.TABLE_NAME);

    }

    /**
     * Vrati listu proizvoda iz baze podataka
     * @param db SQLiteDatabase -> baza
     * @return db.query
     */
    public Cursor getProducts(SQLiteDatabase db) {

        String[] projections = {ProductContract.ProductEntry.ID, ProductContract.ProductEntry.BARCODE, ProductContract.ProductEntry.NAME, ProductContract.ProductEntry.UNIT, ProductContract.ProductEntry.PRICE};

        return db.query(ProductContract.ProductEntry.TABLE_NAME, projections, null, null, null, null, null);

    }

    /**
     * Azuriraj proizvod tj. izmjeni stare podatke
     * @param db SQLiteDatabase ->  baza
     * @param old_barcode String -> stari barkod
     * @param new_barcode String -> novi barkod
     * @param new_name String -> novo ime
     * @param new_unit String -> nova jedinica mjere
     * @param new_price Double -> nova cijena
     * @return db.update
     */
    public int updateProduct(SQLiteDatabase db, String old_barcode, String new_barcode, String new_name, String new_unit, Double new_price) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.ProductEntry.BARCODE, new_barcode);
        contentValues.put(ProductContract.ProductEntry.NAME, new_name);
        contentValues.put(ProductContract.ProductEntry.UNIT, new_unit);
        contentValues.put(ProductContract.ProductEntry.PRICE, new_price);

        String selection = ProductContract.ProductEntry.BARCODE + " LIKE ?";
        String[] selection_args = {old_barcode};

        return db.update(ProductContract.ProductEntry.TABLE_NAME, contentValues, selection, selection_args);

    }

    /**
     * Izbrisi proizvod iz baze podataka
     * @param db -> baza
     * @param barcode -> jedinstveni barkod proizvoda
     * @return db.delete
     */
    public int deleteProduct(SQLiteDatabase db, String barcode, String name) {

        String selection = ProductContract.ProductEntry.BARCODE + " LIKE ? AND " + ProductContract.ProductEntry.NAME + " LIKE ?";
        String[] selection_args = {barcode, name};

        return db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selection_args);

    }

    public Cursor hasProduct(SQLiteDatabase db, String barcode) {

        //*String selectString = "SELECT * FROM " + _TABLE + " WHERE " + _ID + " =?";*/

        String selection = ProductContract.ProductEntry.BARCODE + " =?";
        String[] columns = {ProductContract.ProductEntry.BARCODE};
        String[] selection_args = {barcode};
        String limit = "1";

        return db.query(ProductContract.ProductEntry.TABLE_NAME, columns, selection, selection_args, null, null, limit);

    }

}
