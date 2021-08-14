package edu.neu.madcourse.topdog;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



import edu.neu.madcourse.topdog.DatabaseObjects.FetchDBUserUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.User;

/**
 * NOTES: Need to tweak logic so that selected picture stays and doesnt reset everytime user exits
 * and re-enters in myProfile.
 *
 * Currently: it resets image selected once we leave the page.
 *
 * Created save button for saving changes - need to figure out how to link it to all activities on page
 */
public class MyProfile extends AppCompatActivity {

    private String username;
    private ImageView selectedImage;
    public static final int GALLERY_REQUEST_CODE = 105;
    StorageReference storageReference, imageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        username = getIntent().getStringExtra(MainActivity.USERKEY);
        storageReference = FirebaseStorage.getInstance().getReference().child("pictures/");
        imageReference = FirebaseStorage.getInstance().getReference().child("pictures/" + username + ".jpg");

        try {
            final File localFile = File.createTempFile("JPEG_", "jpg");
            imageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                selectedImage.setImageBitmap(bitmap);
            }).addOnFailureListener(e -> {

            });

        } catch (IOException e) {
            e.printStackTrace();
        }


        //Populate the user profile data from the database
        TextView enterPetName = findViewById(R.id.enterPetName);
        TextView enterPetAge = findViewById(R.id.enterPetAge_et);

        User user = new FetchDBUserUtil().getUser(username);

        enterPetName.setText(user.getDogName());
        enterPetAge.setText(user.getDogAge());


        // views
        selectedImage = findViewById(R.id.image_view);
        Button mGalleryBtn = findViewById(R.id.galleryBtn);
        Button mSaveBtn = findViewById(R.id.saveEdits_btn);

        // onclick listeners for buttons
        mGalleryBtn.setOnClickListener(v -> {
            try {
                if (ActivityCompat.checkSelfPermission(MyProfile.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyProfile.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
                } else {
                    oGallery();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mSaveBtn.setOnClickListener(v -> {
            storageReference = FirebaseStorage.getInstance().getReference().child("pictures/" + username + ".jgp");

            try {
                final File localFile = File.createTempFile("JPEG_", "jpg");
                storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(MyProfile.this,
                            "Save successful", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    selectedImage.setImageBitmap(bitmap);
                }).addOnFailureListener(e -> Toast.makeText(MyProfile.this,
                        "Picture in profile saved", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    //Methods for handling the go Home functionality in the menu bar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_homepage, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.homepage) {
            Intent intent = new Intent(this, HomePage.class);
            intent.putExtra(MainActivity.USERKEY, username);
            startActivity(intent);
        }
        return true;
    }
    ////////////////////////////////////////////////////////////

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0,0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void oGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Pick an Image"), GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        storageReference = FirebaseStorage.getInstance().getReference();
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri contentUri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(contentUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    selectedImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String imageFileName = username + "." + getFileExt(contentUri);
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
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }

}
