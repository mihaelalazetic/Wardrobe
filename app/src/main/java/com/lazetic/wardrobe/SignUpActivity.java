package com.lazetic.wardrobe;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lazetic.wardrobe.models.DBHelper;

public class SignUpActivity extends AppCompatActivity {
    Button login, reg;
    DBHelper dbHelper;
    EditText email, password, name, confPass, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dbHelper = new DBHelper(this);

        login = findViewById(R.id.loginReg);
        reg = findViewById(R.id.regButton);

        email = findViewById(R.id.emailReg);
        password = findViewById(R.id.passwordReg);
        name = findViewById(R.id.nameReg);
        confPass = findViewById(R.id.confirmPasswordReg);
        phone = findViewById(R.id.phoneReg);

        reg.setOnClickListener(view -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * from Userdetails WHERE email LIKE '%" + email.getText().toString() + "%'", null);

            if (password.getText().toString().equals(confPass.getText().toString()) && cursor.getCount() == 0) {
                if (dbHelper.insertUserData(name.getText().toString(), email.getText().toString(), password.getText().toString(), phone.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "User " + name.getText().toString() + " successfully registered!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.putExtra("email",email.getText().toString());
                    startActivity(intent);
                }
            } else if (!password.getText().toString().equals(confPass.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
            } else if (cursor.moveToFirst()) {
                Toast.makeText(SignUpActivity.this, "User with email " + email.getText().toString() + " already exists!", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        });

        login.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
            startActivity(intent);
        });
    }
}