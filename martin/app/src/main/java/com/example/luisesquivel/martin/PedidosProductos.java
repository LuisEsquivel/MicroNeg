package com.example.luisesquivel.martin;

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
import android.widget.TextView;
import android.widget.Toast;

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


public class PedidosProductos extends Fragment {


    private final int MIS_PERMISOS = 100;
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
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    ProgressDialog progreso;

    ImageView image;
    ImageButton btnConsultar;
    EditText ID, tele, direc;
    Button pedir;
    TextView nombre,precio,descripcion;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista= inflater.inflate(R.layout.fragment_pedidos_productos, container, false);
        image = (ImageView) vista.findViewById(R.id.imagen_producto);
        ID=(EditText) vista.findViewById(R.id.Productos);
        pedir=(Button) vista.findViewById(R.id.Pedir);
        nombre=(TextView) vista.findViewById(R.id.NombrePedidos);
        precio=(TextView) vista.findViewById(R.id.PrecioPedidos);
        descripcion=(TextView) vista.findViewById(R.id.DescripcionPedidos);


        tele = (EditText)vista.findViewById(R.id.phonazo);
        direc = (EditText)vista.findViewById(R.id.addressPerra);



        btnConsultar= (ImageButton) vista.findViewById(R.id.btnConsultarPedidos);

        pedir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                cargarWebServicePedir();
            }
        });
        if(solicitaPermisosVersionesSuperiores()){
            image.setEnabled(true);
        }else{
            image.setEnabled(false);
        }
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Metodo para consultar el producto por medio del id
                cargarWebService();
            }
        });

        requestQueue = Volley.newRequestQueue(getContext());


        return vista;
    }



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
        //requestQueue.add(imageRequest);
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
    public void cargarWebServicePedir() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String url = "http://"+ip.IPPERRA+":80/webServiceMartin/insert_pedidos.php?";
        // String url = "http://192.168.0.3:8080/MicroNeg/productosPOST.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();

                if (response.trim().equalsIgnoreCase("pedido registrado")) {
                    nombre.setText("");
                    precio.setText("");
                    descripcion.setText("");
                    //tele.setText("");
                   // direc.setText("");
                    Toast.makeText(getContext(), "Se realizó tu pedido", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Se realizó tu pedido", Toast.LENGTH_LONG).show();
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
                String phone = tele.getText().toString();
                String address = direc.getText().toString();

                String fotillo = convertirImagen(bitmap);

                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombre", name);
                parametros.put("precio", price);
                parametros.put("imagen", fotillo);
                parametros.put("descripcion", description);
                parametros.put("direccion", address);
                parametros.put("telefono", phone);


                return parametros;
            }
        };

        requestQueue.add(stringRequest);
       //VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);


    }



    //Metodo para consultar el producto buscado
    private void cargarWebService() {
        progreso=new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        //String url="http://192.168.0.3:8080/MicroNeg/ConsultarproductosUrl.php?id="+ID.getText().toString();
        String url="http://"+ip.IPPERRA+":80/webServiceMartin/ConsultarproductosUrl.php?id="+ID.getText().toString();

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progreso.hide();

                Productos miProducto=new Productos();

                JSONArray json=response.optJSONArray("productos");
                JSONObject jsonObject=null;

                try {
                    jsonObject=json.getJSONObject(0);
                    miProducto.setId(jsonObject.optInt("id"));
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




                String urlImagen="http://"+ip.IPPERRA+":80/webServiceMartin/"+miProducto.getRutaImagen();
                //String urlImagen="http://192.168.0.3:8080/MicroNeg/"+miProducto.getRutaImagen();
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

        //requestQueue.add(jsonObjectRequest);
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
