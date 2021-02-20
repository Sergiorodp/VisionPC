package com.example.visin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


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
import android.view.View;
import  android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import java.io.File;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    private  final String CARPETA_RAIZ="misImagenesPrueba/";
    private  final String RUTA_IMAGEN = CARPETA_RAIZ + "misFotos";

    ImageView imagen;
    String path;

    private static String TAG = "MainActivity";

    static
    {
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV init");
        }else{
            Log.d(TAG, "OpenCv NO init");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagen = (ImageView) findViewById(R.id.imageId);
    }

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

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return null;
    }
}