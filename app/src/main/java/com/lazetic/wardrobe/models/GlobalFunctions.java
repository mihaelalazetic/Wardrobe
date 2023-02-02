package com.lazetic.wardrobe.models;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class GlobalFunctions {
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static String capitalize(String word) {
        word = word.toLowerCase();
        if (word.length() > 1) {
            return String.valueOf(word.charAt(0)).toUpperCase()
                    + word.substring(1);
        }//from   w  w w  .java2 s .co m
        return word;
    }

    public static String getUserName(Context context){
        DBHelper dbHelper = new DBHelper(context);
        Cursor c = dbHelper.getCurrentUser();
        if(c.moveToFirst() && c.getCount()!=0){
            int name = c.getColumnIndex("name");
            return c.getString(name);
        }
        return "User";
    }

    public static String getUserEmail(Context context){
        DBHelper dbHelper = new DBHelper(context);
        Cursor c = dbHelper.getCurrentUser();
        c.moveToFirst();
        int email = c.getColumnIndex("email");
        return c.getString(email);
    }

    public static String getUserPhoneNumber(Context context){
        DBHelper dbHelper = new DBHelper(context);
        Cursor c = dbHelper.getCurrentUser();
        c.moveToFirst();
        int number = c.getColumnIndex("number");
        return c.getString(number);
    }

}