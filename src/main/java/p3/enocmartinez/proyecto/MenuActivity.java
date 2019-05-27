package p3.enocmartinez.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    LinearLayout nuevo_pedido, mis_pedidos, config;
    Intent intent;
    SharedPreferences preferences;
    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        id = preferences.getString("uID", "nati");

        nuevo_pedido = (LinearLayout) findViewById(R.id.nuevo_pedido);
        mis_pedidos = (LinearLayout) findViewById(R.id.mis_pedidos);
        config = (LinearLayout) findViewById(R.id.configuracion);

        nuevo_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), NuevoPedidoActivity.class);
                startActivity(intent);
            }
        });

        mis_pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), MisPedidosActivity.class);
                startActivity(intent);
            }
        });

        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
