package com.example.luisesquivel.martin;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Pedidos extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    ArrayList<PedidosArray> listaPedidos;
    StringRequest stringRequest;
    ProgressDialog progreso;
    RequestQueue requestQueue;
    EditText id;
    Button eliminar;
    RecyclerView recyclerView;
    JsonObjectRequest jsonObjectRequest;
    JSONArray jsonArray;
    JSONObject jsonObject;

    Ip ip = new Ip();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_pedidos, container, false);
        recyclerView = (RecyclerView)vista.findViewById(R.id.id_RecyclerViewPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));



        id = (EditText)vista.findViewById(R.id.idProductazo);
        eliminar = (Button)vista.findViewById(R.id.eliminarProductazo);


        requestQueue = Volley.newRequestQueue(getContext());



                eliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        webServiceEliminarPedidazo();
                    }
                });


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                cargarWebServicePedido();
            }
        });



        listaPedidos = new ArrayList<PedidosArray>();





        return  vista;

    }





    //Metodo para eliminar un producto de la lista
    private void webServiceEliminarPedidazo() {
        progreso=new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String url="http://"+ip.IPPERRA+":80/webServiceMartin/deletePedidazos.php?id="+id.getText().toString();

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();

                if (response.trim().equalsIgnoreCase("producto eliminado")){
                    id.setText("");
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
      //  requestQueue.add(stringRequest);
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }







    private void cargarWebServicePedido() {


        String url = "http://"+ip.IPPERRA+":80/webServiceMartin/verPedidos.php";
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this,this);
        requestQueue.add(jsonObjectRequest);

    }



    @Override
    public void onResponse(JSONObject response) {


        jsonArray = response.optJSONArray("pedidos");


        try {
            for (int i = 0; i < jsonArray.length(); i++) {
               PedidosArray pedidosArray = new PedidosArray();

                jsonObject = jsonArray.getJSONObject(i);


                pedidosArray.setId(jsonObject.getInt("id"));
                pedidosArray.setNombre(jsonObject.getString("nombre"));
                pedidosArray.setPrecio(jsonObject.getString("precio"));
                pedidosArray.setDescripcion(jsonObject.getString("descripcion"));
                pedidosArray.setRutaImagen(jsonObject.getString("rutaImagen"));
                pedidosArray.setDireccion(jsonObject.getString("direccion"));
                pedidosArray.setTelefono(jsonObject.getString("telefono"));

                listaPedidos.add(pedidosArray);

            }
            PedidosAdapter adapter = new PedidosAdapter(listaPedidos, getContext());
            recyclerView.setAdapter(adapter);



        }catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No se ha podido conectar\nO noo se encuentran pedidos", Toast.LENGTH_LONG).show();

    }



}
