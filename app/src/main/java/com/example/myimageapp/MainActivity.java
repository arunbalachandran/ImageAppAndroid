package com.example.myimageapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 123;
    static final int REQUEST_MULTIPLE_PHOTOS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.CameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        findViewById(R.id.GalleryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_MULTIPLE_PHOTOS);
            }
        });
    }

    private void captureImage() {
        // create intent for photo capture
        Intent takePictures = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictures.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictures, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            System.out.println(extras.get("data"));
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String path = "ImageApp";
//            System.out.println(Environment.getExternalStorageDirectory().toString(), path);
            File file = new File(Environment.getExternalStorageDirectory().toString(), path);

            if (!file.exists()) {
                file.mkdirs();
                System.out.println("Created a new directory");
            }

            OutputStream outputStream;
            long counter = System.currentTimeMillis();
            File f = new File(file, "GalleryApp_" + counter + ".jpg"); // increasing numeric counter to prevent files from getting overwritten.
            try {
                outputStream = new FileOutputStream(f);
                if (imageBitmap != null) {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream); // saving the Bitmap to JPG at 80% compression rate
                }
                outputStream.flush();
                outputStream.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_MULTIPLE_PHOTOS && resultCode == RESULT_OK) {
            if (data.getClipData() != null)
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    System.out.println(uri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            else System.out.println(data.getData().getPath());
        }
    }

}
