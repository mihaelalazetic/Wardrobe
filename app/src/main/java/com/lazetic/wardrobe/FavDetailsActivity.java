package com.lazetic.wardrobe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;

import java.util.Objects;

public class FavDetailsActivity extends AppCompatActivity {

    ImageView top, bottom, shoe, acc;
    TextView date;
    ImageButton back, delete;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_details);

        String name = GlobalFunctions.getUserName(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(name + "'s Favorites");

        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        byte[] timg = intent.getByteArrayExtra("top");
        byte[] bimg = intent.getByteArrayExtra("bottom");
        byte[] simg = intent.getByteArrayExtra("shoe");
        byte[] aimg = intent.getByteArrayExtra("acc");
        String date1 = intent.getStringExtra("date");
        int id = intent.getIntExtra("id", -1);

        top = findViewById(R.id.topPic);
        bottom = findViewById(R.id.bottomPic);
        shoe = findViewById(R.id.shoePic);
        acc = findViewById(R.id.accPic);
        date = findViewById(R.id.date);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);

        top.setImageBitmap(GlobalFunctions.getImage(timg));
        bottom.setImageBitmap(GlobalFunctions.getImage(bimg));
        shoe.setImageBitmap(GlobalFunctions.getImage(simg));
        acc.setImageBitmap(GlobalFunctions.getImage(aimg));
        date.setText(date1);

        back.setOnClickListener(view -> {
            Intent i = new Intent(FavDetailsActivity.this, HistoryActivity.class);
            i.putExtra("what", "f");
            startActivity(i);
        });

        delete.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Are you sure?");
            final TextView input = new TextView(this);
            alert.setView(input);

            alert.setPositiveButton("YES", (dialog, whichButton) -> {
                dbHelper.deleteFavorite(id);
                Intent i = new Intent(FavDetailsActivity.this, HistoryActivity.class);
                i.putExtra("what", "f");
                startActivity(i);
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

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
        switch (item.getItemId()) {
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
                intent.putExtra("email", email);
                dbHelper.logout();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}