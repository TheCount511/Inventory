package com.sugarsinitiative.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sugarsinitiative.inventory.data.InventoryContract.InventoryEntry;

public class DetailedViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    Uri mCurrentProductUri;

    TextView productNameTextView;
    TextView productPriceTextView;
    TextView productQuantityTextView;
    ImageButton reduceQuantityButton;
    ImageButton increaseQuantityButton;
    Button callButton;
    Button mailButton;
    ImageView productImage;
    String supplierPhone;
    String supplierMail;
    int quantity;
    /**
     * URI of product image
     */
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        // Create an intent that gets the intent passed from the referring activity
        // through this intent, get the Uri for the product to be viewed in this activity
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        productNameTextView = findViewById(R.id.product_detail_name);
        productPriceTextView = findViewById(R.id.product_detail_price);
        productQuantityTextView = findViewById(R.id.product_detail_quantity);
        reduceQuantityButton = findViewById(R.id.reduce_quantity);
        increaseQuantityButton = findViewById(R.id.increase_quantity);
        callButton = findViewById(R.id.call_button);
        mailButton = findViewById(R.id.mail_button);
        productImage = findViewById(R.id.product_detail_image);


        reduceQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supplierPhone.trim().isEmpty()){
                    Toast.makeText(DetailedViewActivity.this, getText(R.string.error_phone_number_unavailable), Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ supplierPhone));
                    startActivity(intent);
                }

            }
        });

        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supplierMail.trim().isEmpty()){
                    Toast.makeText(DetailedViewActivity.this, getString(R.string.error_mail_unavailable), Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + supplierMail));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.email_subject));

                    startActivity(Intent.createChooser(emailIntent, getText(R.string.email_chooser)));
                }

            }
        });
        //setup Fab to open editor activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedViewActivity.this, EditorActivity.class);

                intent.setData(mCurrentProductUri);

                startActivity(intent);
            }
        }));


        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
    }

    public void increaseQuantity() {
        quantity++;
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        getContentResolver().update(mCurrentProductUri, values, null, null);
        getContentResolver().notifyChange(mCurrentProductUri, null);
    }

    public void decreaseQuantity() {
        quantity--;
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        getContentResolver().update(mCurrentProductUri, values, null, null);
        getContentResolver().notifyChange(mCurrentProductUri, null);

    }

    public void deleteProduct() {
        int rowsAffected = getContentResolver().delete(mCurrentProductUri, null, null);
        // Show a toast message depending on whether or not the delete was successful.
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.overview_delete_all_products_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.overview_delete_all_products_successfull), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_single_product_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detailed_view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Delete products" menu option
            case R.id.action_delete_product:
                showDeleteConfirmationDialog();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PICTURE,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryEntry.COLUMN_SUPPLIER_MAIL
        };

        return new CursorLoader(this, // Parent activity context
                mCurrentProductUri,    // Query the content URI for the current product
                projection,                            // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);
            int supplierMailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_MAIL);
            int pictureColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PICTURE);





            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            supplierMail = cursor.getString(supplierMailColumnIndex);
            String imageUriString = cursor.getString(pictureColumnIndex);

            if (imageUriString==null){
                productImage.setImageResource(R.drawable.round_add_a_photo_white_48);
            }
            else {
                mImageUri = Uri.parse(imageUriString);
                productImage.setImageURI(mImageUri);
            }


            //Update the views on the screen with the values from the database
            productNameTextView.setText(name);
            productPriceTextView.setText(String.valueOf(price));
            productQuantityTextView.setText(String.valueOf(quantity));


        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //if the loader is invalidated clear out all the data from the input fields.
        productNameTextView.setText("");
        productPriceTextView.setText("");
        productQuantityTextView.setText("");
        productImage.setImageResource(R.drawable.ic_empty_store);
    }

}
