package com.example.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class GalleryPictureActivity extends AppCompatActivity
{
    private String[]        FilePathStrings;
    private File[]          listFile;
    GridView grid;
    GridViewAdapter         adapter;
    File                    file;
    public static Bitmap bmp = null;
    ImageView imageview;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_picture);
        // Check for SD Card
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this, "Error! No SDCARD Found!",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            // Locate the image folder in your SD Card
            file = new File(String.valueOf(getExternalFilesDir(null))+"/aa/");
        }
        if (file.isDirectory())
        {
            listFile = file.listFiles();
            FilePathStrings = new String[listFile.length];
            for (int i = 0; i < listFile.length; i++)
            {
                FilePathStrings[i] = listFile[i].getAbsolutePath();
            }
        }
        grid = (GridView)findViewById(R.id.gridview);
        adapter = new GridViewAdapter(this, FilePathStrings);
        grid.setAdapter(adapter);



        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view,
                                     int position, long id)
            {

//                imageview = (ImageView)findViewById(R.id.imageView1);
//                int targetWidth = 700;
//                int targetHeight = 500;
//                BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
//                bmpOptions.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(FilePathStrings[position],
//                        bmpOptions);
//                int currHeight = bmpOptions.outHeight;
//                int currWidth = bmpOptions.outWidth;
//                int sampleSize = 1;
//                if (currHeight > targetHeight || currWidth > targetWidth)
//                {
//                    if (currWidth > currHeight)
//                        sampleSize = Math.round((float)currHeight
//                                / (float)targetHeight);
//                    else
//                        sampleSize = Math.round((float)currWidth
//                                / (float)targetWidth);
//                }
//                bmpOptions.inSampleSize = sampleSize;
//                bmpOptions.inJustDecodeBounds = false;
//                bmp = BitmapFactory.decodeFile(FilePathStrings[position],
//                        bmpOptions);
//                imageview.setImageBitmap(bmp);
//                imageview.setScaleType(ImageView.ScaleType.FIT_XY);
//                bmp = null;

            }
        });

    }
}