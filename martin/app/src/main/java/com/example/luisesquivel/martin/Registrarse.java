package com.example.luisesquivel.martin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Registrarse extends AppCompatActivity {

    RequestQueue requestQueue;
    StringRequest stringRequest;
    EditText user, pass;
    Button registrarse, irAiniciarSesion;

    Ip ip = new Ip();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        user = (EditText) findViewById(R.id.usuarioRegistrarse);
        pass = (EditText) findViewById(R.id.contraseñaRegistrarse);
        registrarse = (Button) findViewById(R.id.btnRegistrarse);
        irAiniciarSesion = (Button) findViewById(R.id.btnIraIniciarSesion);


        requestQueue = Volley.newRequestQueue(getApplicationContext());



        registrarse.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       cargarWebService();
                   }
               });


              irAiniciarSesion.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      iniciarSesion();
                  }
              });

    }


        private void iniciarSesion() {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        }




        public void cargarWebService() {


            String url ="http://"+ip.IPPERRA+":80/webServiceMartin/registrarse.php?";

            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response.trim().equalsIgnoreCase("registrado con exito")) {
                        //Toast.makeText(getApplicationContext(), "Registrado Exitosamente", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "No se ha podido conectar", Toast.LENGTH_LONG).show();
                }
            }

            ) {
                @Override
                protected Map<String, String> getParams() {

                    String usuario = user.getText().toString();
                    String contra = pass.getText().toString();


                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("usuario", usuario);
                    parametros.put("contraseña", contra);

                    return parametros;
                }
            };
            requestQueue.add(stringRequest);
        }
    }
