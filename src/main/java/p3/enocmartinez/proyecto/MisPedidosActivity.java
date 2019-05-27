package p3.enocmartinez.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import p3.enocmartinez.proyecto.entidades.Pedido;

public class MisPedidosActivity extends AppCompatActivity {
    // Variables que se utilizarán
    ListView mis_pedidos;
    String uID;
    List<Pedido> listPedido = new ArrayList<Pedido>();
    ArrayAdapter<Pedido> adapterPedido;
    Pedido info_pedido;
    TextView no_pedido;
    Boolean existePedido;

    // Obtener referencias de la base de datos
    DatabaseReference ref;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_pedidos);

        preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        uID = preferences.getString("uID", "nati");
        existePedido = preferences.getBoolean("pedido", false);

        mis_pedidos = (ListView) findViewById(R.id.mis_pedidos_list);
        no_pedido = (TextView) findViewById(R.id.no_pedidos);

        // Inicialiazar FireBase
        iniciarFB();

        // Listar los pedidos
        listaPedidos();

        if(existePedido){
            no_pedido.setVisibility(View.GONE);
            mis_pedidos.setVisibility(View.VISIBLE);
        }

        // Controlar lista
        mis_pedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                info_pedido = (Pedido) adapterView.getItemAtPosition(i);
                ArrayList<String> arr = new ArrayList<>(info_pedido.getListaProductos());
                Intent intent = new Intent(MisPedidosActivity.this, InfoPedidoActivity.class);
                String[] datos = {info_pedido.getFolio(), info_pedido.getFecha(), info_pedido.getHora(), info_pedido.getPuerto(), info_pedido.getStatus()};
                intent.putExtra("objeto", datos);
                intent.putStringArrayListExtra("productos", arr);
                startActivity(intent);
            }
        });
    }

    private void listaPedidos() {
        ref.child("usuarios").child(uID).child("pedidos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPedido.clear();

                for(DataSnapshot objSnap: dataSnapshot.getChildren()){
                    Pedido pedido = objSnap.getValue(Pedido.class);
                    listPedido.add(pedido);

                    adapterPedido = new ArrayAdapter<Pedido>(MisPedidosActivity.this, android.R.layout.simple_list_item_1, listPedido);
                    mis_pedidos.setAdapter(adapterPedido);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void iniciarFB() {
        FirebaseApp.initializeApp(this);
        ref = FirebaseDatabase.getInstance().getReference();
    }
}
