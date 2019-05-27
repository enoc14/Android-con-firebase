package p3.enocmartinez.proyecto;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import p3.enocmartinez.proyecto.entidades.Pedido;
import p3.enocmartinez.proyecto.utilidades.Utilidades;

public class NuevoPedidoActivity extends AppCompatActivity {

    // Variables
    EditText fecha, hora, producto, cantidad;
    Spinner puerto;
    ListView productos;
    ImageButton add_producto;
    Button enviar_pedido;
    String[] puertos = {"Altamira","Ensenada","Lázaro Cárdenas","Manzanillo","Tampico","Veracruz"}, datos_capitan = new String[9];
    ArrayList<String> datos = new ArrayList<String>();
    ArrayAdapter<String> adapter1;
    ConexionSQLiteHelper admin;
    long folio_pedido;
    SharedPreferences preferences;

    // Obtener referencias de la base de datos
    DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        // Castear
        fecha = (EditText) findViewById(R.id.fecha);
        hora = (EditText) findViewById(R.id.hora);
        producto = (EditText) findViewById(R.id.input_producto);
        cantidad = (EditText) findViewById(R.id.input_cantidad);
        puerto = (Spinner) findViewById(R.id.opcion_puerto);
        productos = (ListView) findViewById(R.id.lista_productos);
        add_producto = (ImageButton) findViewById(R.id.add_producto);
        enviar_pedido = (Button) findViewById(R.id.enviar_pedido);

        // Iniciar FireBase
        iniciarFB();

        // Llenar el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, puertos);
        puerto.setAdapter(adapter);

