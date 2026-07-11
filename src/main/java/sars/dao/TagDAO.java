package sars.dao;

import sars.database.DatabaseConnection;
import sars.model.Tag;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {

    public List<Tag> listarDisponibles() throws SQLException {
        List<Tag> lista = new ArrayList<>();
        String sql = "SELECT * FROM tag WHERE estado_tag='Disponible' ORDER BY id_tag";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Tag(rs.getInt("id_tag"), rs.getString("codigo_rfid"), rs.getString("estado_tag")));
            }
        }
        return lista;
    }

    public Tag buscarPorRfid(String codigoRfid) throws SQLException {
        String codigoLimpio = codigoRfid.trim().toUpperCase();

        System.out.println("DEBUG: Buscando en BD el código: [" + codigoLimpio + "] con longitud: " + codigoLimpio.length());

        String sql = "SELECT * FROM tag WHERE codigo_rfid=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigoLimpio);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("DEBUG: ¡ENCONTRADO!");
                return new Tag(rs.getInt("id_tag"), rs.getString("codigo_rfid"), rs.getString("estado_tag"));
            } else {
                System.out.println("DEBUG: NO se encontró en la BD.");
            }
        }
        return null;
    }

    public void asignar(int idTag) throws SQLException {
        ejecutar("UPDATE tag SET estado_tag='Asignado' WHERE id_tag=?", idTag);
    }

    public void liberar(int idTag) throws SQLException {
        ejecutar("UPDATE tag SET estado_tag='Disponible' WHERE id_tag=?", idTag);
    }

    private void ejecutar(String sql, int id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}


