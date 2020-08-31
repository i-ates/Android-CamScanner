package com.example.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.camera2.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
//AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2

    //frame ve global mat her görüntü alışında güncelleniyor bir tane kullanarak optimize et.

    private  int PERMISSION_CODE =1 ;       //izin alıp almadığını kontrol etmek için
    public static int PHOTO_COUNT = 0;
    public static Mat globalMat;            //ImageProccessing vs işlemlerde kullancağımız Mat
    public static Mat outputMat;
    public static ArrayList<String> uriList= new ArrayList<>();
    Mat inputFrame;                         //Camaera Viewda kullandığımız Mat
    public static DetectPaper.Quadrilateral quadrilateral;  //İçinde tek bir dörtgeni ve köşe noktalarını bulunduran obje
    JavaCameraView cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    private Tutorial3View mOpenCvCameraView;
    private List<Camera.Size> mResolutionList;

    boolean startCanny=false;
    public void Canny(View Button){
        if (!startCanny)
            startCanny=true;
        else
            startCanny= false;
    }
    public void OpenGalery(View Button){
        Intent intentLoadNewActivity = new Intent(MainActivity.this,GalleryPictureActivity.class);
        startActivity(intentLoadNewActivity);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.my_camera);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mOpenCvCameraView.enableFpsMeter();

        //cameraBridgeViewBase.setMaxFrameSize(cameraBridgeViewBase.getLayoutParams().width,cameraBridgeViewBase.getLayoutParams().height);
//        cameraBridgeViewBase.setMinimumWidth(Resources.getSystem().getDisplayMetrics().widthPixels);
//        cameraBridgeViewBase.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch(status){

                    case BaseLoaderCallback.SUCCESS:
                        mOpenCvCameraView.enableView();

                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };

//        mResolutionList = mOpenCvCameraView.getResolutionList();
//        for (int i=0;i<mResolutionList.size();i++){
//            System.out.println("resolution: "+ mResolutionList.get(i));
//        }
        try {
            mResolutionList = mOpenCvCameraView.getResolutionList();
        }catch (Exception e){
            System.out.println("exception handled");

        }


