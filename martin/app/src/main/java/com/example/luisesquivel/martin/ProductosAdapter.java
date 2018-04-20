package com.example.luisesquivel.martin;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * Created by Luis Esquivel on 29/03/2018.
 */

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {

    ArrayList<Productos>listaProductos;
    Context context;
    RequestQueue requestQueue;
    ImageRequest imageRequest;
    Volley volley;

    Ip ip = new Ip();

    public ProductosAdapter(ArrayList<Productos> listaProductos, Context context) {
        this.listaProductos = listaProductos;
        this.context=context;
        requestQueue =  volley.newRequestQueue(context);
    }

    @Override
    public ProductosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_productos, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductosAdapter.ViewHolder holder, int position) {
        holder.id.setText(listaProductos.get(position).getId().toString());
        holder.name.setText(listaProductos.get(position).getNombre().toString());
        holder.price.setText("$USD "+listaProductos.get(position).getPrecio().toString());
        holder.descripcion.setText(listaProductos.get(position).getDescripcion().toString());


        if(listaProductos.get(position).getRutaImagen() != null){
            cargarImagenPERRA(listaProductos.get(position).getRutaImagen(), holder);
        }else {
            holder.img.setImageResource(R.drawable.ic_menu_camera);
        }

    }

    private void cargarImagenPERRA(String rutaImagen, final ViewHolder holder) {
        String urlImagenPerra = "http://"+ip.IPPERRA+":80/webServiceMartin/"+rutaImagen;

        imageRequest = new ImageRequest(urlImagenPerra, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                holder.img.setImageBitmap(response);
            }
        }, 200, 300, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No se pudo cargar la imagen", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(imageRequest);
    }



    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView  id, name, price, descripcion;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            id   = itemView.findViewById(R.id.idprod);
            name = itemView.findViewById(R.id.nombre_prod);
            price = itemView.findViewById(R.id.precio_prod);
            descripcion = itemView.findViewById(R.id.descripcion_prod);
            img   = itemView.findViewById(R.id.imagen_prod);


        }

    }
}
