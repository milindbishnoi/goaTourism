package com.example.milind.goatourism;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private StorageReference StorageRef;
    private File currentPhotoPath;
    private FirebaseDatabase database;
    private static final int CAMERA_REQUEST = 1888;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image;
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this,ex.toString(),Toast.LENGTH_SHORT);
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }
    Button button;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       button =findViewById(R.id.camera1);
       imageView=findViewById(R.id.img);
       database = FirebaseDatabase.getInstance();
        StorageRef = FirebaseStorage.getInstance().getReference();
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dispatchTakePictureIntent();
           }
       });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri file = Uri.fromFile(currentPhotoPath);
        StorageReference riversRef = StorageRef.child("images");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                       // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        addToFireBaseDB(currentPhotoPath.getAbsolutePath(),taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                        Toast.makeText(MainActivity.this, "avjkafka", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(MainActivity.this, exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void addToFireBaseDB(String ImageId , String downloadUrl )
    {
        DatabaseReference myRef = database.getReference();
        //ImagesToUpload image = new ImagesToUpload(ImageId, downloadUrl);
        //myRef.setValue(image);
        myRef.child("images").child(ImageId.substring(0,12)).setValue(downloadUrl);

    }
}
