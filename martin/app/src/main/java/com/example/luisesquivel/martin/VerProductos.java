package com.example.luisesquivel.martin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class VerProductos extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{

    ArrayList<Productos>listaProductos;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    JsonObjectRequest jsonObjectRequest;
    JSONObject jsonObject;
    JSONArray jsonArray;
    RequestQueue requestQueue;

    Ip ip = new Ip();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View vista = inflater.inflate(R.layout.fragment_ver_productos, container, false);

        recyclerView = (RecyclerView)vista.findViewById(R.id.id_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        requestQueue = Volley.newRequestQueue(getContext());


        listaProductos = new ArrayList<Productos>();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                cargarWebService();
            }
        });

        return vista;
    }




    private void cargarWebService() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando Productos...");
        progressDialog.show();




        String url = "http://"+ip.IPPERRA+":80/webServiceMartin/verProductos.php";
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this,this);
        requestQueue.add(jsonObjectRequest);

    }



    @Override
    public void onResponse(JSONObject response) {
        Productos productos = null;

        jsonArray = response.optJSONArray("productos");


            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    productos = new Productos();
                    jsonObject = jsonArray.getJSONObject(i);

                    productos.setId(jsonObject.getInt("id"));
                    productos.setNombre(jsonObject.getString("nombre"));
                    productos.setPrecio(jsonObject.getString("precio"));
                    productos.setDescripcion(jsonObject.getString("descripcion"));
                    productos.setRutaImagen(jsonObject.optString("rutaImagen"));

                    listaProductos.add(productos);

                }
                progressDialog.dismiss();
                ProductosAdapter adapter = new ProductosAdapter(listaProductos, getContext());
                 recyclerView.setAdapter(adapter);



            }catch (JSONException e) {
                e.printStackTrace();
            }

    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No se ha podido conectar", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();

    }

}
