package sars.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import sars.dao.EstanciaDAO;
import sars.model.Estancia;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AuditoriaView {

    private final VBox root;
    private final ObservableList<Estancia> historialData = FXCollections.observableArrayList();
    private final EstanciaDAO dao = new EstanciaDAO();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DatePicker dpDesde, dpHasta;
    private ComboBox<String> cbEstado;

    private Label lblTotal, lblAlertas, lblAtendidas, lblPromedio;

    public AuditoriaView() {
        root = new VBox(16);
        root.setStyle("-fx-background-color: #0D1117; -fx-padding: 20;");

        root.getChildren().addAll(
                buildHeader(),
                buildMetricas(),
                buildFiltros(),
                buildTabla()
        );
        cargar();
    }

    private HBox buildHeader() {
        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER_LEFT);
        Text t = new Text("Auditoría e Historial");
        t.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-fill:#E6EDF3;");
        hb.getChildren().add(t);
        return hb;
    }

    private HBox buildMetricas() {
        lblTotal     = metricLabel("0");
        lblAlertas   = metricLabel("0");
        lblAtendidas = metricLabel("0");
        lblPromedio  = metricLabel("0 min");

        HBox row = new HBox(12,
                metricCard("Total Registros",   lblTotal,     "#1F6FEB"),
                metricCard("Alertas Generadas", lblAlertas,   "#E84040"),
                metricCard("Finalizadas OK",     lblAtendidas, "#00C896"),
                metricCard("Tiempo Promedio",    lblPromedio,  "#F5A623")
        );
        return row;
    }

    private VBox buildFiltros() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Text titulo = new Text("Filtros de búsqueda");
        titulo.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-fill:#8B949E;");

        HBox fila = new HBox(12);
        fila.setAlignment(Pos.CENTER_LEFT);

        dpDesde = new DatePicker(LocalDate.now().minusDays(7));
        dpDesde.setStyle("-fx-background-color:#1C2128; -fx-border-color:#30363D; -fx-border-radius:6;");

        dpHasta = new DatePicker(LocalDate.now());
        dpHasta.setStyle("-fx-background-color:#1C2128; -fx-border-color:#30363D; -fx-border-radius:6;");

        cbEstado = new ComboBox<>();
        cbEstado.getItems().addAll("Todos","Normal","Advertencia","Alerta","Finalizado");
        cbEstado.getSelectionModel().select("Todos");
        cbEstado.setStyle("-fx-background-color:#1C2128;");

        Button btnFiltrar = new Button("Aplicar filtros");
        btnFiltrar.getStyleClass().add("btn-primary");
        btnFiltrar.setOnAction(e -> cargar());

        Button btnExportPdf = new Button("⬇ Exportar PDF");
        btnExportPdf.getStyleClass().add("btn-secondary");
        btnExportPdf.setOnAction(e -> exportarPdf());

        Button btnExportCsv = new Button("⬇ Exportar CSV");
        btnExportCsv.getStyleClass().add("btn-secondary");
        btnExportCsv.setOnAction(e -> exportarCsv());

        fila.getChildren().addAll(
                labeled("Desde:", dpDesde),
                labeled("Hasta:", dpHasta),
                labeled("Estado:", cbEstado),
                btnFiltrar,
                new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }},
                btnExportCsv, btnExportPdf
        );

        card.getChildren().addAll(titulo, fila);
        return card;
    }

    private VBox buildTabla() {
        VBox box = new VBox(8);
        box.getStyleClass().add("card");
        VBox.setVgrow(box, Priority.ALWAYS);

        TableView<Estancia> tabla = new TableView<>(historialData);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        tabla.getColumns().addAll(
                colSimple("ID",        "idEstancia",      55),
                colSimple("Visitante", "nombreVisitante", 140),
                colSimple("DNI",       "dniVisitante",    80),
                colSimple("Tipo",      "tipoVisitante",   90),
                colSimple("Destino",   "destino",         120),
                colFecha("Ingreso",  true),
                colFecha("Salida",   false),
                colDuracion(),
                colEstadoBadge(),
                colSimple("Tag", "codigoRfid", 90)
        );

        box.getChildren().addAll(
                new Text("Historial de estancias") {{
                    setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-fill:#E6EDF3;");
                }},
                tabla
        );
        return box;
    }

    private <T> TableColumn<Estancia, T> colSimple(String h, String prop, int w) {
        TableColumn<Estancia, T> c = new TableColumn<>(h);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setMinWidth(w);
        return c;
    }

    private TableColumn<Estancia, String> colFecha(String header, boolean ingreso) {
        TableColumn<Estancia, String> col = new TableColumn<>(header);
        col.setMinWidth(120);
        col.setCellValueFactory(cd -> {
            LocalDateTime dt = ingreso ? cd.getValue().getHoraIngreso() : cd.getValue().getHoraSalida();
            return new SimpleStringProperty(dt != null ? dt.format(FMT) : "—");
        });
        return col;
    }

    private TableColumn<Estancia, String> colDuracion() {
        TableColumn<Estancia, String> col = new TableColumn<>("Duración");
        col.setMinWidth(80);
        col.setCellValueFactory(cd -> {
            Estancia e = cd.getValue();
            if (e.getHoraIngreso() == null) return new SimpleStringProperty("—");
            LocalDateTime fin = e.getHoraSalida() != null ? e.getHoraSalida() : LocalDateTime.now();
            long mins = java.time.Duration.between(e.getHoraIngreso(), fin).toMinutes();
            return new SimpleStringProperty(mins + " min");
        });
        return col;
    }

    private TableColumn<Estancia, String> colEstadoBadge() {
        TableColumn<Estancia, String> col = new TableColumn<>("Estado");
        col.setMinWidth(100);
        col.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                String estado = getTableRow().getItem().getEstado();
                Label badge = new Label(estado);
                switch (estado) {
                    case "Normal"      -> badge.getStyleClass().add("badge-normal");
                    case "Advertencia" -> badge.getStyleClass().add("badge-advertencia");
                    case "Alerta"      -> badge.getStyleClass().add("badge-alerta");
                    default            -> badge.getStyleClass().add("badge-finalizado");
                }
                setGraphic(badge);
            }
        });
        col.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getEstado()));
        return col;
    }

    private void cargar() {
        try {
            LocalDateTime desde = dpDesde.getValue().atStartOfDay();
            LocalDateTime hasta = dpHasta.getValue().atTime(23, 59, 59);
            String estado = "Todos".equals(cbEstado.getValue()) ? null : cbEstado.getValue();

            List<Estancia> lista = dao.listarHistorial(desde, hasta, estado);
            historialData.setAll(lista);
            actualizarMetricas(lista);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void actualizarMetricas(List<Estancia> lista) {
        long alertas    = lista.stream().filter(e -> "Alerta".equals(e.getEstado())).count();
        long finalizadas= lista.stream().filter(e -> "Finalizado".equals(e.getEstado())).count();
        double promedio = lista.stream()
                .filter(e -> e.getHoraIngreso() != null && e.getHoraSalida() != null)
                .mapToLong(e -> java.time.Duration.between(e.getHoraIngreso(), e.getHoraSalida()).toMinutes())
                .average().orElse(0);

        lblTotal.setText(String.valueOf(lista.size()));
        lblAlertas.setText(String.valueOf(alertas));
        lblAtendidas.setText(String.valueOf(finalizadas));
        lblPromedio.setText(String.format("%.0f min", promedio));
    }

    private void exportarCsv() {
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Guardar CSV");
        fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV","*.csv"));
        fc.setInitialFileName("sars_historial.csv");

        javafx.stage.Window ventana = null;
        if (root != null && root.getScene() != null) {
            ventana = root.getScene().getWindow();
        }

        java.io.File archivo = fc.showSaveDialog(ventana);
        if (archivo == null) return;

        try (java.io.PrintWriter pw = new java.io.PrintWriter(archivo)) {
            pw.println("ID,Visitante,DNI,Tipo,Destino,Ingreso,Salida,Duracion,Estado,Tag");
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Estancia e : historialData) {
                LocalDateTime fin = e.getHoraSalida() != null ? e.getHoraSalida() : LocalDateTime.now();
                long mins = e.getHoraIngreso() != null
                        ? java.time.Duration.between(e.getHoraIngreso(), fin).toMinutes() : 0;
                pw.printf("%d,%s,%s,%s,%s,%s,%s,%d min,%s,%s%n",
                        e.getIdEstancia(),
                        e.getNombreVisitante(), e.getDniVisitante(), e.getTipoVisitante(),
                        e.getDestino(),
                        e.getHoraIngreso() != null ? e.getHoraIngreso().format(f) : "",
                        e.getHoraSalida()  != null ? e.getHoraSalida().format(f) : "",
                        mins, e.getEstado(), e.getCodigoRfid());
            }
            alert("Exportado", "CSV guardado en:\n" + archivo.getAbsolutePath());
        } catch (Exception ex) { alert("Error", ex.getMessage()); }
    }

    private void exportarPdf() {
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Guardar Reporte PDF");
        fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF", "*.pdf"));
        fc.setInitialFileName("sars_reporte_auditoria.pdf");

        javafx.stage.Window ventana = null;
        if (root != null && root.getScene() != null) {
            ventana = root.getScene().getWindow();
        }

        File archivo = fc.showSaveDialog(ventana);
        if (archivo == null) return;

        Document documento = new Document();
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            documento.add(new Paragraph("SMART-ACCESS RESIDENTIAL SYSTEM (SARS)"));
            documento.add(new Paragraph("REPORTE CONSOLIDADO DE AUDITORIA E HISTORIAL"));
            documento.add(new Paragraph("Fecha de reporte: " + LocalDateTime.now().format(FMT)));
            documento.add(new Paragraph("Total registros encontrados: " + historialData.size() + "\n\n"));

            PdfPTable tablaPDF = new PdfPTable(10);
            tablaPDF.setWidthPercentage(100);

            tablaPDF.addCell("ID");
            tablaPDF.addCell("Visitante");
            tablaPDF.addCell("DNI");
            tablaPDF.addCell("Tipo");
            tablaPDF.addCell("Destino");
            tablaPDF.addCell("Ingreso");
            tablaPDF.addCell("Salida");
            tablaPDF.addCell("Duracion");
            tablaPDF.addCell("Estado");
            tablaPDF.addCell("Tag");

            for (Estancia e : historialData) {
                LocalDateTime fin = e.getHoraSalida() != null ? e.getHoraSalida() : LocalDateTime.now();
                long mins = e.getHoraIngreso() != null ? java.time.Duration.between(e.getHoraIngreso(), fin).toMinutes() : 0;

                tablaPDF.addCell(String.valueOf(e.getIdEstancia()));
                tablaPDF.addCell(e.getNombreVisitante() != null ? e.getNombreVisitante() : "");
                tablaPDF.addCell(e.getDniVisitante() != null ? e.getDniVisitante() : "");
                tablaPDF.addCell(e.getTipoVisitante() != null ? e.getTipoVisitante() : "");
                tablaPDF.addCell(e.getDestino() != null ? e.getDestino() : "");
                tablaPDF.addCell(e.getHoraIngreso() != null ? e.getHoraIngreso().format(FMT) : "");
                tablaPDF.addCell(e.getHoraSalida() != null ? e.getHoraSalida().format(FMT) : "");
                tablaPDF.addCell(mins + " min");
                tablaPDF.addCell(e.getEstado() != null ? e.getEstado() : "");
                tablaPDF.addCell(e.getCodigoRfid() != null ? e.getCodigoRfid() : "");
            }

            documento.add(tablaPDF);
            alert("Exportado", "PDF generado con éxito en:\n" + archivo.getAbsolutePath());
        } catch (Exception ex) {
            alert("Error", "No se pudo generar el archivo PDF: " + ex.getMessage());
        } finally {
            documento.close();
        }
    }

    private HBox labeled(String label, Control control) {
        Label lbl = new Label(label); lbl.setStyle("-fx-font-size:11px; -fx-text-fill:#8B949E;");
        VBox box = new VBox(3, lbl, control);
        return new HBox(box);
    }

    private VBox metricCard(String label, Label valLabel, String color) {
        valLabel.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:" + color + ";");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:10px; -fx-text-fill:#8B949E; -fx-font-weight:bold;");
        VBox card = new VBox(3, lbl, valLabel);
        card.setStyle("-fx-background-color:#1C2128; -fx-background-radius:8; -fx-padding:14;");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private Label metricLabel(String val) {
        Label l = new Label(val);
        l.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:#E6EDF3;");
        return l;
    }

    private void alert(String t, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    public VBox getRoot() { return root; }
}
