package com.basic.xy.takephoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_THUMBNAIL = 1;
    static final int REQUEST_TAKE_FULL_SIZE_PHOTO = 2;
    private ImageView mImg;
    private Button mThumbnailPhotoBtn,mFullSizePhotoBtn;
    private Uri photoURI;
    private String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        mThumbnailPhotoBtn = (Button) findViewById(R.id.btn_thumbnail);
        mThumbnailPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoAndShowThumbnil();
            }
        });
        mFullSizePhotoBtn = (Button) findViewById(R.id.btn_full_size);
        mFullSizePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoAndShowFullSizePhoto();
            }
        });
        mImg = (ImageView) findViewById(R.id.iv_img);
    }

    private void takePhotoAndShowFullSizePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoFile = createImageFile();
                }
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID+".fileprovider",
                        photoFile);
                Log.i("Uri", photoURI.toString());
                //注意，指定了uri,那么data就没有数据
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_FULL_SIZE_PHOTO);
            }
        }
    }

    private void takePhotoAndShowThumbnil() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent,REQUEST_TAKE_THUMBNAIL);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        } else if (resultCode == RESULT_OK){
            Bitmap imageBitmap = null ;
            switch (requestCode) {
                case REQUEST_TAKE_FULL_SIZE_PHOTO:
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),photoURI);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_TAKE_THUMBNAIL:
                    Bundle bundle = data.getExtras();
                    imageBitmap = (Bitmap)bundle.get("data");
                    break;
            }
            mImg.setImageBitmap(imageBitmap);
        }
    }
}
