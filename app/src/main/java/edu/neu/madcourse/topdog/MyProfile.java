package edu.neu.madcourse.topdog;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

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
    String currentPhotoPath;
    public static final int GALLERY_REQUEST_CODE = 105;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        storageReference = FirebaseStorage.getInstance().getReference();

        username = getIntent().getStringExtra(MainActivity.USERKEY);

        // views
        selectedImage = findViewById(R.id.image_view);
        mGalleryBtn = findViewById(R.id.galleryBtn);

        // onclick listeners for buttons
        mGalleryBtn.setOnClickListener(v -> {
            try {
                if (ActivityCompat.checkSelfPermission(MyProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
                } else {
                    openGallery();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "PNG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri: " + imageFileName);
                selectedImage.setImageURI(contentUri);

                uploadImageToFirebase(imageFileName, contentUri);
            }
        }
    }

    private void uploadImageToFirebase(String name, Uri contentURI) {
        final StorageReference image = storageReference.child("pictures/" + name);
        image.putFile(contentURI).addOnSuccessListener(taskSnapshot -> {
            image.getDownloadUrl().addOnSuccessListener(uri -> Log.d("tag", "onSuccess: Upload Image URl is " + uri.toString()));

            Toast.makeText(MyProfile.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(MyProfile.this, "Upload failed.", Toast.LENGTH_SHORT).show());
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY,username);
        startActivity(intent);
    }

}
