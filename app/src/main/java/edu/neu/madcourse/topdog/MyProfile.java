package edu.neu.madcourse.topdog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

/**
 * NOTES: Need to tweak logic so that selected picture stays and doesnt reset everytime user exits
 * and re-enters in myProfile.
 *
 * Currently: it resets image selected once we leave the page.
 *
 * Created save button for saving changes - need to figure out how to link it to all activities on page
 */
public class MyProfile extends AppCompatActivity {

    String username;
    ImageView selectedImage;
    Button mGalleryBtn;
    Button mCameraBtn;
    String currentPhotoPath;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQ_CODE = 102;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        username = getIntent().getStringExtra(MainActivity.USERKEY);

        // views
        selectedImage = findViewById(R.id.image_view);
        mGalleryBtn = findViewById(R.id.galleryBtn);
        mCameraBtn = findViewById(R.id.cameraBtn);

        mGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        // permission denied, request it until accessed?
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        // show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        // permission already granted
                        pickImageFromGallery();
                    }
                } else {
                    // system os is less than marshmallow
                    pickImageFromGallery();
                }

            }
        });

        mCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ask for runtime permission to use camera
                askCameraPermission();
            }
        });
    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }
        else {
            openCamera();
        }
    }

    private void openCamera() {
        // Toast.makeText(this, "Camera Open Request", Toast.LENGTH_SHORT).show();
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // opens default camera on phone
        startActivityForResult(camera, CAMERA_REQ_CODE);
    }

    private void pickImageFromGallery() {
        // intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    // handle result of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //dispatchTakePictureIntent();
                openCamera();
            }
        }
        else if (requestCode == IMAGE_PICK_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            }
        }
        else {
            // permission was denied
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
//        switch (requestCode) {
//            case PERMISSION_CODE:{
//                if (grantResults.length > 0 && grantResults[0] ==
//                        PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted
//                    pickImageFromGallery();
//                }

//            }
//        }
    }

//    private void dispatchTakePictureIntent() {
//    Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//    // ensure there is camera activity to handle intent
//        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
//            // create file where photo should go
//            File photoFile = null; // instantiate empty
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // error occurred while creating file
//            }
//            // if photo file is created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
//                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePicIntent, CAMERA_REQ_CODE);
//            }
//        }
//    }

    private File createImageFile() throws IOException {
        // create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // save a file - path for use with action view intents
        currentPhotoPath = image.getAbsolutePath();
        return image;

    }

    // handle result of picked image

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQ_CODE || requestCode == PERMISSION_CODE) {
            // set image to image view
            //mImageView.setImageURI(data.getData());
            Bitmap image = (Bitmap) data.getExtras().get("data");
            selectedImage.setImageBitmap(image);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY,username);
        startActivity(intent);
    }

}
