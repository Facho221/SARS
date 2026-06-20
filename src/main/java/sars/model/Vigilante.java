package sars.model;

public class Vigilante {
    private int    idVigilante;
    private String nomVigilante;
    private String turno;
    private String usuario;
    private String contrasena;
    private String rol;

    public Vigilante() {}

    public int    getIdVigilante()           { return idVigilante; }
    public void   setIdVigilante(int v)      { this.idVigilante = v; }
    public String getNomVigilante()          { return nomVigilante; }
    public void   setNomVigilante(String v)  { this.nomVigilante = v; }
    public String getTurno()                 { return turno; }
    public void   setTurno(String v)         { this.turno = v; }
    public String getUsuario()               { return usuario; }
    public void   setUsuario(String v)       { this.usuario = v; }
    public String getContrasena()            { return contrasena; }
    public void   setContrasena(String v)    { this.contrasena = v; }
    public String getRol() { return rol; }
    public void setRol(String v) { rol = v; }
}
