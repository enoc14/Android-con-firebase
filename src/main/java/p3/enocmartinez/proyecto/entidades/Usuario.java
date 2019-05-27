package p3.enocmartinez.proyecto.entidades;

public class Usuario {
    private String uid;
    private String naviera;
    private String buque;
    private String capitan;
    private String genero;
    private String telefono;
    private String descripcion;
    private String token;
    private String recibe;

    public String getRecibe() {
        return recibe;
    }

    public void setRecibe(String recibe) {
        this.recibe = recibe;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuario(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNaviera() {
        return naviera;
    }

    public void setNaviera(String naviera) {
        this.naviera = naviera;
    }

    public String getBuque() {
        return buque;
    }

    public void setBuque(String buque) {
        this.buque = buque;
    }

    public String getCapitan() {
        return capitan;
    }

    public void setCapitan(String capitan) {
        this.capitan = capitan;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
