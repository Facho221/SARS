package sars.dao;

import sars.database.DatabaseConnection;
import sars.model.Estancia;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EstanciaDAO {


    public int registrar(Estancia e) throws SQLException {
        String sql = """
            INSERT INTO estancia
              (destino, tipo_ingreso, desc_vehiculo, tiempo_max_minutos,
               dni_visitante, id_tag, id_vigilante)
            VALUES (?,?,?,?,?,?,?)
            RETURNING id_estancia
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, e.getDestino());
            ps.setString(2, e.getTipoIngreso());
            ps.setString(3, e.getDescVehiculo());
            ps.setInt(4, e.getTiempoMaxMinutos());
            ps.setString(5, e.getDniVisitante());
            ps.setInt(6, e.getIdTag());
            ps.setInt(7, e.getIdVigilante());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public List<Estancia> listarActivas() throws SQLException {
        String sql = """
            SELECT e.*, v.nombre AS nom_visitante, v.tipo AS tipo_visitante,
                   t.codigo_rfid
            FROM estancia e
            JOIN visitante v ON e.dni_visitante = v.dni
            JOIN tag       t ON e.id_tag = t.id_tag
            WHERE e.estado IN ('Normal','Advertencia','Alerta')
            ORDER BY e.hora_ingreso DESC
            """;
        return ejecutarConsulta(sql);
    }

    public List<Estancia> listarHistorial(LocalDateTime desde, LocalDateTime hasta,
                                          String estado) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT e.*, v.nombre AS nom_visitante, v.tipo AS tipo_visitante,
                   t.codigo_rfid
            FROM estancia e
            JOIN visitante v ON e.dni_visitante = v.dni
            JOIN tag       t ON e.id_tag = t.id_tag
            WHERE 1=1
            """);
        List<Object> params = new ArrayList<>();
        if (desde != null) { sql.append(" AND e.hora_ingreso >= ?"); params.add(desde); }
        if (hasta != null) { sql.append(" AND e.hora_ingreso <= ?"); params.add(hasta); }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND e.estado = ?"); params.add(estado);
        }
        sql.append(" ORDER BY e.hora_ingreso DESC LIMIT 500");

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof LocalDateTime) ps.setTimestamp(i+1, Timestamp.valueOf((LocalDateTime) p));
                else ps.setString(i+1, p.toString());
            }
            return mapear(ps.executeQuery());
        }
    }

    public void cerrar(int idEstancia) throws SQLException {
        String sql = "UPDATE estancia SET hora_salida=NOW(), estado='Finalizado' WHERE id_estancia=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idEstancia);
            ps.executeUpdate();
        }
        // Liberar tag
        String sqlTag = """
            UPDATE tag SET estado_tag='Disponible'
            WHERE id_tag = (SELECT id_tag FROM estancia WHERE id_estancia=?)
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sqlTag)) {
            ps.setInt(1, idEstancia);
            ps.executeUpdate();
        }
    }

    public void actualizarEstado(int idEstancia, String nuevoEstado) throws SQLException {
        String sql = "UPDATE estancia SET estado=? WHERE id_estancia=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idEstancia);
            ps.executeUpdate();
        }
    }

    private List<Estancia> ejecutarConsulta(String sql) throws SQLException {
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return mapear(rs);
        }
    }

    private List<Estancia> mapear(ResultSet rs) throws SQLException {
        List<Estancia> lista = new ArrayList<>();
        while (rs.next()) {
            Estancia e = new Estancia();
            e.setIdEstancia(rs.getInt("id_estancia"));
            Timestamp ti = rs.getTimestamp("hora_ingreso");
            if (ti != null) e.setHoraIngreso(ti.toLocalDateTime());
            Timestamp ts = rs.getTimestamp("hora_salida");
            if (ts != null) e.setHoraSalida(ts.toLocalDateTime());
            e.setEstado(rs.getString("estado"));
            e.setDestino(rs.getString("destino"));
            e.setTipoIngreso(rs.getString("tipo_ingreso"));
            e.setDescVehiculo(rs.getString("desc_vehiculo"));
            e.setTiempoMaxMinutos(rs.getInt("tiempo_max_minutos"));
            e.setDniVisitante(rs.getString("dni_visitante"));
            e.setIdTag(rs.getInt("id_tag"));
            e.setIdVigilante(rs.getInt("id_vigilante"));
            e.setNombreVisitante(rs.getString("nom_visitante"));
            e.setTipoVisitante(rs.getString("tipo_visitante"));
            e.setCodigoRfid(rs.getString("codigo_rfid"));
            lista.add(e);
        }
        return lista;
    }
}
