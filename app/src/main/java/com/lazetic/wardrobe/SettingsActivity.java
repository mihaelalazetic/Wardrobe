package com.lazetic.wardrobe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    Button changeName, changePass, changeEmail, changeNumber, logout;
    TextView wholeName, phone, email;
    ImageButton back;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        String name = GlobalFunctions.getUserName(this);
        String mail = GlobalFunctions.getUserEmail(this);
        String number = GlobalFunctions.getUserPhoneNumber(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");

        dbHelper = new DBHelper(this);

        wholeName = findViewById(R.id.wholeName);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        back = findViewById(R.id.back);
        changeName = findViewById(R.id.changeName);
        changeEmail = findViewById(R.id.changeEmail);
        changeNumber = findViewById(R.id.changeNumber);
        changePass = findViewById(R.id.changePassword);
        logout = findViewById(R.id.logout);

        logout.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            String email = GlobalFunctions.getUserEmail(this);
            intent.putExtra("email", email);
            dbHelper.logout();
            startActivity(intent);
        });

        back.setOnClickListener(view -> {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        });

        wholeName.setText(name);
        phone.setText(number);
        email.setText(mail);

        changePass.setOnClickListener(view -> alertPass(view, mail));
        changeName.setOnClickListener(view -> alertName(view, mail, name));
        changeNumber.setOnClickListener(view -> alertPhone(view, mail, number));
        changeEmail.setOnClickListener(view -> alertEmail(view, mail, mail));
    }


    public void alertPass(View view, String email) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Change password");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50, 0, 50, 10);

        EditText oldPass = new EditText(this);
        oldPass.setHint("Old Password");
        oldPass.setPadding(0, 70, 0, 20);

        EditText newPass = new EditText(this);
        newPass.setHint("New Password");
        newPass.setPadding(0, 70, 0, 20);

        EditText confPass = new EditText(this);
        confPass.setHint("Confirm New Password");
        confPass.setPadding(0, 70, 0, 20);

        linearLayout.addView(oldPass);
        linearLayout.addView(newPass);
        linearLayout.addView(confPass);

        alert.setView(linearLayout);

        alert.setPositiveButton("Change password", (dialog, whichButton) -> {
            String result = dbHelper.changePassword(email, oldPass.getText().toString(),
                    newPass.getText().toString(),
                    confPass.getText().toString());
            if (result.equals("old null")) {
                oldPass.setHint("Field old password can't be empty!");
                oldPass.setError("Field old password can't be empty!");
                snack("Field old password can't be empty!", view, R.color.Red, R.drawable.warning);
            } else if (result.equals("new null")) {
                newPass.setHint("Field new password can't be empty!");
                newPass.setError("Field new password can't be empty!");
                snack("Field new password can't be empty!", view, R.color.Red, R.drawable.warning);
            } else if (result.equals("conf null")) {
                confPass.setHint("Field confirm password can't be empty!");
                confPass.setError("Field confirm password can't be empty!");
                snack("Field confirm password can't be empty!", view, R.color.Red, R.drawable.warning);
            } else if (result.equals("current not old")) {
                snack("Old password not correct! Try again.", view, R.color.Red, R.drawable.warning);
            } else if (result.equals("match")) {
                snack("Password doesn't match", view, R.color.Red, R.drawable.warning);
            } else if (result.equals("old same")) {
                snack("New password can't be the same as your old password!", view, R.color.Red, R.drawable.warning);
            } else {
                snack("Password successfully changed!", view, R.color.Green, R.drawable.check);
                Intent intent = new Intent(this, MainActivity.class);
                String email1 = GlobalFunctions.getUserEmail(this);
                intent.putExtra("email", email1);
                dbHelper.logout();
                startActivity(intent);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public void alertName(View view, String email, String name) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Change name");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50, 0, 50, 10);

        EditText newName = new EditText(this);
        newName.setHint("New name");
        newName.setText(name);
        newName.setPadding(0, 70, 0, 20);

        linearLayout.addView(newName);

        alert.setView(linearLayout);

        alert.setPositiveButton("Change name", (dialog, whichButton) -> {
            if (dbHelper.changeName(newName.getText().toString(), email).equals("ok")) {
                snack("Name successfully changed!", view, R.color.Green, R.drawable.check);
                wholeName.setText(newName.getText().toString());
            } else {
                snack("Something went wrong! Try again!", view, R.color.Red, R.drawable.warning);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    public void alertPhone(View view, String email, String num) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Change phone number");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50, 0, 50, 10);

        EditText newNumber = new EditText(this);
        newNumber.setHint("New phone number");
        newNumber.setText(num);
        newNumber.setPadding(0, 70, 0, 20);

        linearLayout.addView(newNumber);

        alert.setView(linearLayout);

        alert.setPositiveButton("Change phone number", (dialog, whichButton) -> {
            if (dbHelper.changeNumber(newNumber.getText().toString(), email).equals("ok")) {
                snack("Phone number successfully changed!", view, R.color.Green, R.drawable.check);
                phone.setText(newNumber.getText().toString());
            } else {
                snack("Something went wrong! Try again!", view, R.color.Red, R.drawable.warning);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public void alertEmail(View view, String email, String mail) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Change email");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50, 0, 50, 10);

        EditText newEmail = new EditText(this);
        newEmail.setHint("New email");
        newEmail.setText(mail);
        newEmail.setPadding(0, 70, 0, 20);

        linearLayout.addView(newEmail);

        alert.setView(linearLayout);

        alert.setPositiveButton("Change email", (dialog, whichButton) -> {
            if (dbHelper.changeEmail(newEmail.getText().toString(), email).equals("ok")) {
                snack("Email successfully changed!", view, R.color.Green, R.drawable.check);
                Intent intent = new Intent(this, MainActivity.class);
                String email1 = GlobalFunctions.getUserEmail(this);
                intent.putExtra("email", email1);
                dbHelper.logout();
                startActivity(intent);
            } else {
                snack("Something went wrong! Try again!", view, R.color.Red, R.drawable.warning);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void snack(String text, View view, int color, int emoji) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getBaseContext(), color));
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(emoji, 0, 0, 0);
        textView.setCompoundDrawablePadding(view.getResources().getDimensionPixelOffset(R.dimen.fab_margin));
        snackbar.setAction("Action", null).show();
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