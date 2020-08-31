package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.myapplication.MainActivity.PHOTO_COUNT;
import static com.example.myapplication.MainActivity.captureBitmap;
import static com.example.myapplication.MainActivity.globalMat;
import static com.example.myapplication.MainActivity.outputMat;
import static com.example.myapplication.MainActivity.quadrilateral;


public class deneme extends AppCompatActivity {
    public static  Mat img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deneme);


        String path= getExternalFilesDir(null)+"/ScreenShot"+(MainActivity.PHOTO_COUNT-1+".jpg");
        File imgFile= new File(path);
        ImageView imageView = findViewById(R.id.imageView);


        if(imgFile.exists())
        {
            imageView.setImageURI(Uri.fromFile(imgFile));

        }
        img= Imgcodecs.imread(path);

        Core.flip(globalMat.t(),globalMat,1);
        quadrilateral=DetectPaper.findDocument(globalMat);

        MovableFloatingActionButton fab = (MovableFloatingActionButton) findViewById(R.id.fab);
        MovableFloatingActionButton fab1 = (MovableFloatingActionButton) findViewById(R.id.fab1);
        MovableFloatingActionButton fab2 = (MovableFloatingActionButton) findViewById(R.id.fab2);
        MovableFloatingActionButton fab3 = (MovableFloatingActionButton) findViewById(R.id.fab3);
        //CoordinatorLayout.LayoutParams lp  = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        //fab.setCoordinatorLayout(lp);
        fab.setCordinates(findViewById(R.id.fab),quadrilateral,0);
        fab1.setCordinates(findViewById(R.id.fab1),quadrilateral,1);
        fab2.setCordinates(findViewById(R.id.fab2),quadrilateral,2);
        fab3.setCordinates(findViewById(R.id.fab3),quadrilateral,3);

    }
    public void doPespectiveTranformation(View Button){
        MainActivity.perpectiveTransformation();
        Bitmap bitmap = captureBitmap();
        store(bitmap);
//        Intent intentLoadNewActivity = new Intent(deneme.this,deneme2.class);
//        startActivity(intentLoadNewActivity);
//        Toast.makeText(this,""+PHOTO_COUNT,Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),deneme2.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }


    public void store(Bitmap bm){

        File file1=new File(getExternalFilesDir(null)+"/aa/");
        if (!file1.exists()){
            file1.mkdirs();
        }

        File file= new File(getExternalFilesDir(null)+"/aa/","ScreenShot"+MainActivity.PHOTO_COUNT+".jpg");

        try {
            FileOutputStream fos= new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
            PHOTO_COUNT=PHOTO_COUNT+1;
//            Toast.makeText(this,dirPath,Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"hata1",Toast.LENGTH_SHORT).show();
        }


    }





}