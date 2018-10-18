package com.example.mariliatmf.grayscale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    Bitmap mbitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast.makeText(this, "ERROR2", Toast.LENGTH_SHORT);
        dispatchTakePictureIntent();

        Button photoButton = (Button) findViewById(R.id.button_photo);
        photoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        final ImageView myImage = (ImageView) findViewById(R.id.pro);
        Button myButton = (Button) findViewById(R.id.button_gray);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myImage.setImageBitmap(grayScale(mbitmap));
            }
        });

        Button saveButton = (Button) findViewById(R.id.button_save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ImageView myImage = (ImageView) findViewById(R.id.pro);
                myImage.buildDrawingCache();
                Bitmap bm=myImage.getDrawingCache();
                OutputStream fOut = null;
                Uri outputFileUri;
                try {
                    File root = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            + File.separator + "Saved" + File.separator);
                    root.mkdirs();
                    Random gen = new Random();
                    int n = 10000;
                    n = gen.nextInt(n);
                    String fotoname = "Photo-"+ n +".jpg";
                    File sdImageMainDirectory = new File(root, fotoname);
                    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                    fOut = new FileOutputStream(sdImageMainDirectory);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error occured. Please try again later.",
                            Toast.LENGTH_SHORT).show();
                }
                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                }

                Toast.makeText(MainActivity.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();
                myImage.destroyDrawingCache();
            }
        });
    }

    String mCurrentPhotoPath;

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
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Toast.makeText(this, "ERROR1", Toast.LENGTH_SHORT);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT);
            }
            // Continue only if the File was successfully created
            //Log.v("AAAAA","ska");
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.mariliatmf.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gray();
    }

    /*private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }*/

    private void gray() {
        File imgFile = new File(mCurrentPhotoPath);
        if (imgFile.exists()) {
            //Log.v("AAAAA","ska");
            mbitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = (ImageView) findViewById(R.id.pro);
            myImage.setImageBitmap(mbitmap);
        }
    }

    private  Bitmap grayScale(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap bitmapResult = Bitmap.createBitmap(width, height, bitmap.getConfig());

        int A,R,G,B;
        int pixel;
        for(int x=0; x<width; x++){
            for(int y=0; y<height; y++){
                pixel = bitmap.getPixel(x,y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                int gray = (int) (0.299*R + 0.587*G + 0.114*B);
                bitmapResult.setPixel(x,y,Color.argb(A, gray, gray, gray));
            }
        }
        return bitmapResult;
    }


    /*private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }*/

}
