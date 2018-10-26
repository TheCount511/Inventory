package com.sugarsinitiative.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sugarsinitiative.inventory.data.InventoryContract.InventoryEntry;

public class OverviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 1;
    InventoryCursorAdapter mCursorAdapter;
    Uri currentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);


        //setup Fab to open editor activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OverviewActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        }));

        ListView productsListView = findViewById(R.id.products_list_view);

        View emptyView = findViewById(R.id.empty_view);
        productsListView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null);

        productsListView.setAdapter(mCursorAdapter);

        productsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OverviewActivity.this, DetailedViewActivity.class);

                //Form the content URI that represents the specific product that was clicked on,
                //by appending the "id" (passed as input to this method) onto the
                //{@link InventoryEntry#CONTENT_URI}.
                //For example, the URI would be "content://com.sugarsInitiative.inventory/products/6"
                //if the product with ID 6 was clicked on.
                currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                intent.setData(currentProductUri);

                startActivity(intent);
            }


        });


        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }

    public void deleteProducts() {
        //Only perform the delete if there are existing products
        if (!mCursorAdapter.isEmpty()) {
            //Call the ContentResolver to delete the products
            //Pass in null for the selection and selection args because we are deleting the entire record

            int rowsAffected = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.overview_delete_all_products_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.overview_delete_all_products_successfull), Toast.LENGTH_SHORT).show();
            }

        } else
            Toast.makeText(this, getString(R.string.overview_delete_all_products_unavailable), Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_products_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProducts();
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
        // Inflate the menu options from the res/menu/menu_overview.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_inventory_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_records:
                if (mCursorAdapter.isEmpty()) {
                    deleteProducts();
                } else {
                    showDeleteConfirmationDialog();
                }
        }
        return super.onOptionsItemSelected(item);
    }


    //called when a new loader needs to be created
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PICTURE,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY
                };

        return new CursorLoader(this, InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }


    //Called when a previously created loader has finished loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
        int pictureColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PICTURE);
        String imageUriString = data.getString(pictureColumnIndex);
       }

    }

    //Called when a previously created loader is reset, making the data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //This is called when the last Cursor provided to onLoadFinished()
        //above is about to be closed. We need to make sure we are no
        //longer using it.
        mCursorAdapter.swapCursor(null);

    }
}
