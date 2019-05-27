package p3.enocmartinez.proyecto;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import p3.enocmartinez.proyecto.entidades.Usuario;
import p3.enocmartinez.proyecto.utilidades.Utilidades;

public class RegistroActivity extends AppCompatActivity {

    //Declaración de variables
    EditText nombreNaviera, numeroBuque, nombreCapitan, genero, numeroTelefono, descripcion, recibe;
    Button registro;
    String uID, token;
    int vista;

    // Obtener referencias de la base de datos
    DatabaseReference ref;
    ConexionSQLiteHelper admin;

    // Preferencias de muestra
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Utilizar las preferencias
        preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        vista = preferences.getInt("vista", 0);

        // Si ya completó el registro, no se debe volver a mostrar la pantalla
        if(vista != 0){
            Intent intent = new Intent(RegistroActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        }

        // Enlazar variables
        nombreNaviera = (EditText) findViewById(R.id.input_nombreNaviera);
        numeroBuque = (EditText) findViewById(R.id.input_numeroBuque);
        nombreCapitan = (EditText) findViewById(R.id.input_nombreCapitan);
        genero = (EditText) findViewById(R.id.input_genero);
        numeroTelefono = (EditText) findViewById(R.id.input_numeroTelefono);
        descripcion = (EditText) findViewById(R.id.input_descripcion);
        recibe = (EditText) findViewById(R.id.input_nombre_recibe);
        registro = (Button) findViewById(R.id.btn_registro);

        // Inicializar FireBase
        iniciarFireBase();
        admin = new ConexionSQLiteHelper(this);

        //Evento del botón
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar()){
                    token = getToken();
                    registarCapitanFB();
                    registarCapitanBD();
                    limpiar();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("vista", 1);
                    editor.commit();
                    Intent intent = new Intent(RegistroActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void limpiar() {
        nombreNaviera.setText("");
        numeroBuque.setText("");
        nombreCapitan.setText("");
        genero.setText("");
        numeroTelefono.setText("");
        descripcion.setText("");
        recibe.setText("");
    }

    private void iniciarFireBase() {
        FirebaseApp.initializeApp(this);
        ref = FirebaseDatabase.getInstance().getReference();
    }

    private void registarCapitanFB() {
        uID = UUID.randomUUID().toString();
        Usuario usr = new Usuario();
        usr.setUid(uID);
        usr.setNaviera(nombreNaviera.getText().toString());
        usr.setBuque(numeroBuque.getText().toString());
        usr.setCapitan(nombreCapitan.getText().toString());
        usr.setGenero(genero.getText().toString());
        usr.setTelefono(numeroTelefono.getText().toString());
        usr.setDescripcion(descripcion.getText().toString());
        usr.setRecibe(recibe.getText().toString());
        usr.setToken(token);
        ref.child("usuarios").child(usr.getUid()).setValue(usr);

        // Poner uID en SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("uID", uID);
        editor.commit();
    }

    private void registarCapitanBD() {
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.NAVIERA, nombreNaviera.getText().toString());
        values.put(Utilidades.BUQUE, numeroBuque.getText().toString());
        values.put(Utilidades.NOMBRE, nombreCapitan.getText().toString());
        values.put(Utilidades.GENERO, genero.getText().toString());
        values.put(Utilidades.TELEFONO, numeroTelefono.getText().toString());
        values.put(Utilidades.DESCRIPCION, descripcion.getText().toString());
        values.put(Utilidades.UID, uID);
        values.put(Utilidades.RECIBE, recibe.getText().toString());

        int up = db.update("capitan", values, Utilidades.HACK + "='uno'", null);

        /*String insert = "insert into capitan values('" +
                nombreNaviera.getText().toString()+"'," +
                "'"+numeroBuque.getText().toString()+"'," +
                "'"+nombreCapitan.getText().toString()+"'," +
                "'"+genero.getText().toString()+"'," +
                "'"+numeroTelefono.getText().toString()+"'," +
                "'"+descripcion.getText().toString()+"'," +
                "'"+uID+"')";

        db.execSQL(insert);*/
        db.close();
    }

    private String getToken(){
        String tkn = "";
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor cursor = db.rawQuery(Utilidades.CONSULTA_CAPITAN2,null);
        cursor.moveToFirst();
        tkn = cursor.getString(0);
        db.close();

        return tkn;
    }

    private boolean validar() {
        boolean continua = true; // Bandera para saber si all está correcto

        // Datos
        String naviera = nombreNaviera.getText().toString();
        String buque = numeroBuque.getText().toString();
        String capitan = nombreCapitan.getText().toString();
        String gender = genero.getText().toString();
        String telefono = numeroTelefono.getText().toString();
        String info = descripcion.getText().toString();
        String rec = recibe.getText().toString();

        // Validaciones
        if(naviera.isEmpty() || naviera.length() < 5){
            nombreNaviera.setError("El nombre debe ser válido");
            continua = false;
        } else
            nombreNaviera.setError(null);

        if(buque.isEmpty() || buque.length() < 5){
            numeroBuque.setError("El número debe ser válido");
            continua = false;
        } else
            numeroBuque.setError(null);

        if(capitan.isEmpty() || capitan.length() < 5){
            nombreCapitan.setError("El nombre debe ser válido");
            continua = false;
        } else
            nombreCapitan.setError(null);

        if(gender.isEmpty()){
            genero.setError("El género debe ser M ó F");
            continua = false;
        } else if(gender.equals("M") || gender.equals("F")){
            genero.setError(null);
        } else {
            genero.setError("El género debe ser M ó F");
            continua = false;
        }

        if(telefono.isEmpty() || telefono.length() < 10){
            numeroTelefono.setError("El teléfono de tener 10 dígitos");
            continua = false;
        } else
            numeroTelefono.setError(null);

        if(rec.isEmpty() || rec.length() < 5){
            recibe.setError("El nombre debe ser válido");
            continua = false;
        } else
            recibe.setError(null);

        if(info.isEmpty() || info.length() < 5){
            descripcion.setError("La descripción debe ser válida");
            continua = false;
        } else
            descripcion.setError(null);

        return continua;
    }
}
