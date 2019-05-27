package p3.enocmartinez.proyecto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import p3.enocmartinez.proyecto.entidades.Pedido;

public class InfoPedidoActivity extends AppCompatActivity {
    // Declarar variables
    TextView folio, fecha, hora, puerto, productos, status, nom_prod, cant_prod, cost_prod, tot;
    String[] pedido;
    ArrayList<String> prod;
    String uID;

    // Obtener referencias de la base de datos
    DatabaseReference ref;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pedido);

        preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        uID = preferences.getString("uID", "nati");

        // Castear Variables
        folio = (TextView) findViewById(R.id.folio_info);
        fecha = (TextView) findViewById(R.id.fecha_info);
        hora = (TextView) findViewById(R.id.hora_info);
        puerto = (TextView) findViewById(R.id.puerto_info);
        productos = (TextView) findViewById(R.id.productos_info);
        status = (TextView) findViewById(R.id.status_info);
        nom_prod = (TextView) findViewById(R.id.nom_prod);
        cant_prod = (TextView) findViewById(R.id.cant_prod);
        cost_prod = (TextView) findViewById(R.id.cost_prod);
        tot = (TextView) findViewById(R.id.total);

        // Inicializar FireBase
        iniciarFB();

        // Obtener productos del bundle
        pedido = getIntent().getStringArrayExtra("objeto");
        prod = new ArrayList<>(getIntent().getStringArrayListExtra("productos"));


        // Insertar en los textView
        insertarProducto();

        ref.child("usuarios").child(uID).child("pedidos").child(pedido[0]).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status.setText(Html.fromHtml("<b>Status:</b> " + dataSnapshot.getValue().toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InfoPedidoActivity.this, "Ha ocurrido un error " + databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertarProducto() {
        productos.setText(Html.fromHtml("<b>Productos:</b>"));
        folio.setText(Html.fromHtml("<b>Folio:</b> " + pedido[0]));
        fecha.setText(Html.fromHtml("<b>Fecha:</b> " + pedido[1]));
        hora.setText(Html.fromHtml("<b>Hora:</b> " + pedido[2]));
        puerto.setText(Html.fromHtml("<b>Puerto:</b> " + pedido[3]));
        status.setText(Html.fromHtml("<b>Status:</b> " + pedido[4]));
        insertaDetalle();
    }

    private void insertaDetalle() {
        String name = "", pz = "", price = "";
        float total = 500.0f;

        for (int i = 0; i < prod.size(); i++) {
            String[] data = prod.get(i).split("-");

            if(data.length == 2){
                name += data[0] + "\n";
                pz += data[1] + "\n";
                price += "-" + "\n";
            } else if (data.length == 3) {
                name += data[0] + "\n";
                pz += data[1] + "\n";
                price += "$" + Float.parseFloat(data[2]) + "\n";
                float r = Float.parseFloat(data[2]) * Float.parseFloat(data[1]);
                total += r;
            }
        }

        nom_prod.setText(name);
        cant_prod.setText(pz);
        cost_prod.setText(price);
        tot.setText("$"+total);
    }

    private void iniciarFB() {
        FirebaseApp.initializeApp(this);
        ref = FirebaseDatabase.getInstance().getReference();
    }
}
