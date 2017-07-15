package com.example.stamatis.inventorymanagerapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.stamatis.inventorymanagerapp.models.StockItem;

/**
 * Created by Stamatis Stiliatis Togrou on 11/7/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "inventory.db";
    public final static int DB_VERSION = 1;
    public final static String TAG = "DbHelper";

    public DbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(StockContract.StockEntry.CREATE_TABLE_STOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertItem(StockItem item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StockContract.StockEntry.COLUMN_NAME, item.getProductName());
        values.put(StockContract.StockEntry.COLUMN_PRICE, item.getPrice());
        values.put(StockContract.StockEntry.COLUMN_QUANTITY, item.getQuantity());
        values.put(StockContract.StockEntry.COLUMN_SUPPLIER_NAME, item.getSupplierName());
        values.put(StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL, item.getSupplierEmail());
        values.put(StockContract.StockEntry.COLUMN_IMAGE, item.getImage());
        long id = db.insert(StockContract.StockEntry.TABLE_NAME, null, values);
    }

    public void sellItem(long itemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        int newQuantity;
        if (quantity > 0) {
            newQuantity = quantity -1;

            ContentValues values = new ContentValues();
            values.put(StockContract.StockEntry.COLUMN_QUANTITY, newQuantity);
            String selection = StockContract.StockEntry._ID + "=?";
            String[] args = new String[] { String.valueOf(itemId) };
            db.update(StockContract.StockEntry.TABLE_NAME, values, selection, args);
        }
    }

    public Cursor getItem(long itemId) {
        SQLiteDatabase sqlDb = getReadableDatabase();
        String[] projection = {
                StockContract.StockEntry._ID,
                StockContract.StockEntry.COLUMN_NAME,
                StockContract.StockEntry.COLUMN_PRICE,
                StockContract.StockEntry.COLUMN_QUANTITY,
                StockContract.StockEntry.COLUMN_SUPPLIER_NAME,
                StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL,
                StockContract.StockEntry.COLUMN_IMAGE
        };
        String selection = StockContract.StockEntry._ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(itemId)};

        Cursor c = sqlDb.query(
                StockContract.StockEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return c;
    }

    public Cursor getStock() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                StockContract.StockEntry._ID,
                StockContract.StockEntry.COLUMN_NAME,
                StockContract.StockEntry.COLUMN_PRICE,
                StockContract.StockEntry.COLUMN_QUANTITY,
                StockContract.StockEntry.COLUMN_SUPPLIER_NAME,
                StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL,
                StockContract.StockEntry.COLUMN_IMAGE
        };
        return db.query(
                StockContract.StockEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }

    public void updateItem(long itemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StockContract.StockEntry.COLUMN_QUANTITY, quantity);
        String selection = StockContract.StockEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(itemId) };
        db.update(StockContract.StockEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
