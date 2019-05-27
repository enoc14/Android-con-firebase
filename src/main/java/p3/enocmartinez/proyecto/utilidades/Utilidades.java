package p3.enocmartinez.proyecto.utilidades;

public class Utilidades {

    // Constantes de los campos para la tabla Capitán
    public static final String NAVIERA = "naviera";
    public static final String BUQUE = "buque";
    public static final String NOMBRE = "nombre";
    public static final String GENERO = "genero";
    public static final String TELEFONO = "telefono";
    public static final String DESCRIPCION = "info";
    public static final String UID = "uid";
    public static final String TOKEN = "token";
    public static final String HACK = "hack";
    public static final String RECIBE = "recibe";

    // Constantes de los campos para la tabla Pedidos
    public static final String PEDIDOS = "pedidos";
    public static final String PUERTO = "puerto";
    public static final String FECHA = "fecha";
    public static final String HORA = "hora";
    public static final String FOLIO = "folio";
    public static final String LISTA = "lista";

    // Creación de tabla Capitán
    public static final String CAPITAN =
            "create table capitan ("+NAVIERA+" TEXT, "+BUQUE+" TEXT, "+NOMBRE+" TEXT, " +
                    GENERO+"  TEXT, "+TELEFONO+" TEXT, "+DESCRIPCION+" TEXT, "+
                    UID+" TEXT, "+TOKEN+" TEXT, "+HACK+" TEXT, "+RECIBE+" TEXT)";

    // Creación de tabla Pedidos
    public static final String PED =
            "create table "+PEDIDOS+" ("+FOLIO+" TEXT, "+PUERTO+" TEXT, "+FECHA+" TEXT, " +
                    HORA+" TEXT, "+LISTA+" TEXT)";

    // Consulta de datos del capitán
    public static final String CONSULTA_CAPITAN = "select * from capitan";
    public static final String CONSULTA_CAPITAN2 = "select token from capitan";

    //
}
