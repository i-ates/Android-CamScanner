package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import static com.example.myapplication.MainActivity.uriList;

public class deneme2 extends AppCompatActivity {
    public String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deneme2);
        ImageView imageView = findViewById(R.id.imageView);


        path= getExternalFilesDir(null)+"/aa/"+"/ScreenShot"+(MainActivity.PHOTO_COUNT-1+".jpg");
        File imgFile= new File(path);
        if(imgFile.exists())
        {
            imageView.setImageURI(Uri.fromFile(imgFile));

        }
    }
    public void cancelButton(View Button){
        finish();
    }
    public void doneButton(View Button){
        uriList.add(path);
//        for (int i=0; i<uriList.size();i++){
//            Toast.makeText(this,uriList.get(i),Toast.LENGTH_SHORT).show();
//        }
//        Toast.makeText(this,""+MainActivity.PHOTO_COUNT,Toast.LENGTH_SHORT).show();
        finish();

    }

}