//        System.out.println("size "+mResolutionList.size());


    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        this.inputFrame = inputFrame.rgba();
        //System.out.println(this.inputFrame.width()+"***"+this.inputFrame.height());
        globalMat=this.inputFrame.clone();
        if (startCanny) {
            quadrilateral=DetectPaper.findDocument(this.inputFrame);  //contour ve pointleri bulan fonksiyon
            if (quadrilateral!=null){
                //findocument hareketli bir görüntüden frame frame görüntüleri çekerken dörtgen bulamazsa nullPointer exception ile
                //karşılaşıyor o yüzden objeyi kontrol etmek gerekiyor
                //draw contour bulduğumuz contourları çiziyor
                //Imgproc.drawContours(this.inputFrame,quadrilateral.contour,1,new Scalar(0,255,0),3);
                //Her köşe için pointleri çizdiriyoruz
                for (int i=0;i<quadrilateral.points.length;i++){
                    Imgproc.drawMarker(this.inputFrame,quadrilateral.points[i],new Scalar(255,0,0),Imgproc.MARKER_STAR,16);
                }
            }
        }

        return this.inputFrame;
    }

    public void store(Bitmap bm){
        //Uygulamanın kurulu olduğu dizini buluyoruz
        File file= new File(getExternalFilesDir(null),"ScreenShot"+PHOTO_COUNT+".jpg");
        try {
            FileOutputStream fos= new FileOutputStream(file);
            //aldığımız bitmapı orjinal kalitede yazdırıyoruz
            bm.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
            PHOTO_COUNT=PHOTO_COUNT+1;
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"hataaaa",Toast.LENGTH_SHORT).show();
        }
    }

    public void takePhoto(View Button){
        Bitmap bitmap = captureBitmap(); //Global Mat -> Bitmap
        store(bitmap);
        //Fotoğraf çektikten sonra deneme activity açılıyor /isimleri düzelt
//        Toast.makeText(this,""+PHOTO_COUNT,Toast.LENGTH_SHORT).show();
        Intent intentLoadNewActivity = new Intent(MainActivity.this,deneme.class);
        startActivity(intentLoadNewActivity);
    }

    public static Bitmap captureBitmap( ){
        Core.flip(globalMat.t(),globalMat,1); // Dik ekrandayken Mat 90 derece sağa yatık olduğu için döndürüyoruz

//        for (int i=0;i<quadrilateral.points.length;i++){
//            Imgproc.drawMarker(globalMat,quadrilateral.points[i],new Scalar(255,0,0),Imgproc.MARKER_STAR,16);
//        }
        //İleride bunu çizdirmek yerine butonların kordinatlarını belirlemek için kullanacağız
        Bitmap bitmap= Bitmap.createBitmap(globalMat.width(),globalMat.height(),Bitmap.Config.ARGB_8888);
        try {
            bitmap = Bitmap.createBitmap(globalMat.cols(), globalMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(globalMat, bitmap);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return bitmap;
    }

    public static void perpectiveTransformation(){
        quadrilateral=DetectPaper.findDocument(globalMat); // FAB ve perspective için tekrar point noktalarını buluyoruz
        double ratio = globalMat.size().height / 500;

        Point tl = quadrilateral.points[0];
        Point tr = quadrilateral.points[1];
        Point br = quadrilateral.points[2];
        Point bl = quadrilateral.points[3];

        double widthA = Math.sqrt(Math.pow(br.x - bl.x, 2) + Math.pow(br.y - bl.y, 2));
        double widthB = Math.sqrt(Math.pow(tr.x - tl.x, 2) + Math.pow(tr.y - tl.y, 2));

        double dw = Math.max(widthA, widthB)*ratio;
        int maxWidth = Double.valueOf(dw).intValue();

        double heightA = Math.sqrt(Math.pow(tr.x - br.x, 2) + Math.pow(tr.y - br.y, 2));
        double heightB = Math.sqrt(Math.pow(tl.x - bl.x, 2) + Math.pow(tl.y - bl.y, 2));

        double dh = Math.max(heightA, heightB)*ratio;
        int maxHeight = Double.valueOf(dh).intValue();

        //determining point sets to get the transformation matrix
        List<org.opencv.core.Point> srcPts = new ArrayList<org.opencv.core.Point>();
        srcPts.add(new org.opencv.core.Point((tl.x),tl.y));
        srcPts.add(new org.opencv.core.Point((tr.x),tr.y));
        srcPts.add(new org.opencv.core.Point((br.x),br.y));
        srcPts.add(new org.opencv.core.Point((bl.x),bl.y));

        List<org.opencv.core.Point> dstPoints= new ArrayList<Point>();
        dstPoints.add(new org.opencv.core.Point(0,0));
        dstPoints.add(new org.opencv.core.Point(maxWidth-1,0));
        dstPoints.add(new org.opencv.core.Point(maxWidth-1,maxHeight-1));
        dstPoints.add(new org.opencv.core.Point(0,maxHeight));

        Mat srcMat = Converters.vector_Point2f_to_Mat(srcPts);
        Mat dstMat = Converters.vector_Point2f_to_Mat(dstPoints);

        //getting the transformation matrix
        Mat perspectiveTransformation = Imgproc.getPerspectiveTransform(srcMat,dstMat);

        //getting the input matrix from the given bitmap
        Mat inputMat = new Mat(globalMat.height(),globalMat.width(),CvType.CV_8UC1);

//        Bitmap bitmap= Bitmap.createBitmap(globalMat.width(),globalMat.height(),Bitmap.Config.ARGB_8888);
//        try {
//            bitmap = Bitmap.createBitmap(globalMat.cols(), globalMat.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(globalMat, bitmap);
//        }catch(Exception ex){
//            System.out.println(ex.getMessage());
//        }
//
//        Utils.bitmapToMat(bitmap,inputMat);

//        Imgproc.cvtColor(inputMat,inputMat,Imgproc.COLOR_RGB2GRAY);

        //getting the output matrix with the previously determined sizes
        outputMat = new Mat((int) maxWidth,(int) maxHeight,CvType.CV_8UC1);

        //applying the transformation
        Imgproc.warpPerspective(globalMat,globalMat,perspectiveTransformation,new Size(maxWidth,maxHeight));

        //creating the output bitmap
//        Bitmap outputBitmap = Bitmap.createBitmap((int)maxWidth,(int)maxHeight, Bitmap.Config.RGB_565);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)+
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)+
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this,"You havee already have permission",Toast.LENGTH_SHORT).show();

        }else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[] {
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA},
                                        PERMISSION_CODE);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        List<Camera.Size> resolutionList=  mOpenCvCameraView.getResolutionList();
        mOpenCvCameraView.setResolution(resolutionList.get(0));

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){

            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

}