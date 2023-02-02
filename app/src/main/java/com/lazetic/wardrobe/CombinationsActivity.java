package com.lazetic.wardrobe;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;
import com.lazetic.wardrobe.models.WardrobeCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CombinationsActivity extends AppCompatActivity {
    ImageView top, bottom, shoe, accessory;
    Button generate;
    ImageButton back, heart;
    DBHelper dbHelper;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combinations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Combinations");

        dbHelper = new DBHelper(this);

        generate = findViewById(R.id.generate);
        top = findViewById(R.id.top);
        bottom = findViewById(R.id.bottom);
        shoe = findViewById(R.id.shoe);
        accessory = findViewById(R.id.acc);
        back = findViewById(R.id.back);
        heart = findViewById(R.id.heart);

        back.setOnClickListener(view -> {
            Intent i = new Intent(CombinationsActivity.this, HomeActivity.class);
            startActivity(i);
        });

        heart.setOnClickListener(view1 -> {
            if (heart.getDrawable() == getResources().getDrawable(R.drawable.heart)) {
                heart.setImageResource(R.drawable.heart_o);
            } else  {
                heart.setImageResource(R.drawable.heart);
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Name");
            alert.setMessage("Please insert the name of your outfit");

// Set an EditText view to get user input
            final EditText input = new EditText(this);
            input.setHint("Name");
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dbHelper.insertFavorite(input.getText().toString(),view1,this);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

        });

        generate.setOnClickListener(view -> {
            int topId = getCombo(WardrobeCategory.TOP.getName(), top);
            int bottomId = getCombo(WardrobeCategory.BOTTOM.getName(), bottom);
            int shoeId = getCombo(WardrobeCategory.SHOE.getName(), shoe);
            int accessoryId = getCombo(WardrobeCategory.ACCESSORY.getName(), accessory);
            Bitmap bm = overlay(Bitmap.createScaledBitmap(GlobalFunctions.getImage(getImageFromCombo(topId)), 220, 240, false),
                    Bitmap.createScaledBitmap(GlobalFunctions.getImage(getImageFromCombo(bottomId)), 220, 240, false),
                    Bitmap.createScaledBitmap(GlobalFunctions.getImage(getImageFromCombo(accessoryId)), 220, 240, false),
                    Bitmap.createScaledBitmap(GlobalFunctions.getImage(getImageFromCombo(shoeId)), 220, 240, false));
            dbHelper.insertCombination(topId, bottomId, shoeId, accessoryId,
                    getImageFromCombo(topId),
                    getImageFromCombo(bottomId),
                    getImageFromCombo(shoeId),
                    getImageFromCombo(accessoryId),
                    GlobalFunctions.getBytes(bm));

            heart.setImageResource(R.drawable.heart_o);

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        String name = GlobalFunctions.getUserName(this);
        switch (item.getItemId())
        {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_favorite:
                intent = new Intent(this, HistoryActivity.class);
                intent.putExtra("what", "f");
                startActivity(intent);
                return true;
            case R.id.action_camera:
                intent = new Intent(this, UploadPhotoActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
                return true;
            case R.id.action_search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                intent = new Intent(this, MainActivity.class);
                String email = GlobalFunctions.getUserEmail(this);
                intent.putExtra("email",email);
                dbHelper.logout();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    int getCombo(String category, ImageView imageView) {
        int theId;
        Random rand = new Random();
        List<Integer> ids = new ArrayList<>();
        Cursor cursor = dbHelper.getWardrobeByCategory(category);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            int id = cursor.getColumnIndex("id");
            do {
//                System.out.println(cursor.getInt(id));
                ids.add(cursor.getInt(id));
            } while (cursor.moveToNext());
            theId = ids.get(rand.nextInt(ids.size()));
            Cursor c = dbHelper.getWardrobeById(theId);
            c.moveToFirst();
            int image = c.getColumnIndex("image");
            Bitmap bitmap = GlobalFunctions.getImage(c.getBlob(image));
            imageView.setImageBitmap(bitmap);
            c.close();
            return theId;
        }
        cursor.close();
        return -1;
    }

    byte[] getImageFromCombo(int id) {
        Cursor one = dbHelper.getWardrobeById(id);
        one.moveToFirst();
        int image = one.getColumnIndex("image");
        byte[] im = one.getBlob(image);
        one.close();
        return im;
    }

    public static Bitmap overlay(Bitmap firstImage, Bitmap secondImage, Bitmap thirdImage, Bitmap fourthImage) {

        List<Integer> h = new ArrayList<>(Arrays.asList(firstImage.getHeight(), secondImage.getHeight(),thirdImage.getHeight(),fourthImage.getHeight()));
        List<Integer> w = new ArrayList<>(Arrays.asList(firstImage.getWidth(), secondImage.getWidth(),thirdImage.getWidth(),fourthImage.getWidth()));

        Bitmap result = Bitmap.createBitmap((Collections.max(w)*2)+20, (Collections.max(h)*2)+20, firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, firstImage.getWidth() + 10, 0f, null);
        canvas.drawBitmap(thirdImage, 0f, firstImage.getHeight() + 10, null);
        canvas.drawBitmap(fourthImage, thirdImage.getWidth() + 10, secondImage.getHeight() + 10, null);

        return result;
    }
}