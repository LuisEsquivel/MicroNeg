package com.example.luisesquivel.martin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
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

public class Login extends AppCompatActivity {


    ProgressDialog progressDialog;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    Ip ip = new Ip();

    EditText user, pass;
    Button iniciar, irAregistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        user = (EditText) findViewById(R.id.usuarioLogin);
        pass = (EditText) findViewById(R.id.contraseñaLogin);
        iniciar = (Button) findViewById(R.id.btnIniciar);
        irAregistrarse = (Button) findViewById(R.id.btnRegistrarse2);


        requestQueue = Volley.newRequestQueue(getApplicationContext());


        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarWebService();
            }
        });


        irAregistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAregistrarseMetodo();
            }
        });


    }


    private void irAregistrarseMetodo() {
        Intent intent = new Intent(getApplicationContext(), Registrarse.class);
        startActivity(intent);
    }




    public void cargarWebService() {


        String url = "http://"+ip.IPPERRA+":80/webServiceMartin/login.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.trim().equalsIgnoreCase("Bienvenido") && user.getText().toString().equals("Luis") && pass.getText().toString().equals("Luis")) {
                   // Toast.makeText(getApplicationContext(), "Bienvenido Perro", Toast.LENGTH_LONG).show();
                    Intent MenuDueNo = new Intent(getApplicationContext(), MenuDueño.class);
                    startActivity(MenuDueNo);
                }else if(response.trim().equalsIgnoreCase("Bienvenido") && !user.getText().toString().equals("Luis") && !pass.getText().toString().equals("Luis") ){
                    Intent MenuCliente = new Intent(getApplicationContext(), MenuCliente.class);
                    startActivity(MenuCliente);
                }

                else {
                    Toast.makeText(getApplicationContext(), "Error al iniciar sesión\nPor favor verifique sus datos", Toast.LENGTH_LONG).show();
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

