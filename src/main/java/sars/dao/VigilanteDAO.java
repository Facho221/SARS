package sars.dao;

import sars.database.DatabaseConnection;
import sars.model.Vigilante;
import java.sql.*;

public class VigilanteDAO {

    public Vigilante login(String usuario, String contrasena) throws SQLException {
        String sql = "SELECT * FROM vigilante WHERE usuario=? AND contrasena=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Vigilante vig = new Vigilante();
                vig.setIdVigilante(rs.getInt("id_vigilante"));
                vig.setNomVigilante(rs.getString("nom_vigilante"));
                vig.setTurno(rs.getString("turno"));
                vig.setUsuario(rs.getString("usuario"));
                return vig;
            }
        }
        return null;
    }
}