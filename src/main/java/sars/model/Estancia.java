package sars.model;

import java.time.LocalDateTime;

public class Estancia {

    private int           idEstancia;
    private LocalDateTime horaIngreso;
    private LocalDateTime horaSalida;
    private String        estado;
    private String        destino;
    private String        tipoIngreso;
    private String        descVehiculo;
    private int           tiempoMaxMinutos;
    private String        dniVisitante;
    private int           idTag;
    private int           idVigilante;

    private String        nombreVisitante;
    private String        tipoVisitante;
    private String        codigoRfid;

    public Estancia() {}


    public int getIdEstancia()               { return idEstancia; }
    public void setIdEstancia(int v)         { this.idEstancia = v; }

    public LocalDateTime getHoraIngreso()    { return horaIngreso; }
    public void setHoraIngreso(LocalDateTime v) { this.horaIngreso = v; }

    public LocalDateTime getHoraSalida()     { return horaSalida; }
    public void setHoraSalida(LocalDateTime v)  { this.horaSalida = v; }

    public String getEstado()                { return estado; }
    public void setEstado(String v)          { this.estado = v; }

    public String getDestino()               { return destino; }
    public void setDestino(String v)         { this.destino = v; }

    public String getTipoIngreso()           { return tipoIngreso; }
    public void setTipoIngreso(String v)     { this.tipoIngreso = v; }

    public String getDescVehiculo()          { return descVehiculo; }
    public void setDescVehiculo(String v)    { this.descVehiculo = v; }

    public int getTiempoMaxMinutos()         { return tiempoMaxMinutos; }
    public void setTiempoMaxMinutos(int v)   { this.tiempoMaxMinutos = v; }

    public String getDniVisitante()          { return dniVisitante; }
    public void setDniVisitante(String v)    { this.dniVisitante = v; }

    public int getIdTag()                    { return idTag; }
    public void setIdTag(int v)              { this.idTag = v; }

    public int getIdVigilante()              { return idVigilante; }
    public void setIdVigilante(int v)        { this.idVigilante = v; }

    public String getNombreVisitante()       { return nombreVisitante; }
    public void setNombreVisitante(String v) { this.nombreVisitante = v; }

    public String getTipoVisitante()         { return tipoVisitante; }
    public void setTipoVisitante(String v)   { this.tipoVisitante = v; }

    public String getCodigoRfid()            { return codigoRfid; }
    public void setCodigoRfid(String v)      { this.codigoRfid = v; }
}
