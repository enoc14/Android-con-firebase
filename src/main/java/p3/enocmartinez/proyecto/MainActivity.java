package p3.enocmartinez.proyecto;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import p3.enocmartinez.proyecto.entidades.Usuario;
import p3.enocmartinez.proyecto.utilidades.Utilidades;

public class MainActivity extends AppCompatActivity {

    // Declarar variables
    EditText edit_Buque, edit_recibe, edit_info, edit_Capitan, edit_Naviera, edit_Genero, edit_Tel;
    Button editar, aplicar;
    String[] datos_capitan = new String[8];

    // Obtener referencias de la base de datos
    DatabaseReference ref;
    //DatabaseReference mensajeRef = ref.child("mensaje");

    ConexionSQLiteHelper admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Castear variables
        edit_Capitan = (EditText) findViewById(R.id.edit_Capitan);
        edit_Naviera = (EditText) findViewById(R.id.edit_Naviera);
        edit_Buque = (EditText) findViewById(R.id.edit_Buque);
        edit_recibe = (EditText) findViewById(R.id.edit_Recibe);
        edit_info = (EditText) findViewById(R.id.edit_info);
        edit_Genero = (EditText) findViewById(R.id.edit_Genero);
        edit_Tel = (EditText) findViewById(R.id.edit_numeroTelefono);
        editar = (Button) findViewById(R.id.btn_editar);
        aplicar = (Button) findViewById(R.id.btn_aplica);

        // Base de datos Firebase
        inciarFB();

        // Base de datos Interna
        admin = new ConexionSQLiteHelper(this);

        llenarCampos();

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editar.setVisibility(View.GONE);
                aplicar.setVisibility(View.VISIBLE);
                confirmaEditar();
            }
        });

        aplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos())
                    confirmaCambios();
            }
        });
    }

    private boolean validarCampos() {
        boolean continua = true;

        // Datos
        String eBuque = edit_Buque.getText().toString();
        String eRecibe = edit_recibe.getText().toString();
        String eInfo = edit_info.getText().toString();
        String gender = edit_Genero.getText().toString();
        String telefono = edit_Tel.getText().toString();

        if(eBuque.isEmpty() || eBuque.length() < 5){
            edit_Buque.setError("El número debe ser válido");
            continua = false;
        } else
            edit_Buque.setError(null);

        if(eRecibe.isEmpty() || eRecibe.length() < 5){
            edit_recibe.setError("El nombre debe ser válido");
            continua = false;
        } else
            edit_recibe.setError(null);

        if(gender.isEmpty()){
            edit_Genero.setError("El género debe ser M ó F");
            continua = false;
        } else if(gender.equals("M") || gender.equals("F")){
            edit_Genero.setError(null);
        } else {
            edit_Genero.setError("El género debe ser M ó F");
            continua = false;
        }

        if(telefono.isEmpty() || telefono.length() < 10){
            edit_Tel.setError("El teléfono de tener 10 dígitos");
            continua = false;
        } else
            edit_Tel.setError(null);

        if(eInfo.isEmpty() || eInfo.length() < 5){
            edit_info.setError("La descripción debe ser válida");
            continua = false;
        } else
            edit_info.setError(null);

        return continua;
    }

    private void inciarFB() {
        FirebaseApp.initializeApp(this);
        ref = FirebaseDatabase.getInstance().getReference();
    }

    private void confirmaCambios() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        alerta.setMessage("¿Estás seguro de que deseas cambiar los Datos?")
                .setPositiveButton("¡Estoy Seguro!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateCapitanFB();
                        updateCapitanBD();
                        Toast.makeText(MainActivity.this, "Información Actualizada", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        AlertDialog mostrar = alerta.create();
        mostrar.setTitle("¡IMPORTANTE!");
        mostrar.show();
    }

    private void llenarCampos() {
        getDatosCapitan();
        edit_Naviera.setText(datos_capitan[0]);
        edit_Capitan.setText(datos_capitan[1]);
        edit_Buque.setText(datos_capitan[2]);
        edit_recibe.setText(datos_capitan[3]);
        edit_Genero.setText(datos_capitan[6]);
        edit_Tel.setText(datos_capitan[7]);
        edit_info.setText(datos_capitan[4]);
    }

    private void confirmaEditar() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        alerta.setMessage("Sólo podrás editar algunos datos.")
                .setCancelable(false)
                .setPositiveButton("¡ENTENDIDO!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activarCampos();
                    }
                });
        AlertDialog mostrar = alerta.create();
        mostrar.setTitle("AVISO");
        mostrar.show();
    }

    private void activarCampos() {
        edit_Buque.setEnabled(true);
        edit_recibe.setEnabled(true);
        edit_Genero.setEnabled(true);
        edit_Tel.setEnabled(true);
        edit_info.setEnabled(true);
        edit_Buque.setSelection(edit_Buque.getText().toString().length());
    }

    private void getDatosCapitan() {
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor cursor = db.rawQuery(Utilidades.CONSULTA_CAPITAN,null);
        cursor.moveToFirst();
        datos_capitan[0] = cursor.getString(0); // Naviera
        datos_capitan[1] = cursor.getString(2); // Nombre
        datos_capitan[2] = cursor.getString(1); // Buque
        datos_capitan[3] = cursor.getString(9); // Nombre de quién recibe
        datos_capitan[6] = cursor.getString(3); // Género de quién recibe
        datos_capitan[7] = cursor.getString(4); // Tel de quién recibe
        datos_capitan[4] = cursor.getString(5); // Descripción
        datos_capitan[5] = cursor.getString(6); // uID

        db.close();
    }

    private void updateCapitanBD() {
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.BUQUE, edit_Buque.getText().toString());
        values.put(Utilidades.RECIBE, edit_recibe.getText().toString());
        values.put(Utilidades.DESCRIPCION, edit_info.getText().toString());
        values.put(Utilidades.GENERO, edit_Genero.getText().toString());
        values.put(Utilidades.TELEFONO, edit_Tel.getText().toString());

        int up = db.update("capitan", values, Utilidades.HACK + "='uno'", null);
        db.close();
    }

    private void updateCapitanFB(){
        ref.child("usuarios").child(datos_capitan[5]).child("buque").setValue(edit_Buque.getText().toString());
        ref.child("usuarios").child(datos_capitan[5]).child("recibe").setValue(edit_recibe.getText().toString());
        ref.child("usuarios").child(datos_capitan[5]).child("genero").setValue(edit_Genero.getText().toString());
        ref.child("usuarios").child(datos_capitan[5]).child("telefono").setValue(edit_Tel.getText().toString());
        ref.child("usuarios").child(datos_capitan[5]).child("descripcion").setValue(edit_info.getText().toString());
    }
}