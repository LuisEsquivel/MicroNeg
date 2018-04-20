package com.example.luisesquivel.martin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by Luis Esquivel on 22/03/2018.
 */

public class PedidosArray {
    private String nombre;
    private String precio;
    private String descripcion;
    private String dato;
    private String rutaImagen;
    private String direccion;
    private String telefono;
    private Bitmap imagen;
    private Integer id;



    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }



    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaImagen() {
        return rutaImagen;

    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public Bitmap getImagen() {

        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }


    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;

        try {
            byte[] byteCode = Base64.decode(dato, Base64.DEFAULT);
            //this.imagen = BitmapFactory.decodeByteArray(byteCode, 0, byteCode.length);

            int ancho = 120;
            int alto = 120;

            Bitmap foto = BitmapFactory.decodeByteArray(byteCode, 0, byteCode.length);
            this.imagen  = Bitmap.createScaledBitmap(foto, alto, ancho, true);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
