package com.sugarsinitiative.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sugarsinitiative.inventory.data.InventoryContract.InventoryEntry;

import java.io.IOException;


public class InventoryCursorAdapter extends CursorAdapter {


    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     */

    InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }


    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        /*
          Find the fields to populate in inflated template
         */
        TextView productName = view.findViewById(R.id.overview_product_name);
        TextView productPrice = view.findViewById(R.id.overview_product_price);
        TextView productQuantity = view.findViewById(R.id.overview_product_remaining);
        Button sellButton = view.findViewById(R.id.button);
        ImageView productImageView = view.findViewById(R.id.product_image);

        /*
          Extract values from the cursor
         */
        String product_Name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_NAME));
        String product_Price = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PRICE));
        final int product_Quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
        String productImageUriString = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PICTURE));
        String quantityString = String.valueOf(product_Quantity);
        int product_id = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        final Uri mCurrentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, product_id);


        if (productImageUriString==null){




            productImageView.setImageResource(R.drawable.round_add_a_photo_white_48);
        }
        else {
            Uri productImageUri = Uri.parse(productImageUriString);
            Matrix Mat = new Matrix();
            Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), productImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            Bitmap Destination = Bitmap.createScaledBitmap(bitmap, 100, 80, true);
            bitmap = Bitmap.createBitmap(Destination, 0,0, Destination.getWidth(), Destination.getHeight(), Mat,true);
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
                productImageView.setImageDrawable(roundedBitmapDrawable);
        }


        /*
        Create a content value that would be used in updating the database when a product is sold
        this would be used the sell button is clicked
        */
        final ContentValues values = new ContentValues();

        //Set a listener on the sell button
        sellButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*
                check if the quantity available for a product is more than 0 before selling
                if it is then proceed with the sale else give an error notification to the user
                */

                if (product_Quantity > 0) {
                    int availableQuantity = product_Quantity;
                    availableQuantity--;
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, availableQuantity);
                    context.getContentResolver().update(mCurrentProductUri, values, null, null);
                    context.getContentResolver().notifyChange(mCurrentProductUri, null);
                } else {
                    Toast.makeText(context, context.getText(R.string.product_out_of_stock),
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

        productName.setText(product_Name);
        productPrice.setText(product_Price);
        productQuantity.setText(quantityString);
    }

}

