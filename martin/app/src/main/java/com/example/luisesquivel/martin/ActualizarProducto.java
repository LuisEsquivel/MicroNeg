package com.example.luisesquivel.martin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ActualizarProducto extends Fragment {

    private final int MIS_PERMISOS = 100;
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;

    private static final String CARPETA_PRINCIPAL = "misImagenes/";
    private static final String CARPETA_IMAGEN = "imagenes";
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;
    private String path;
    File fileImagen;
    Bitmap bitmap;

    //variables y el string request para enviar los parámetros por método POST
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;
    RequestQueue queue;
    ProgressDialog progreso;
    ImageView image;
    ImageButton btnConsultar;//SE MODIFICA
    EditText nombre, precio, descripcion, ID;
    Button eliminar,actualizar,examinar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista =inflater.inflate(R.layout.fragment_actualizar_producto, container, false);
        image = (ImageView) vista.findViewById(R.id.imagen_producto);
        ID=(EditText) vista.findViewById(R.id.ID_Productos);
        nombre = (EditText) vista.findViewById(R.id.nombre_producto);
        precio = (EditText) vista.findViewById(R.id.precio_producto);
        descripcion = (EditText)vista.findViewById(R.id.descripcion_prod);
        eliminar= (Button) vista.findViewById(R.id.Eliminar);
        examinar = (Button) vista.findViewById(R.id.examinar);
        actualizar=(Button) vista.findViewById(R.id.Actualizar);
        btnConsultar= (ImageButton) vista.findViewById(R.id.btnConsultarProductos);

        queue = Volley.newRequestQueue(getContext());

        //Al momento de dar clic al boton actualizar ,se actualizara el producto seleccionado
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Este es el metodo que ejecutara para hacer la conexion y actualizacion
                WebServiceActualizar();
            }
        });
        //Permisos
        if(solicitaPermisosVersionesSuperiores()){
            image.setEnabled(true);
        }else{
            image.setEnabled(false);
        }
        //Al momento de dar clic al boton de la lupa buscara el producto por medio del id y
        //te traera toda su informacion
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Metodo para consultar el producto por medio del id
                cargarWebService();
            }
        });

        //Al dar clic al boton eliminar ,se eliminara el producto buscado
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Metodo para eliminar el producto buscado
                webServiceEliminar();
            }
        });

        //Al momento de dar clic al boton examinar ,te dara a elegir si quieres tomar una foto,buscar en galeria
        //y cancelar para cambiar la foto que ya estaba anteriormente
        examinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Metodo que muestra las opciones de las fotos
                mostrarDialogOpciones();
            }
        });

        return vista;
    }

//Metodo que muestra los dialogos para tomar foto o buscar en galeria del celular
    private void mostrarDialogOpciones() {
        final CharSequence[] opciones = {"Tomar Foto", "Elegir de la Galería", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Elige una opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")) {
                    //Metodo para abrir la camara
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

//Este es el metodo que sirve para abrur la camara del celular y poder tomar una foto
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
//Este metodo sirve  para cargar la imagen
    private void cargarWebServiceImagen(String urlImagen) {
        urlImagen=urlImagen.replace(" ","%20");

        ImageRequest imageRequest=new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                bitmap=response;//SE MODIFICA
                image.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error al cargar la imagen",Toast.LENGTH_SHORT).show();
                Log.i("ERROR IMAGEN","Response -> "+error);
            }
        });
        //  request.add(imageRequest);
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(imageRequest);
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
//Metodo para ridimensionar la imagen o mas bien que se aprecie bien en la aplicacion
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
    // Metodo para actualizar el producto seleccionado
    public void WebServiceActualizar() {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Cargando...");
            progreso.show();

        String url = "http://192.168.0.3:8080/MicroNeg/update_productos.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();

                if (response.trim().equalsIgnoreCase("producto actualizado")) {
                    //nombre.setText("");
                    //precio.setText("");
                    //descripcion.setText("");
                    Toast.makeText(getContext(),"Se ha Actualizado con exito",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"No se Actualizo ",Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ",""+response);
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
            protected Map<String, String> getParams()throws AuthFailureError {
                String id=ID.getText().toString();
                String name = nombre.getText().toString();
                String price = precio.getText().toString();
                String description = descripcion.getText().toString();

                String fotillo = convertirImagen(bitmap);

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id", id);
                parametros.put("nombre", name);
                parametros.put("precio", price);
                parametros.put("descripcion", description);
                parametros.put("imagen", fotillo);

                return parametros;
            }
        };
        //queue.add(stringRequest);
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }
    //Metodo para eliminar un producto de la lista
    private void webServiceEliminar() {
        progreso=new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String url="http://192.168.0.3:8080/MicroNeg/delete_productos.php?id="+ID.getText().toString();

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();

                if (response.trim().equalsIgnoreCase("producto eliminado")){
                    nombre.setText("");
                    ID.setText("");
                    descripcion.setText("");
                    precio.setText("");
                    image.setImageResource(R.drawable.ic_menu_camera);
                    Toast.makeText(getContext(),"Producto eliminado con exito",Toast.LENGTH_SHORT).show();
                }else{
                    if (response.trim().equalsIgnoreCase("No existe")){
                        Toast.makeText(getContext(),"No se encuentra el producto ",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);
                    }else{
                        Toast.makeText(getContext()," No se ha Eliminado el producto ",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                progreso.hide();
            }
        });
        queue.add(stringRequest);
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }
    //Metodo para consultar el producto buscado
    private void cargarWebService() {
        progreso=new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String url="http://192.168.0.3:8080/MicroNeg/ConsultarproductosUrl.php?id="+ID.getText().toString();

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progreso.hide();

                Productos miProducto=new Productos();

                JSONArray json=response.optJSONArray("productos");
                JSONObject jsonObject=null;

                try {
                    jsonObject=json.getJSONObject(0);
                    miProducto.setNombre(jsonObject.optString("nombre"));
                    miProducto.setPrecio(jsonObject.optString("precio"));
                    miProducto.setDescripcion(jsonObject.optString("descripcion"));
                    miProducto.setRutaImagen(jsonObject.optString("rutaImagen"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                nombre.setText(miProducto.getNombre());//SE MODIFICA
                precio.setText(miProducto.getPrecio());//SE MODIFICA
                descripcion.setText(miProducto.getDescripcion());//SE MODIFICA

                String urlImagen="http://192.168.0.3:8080/MicroNeg/"+miProducto.getRutaImagen();
                //Toast.makeText(getContext(), "url "+urlImagen, Toast.LENGTH_LONG).show();
                cargarWebServiceImagen(urlImagen);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                progreso.hide();
                Log.d("ERROR: ", error.toString());
            }
        });

        // request.add(jsonObjectRequest);
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    private String convertirImagen(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte, Base64.DEFAULT);

        return imagenString;
    }

    private boolean solicitaPermisosVersionesSuperiores() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){//validamos si estamos en android menor a 6 para no buscar los permisos
            return true;
        }

        //validamos si los permisos ya fueron aceptados
        if((getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&getContext().checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED){
            return true;
        }

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)||(shouldShowRequestPermissionRationale(CAMERA)))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MIS_PERMISOS);
        }

        return false;//implementamos el que procesa el evento dependiendo de lo que se defina aqui
    }
    private void cargarDialogoRecomendacion() {
        android.support.v7.app.AlertDialog.Builder dialogo=new android.support.v7.app.AlertDialog.Builder(getContext());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }
}
