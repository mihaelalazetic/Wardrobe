package com.lazetic.wardrobe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;
import com.lazetic.wardrobe.models.WardrobeCategory;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button login, Reg;
    DBHelper dbHelper;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        email = findViewById(R.id.emailReg);
        password = findViewById(R.id.passwordReg);
        login = (Button) findViewById(R.id.regButton);
        //dummy data go pustame samo pri prvoto pustanje na aplikacijata, koga e prazna bazata
        // sodrzi eden user i nekolku artikli alista za da se simulira garderoba
//        dummyData();
        Intent intent1 = getIntent();
        email.setText(intent1.getStringExtra("email"));

        login.setOnClickListener(view -> {
            String name = dbHelper.loginUser(email.getText().toString(), password.getText().toString(), this, view);
            if (!Objects.equals(name, "")) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
        Reg = findViewById(R.id.logInButton);
        Reg.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    void dummyData(){
        dbHelper.insertUserData("Mihaela Lazetic","miha@email.com","123","+38970000000");

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.shirt);
        icon = Bitmap.createScaledBitmap(icon, (int) (icon.getWidth() * 0.5), (int) (icon.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("T-Shirt", "Red", WardrobeCategory.TOP.getName(), GlobalFunctions.getBytes(icon), this);

        Bitmap icon2 = BitmapFactory.decodeResource(getResources(), R.mipmap.a1);
        icon2 = Bitmap.createScaledBitmap(icon2, (int) (icon2.getWidth() * 0.5), (int) (icon2.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("Purse", "Blue", WardrobeCategory.ACCESSORY.getName(), GlobalFunctions.getBytes(icon2), this);

        Bitmap icon3 = BitmapFactory.decodeResource(getResources(), R.mipmap.a2);
        icon3 = Bitmap.createScaledBitmap(icon3, (int) (icon3.getWidth() * 0.5), (int) (icon3.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("Belt", "Brown", WardrobeCategory.ACCESSORY.getName(), GlobalFunctions.getBytes(icon3), this);

        Bitmap icon4 = BitmapFactory.decodeResource(getResources(), R.mipmap.b1);
        icon4 = Bitmap.createScaledBitmap(icon4, (int) (icon4.getWidth() * 0.5), (int) (icon4.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("T-Shirt", "Red", WardrobeCategory.BOTTOM.getName(), GlobalFunctions.getBytes(icon4), this);


        Bitmap icon5 = BitmapFactory.decodeResource(getResources(), R.mipmap.b2);
        icon5 = Bitmap.createScaledBitmap(icon5, (int) (icon5.getWidth() * 0.5), (int) (icon5.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("Beige Pants", "Beige", WardrobeCategory.BOTTOM.getName(), GlobalFunctions.getBytes(icon5), this);

        Bitmap icon6 = BitmapFactory.decodeResource(getResources(), R.mipmap.b3);
        icon6 = Bitmap.createScaledBitmap(icon6, (int) (icon6.getWidth() * 0.5), (int) (icon6.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("Jeans", "Denim", WardrobeCategory.BOTTOM.getName(), GlobalFunctions.getBytes(icon6), this);

        Bitmap icon7 = BitmapFactory.decodeResource(getResources(), R.mipmap.s1);
        icon7 = Bitmap.createScaledBitmap(icon7, (int) (icon7.getWidth() * 0.5), (int) (icon7.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("Cream Heels", "Beige", WardrobeCategory.SHOE.getName(), GlobalFunctions.getBytes(icon7), this);

        Bitmap icon12 = BitmapFactory.decodeResource(getResources(), R.mipmap.s2);
        icon12 = Bitmap.createScaledBitmap(icon12, (int) (icon12.getWidth() * 0.5), (int) (icon12.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("Addidas white red", "White", WardrobeCategory.SHOE.getName(), GlobalFunctions.getBytes(icon12), this);

        Bitmap icon8 = BitmapFactory.decodeResource(getResources(), R.mipmap.s3);
        icon8 = Bitmap.createScaledBitmap(icon8, (int) (icon8.getWidth() * 0.5), (int) (icon8.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("Converse low", "White", WardrobeCategory.SHOE.getName(), GlobalFunctions.getBytes(icon8), this);

        Bitmap icon9 = BitmapFactory.decodeResource(getResources(), R.mipmap.top);
        icon9 = Bitmap.createScaledBitmap(icon9, (int) (icon9.getWidth() * 0.5), (int) (icon9.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("Brownish-beige tank top", "Beige", WardrobeCategory.TOP.getName(), GlobalFunctions.getBytes(icon9), this);

        Bitmap icon10 = BitmapFactory.decodeResource(getResources(), R.mipmap.top2);
        icon10 = Bitmap.createScaledBitmap(icon10, (int) (icon10.getWidth() * 0.5), (int) (icon10.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("White crop top", "White", WardrobeCategory.TOP.getName(), GlobalFunctions.getBytes(icon10), this);

        Bitmap icon11 = BitmapFactory.decodeResource(getResources(), R.mipmap.top3);
        icon11 = Bitmap.createScaledBitmap(icon11, (int) (icon11.getWidth() * 0.5), (int) (icon11.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("Quarter sleeve pink blouse", "Pink", WardrobeCategory.TOP.getName(), GlobalFunctions.getBytes(icon11), this);

        Bitmap icon13 = BitmapFactory.decodeResource(getResources(), R.mipmap.b4);
        icon13 = Bitmap.createScaledBitmap(icon13, (int) (icon13.getWidth() * 0.5), (int) (icon13.getHeight() * 0.5), true);
        dbHelper.insertWardrobe("Flannel Skirt", "Other", WardrobeCategory.BOTTOM.getName(), GlobalFunctions.getBytes(icon13), this);
    }

}