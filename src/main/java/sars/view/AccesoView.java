package sars.view;

import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sars.arduino.ArduinoReader;
import sars.dao.TagDAO;
import sars.dao.VisitanteDAO;
import sars.model.*;
import sars.service.AlertaService;
import sars.service.EstanciaService;
import sars.service.TagService;
import javafx.application.Platform;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AccesoView {

    private final BorderPane root;
    private final Vigilante  vigilante;

    private final ObservableList<Estancia> estanciasData = FXCollections.observableArrayList();
    private final EstanciaService estanciaService = new EstanciaService();
    private final TagService      tagService      = new TagService();
    private final AlertaService   alertaService   = new AlertaService();
    private final VisitanteDAO    visitanteDAO    = new VisitanteDAO();
    private final TagDAO          tagDAO          = new TagDAO();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private TextField    txtDni, txtNombre, txtDestino, txtDescVehiculo;
    private ComboBox<String> cbTipo, cbSubtipo, cbTipoIngreso, cbTiempoMax, cbPuerto;
    private Label        lblRfidLeido;
    private Tag          tagSeleccionado;
    private ArduinoReader arduino;

    public AccesoView(Vigilante vigilante) {
        this.vigilante = vigilante;
        root = new BorderPane();
        root.setStyle("-fx-background-color: #0D1117; -fx-padding: 20;");
        root.setLeft(buildFormulario());
        root.setCenter(buildTabla());
        iniciarMonitoreo();
    }

    private VBox buildFormulario() {
        VBox form = new VBox(12);
        form.getStyleClass().add("card");
        form.setPrefWidth(320);
        form.setMaxWidth(320);
        BorderPane.setMargin(form, new Insets(0, 16, 0, 0));

        Text title = new Text("Registro de Ingreso");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: #E6EDF3;");

        Separator sep = new Separator();

        Text rfidLabel = new Text("Lectura RFID");
        rfidLabel.setStyle("-fx-font-size: 11px; -fx-fill: #8B949E; -fx-font-weight: bold;");

        lblRfidLeido = new Label("Sin lectura");
        lblRfidLeido.getStyleClass().add("rfid-display");
        lblRfidLeido.setMaxWidth(Double.MAX_VALUE);

        cbPuerto = new ComboBox<>();
        cbPuerto.getItems().addAll(ArduinoReader.listarPuertos());
        if (!cbPuerto.getItems().isEmpty()) cbPuerto.getSelectionModel().selectFirst();
        cbPuerto.setPromptText("Puerto Arduino");
        cbPuerto.setMaxWidth(Double.MAX_VALUE);

        Button btnConectar = new Button("Conectar Arduino");
        btnConectar.getStyleClass().add("btn-secondary");
        btnConectar.setMaxWidth(Double.MAX_VALUE);
        btnConectar.setOnAction(e -> conectarArduino(cbPuerto.getValue(), btnConectar));

        txtDni = field("DNI *", "12345678");
        txtDni.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused && !txtDni.getText().isEmpty()) autocompletarVisitante();
        });

        txtNombre       = field("Nombre completo *", "Ej: Juan Pérez");
        cbTipo          = combo("Tipo de visita *", "Familiar","Delivery","Proveedor","Residente","Otro");
        cbSubtipo       = combo("Subtipo", "Externo","Residente","Negocio");
        txtDestino      = field("Destino (Lote/Dpto) *", "Ej: Lote 12 / Dpto 3B");
        cbTipoIngreso   = combo("Tipo de ingreso *", "Peatón","Vehículo");
        txtDescVehiculo = field("Desc. vehículo (opcional)", "Placa, color, modelo");
        txtDescVehiculo.setDisable(true);

        cbTipoIngreso.valueProperty().addListener((obs, old, val) ->
                txtDescVehiculo.setDisable(!"Vehículo".equals(val)));

        cbTiempoMax = combo("Tiempo máximo (min) *", "15","30","60","90","120");
        cbTiempoMax.getSelectionModel().select("60");

        Button btnRegistrar = new Button("Registrar y Vincular Tag");
        btnRegistrar.getStyleClass().add("btn-primary");
        btnRegistrar.setMaxWidth(Double.MAX_VALUE);
        btnRegistrar.setPrefHeight(42);
        btnRegistrar.setOnAction(e -> registrarEstancia());

        Button btnLimpiar = new Button("Limpiar formulario");
        btnLimpiar.getStyleClass().add("btn-secondary");
        btnLimpiar.setMaxWidth(Double.MAX_VALUE);
        btnLimpiar.setOnAction(e -> limpiarFormulario());

        form.getChildren().addAll(
                title, sep,
                rfidLabel, lblRfidLeido, cbPuerto, btnConectar,
                new Separator(),
                labeledField("DNI *", txtDni),
                labeledField("Nombre completo *", txtNombre),
                labeledField("Tipo de visita *", cbTipo),
                labeledField("Subtipo", cbSubtipo),
                labeledField("Destino *", txtDestino),
                labeledField("Ingreso *", cbTipoIngreso),
                labeledField("Vehículo", txtDescVehiculo),
                labeledField("Tiempo máx. (min)", cbTiempoMax),
                btnRegistrar, btnLimpiar
        );
        return form;
    }

    private VBox buildTabla() {
        VBox box = new VBox(12);
        box.getStyleClass().add("card");

        HBox metricas = new HBox(12);
        metricas.getChildren().addAll(
                metricCard("Activas", "0", "#00C896"),
                metricCard("Alertas", "0", "#E84040"),
                metricCard("Hoy Total", "0", "#1F6FEB")
        );

        TableView<Estancia> tabla = new TableView<>(estanciasData);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.getStyleClass().add("table-view");
        VBox.setVgrow(tabla, Priority.ALWAYS);

        tabla.getColumns().addAll(
                col("ID",        "idEstancia",      60),
                col("Visitante", "nombreVisitante", 150),
                col("Tipo",      "tipoVisitante",   90),
                col("Destino",   "destino",         120),
                col("Tag",       "codigoRfid",      90),
                colHora(),
                colTiempo(),
                colEstado(),
                colAccion()
        );

        box.getChildren().addAll(
                new Text("{{ Estancias Activas }}") {{
                    setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-fill:#E6EDF3;");
                }},
                metricas, tabla
        );
        return box;
    }

    private TableColumn<Estancia, String> colHora() {
        TableColumn<Estancia, String> col = new TableColumn<>("Ingreso");
        col.setMinWidth(75);
        col.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHoraIngreso() != null
                        ? cd.getValue().getHoraIngreso().format(FMT) : "-"));
        return col;
    }

    private TableColumn<Estancia, String> colTiempo() {
        TableColumn<Estancia, String> col = new TableColumn<>("Tiempo");
        col.setMinWidth(70);
        col.setCellValueFactory(cd -> {
            if (cd.getValue().getHoraIngreso() == null) return new SimpleStringProperty("-");
            long mins = java.time.Duration.between(
                    cd.getValue().getHoraIngreso(), java.time.LocalDateTime.now()).toMinutes();
            return new SimpleStringProperty(mins + " min");
        });
        return col;
    }

    private TableColumn<Estancia, String> colEstado() {
        TableColumn<Estancia, String> col = new TableColumn<>("Estado");
        col.setMinWidth(100);
        col.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null); return;
                }
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

    private TableColumn<Estancia, Void> colAccion() {
        TableColumn<Estancia, Void> col = new TableColumn<>("Acción");
        col.setMinWidth(90);
        col.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("Cerrar");
            { btn.getStyleClass().add("btn-danger");
                btn.setOnAction(e -> cerrarEstancia(getTableRow().getItem())); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });
        return col;
    }

    private void conectarArduino(String puerto, Button btnConectar) {
        if (puerto == null || puerto.isEmpty()) {
            alert("Sin puerto", "Selecciona un puerto serial."); return;
        }
        arduino = new ArduinoReader(puerto, this::onTagLeido);
        arduino.iniciar();
        btnConectar.setText("✓ Conectado: " + puerto);
        btnConectar.setDisable(true);
    }

    private void onTagLeido(String codigoRfid) {
        lblRfidLeido.setText(codigoRfid);
        lblRfidLeido.getStyleClass().add("rfid-flash");
        new Timeline(new KeyFrame(Duration.seconds(1), e ->
                lblRfidLeido.getStyleClass().remove("rfid-flash"))).play();
        try {
            tagSeleccionado = tagService.buscarPorRfid(codigoRfid);
            if (tagSeleccionado == null)
                lblRfidLeido.setText("⚠ Tag no registrado: " + codigoRfid);
            else if ("Asignado".equals(tagSeleccionado.getEstadoTag()))
                lblRfidLeido.setText("⚠ Tag ya asignado: " + codigoRfid);
        } catch (Exception ex) {
            lblRfidLeido.setText("⚠ " + ex.getMessage());
        }
    }

    private void registrarEstancia() {
        if (!validar()) return;
        try {
            estanciaService.registrarIngreso(
                    txtDni.getText().trim(),
                    txtNombre.getText().trim(),
                    cbTipo.getValue(),
                    cbSubtipo.getValue(),
                    txtDestino.getText().trim(),
                    cbTipoIngreso.getValue(),
                    txtDescVehiculo.getText().trim(),
                    Integer.parseInt(cbTiempoMax.getValue()),
                    vigilante.getIdVigilante(),
                    tagSeleccionado
            );
            limpiarFormulario();
            cargarEstancias();
        } catch (Exception ex) {
            alert("Error", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cerrarEstancia(Estancia e) {
        if (e == null) return;
        try {
            estanciaService.cerrarEstancia(e.getIdEstancia());
            cargarEstancias();
        } catch (Exception ex) { alert("Error", ex.getMessage()); }
    }

    private void autocompletarVisitante() {
        try {
            Visitante v = visitanteDAO.buscarPorDni(txtDni.getText().trim());
            if (v != null) {
                txtNombre.setText(v.getNombre());
                cbTipo.setValue(v.getTipo());
                if (v.getSubtipo() != null) cbSubtipo.setValue(v.getSubtipo());
            }
        } catch (Exception ignored) {}
    }

    private void cargarEstancias() {
        try {
            estanciasData.setAll(estanciaService.obtenerActivas());
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void iniciarMonitoreo() {
        cargarEstancias();
        Timeline monitoreo = new Timeline(new KeyFrame(Duration.seconds(30), e -> {
            verificarAlertas();
            cargarEstancias();
        }));
        monitoreo.setCycleCount(Animation.INDEFINITE);
        monitoreo.play();
    }

    private void verificarAlertas() {
        try {
            List<Estancia> alertadas = alertaService.verificarAlertas();
            for (Estancia e : alertadas) {
                mostrarModalAlerta(e);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void mostrarModalAlerta(Estancia e) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("⚠ ALERTA DE PERMANENCIA");
        alert.setHeaderText("Tiempo excedido — " + e.getNombreVisitante());
        alert.setContentText(
                "Destino: " + e.getDestino() + "\n" +
                        "Tag: " + e.getCodigoRfid() + "\n" +
                        "Tiempo máximo: " + e.getTiempoMaxMinutos() + " min\n\n" +
                        "Por favor verifica la situación del visitante.");

        // De esta manera JavaFX saca el modal de la animación y lo agenda de forma segura
        Platform.runLater(() -> {
            alert.showAndWait();
        });
    }

    private boolean validar() {
        if (txtDni.getText().trim().isEmpty())     { alert("Campo requerido","Ingresa el DNI."); return false; }
        if (txtNombre.getText().trim().isEmpty())  { alert("Campo requerido","Ingresa el nombre."); return false; }
        if (cbTipo.getValue() == null)             { alert("Campo requerido","Selecciona el tipo de visita."); return false; }
        if (txtDestino.getText().trim().isEmpty()) { alert("Campo requerido","Ingresa el destino."); return false; }
        if (cbTipoIngreso.getValue() == null)      { alert("Campo requerido","Selecciona el tipo de ingreso."); return false; }
        return true;
    }

    private void limpiarFormulario() {
        txtDni.clear(); txtNombre.clear(); txtDestino.clear(); txtDescVehiculo.clear();
        cbTipo.getSelectionModel().clearSelection();
        cbSubtipo.getSelectionModel().clearSelection();
        cbTipoIngreso.getSelectionModel().clearSelection();
        cbTiempoMax.getSelectionModel().select("60");
        lblRfidLeido.setText("Sin lectura");
        tagSeleccionado = null;
    }

    private <T> TableColumn<Estancia, T> col(String header, String property, int minW) {
        TableColumn<Estancia, T> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setMinWidth(minW);
        return c;
    }

    private VBox labeledField(String label, Control field) {
        Label lbl = new Label(label); lbl.getStyleClass().add("form-label");
        VBox box = new VBox(4, lbl, field); return box;
    }

    private TextField field(String prompt, String placeholder) {
        TextField tf = new TextField();
        tf.getStyleClass().add("text-field");
        tf.setPromptText(placeholder);
        return tf;
    }

    private ComboBox<String> combo(String prompt, String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.setPromptText(prompt);
        cb.setMaxWidth(Double.MAX_VALUE);
        return cb;
    }

    private VBox metricCard(String label, String value, String color) {
        VBox card = new VBox(2);
        card.setStyle("-fx-background-color: #1C2128; -fx-background-radius:8; -fx-padding:12; -fx-min-width:90;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:10px; -fx-text-fill:#8B949E; -fx-font-weight:bold;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + color + ";");
        card.getChildren().addAll(lbl, val);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    public BorderPane getRoot() { return root; }
}