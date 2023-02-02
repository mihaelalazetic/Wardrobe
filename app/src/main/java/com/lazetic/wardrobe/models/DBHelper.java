package com.lazetic.wardrobe.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.lazetic.wardrobe.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "UserData", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table UserDetails(email TEXT primary key,name TEXT,password PASSWORD,number NUMBER)");
        DB.execSQL("create Table Wardrobe(id INTEGER primary key autoincrement ,name TEXT ,color TEXT,category TEXT, image BLOB)");
        // TODO
        DB.execSQL("create Table History(id INTEGER primary key autoincrement,date TEXT, top INTEGER, topImg BLOB, bottom INTEGER, bottomImg BLOB,shoe INTEGER,shoeImg BLOB, accessory INTEGER, accessoryImg BLOB, image BLOB)");
        DB.execSQL("create Table Favorites(id INTEGER primary key autoincrement,combination INTEGER,name TEXT, date TEXT)");
        DB.execSQL("create Table CurrentUser(id INTEGER  primary key, email TEXT, name TEXT,number NUMBER, password TEXT)");
        DB.execSQL("create Table Posts(id INTEGER primary key,outfit_name TEXT,location_name TEXT,image BLOB, lat REAL, lng REAL,date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists UserDetails");
        DB.execSQL("drop Table if exists Wardrobe");
        DB.execSQL("drop Table if exists History");
        DB.execSQL("drop Table if exists Favorites");
        DB.execSQL("drop Table if exists CurrentUser");
        DB.execSQL("drop Table if exists Posts");
    }


    public void insertCurrentUser(String name, String email, String number, String password) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("name", name);
        contentValues.put("number", number);
        contentValues.put("password", password);
        DB.insert("CurrentUser", null, contentValues);
    }

    public Cursor getCurrentUser() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from CurrentUser ", null);
    }

    public boolean deleteCurrentUser(String email) {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.delete("Wardrobe", "email=?", new String[]{email}) > 0;
    }

    public Boolean insertUserData(String name, String email, String password, String number) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("name", name);
        contentValues.put("password", password);
        contentValues.put("number", number);
        long result = DB.insert("UserDetails", null, contentValues);
        return result != -1;
    }

    public Cursor getData() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from Userdetails ", null);
    }

    public Cursor getAllWardrobe() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from Wardrobe", null);
    }


    public boolean deleteWardrobeById(int id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.delete("Wardrobe", "id=?", new String[]{Integer.toString(id)}) > 0;
    }

    public Boolean insertWardrobe(String name, String color, String category, byte[] image, Context context) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("color", color);
        contentValues.put("name", name);
        contentValues.put("category", category);
        contentValues.put("image", image);
        long result;
        if (getWardrobeByName1(name, DB).getCount() <= 0) {
            result = DB.insert("Wardrobe", null, contentValues);
            Toast.makeText(context, "Article " + name + " added successfully!", Toast.LENGTH_SHORT).show();
        } else {
            result = -1;
            Toast.makeText(context, "Article " + name + " already exists!", Toast.LENGTH_SHORT).show();
        }
        return result != -1;
    }

    public Cursor getWardrobe() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from Wardrobe ORDER BY id DESC LIMIT 4", null);
    }

    public Cursor getWardrobeByName1(String name, SQLiteDatabase DB) {
        return DB.rawQuery("Select * from Wardrobe WHERE name = '" + name + "'", null);
    }

    public Cursor getWardrobeByName(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from Wardrobe WHERE name = '" + name + "'", null);
    }

    public Cursor getWardrobeById(int id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from Wardrobe WHERE id = '" + id + "'", null);
    }

    public Cursor getWardrobeByCategory(String category) {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from Wardrobe WHERE category = '" + category + "';", null);
    }

    public Boolean insertCombination(int top, int bottom, int shoe, int accessory, byte[] topImg, byte[] bottomImg, byte[] shoeImg, byte[] accessoryImg, byte[] image) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("top", top);
        contentValues.put("bottom", bottom);
        contentValues.put("shoe", shoe);
        contentValues.put("accessory", accessory);
        contentValues.put("topImg", topImg);
        contentValues.put("bottomImg", bottomImg);
        contentValues.put("shoeImg", shoeImg);
        contentValues.put("accessoryImg", accessoryImg);
        contentValues.put("image", image);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        contentValues.put("date", dateFormat.format(date));
        long result = DB.insert("History", null, contentValues);
        return result != -1;
    }

    public Cursor getCombination(int id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from History WHERE id = " + id + ";", null);
    }

    public Cursor getAllFavorites() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor c = DB.rawQuery("Select * from Favorites ORDER BY id DESC", null);
        c.moveToFirst();
        List<Integer> ids = new ArrayList<>();
        StringBuilder sql = new StringBuilder("Select * from Favorites ORDER BY ID DESC");
        if (c.getCount() > 0) {
            sql = new StringBuilder("SELECT * FROM History ");
            int id = c.getColumnIndex("combination");
            do {
                ids.add(c.getInt(id));
            } while (c.moveToNext());
            c.close();
            sql.append("WHERE id IN (");
            for (Integer i : ids) {
                sql.append(i).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") ORDER BY id DESC");
        }
        return DB.rawQuery(String.valueOf(sql), null);
    }

    public Cursor getAllFavoritesNames() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from Favorites ORDER BY id DESC", null);
    }

    public Cursor getJustFavorites() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from Favorites ORDER BY id DESC", null);
    }

    public Cursor getAllHistory() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from History", null);
    }

    public byte[] getHistoryImageByFavName(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor c1 = DB.rawQuery("Select combination from Favorites WHERE name='" + name + "'", null);
        int id;
        if (c1.moveToFirst()) {
            int idd = c1.getColumnIndex("combination");
            id = c1.getInt(idd);
            c1.close();
            Cursor c = DB.rawQuery("Select image from History WHERE id='" + id + "'", null);
            if (c.moveToFirst()) {
                int image = c.getColumnIndex("image");
                byte[] bytes = c.getBlob(image);
                c.close();
                return bytes;
            }
            c.close();
        }
        return new byte[]{};
    }

    public Cursor getHistory() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from History ORDER BY id DESC LIMIT 4", null);
    }

    public Cursor getFavorites4() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor c = DB.rawQuery("Select * from Favorites LIMIT 4", null);
        c.moveToFirst();
        List<Integer> ids = new ArrayList<>();
        StringBuilder sql = new StringBuilder("Select * from Favorites LIMIT 4");
        if (c.getCount() > 0) {
            sql = new StringBuilder("SELECT * FROM History ");
            int id = c.getColumnIndex("combination");
            do {
                ids.add(c.getInt(id));
            } while (c.moveToNext());
            c.close();
            sql.append("WHERE id IN (");
            for (Integer i : ids) {
                sql.append(i).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") ORDER BY id DESC");
        }
        return DB.rawQuery(String.valueOf(sql), null);
    }


    public Boolean insertFavorite(String name, View view, DialogInterface.OnClickListener context) {
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = 0;
        ContentValues contentValues = new ContentValues();
        Cursor c = DB.rawQuery("SELECT * FROM History ORDER BY id DESC LIMIT 1", null);
        c.moveToFirst();
        int id = c.getColumnIndex("id");
        contentValues.put("combination", c.getInt(id));
        Cursor c2 = DB.rawQuery("SELECT * FROM Favorites WHERE name ='" + name + "'", null);
        ;
        if (!c2.moveToFirst()) {
            contentValues.put("name", name);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            contentValues.put("date", dateFormat.format(date));
            c.close();
            result = DB.insert("Favorites", null, contentValues);
        } else {
            Snackbar snackbar = Snackbar.make(view, "Name " + name + " already exists! Pick a new name!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor((Context) context, R.color.Red));
            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.warning, 0, 0, 0);
            textView.setCompoundDrawablePadding(view.getResources().getDimensionPixelOffset(R.dimen.fab_margin));
            snackbar.setAction("Action", null).show();
        }
        c2.close();
        return result != -1;
    }

    public boolean insertPost(String outfit_name, String location_name, byte[] image, double lat, double lng) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("location_name", location_name);
        contentValues.put("outfit_name", outfit_name);
        contentValues.put("image", image);
        contentValues.put("lat", lat);
        contentValues.put("lng", lng);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        contentValues.put("date", dateFormat.format(date));
        long result = DB.insert("Posts", null, contentValues);
        return result != -1;
    }

    public Cursor getAllPosts() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from Posts ORDER BY id DESC", null);
    }

    public boolean deleteFavorite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Favorites", "id=?", new String[]{Integer.toString(id)}) > 0;

    }

    public String loginUser(String email, String password, Context context, View view) {
        SQLiteDatabase db = this.getWritableDatabase();
        String name;
        Cursor cursor = db.rawQuery("SELECT * from Userdetails WHERE email LIKE '%" + email + "%' AND password LIKE '" + password + "'", null);
        if (cursor.moveToFirst()) {
            Snackbar snackbar = Snackbar.make(view, "Successful login!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.Green));
            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
            textView.setCompoundDrawablePadding(view.getResources().getDimensionPixelOffset(R.dimen.fab_margin));
            snackbar.setAction("Action", null).show();
//            Toast.makeText(context, "Successful login!", Toast.LENGTH_LONG).show();
            int n = cursor.getColumnIndex("name");
            name = cursor.getString(n);
            int phone = cursor.getColumnIndex("number");
            int mail = cursor.getColumnIndex("email");
            insertCurrentUser(name, cursor.getString(mail), cursor.getString(phone), password);
            cursor.close();
        } else {
            Snackbar snackbar = Snackbar.make(view, "Wrong email or password! Try again!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.Red));
            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.warning, 0, 0, 0);
            textView.setCompoundDrawablePadding(view.getResources().getDimensionPixelOffset(R.dimen.fab_margin));
            snackbar.setAction("Action", null).show();
//            Toast.makeText(context, "Wrong email or password! Try again!", Toast.LENGTH_LONG).show();
            name = "";
        }
        return name;
    }

    public boolean logout() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from CurrentUser ", null);
        c.moveToFirst();
        int email = c.getColumnIndex("email");
        String e = c.getString(email);
        c.close();
        return db.delete("CurrentUser", "email=?", new String[]{e}) > 0;
    }

    public String changePassword(String email, String oldPass, String newPass, String newPassRepeat) {
        String currentPass = getCurrentPassword();
        if (!Objects.equals(newPass, newPassRepeat)) {
            return "match";
        } else if (Objects.equals(newPass, oldPass)) {
            return "old same";
        } else if (oldPass.equals("")) {
            return "old null";
        } else if (newPass.equals("")) {
            return "new null";
        } else if (newPassRepeat.equals("")) {
            return "conf null";
        } else if (!currentPass.equals(oldPass)) {
            return "current not old";
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("password", newPass);
            db.update("CurrentUser", cv, "id=?", new String[]{Integer.toString(0)});
            db.update("UserDetails", cv, "email=?", new String[]{email});
            return "ok";
        }
    }

    public String changeName(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        long result = db.update("CurrentUser", cv, "id=?", new String[]{Integer.toString(0)});
        long result1 = db.update("UserDetails", cv, "email=?", new String[]{email});
        if (result1 != -1 && result != -1) {
            return "ok";
        } else {
            return "no";
        }

    }

    public String changeNumber(String number, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("number", number);
        long result = db.update("CurrentUser", cv, "id=?", new String[]{Integer.toString(0)});
        long result1 = db.update("UserDetails", cv, "email=?", new String[]{email});
        if (result1 != -1 && result != -1) {
            return "ok";
        } else {
            return "no";
        }
    }

    public String changeEmail(String newEmail, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("email", newEmail);
        long result = db.update("CurrentUser", cv, "id=?", new String[]{Integer.toString(0)});
        long result1 = db.update("UserDetails", cv, "email=?", new String[]{email});
        if (result1 != -1 && result != -1) {
            return "ok";
        } else {
            return "no";
        }
    }

    private String getCurrentPassword() {
        Cursor c = getCurrentUser();
        c.moveToFirst();
        int password = c.getColumnIndex("password");
        return c.getString(password);
    }

    public Cursor wardrobeQuery(String search){
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("Select * from Wardrobe WHERE name LIKE '"+search+"%' OR name LIKE '%"+search+"' OR name LIKE '%"+search+"%'", null);
    }
}