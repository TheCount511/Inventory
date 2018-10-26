package com.sugarsinitiative.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    /**
     * Content authority
     **/
    public final static String CONTENT_AUTHORITY = "com.sugarsinitiative.inventory";


    /**
     * base content uri
     **/
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * URI path
     **/
    public final static String PATH_INVENTORY = "inventory";


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }


    /**
     * Inner class that defines constant values for the products database table.
     * Each entry in the table represents a single type of product.
     */

    public static final class InventoryEntry implements BaseColumns {


        /**
         * Complete URI
         **/
        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single type of product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * Name of database table for the inventory
         */
        public final static String TABLE_NAME = "inventory";


        /**
         * Unique ID number for the product (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;


        /**
         * Name of the product.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "name";


        /**
         * Picture of the product.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_PICTURE = "picture";


        /**
         * Price of the product
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";


        /**
         * Quantity of the product
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";


        /**
         * Name of the product supplier.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";


        /**
         * Name of the product supplier mail.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_MAIL = "supplier_mail";


        /**
         * Name of the product supplier phone.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_PHONE = "supplier_phone";


    }

}
