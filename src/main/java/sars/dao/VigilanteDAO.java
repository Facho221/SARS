package sars.dao;

import org.mindrot.jbcrypt.BCrypt;
import sars.database.DatabaseConnection;
import sars.model.Vigilante;
import java.sql.*;

public class VigilanteDAO {

    public Vigilante login(String usuario, String contrasena) throws SQLException {
        String sql = "SELECT * FROM vigilante WHERE usuario=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashAlmacenado = rs.getString("contrasena");

                if (BCrypt.checkpw(contrasena, hashAlmacenado)) {
                    Vigilante vig = new Vigilante();
                    vig.setIdVigilante(rs.getInt("id_vigilante"));
                    vig.setNomVigilante(rs.getString("nom_vigilante"));
                    vig.setTurno(rs.getString("turno"));
                    vig.setUsuario(rs.getString("usuario"));
                    vig.setRol(rs.getString("rol"));
                    return vig;
                }
            }
        }
        return null;
    }
}