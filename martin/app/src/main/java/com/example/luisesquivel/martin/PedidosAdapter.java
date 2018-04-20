package com.example.luisesquivel.martin;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.ViewHolder> {

    ArrayList<PedidosArray>listaPedidos;
    Context context;
    RequestQueue requestQueue;
    ImageRequest imageRequest;
    Volley volley;

    Ip ip = new Ip();

    public PedidosAdapter(ArrayList<PedidosArray> listaPedidos, Context context) {
        this.listaPedidos = listaPedidos;
        this.context=context;
        requestQueue =  volley.newRequestQueue(context);
    }

    @Override
    public PedidosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_pedidos, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PedidosAdapter.ViewHolder holder, int position) {
        holder.id.setText("ID: "+listaPedidos.get(position).getId().toString());
        holder.name.setText(listaPedidos.get(position).getNombre().toString());
        holder.price.setText("$USD "+listaPedidos.get(position).getPrecio().toString());
        holder.direccion.setText("DIRECCIÓN: "+listaPedidos.get(position).getDireccion().toString());
        holder.telefono.setText("TELÉFONO "+listaPedidos.get(position).getTelefono().toString());


        if(listaPedidos.get(position).getRutaImagen() != null){
            cargarImagenPERRA(listaPedidos.get(position).getRutaImagen(), holder);
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
        return listaPedidos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView  id, name, price;
        TextView direccion, telefono;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            id   = itemView.findViewById(R.id.id_prod_clientes);
            name = itemView.findViewById(R.id.nombre_prod_clientes);
            price = itemView.findViewById(R.id.precio_prod_clientes);
            direccion = itemView.findViewById(R.id.direction);
            telefono = itemView.findViewById(R.id.telefonsin);
            img   = itemView.findViewById(R.id.imagen_prod_clientes);
        }

    }
}
