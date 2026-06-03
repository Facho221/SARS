package sars.dao;

import sars.database.DatabaseConnection;
import sars.model.Visitante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitanteDAO {

    public void registrar(Visitante v) throws SQLException {
        String sql = "INSERT INTO visitante (dni,nombre,tipo,subtipo,telefono) VALUES (?,?,?,?,?) ON CONFLICT (dni) DO NOTHING";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, v.getDni());
            ps.setString(2, v.getNombre());
            ps.setString(3, v.getTipo());
            ps.setString(4, v.getSubtipo());
            ps.setString(5, v.getTelefono());
            ps.executeUpdate();
        }
    }

    public Visitante buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT * FROM visitante WHERE dni=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Visitante v = new Visitante();
                v.setDni(rs.getString("dni"));
                v.setNombre(rs.getString("nombre"));
                v.setTipo(rs.getString("tipo"));
                v.setSubtipo(rs.getString("subtipo"));
                v.setTelefono(rs.getString("telefono"));
                return v;
            }
        }
        return null;
    }

    public List<Visitante> listarTodos() throws SQLException {
        List<Visitante> lista = new ArrayList<>();
        String sql = "SELECT * FROM visitante ORDER BY nombre";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Visitante v = new Visitante();
                v.setDni(rs.getString("dni"));
                v.setNombre(rs.getString("nombre"));
                v.setTipo(rs.getString("tipo"));
                v.setSubtipo(rs.getString("subtipo"));
                v.setTelefono(rs.getString("telefono"));
                lista.add(v);
            }
        }
        return lista;
    }
}
