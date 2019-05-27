package p3.enocmartinez.proyecto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import p3.enocmartinez.proyecto.utilidades.Utilidades;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    public ConexionSQLiteHelper(Context context) {
        super(context, "bd_usuarios", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Utilidades.CAPITAN);
        db.execSQL(Utilidades.PED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {
        db.execSQL("drop table if exists capitan");
        db.execSQL("drop table if exists pedidos");
        onCreate(db);
    }
}
