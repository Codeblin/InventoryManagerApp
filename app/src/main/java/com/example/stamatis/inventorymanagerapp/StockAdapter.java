package com.example.stamatis.inventorymanagerapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stamatis.inventorymanagerapp.data.StockContract;

/**
 * Created by Stamatis Stiliatis Togrou on 13/7/2017.
 */

public class StockAdapter extends CursorAdapter {

    public interface StockItemClickListener{
        public void onStockItemClick(View view, long id, int quantity);
    }

    private Context mContext;
    private StockItemClickListener stockItemClickListener;
    private int mQuantity;

    public StockAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;

        if (!(context instanceof StockItemClickListener)) {
            throw new ClassCastException("Context must implement TopListClickListener");
        }
        this.stockItemClickListener = (StockItemClickListener) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.product_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final long id = cursor.getLong(cursor.getColumnIndex(StockContract.StockEntry._ID));
        mQuantity = cursor.getInt(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_QUANTITY));
        TextView txtName = view.findViewById(R.id.txt_product_item_name);
        TextView txtQuantity = view.findViewById(R.id.txt_product_item_quantity);
        TextView txtPrice = view.findViewById(R.id.txt_product_item_price);
        Button btnSell = view.findViewById(R.id.btn_sell);
        ImageView imgPic = view.findViewById(R.id.img_product_item);

        String name = cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_QUANTITY));
        String price = cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_PRICE));

        txtName.setText(name);
        txtQuantity.setText(String.valueOf(quantity));
        txtPrice.setText(price);
        imgPic.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_IMAGE))));

        imgPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockItemClickListener.onStockItemClick(view, id, quantity);
            }
        });

        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockItemClickListener.onStockItemClick(view, id, quantity);
            }
        });
    }
}
