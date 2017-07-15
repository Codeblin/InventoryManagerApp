package com.example.stamatis.inventorymanagerapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.stamatis.inventorymanagerapp.data.DbHelper;
import com.example.stamatis.inventorymanagerapp.data.StockContract;
import com.example.stamatis.inventorymanagerapp.models.StockItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stamatis Stiliatis Togrou on 11/7/2017.
 */

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private static final int REQUEST_CODE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_PIC = 0;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private EditText mEditProductName, mEditProductPrice, mEditProductQuantity,
            mEditSupplierName, mEditSupplierEmail;
    private Button mBtnCreate;
    private ImageButton mBtnSub, mBtnAdd;
    private ImageView mImg;

    private Uri mImgUri;
    private DbHelper dbHelper;
    private long itemId = 0;
    private String[] addresses = new String[1];
    private boolean onlyForEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        dbHelper = new DbHelper(this);
        initViews();
        initListeners();

        // if there is flag for edit and id, disable the views
        if (getIntent().getExtras() != null){
            if (getIntent().getExtras().get("flag").equals("info")){
                itemId = (long)getIntent().getExtras().get("id");
                setupForInfoMode((long)getIntent().getExtras().get("id"));
                onlyForEdit = true;
            }
        }
    }

    private void initViews() {
        mEditProductName = (EditText) findViewById(R.id.txt_product_name);
        mEditProductPrice = (EditText) findViewById(R.id.txt_product_price);
        mEditProductQuantity = (EditText) findViewById(R.id.txt_product_quantity);
        mEditSupplierName = (EditText) findViewById(R.id.txt_supplier_name);
        mEditSupplierEmail = (EditText) findViewById(R.id.txt_supplier_email);
        mBtnSub = (ImageButton) findViewById(R.id.btn_sub);
        mBtnAdd = (ImageButton) findViewById(R.id.btn_add);
        mBtnCreate = (Button) findViewById(R.id.btn_create);
        mImg = (ImageView) findViewById(R.id.img);
    }

    private void initListeners() {
        mBtnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractQuantity();
            }
        });

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuantity();
            }
        });

        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissionImagePicker();
            }
        });

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemId == 0){
                    if (addItemToDb()){
                        startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    addresses[0] = mEditSupplierEmail.getText().toString();
                    composeEmail(addresses);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (onlyForEdit)
            getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_done:
                if (checkField(mEditProductQuantity)){
                    dbHelper.updateItem(itemId, Integer.valueOf(mEditProductQuantity.getText().toString()));
                    startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                    DetailsActivity.this.finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please don't let empty fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.item_delete:
                showConfirmationDialog(itemId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationDialog(final long itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this product?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct(itemId);
                startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                DetailsActivity.this.finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int deleteProduct(long id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = StockContract.StockEntry._ID + "=?";
        String[] selectionArgs = { String.valueOf(id) };
        return db.delete(StockContract.StockEntry.TABLE_NAME, selection, selectionArgs);
    }

    // User interaction methods
    // ...
    private void subtractQuantity(){
        int quantity = 0;
        if (checkField(mEditProductQuantity))
            quantity = Integer.valueOf(mEditProductQuantity.getText().toString());

        if (quantity > 0){
            quantity--;
            mEditProductQuantity.setText(String.valueOf(quantity));
        }
    }

    private void addQuantity(){
        int quantity = 0;
        if (checkField(mEditProductQuantity))
                quantity = Integer.valueOf(mEditProductQuantity.getText().toString());

        quantity++;
        mEditProductQuantity.setText(String.valueOf(quantity));
    }

    // Misc methods
    // ...
    private void requestPermissionImagePicker(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_EXTERNAL_STORAGE);
            return;
        }

        // Open picker once granted
        openImagePicker();
    }

    private void openImagePicker(){Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PIC);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == REQUEST_CODE_PIC && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                mImgUri = resultData.getData();
                mImg.setImageURI(mImgUri);
                mImg.invalidate();
            }
        }
    }

    // Data methods
    // ...
    private boolean addItemToDb(){
        boolean fieldsAreValid = true;
        if (!checkField(mEditProductName))
            fieldsAreValid = false;
        if (!checkField(mEditProductPrice))
            fieldsAreValid = false;
        if (!checkField(mEditProductQuantity))
            fieldsAreValid = false;
        if (!checkField(mEditSupplierName))
            fieldsAreValid = false;
        if (!checkField(mEditSupplierEmail)){
            fieldsAreValid = false;
        }else {
            if (!checkEmail(mEditSupplierEmail.getText().toString()))
                fieldsAreValid = false;
        }
        if (mImgUri == null)
            fieldsAreValid = false;

        // if not all have changed, then exit with false
        if (!fieldsAreValid) return false;

        StockItem item = new StockItem(mEditProductName.getText().toString().trim(),
                mEditProductPrice.getText().toString().trim(),
                Integer.parseInt(mEditProductQuantity.getText().toString().trim()),
                mEditSupplierName.getText().toString().trim(),
                mEditSupplierEmail.getText().toString().trim(), mImgUri.toString());
        dbHelper.insertItem(item);

        return true;
    }

    private boolean checkField(EditText editText){
        if (!TextUtils.isEmpty(editText.getText()))
            return true;
        else
            return false;
    }

    private boolean checkEmail(String email){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    // Disable all views
    private void setupForInfoMode(long id){
        Cursor cursor = dbHelper.getItem(id);
        cursor.moveToFirst();
        mEditProductName.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_NAME)));
        mEditProductPrice.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_PRICE)));
        mEditProductQuantity.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_QUANTITY)));
        mEditSupplierName.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_SUPPLIER_NAME)));
        mEditSupplierEmail.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL)));
        mImg.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_IMAGE))));
        mBtnCreate.setText("Order more");

        // Only let quantity edit
        mEditProductName.setEnabled(false);
        mEditProductPrice.setEnabled(false);
        mEditSupplierName.setEnabled(false);
        mEditSupplierEmail.setEnabled(false);
        mImg.setEnabled(false);
    }

    public void composeEmail(String[] addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_TEXT, "I need more of " + mEditProductName.getText().toString() + ".\n\nThank you");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
