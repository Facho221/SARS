package sars.service;

import sars.dao.EstanciaDAO;
import sars.dao.TagDAO;
import sars.dao.VisitanteDAO;
import sars.model.Estancia;
import sars.model.Tag;
import sars.model.Visitante;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class EstanciaService {

    private final EstanciaDAO estanciaDAO = new EstanciaDAO();
    private final TagDAO tagDAO = new TagDAO();
    private final VisitanteDAO visitanteDAO = new VisitanteDAO();

    private void validarDni(String dni) throws Exception {
        if (dni == null || !dni.matches("[0-9]{8}")) {
            throw new Exception("DNI inválido. Debe contener exactamente 8 dígitos numéricos.");
        }
    }

    public int registrarIngreso(String dni, String nombre, String tipo, String subtipo,
                                String destino, String tipoIngreso, String descVehiculo,
                                int tiempoMax, int idVigilante, Tag tagLeido) throws Exception {

        validarDni(dni);

        Visitante existente = visitanteDAO.buscarPorDni(dni);
        if (existente != null) {
            List<Estancia> activas = estanciaDAO.listarActivas();
            for (Estancia e : activas) {
                if (e.getDniVisitante().equals(dni)) {
                    throw new Exception("El visitante ya tiene una estancia activa.");
                }
            }
        }

        Tag tag = tagLeido;
        if (tag == null) {
            List<Tag> disponibles = tagDAO.listarDisponibles();
            if (disponibles.isEmpty()) throw new Exception("No hay tags RFID disponibles.");
            tag = disponibles.get(0);
        }

        if ("Asignado".equals(tag.getEstadoTag())) {
            throw new Exception("El tag seleccionado ya está asignado a otra estancia.");
        }

        Visitante v = new Visitante(dni, nombre, tipo, subtipo, null);
        visitanteDAO.registrar(v);

        Estancia e = new Estancia();
        e.setDniVisitante(dni);
        e.setDestino(destino);
        e.setTipoIngreso(tipoIngreso);
        e.setDescVehiculo(descVehiculo);
        e.setTiempoMaxMinutos(tiempoMax);
        e.setIdTag(tag.getIdTag());
        e.setIdVigilante(idVigilante);

        int id = estanciaDAO.registrar(e);
        tagDAO.asignar(tag.getIdTag());
        return id;
    }

    public void cerrarEstancia(int idEstancia) throws SQLException {
        estanciaDAO.cerrar(idEstancia);
    }

    public List<Estancia> obtenerActivas() throws SQLException {
        return estanciaDAO.listarActivas();
    }

    public List<Estancia> obtenerHistorial(LocalDateTime desde, LocalDateTime hasta,
                                           String estado) throws SQLException {
        return estanciaDAO.listarHistorial(desde, hasta, estado);
    }
}