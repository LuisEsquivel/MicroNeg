package com.example.luisesquivel.martin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.*;

public class AgregarProducto extends Fragment {



    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;

    private static final String CARPETA_PRINCIPAL = "misImagenes/";
    private static final String CARPETA_IMAGEN = "imagenes";
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;
    private String path;
    File fileImagen;
    Bitmap bitmap;
    Ip ip = new Ip();


    //variables y el string request para enviar los parámetros por método POST
    StringRequest stringRequest;
    RequestQueue queue;
    ProgressDialog progreso;
    ImageView image;
    EditText nombre, precio, descripcion;
    Button guardar, examinar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_agregar_producto, container, false);
        image = (ImageView) vista.findViewById(R.id.imagen_producto);
        nombre = (EditText) vista.findViewById(R.id.nombre_producto);
        precio = (EditText) vista.findViewById(R.id.precio_producto);
        descripcion = (EditText)vista.findViewById(R.id.direction);
        guardar = (Button) vista.findViewById(R.id.guardar);
        examinar = (Button) vista.findViewById(R.id.examinar);

        queue = Volley.newRequestQueue(getContext());


        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarWebService();
            }
        });


        examinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogOpciones();
            }
        });

        return vista;
    }




    private void mostrarDialogOpciones() {
        final CharSequence[] opciones = {"Tomar Foto", "Elegir de la Galería", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Elige una opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")) {
                    abrirCamara();
                } else if (opciones[i].equals("Elegir de la Galería")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);
                } else if (opciones[i].equals("Cancelar")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }


    private void abrirCamara() {
        File miFile = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isCreada = miFile.exists();

        if (isCreada == false) {
            isCreada = miFile.mkdirs();
        } else if (isCreada == true) {
            Long consecutivo = System.currentTimeMillis() / 1000;
            String nombre = consecutivo.toString() + " jpg";

            path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN + File.separator + nombre;

            fileImagen = new File(path);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));

            startActivityForResult(intent, COD_FOTO);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case COD_SELECCIONA:
                Uri miPath = data.getData();
                image.setImageURI(miPath);

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),miPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case COD_FOTO:
                MediaScannerConnection.scanFile(getContext(), new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String s, Uri uri) {
                                Log.i("Path", "" + path);
                            }
                        });
                bitmap = BitmapFactory.decodeFile(path);
                image.setImageBitmap(bitmap);
                break;
        }
        bitmap = redimensionarImagen(bitmap, 500, 500);
    }

    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {
        int alto = bitmap.getHeight();
        int ancho = bitmap.getWidth();

        if(alto>altoNuevo || ancho>anchoNuevo){
            float escalaAncho = anchoNuevo/ancho;
            float escalaAlto  = altoNuevo/alto;

            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho, escalaAlto);
            return Bitmap.createBitmap(bitmap, 0,0, ancho,alto,matrix,false);
        }else {
            return bitmap;
        }
    }


    public void cargarWebService() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();


        String url = "http://"+ip.IPPERRA+":80/webServiceMartin/productosPOST.php?";

        stringRequest = new StringRequest(Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();

                if (response.trim().equalsIgnoreCase("producto registrado")) {
                    nombre.setText("");
                    precio.setText("");
                    descripcion.setText("");
                    Toast.makeText(getContext(), "Registrado Exitosamente", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No se ha podido conectar", Toast.LENGTH_LONG).show();
                progreso.hide();
            }
        }

        ) {
            @Override
            protected Map<String, String>getParams(){

                String name = nombre.getText().toString();
                String price = precio.getText().toString();
                String description = descripcion.getText().toString();

                String fotillo = convertirImagen(bitmap);

                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombre", name);
                parametros.put("precio", price);
                parametros.put("descripcion", description);
                parametros.put("imagen", fotillo);

                return parametros;
            }
        };
        queue.add(stringRequest);
    }



    private String convertirImagen(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte, Base64.DEFAULT);

        return imagenString;
    }



}
