package com.example.stamatis.inventorymanagerapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.stamatis.inventorymanagerapp.data.DbHelper;
import com.example.stamatis.inventorymanagerapp.data.StockContract;

/**
 * Created by Stamatis Stiliatis Togrou on 11/7/2017.
 */

public class MainActivity extends AppCompatActivity implements StockAdapter.StockItemClickListener {

    private FloatingActionButton mFab;
    private LinearLayout mLayoutEmpty;
    private ListView mListView;

    private DbHelper dbHelper;
    private StockAdapter mStockAdapter;
    private Cursor mCursor;
    int visibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();

        dbHelper = new DbHelper(this);

        mCursor = dbHelper.getStock();

        mStockAdapter = new StockAdapter(MainActivity.this, mCursor);
        mListView.setAdapter(mStockAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(i == 0) return;
                final int currentFirstVisibleItem = absListView.getFirstVisiblePosition();
                if (currentFirstVisibleItem > visibleItem) {
                    mFab.show();
                } else if (currentFirstVisibleItem < visibleItem) {
                    mFab.hide();
                }
                visibleItem = currentFirstVisibleItem;
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {}
        });
    }

    private void initViews() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mLayoutEmpty = (LinearLayout) findViewById(R.id.layout_empty);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setEmptyView(mLayoutEmpty);
    }

    private void initListeners(){
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetailsActivity.class));
            }
        });
    }

    @Override
    public void onStockItemClick(View view, long id, int quantity) {
        switch (view.getId()){
            case R.id.img_product_item:
                inspectProduct(id);
                break;
            case R.id.btn_sell:
                sellProduct(id, quantity);
                mStockAdapter.swapCursor(dbHelper.getStock());
                break;
        }
    }

    private void sellProduct(long id, int quantity){
        dbHelper.sellItem(id, quantity);
        mStockAdapter.swapCursor(dbHelper.getStock());
    }

    private void inspectProduct(long id){
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("flag", "info");
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
