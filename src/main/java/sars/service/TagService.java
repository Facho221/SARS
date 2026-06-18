package sars.service;

import sars.dao.TagDAO;
import sars.model.Tag;
import java.sql.SQLException;
import java.util.List;

public class TagService {

    private final TagDAO tagDAO = new TagDAO();

    public List<Tag> obtenerTagsDisponibles() throws SQLException {
        return tagDAO.listarDisponibles();
    }

    public Tag buscarPorRfid(String codigo) throws Exception {
        if (codigo == null || codigo.isEmpty()) {
            throw new Exception("Código RFID vacío.");
        }
        Tag tag = tagDAO.buscarPorRfid(codigo);
        if (tag == null) {
            throw new Exception("Tag no registrado en el sistema: " + codigo);
        }
        return tag;
    }

    public void liberarTag(int idTag) throws SQLException {
        tagDAO.liberar(idTag);
    }
}