        // Llenar ListView
        adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, datos);
        productos.setAdapter(adapter1);

        // Objeto de la base de Datos
        admin = new ConexionSQLiteHelper(this);
        //folio_pedido = getFolio();

        // Dialogo para nota de Recuerdo
        verNota();

        add_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validaAgregar()){
                    String nombre = producto.getText().toString();
                    String pza = cantidad.getText().toString();
                    String data = nombre.toUpperCase() + " - " + pza;
                    producto.setText("");
                    cantidad.setText("");
                    datos.add(data);
                    adapter1.notifyDataSetChanged();
                }
            }
        });

        productos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                datos.remove(i);
                adapter1.notifyDataSetChanged();
                return true;
            }
        });

        productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(NuevoPedidoActivity.this, "Mantén presionado para eliminar", Toast.LENGTH_SHORT).show();
            }
        });

        enviar_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar())
                    isSeguro();
            }
        });
    }

    private void isSeguro() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        alerta.setMessage("¿Estás seguro de que están bien tus Datos?")
                .setPositiveButton("¡Estoy Seguro!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        folio_pedido = getFolio();
                        getDatosCapitan();
                        registrarPedidoFB();
                        registrarPedidoBD();
                        sendEmail();
                        limpiar();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("pedido", true);
                        editor.commit();
                    }
                })
                .setNegativeButton("Volver a revisar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog mostrar = alerta.create();
        mostrar.setTitle("¡IMPORTANTE!");
        mostrar.show();
    }

    private void verNota() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        alerta.setMessage("Recuerda que este pedido se debe hacer con 24 horas de anticipación.")
                .setPositiveButton("¡Lo tengo!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog mostrar = alerta.create();
        mostrar.setTitle("Nota");
        mostrar.show();
    }

    private long getFolio() {
        SQLiteDatabase db = admin.getReadableDatabase();
        long cont = DatabaseUtils.queryNumEntries(db, "pedidos");

        db.close();
        return ++cont;
    }

    private void getDatosCapitan() {
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor cursor = db.rawQuery(Utilidades.CONSULTA_CAPITAN,null);
        cursor.moveToFirst();
        datos_capitan[0] = cursor.getString(0); // Naviera
        datos_capitan[1] = cursor.getString(1); // Buque
        datos_capitan[2] = cursor.getString(2); // Nombre
        datos_capitan[3] = cursor.getString(3); // Género
        datos_capitan[4] = cursor.getString(4); // Teléfono
        datos_capitan[5] = cursor.getString(5); // Descripción
        datos_capitan[6] = cursor.getString(6); // UID
        datos_capitan[7] = cursor.getString(7); // Token
        datos_capitan[8] = cursor.getString(9); // Nombre de quién recibe

        db.close();
    }

    private void registrarPedidoBD() {
        String convertir = convertirPedido();
        String f = "folio_"+folio_pedido;
        SQLiteDatabase db = admin.getWritableDatabase();

        String insert = "insert into pedidos values(" +
                "'"+f+"'," +
                "'"+puerto.getSelectedItem().toString()+"'," +
                "'"+fecha.getText().toString()+"'," +
                "'"+hora.getText().toString()+"'," +
                "'"+convertir+"')";

        db.execSQL(insert);
        db.close();
    }

    private String convertirPedido() {
        String cadena = "";

        for (int i = 0; i < datos.size(); i++) {
            cadena += datos.get(i);

            if(i < datos.size()-1)
                cadena += ",";
        }

        return cadena;
    }

    private boolean validaAgregar() {
        boolean siguiente = true;

        String vProducto = producto.getText().toString();
        String vCantidad = cantidad.getText().toString();

        if(vProducto.isEmpty() || vProducto.length() < 4){
            producto.setError("El producto debe ser válido");
            siguiente = false;
        } else
            producto.setError(null);

        if(vCantidad.isEmpty() || vCantidad.equals("0")){
            cantidad.setError("La cantidad debe ser válida");
            siguiente = false;
        } else
            cantidad.setError(null);

        return siguiente;
    }

    private void registrarPedidoFB() {
        String folio = "FSMNX_"+folio_pedido;
        Pedido ped = new Pedido();
        ped.setFolio(folio);
        ped.setFecha(fecha.getText().toString());
        ped.setPuerto(puerto.getSelectedItem().toString());
        ped.setHora(hora.getText().toString());
        ped.setListaProductos(datos);
        ped.setStatus("Enviado");
        ref.child("usuarios").child(datos_capitan[6]).child("pedidos").child(folio).setValue(ped);
    }

    private void iniciarFB() {
        FirebaseApp.initializeApp(this);
        ref = FirebaseDatabase.getInstance().getReference();
    }

    private void limpiar() {
        fecha.setText("");
        hora.setText("");
        producto.setText("");
        cantidad.setText("");
        datos.clear();
        adapter1.notifyDataSetChanged();
    }

    private boolean validar() {
        boolean continua = true;

        // Datos
        String v_fecha = fecha.getText().toString();
        String v_hora = hora.getText().toString();

        if(v_fecha.isEmpty() || !v_fecha.contains("/")){
            fecha.setError("La fécha debe ser válida");
            continua = false;
        } else
            fecha.setError(null);

        if(v_hora.isEmpty() || !v_hora.contains(":")){
            hora.setError("La hora debe ser válida");
            continua = false;
        } else
            hora.setError(null);

        if(datos.size() < 1){
            Toast.makeText(this, "Agregue al menos 1 producto", Toast.LENGTH_SHORT).show();
            continua = false;
        }

        return continua;
    }

    protected void sendEmail() {
        String user = "jenifer.del.leon@hotmail.com"; //"shanock.charras@gmail.com";
        String asunto = "Nuevo Pedido del Capitán: " + datos_capitan[2];

        String mensaje = "<h3>Detalles del Receptor.</h3><br>";
        mensaje += "<ins>Nombre del Capitán:</ins> <b>" + datos_capitan[2] + "</b><br>";
        mensaje += "<ins>Nombre de la Naviera:</ins> <b>" + datos_capitan[0] + "</b><br>";
        mensaje += "<ins>Número del Buque:</ins> <b>" + datos_capitan[1] + "</b><br>";
        mensaje += "<ins>Nombre de quién recibe:</ins> <b>" + datos_capitan[8] + "</b><br>";
        mensaje += "<ins>Género de quién recibe:</ins> <b>" + datos_capitan[3] + "</b><br>";
        mensaje += "<ins>Teléfono de quién recibe :</ins> <b>" + datos_capitan[4] + "</b><br>";
        mensaje += "<ins>Descripción de quién recibe:</ins> <b>" + datos_capitan[5] + "</b><br><br><br>";

        mensaje += "<h3>Detalles del Pedido con Folio (<i>FSMNX_"+folio_pedido+"</i>).</h3><br>";
        mensaje += "<ins>Puerto:</ins> <b>" + puerto.getSelectedItem().toString() + "</b><br>";
        mensaje += "<ins>Fecha de llegada:</ins> <b>" + fecha.getText().toString() + "</b><br>";
        mensaje += "<ins>Hora de llegada:</ins> <b>" + hora.getText().toString() + "</b><br><br>";
        mensaje += "<ins>Producto(s):</ins><br>";

        for (int i = 0; i < datos.size(); i++) {
            mensaje += "<b>* " + datos.get(i) + "</b>.<br>";
        }

        mensaje += "<br><ins>***TOKEN PARA NOTIFICACIÓN PUSH***</ins><br><b>" + datos_capitan[7]+"</b>";

        GMailSender sender = new GMailSender(getApplicationContext());
        sender.enviarEmail(user, asunto, mensaje);
        Toast.makeText(getApplicationContext(), "Pedido enviado Exitosamente.", Toast.LENGTH_LONG).show();
    }
}
