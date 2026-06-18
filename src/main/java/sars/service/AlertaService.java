package sars.service;

import sars.dao.EstanciaDAO;
import sars.model.Estancia;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlertaService {

    private final EstanciaDAO estanciaDAO = new EstanciaDAO();

    public List<Estancia> verificarAlertas() throws SQLException {
        List<Estancia> alertadas = new ArrayList<>();
        List<Estancia> activas = estanciaDAO.listarActivas();

        for (Estancia e : activas) {
            if (e.getHoraIngreso() == null) continue;
            long mins = java.time.Duration.between(
                    e.getHoraIngreso(), LocalDateTime.now()).toMinutes();

            if (mins >= e.getTiempoMaxMinutos()) {
                estanciaDAO.actualizarEstado(e.getIdEstancia(), "Alerta");
                alertadas.add(e);
            } else if (mins >= e.getTiempoMaxMinutos() * 0.8) {
                estanciaDAO.actualizarEstado(e.getIdEstancia(), "Advertencia");
            }
        }
        return alertadas;
    }
}