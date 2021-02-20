package com.example.visin;
import com.example.visin.OpenCvCamera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import  android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2{

    private  final String CARPETA_RAIZ="misImagenesPrueba/";
    private  final String RUTA_IMAGEN = CARPETA_RAIZ + "misFotos";

    private OpenCvCamera mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;
    private Mat                    mRgba;
    private Mat                    mGray;

    ImageView imagen;
    String path;

    private static final String TAG = "OCVSample::Activity";


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //imagen = (ImageView) findViewById(R.id.imageId);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // here, Permission is not granted
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, 50);
        }
        setContentView(R.layout.activity_main);
        System.loadLibrary("mixed_sample");

        mOpenCvCameraView = (OpenCvCamera) findViewById(R.id.tutorial3_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

    }

    public void onCameraViewStopped() {

    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        FindFeatures(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
        return mRgba;
    }

    public native void FindFeatures(long matAddrGr, long matAddrRgba);

    /* inicio comentado
    public void onClick(View view) {
        cargarImagen();
    }


    private void cargarImagen() {
        final CharSequence[] opciones = {"Tomar Foto","Cargar Imagen","Cancelar"};

        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(MainActivity.this);
        alertOpciones.setTitle("Selecciones una opción");

        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (opciones[which].equals("Tomar Foto")){
                    Toast.makeText(getApplication(),"Tomar Foto",Toast.LENGTH_SHORT).show();
                    TomarFoto();

                }else if(opciones[which].equals("Cargar Imagen")){

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(intent.createChooser(intent,"Seleccione la aplicación"),10);

                }else{
                    dialog.dismiss();
                }
            }
        });

        alertOpciones.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            switch (requestCode){
                case 10:
                    Uri myPath = data.getData();
                    imagen.setImageURI(myPath);
                    break;

                case 20:
                    MediaScannerConnection.scanFile(this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("Ruta e lamacenamiento","path: " + path);
                        }
                    });

                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imagen.setImageBitmap(bitmap);
                    break;

            }

        }
    }

    private void TomarFoto(){
        File fileimagen = new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada = fileimagen.exists();
        String nombre  = "";

        if (!isCreada){
            isCreada = fileimagen.mkdir();
        }else{
            nombre = (System.currentTimeMillis()/1000)+".jpg";
        }

        path = Environment.getExternalStorageDirectory() + File.separator + RUTA_IMAGEN + File.separator + nombre;

        File imagen = new File(path);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));

        startActivityForResult(intent,20);

    }
    */


}