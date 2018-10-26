package com.sugarsinitiative.inventory;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sugarsinitiative.inventory.data.InventoryContract.InventoryEntry;

import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    Uri mCurrentProductUri;

    /**
     * set variable names for the Edit text fields
     */
    EditText productNameEditText;
    EditText productPriceEditText;
    EditText availableQuantityEditText;
    EditText supplierNameEditText;
    EditText supplierMailEditText;
    EditText supplierPhoneEditText;
    ImageButton addImageButton;
    private boolean mProductHasChanged;
    /**
     * URI of product image
     */
    private Uri mImageUri = null;


    /*
    OnTouchListener set to take note of touches on the EditText views which,
    which imply that; the user might have made changes to the data.
    on this happening we change the mProductHasChanged boolean to true
    */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_add_product));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        productNameEditText = findViewById(R.id.edit_product_name);
        productPriceEditText = findViewById(R.id.edit_product_price);
        availableQuantityEditText = findViewById(R.id.edit_available_quantity);
        supplierNameEditText = findViewById(R.id.edit_supplier_name);
        supplierMailEditText = findViewById(R.id.edit_supplier_E_mail);
        supplierPhoneEditText = findViewById(R.id.edit_supplier_phone);
        addImageButton = findViewById(R.id.add_image_button);

        addImageButton.setOnTouchListener(mTouchListener);
        productNameEditText.setOnTouchListener(mTouchListener);
        productPriceEditText.setOnTouchListener(mTouchListener);
        availableQuantityEditText.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierMailEditText.setOnTouchListener(mTouchListener);
        supplierPhoneEditText.setOnTouchListener(mTouchListener);


        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySelector();
            }
        });

    }

    public void trySelector() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(getString(R.string.intent_type));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mImageUri = data.getData();
                addImageButton.setImageDrawable(placeholderImage(mImageUri));
                addImageButton.invalidateDrawable(placeholderImage(mImageUri));
            }
        }
    }

    private Drawable placeholderImage(Uri picture){

        Bitmap bitmap = null;
        Matrix Mat = new Matrix();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap Destination = Bitmap.createScaledBitmap(bitmap, 128, 128, true);
        bitmap = Bitmap.createBitmap(Destination, 0,0, Destination.getWidth(), Destination.getHeight(), Mat,true);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);

        return roundedBitmapDrawable;
    }

    private Drawable placeholderImage(int pictureRes){

        Bitmap bitmap;
        Matrix Mat = new Matrix();
            bitmap = BitmapFactory.decodeResource(getResources(), pictureRes);
            Bitmap Destination = Bitmap.createScaledBitmap(bitmap, 128, 128, true);
            bitmap = Bitmap.createBitmap(Destination, 0,0, Destination.getWidth(), Destination.getHeight(), Mat,true);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);

        return roundedBitmapDrawable;
    }


    private void saveProduct() {
        // Read from the input fields
        //use the trim function to eliminate leading or trailing white spaces

        String productName = productNameEditText.getText().toString().trim();

        String productPriceString = productPriceEditText.getText().toString();

        String availableQuantityString = availableQuantityEditText.getText().toString();

        int productPrice = 0;


        int availableQuantity = 0;

        String supplierName = supplierNameEditText.getText().toString().trim();

        String supplierMail = supplierMailEditText.getText().toString().trim();

        String supplierPhone = supplierPhoneEditText.getText().toString().trim();

        //give an error message if the product name is left empty or supplier name is not filled
        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(supplierName)) {
            Toast.makeText(this, R.string.error_incomplete_data,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //give an error message if the supplier mail phone number is not filled
        if (TextUtils.isEmpty(supplierMail) && TextUtils.isEmpty(supplierPhone)) {
            Toast.makeText(this, R.string.editor_incomplete_supplier_details,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and product and supplier details from the editor are the values.
        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(InventoryEntry.COLUMN_SUPPLIER_MAIL, supplierMail);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        /*
         * fill on the price and quantity as zero if they are left empty
         */
        if (TextUtils.isEmpty(productPriceString)) {
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPrice);
        } else {
            productPrice = Integer.parseInt(productPriceString);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPrice);
        }

        if (TextUtils.isEmpty(availableQuantityString)) {
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, availableQuantity);
        } else {
            availableQuantity = Integer.parseInt(availableQuantityString);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, availableQuantity);
        }
        if (mImageUri != null){
            values.put(InventoryEntry.COLUMN_PRODUCT_PICTURE, mImageUri.toString());
        }

        if (mCurrentProductUri == null) {

            // Insert a new product into the provider returning the content URI for the new product
            Uri mUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (mUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();

            }
        }

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

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                  if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
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
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_MAIL,
                InventoryEntry.COLUMN_SUPPLIER_PHONE
        };

        return new CursorLoader(this, // Parent activity context
                mCurrentProductUri,    // Query the content URI for the current Product
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
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int pictureColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PICTURE);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int availableQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierMailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_MAIL);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);


            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int availableQuantity = cursor.getInt(availableQuantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierMail = cursor.getString(supplierMailColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String imageUriString = cursor.getString(pictureColumnIndex);

            if(imageUriString==null){
                addImageButton.setImageDrawable(placeholderImage(R.drawable.round_add_a_photo_white_48));
            }
            else {
                mImageUri = Uri.parse(imageUriString);
                addImageButton.setImageDrawable(placeholderImage(mImageUri));
            }


            //Update the views on the screen with the values from the database
            productNameEditText.setText(name);
            productPriceEditText.setText(String.valueOf(price));
            availableQuantityEditText.setText(String.valueOf(availableQuantity));
            supplierNameEditText.setText(supplierName);
            supplierMailEditText.setText(supplierMail);
            supplierPhoneEditText.setText(supplierPhone);

        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        //if the loader is invalidated clear out all the data from the input fields.
        productNameEditText.setText("");
        addImageButton.setImageResource(R.drawable.round_add_a_photo_white_48);
        productPriceEditText.setText("");
        availableQuantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierMailEditText.setText("");
        supplierPhoneEditText.setText("");


    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}
