package sars.dao;

import sars.database.DatabaseConnection;
import sars.model.Tag;
import sars.model.Vigilante;

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
        String sql = "SELECT * FROM tag WHERE codigo_rfid=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigoRfid);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new Tag(rs.getInt("id_tag"), rs.getString("codigo_rfid"), rs.getString("estado_tag"));
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
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
}



