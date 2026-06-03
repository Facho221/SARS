package sars.model;

public class Visitante {
    private String dni;
    private String nombre;
    private String tipo;
    private String subtipo;
    private String telefono;

    public Visitante() {}
    public Visitante(String dni, String nombre, String tipo, String subtipo, String telefono) {
        this.dni = dni; this.nombre = nombre; this.tipo = tipo;
        this.subtipo = subtipo; this.telefono = telefono;
    }

    public String getDni()             { return dni; }
    public void setDni(String v)       { this.dni = v; }
    public String getNombre()          { return nombre; }
    public void setNombre(String v)    { this.nombre = v; }
    public String getTipo()            { return tipo; }
    public void setTipo(String v)      { this.tipo = v; }
    public String getSubtipo()         { return subtipo; }
    public void setSubtipo(String v)   { this.subtipo = v; }
    public String getTelefono()        { return telefono; }
    public void setTelefono(String v)  { this.telefono = v; }

    @Override public String toString() { return nombre + " (" + dni + ")"; }
}
