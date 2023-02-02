package com.lazetic.wardrobe;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.lazetic.wardrobe.models.DBHelper;
import com.lazetic.wardrobe.models.GlobalFunctions;
import com.lazetic.wardrobe.models.WardrobeCategory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UploadPhotoActivity extends AppCompatActivity {

    ImageView viewImage;
    Button uploadPhoto, uploadArticle;
    EditText picName;
    Spinner category, color;
    DBHelper dbHelper;
    ImageButton back;

    private static final int REQ_CODE_TAKE_PICTURE = 910;
    private ActivityResultLauncher<Intent> myActivityResultLauncher = null;
    private ActivityResultLauncher<String> mGetContent = null;

    //uste podobar nacin za take picture
    private ActivityResultLauncher<Uri> mGetPicture = null;
    private File file = null;
    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        String name = GlobalFunctions.getUserName(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(name.toString());

        viewImage = findViewById(R.id.top);
        uploadPhoto = findViewById(R.id.uploadPhoto);
        uploadArticle = findViewById(R.id.uploadArticle);
        picName = findViewById(R.id.picName);
        color = findViewById(R.id.picColor);
        category = findViewById(R.id.picCategory);
        back = findViewById(R.id.back);

        back.setOnClickListener(view -> {
            Intent i = new Intent(UploadPhotoActivity.this, HomeActivity.class);
            startActivity(i);
        });

        List<String> myTasksList = WardrobeCategory.getWardrobeCategories();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myTasksList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(dataAdapter);

        List<String> colorList = new ArrayList<>();
        colorList.add("");
        String[] colorNames = getResources().getStringArray(R.array.colorNames);
        for (int i = 0; i < colorNames.length; i++) {
//            TypedArray ta = getResources().obtainTypedArray(R.array.colors);
//            int colorToUse = ta.getResourceId(i, 0);
//            colorList.add(new Pair(colorNames[i],colorToUse));
            colorList.add(colorNames[i]);
        }
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colorList);

        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color.setAdapter(colorAdapter);

        dbHelper = new DBHelper(this);

        uploadPhoto.setOnClickListener(view -> selectImage());

        myActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent intent = result.getData();
                    Bitmap bmp = (Bitmap) intent.getExtras().get("data");
                    viewImage.setImageBitmap(bmp);
                    viewImage.setVisibility(View.VISIBLE);
                    uploadArticle.setOnClickListener(view -> {
                        String picNameS = picName.getText().toString();
                        String colorS = color.getSelectedItem().toString();
                        String categoryS = category.getSelectedItem().toString();
                        if (picNameS.equals("")) {
                            picName.setHint("Please enter article name!");
                            picName.setError("Please enter article name!");
                        } else if (colorS.equals("")) {
                            Toast.makeText(UploadPhotoActivity.this, "Please select article color!!", Toast.LENGTH_SHORT).show();
                        } else if (categoryS.equals("")) {
                            Toast.makeText(UploadPhotoActivity.this, "Please select category!", Toast.LENGTH_SHORT).show();
                        } else if (!hasImage(viewImage)) {
                            viewImage.setVisibility(View.GONE);
                            Toast.makeText(UploadPhotoActivity.this, "Please upload photo!", Toast.LENGTH_SHORT).show();
                        } else {
                            dbHelper.insertWardrobe(picNameS, colorS, categoryS, GlobalFunctions.getBytes(bmp), getBaseContext());
                        }
                        Intent i = new Intent(UploadPhotoActivity.this, UploadPhotoActivity.class);
                        startActivity(i);
                    });
                }
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                viewImage.setImageURI(result);
                viewImage.setVisibility(View.VISIBLE);
                uploadArticle.setOnClickListener(view -> {
                    String picNameS = picName.getText().toString();
                    String colorS = color.getSelectedItem().toString();
                    String categoryS = category.getSelectedItem().toString();
                    if (picNameS.equals("")) {
                        picName.setHint("Please enter article name!");
                        picName.setError("Please enter article name!");
                    } else if (colorS.equals("")) {
                        Toast.makeText(UploadPhotoActivity.this, "Please select article color!", Toast.LENGTH_SHORT).show();
                    } else if (categoryS.equals("")) {
                        Toast.makeText(UploadPhotoActivity.this, "Please select category!", Toast.LENGTH_SHORT).show();
                    } else if (!hasImage(viewImage)) {
                        viewImage.setVisibility(View.GONE);
                        Toast.makeText(UploadPhotoActivity.this, "Please upload photo!", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            dbHelper.insertWardrobe(picNameS, colorS, categoryS, GlobalFunctions.getBytes(getThumbnail(result)), getBaseContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent i = new Intent(UploadPhotoActivity.this, UploadPhotoActivity.class);
                    i.putExtra("name", name);
                    startActivity(i);
                });
            }
        });

        //uste podobar nacin za take picture
        //pokreva camera aplikacija i zema slika sto ja zacuvuva vo temporary file na URI uri so full rezolucija
        try {
            file = File.createTempFile("tmp_image_file", ".png", getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
        mGetPicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    viewImage.setImageURI(uri);
                }
            }
        });


    }

    private void selectImage() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(UploadPhotoActivity.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    captureImage();
                } else if (options[item].equals("Choose from Gallery")) {
                    fromGalery();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_TAKE_PICTURE);
        } else {
            myActivityResultLauncher.launch(intent);
            //deprecated
            //startActivityForResult(intent, REQ_CODE_TAKE_PICTURE);
            //alternativen nacin (za full res slika)
            //mGetPicture.launch(uri);
        }
    }

    public void fromGalery() {
        mGetContent.launch("image/*");
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 250.0) ? (originalSize / 250.0) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
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
}