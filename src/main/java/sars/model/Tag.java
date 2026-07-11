package sars.model;

public class Tag {
    private int    idTag;
    private String codigoRfid;
    private String estadoTag;

    public Tag() {}
    public Tag(int idTag, String codigoRfid, String estadoTag) {
        this.idTag = idTag; this.codigoRfid = codigoRfid; this.estadoTag = estadoTag;
    }

    public int    getIdTag()              { return idTag; }
    public void   setIdTag(int v)         { this.idTag = v; }
    public String getCodigoRfid()         { return codigoRfid; }
    public void   setCodigoRfid(String v) { this.codigoRfid = v; }
    public String getEstadoTag()          { return estadoTag; }
    public void   setEstadoTag(String v)  { this.estadoTag = v; }

    @Override public String toString() { return codigoRfid + " [" + estadoTag + "]"; }
}
