package sars.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sars.model.Vigilante;

public class MainPanelView {

    private final BorderPane root;
    private final Vigilante  vigilante;
    private final Stage      stage;

    private final StackPane contentArea = new StackPane();

    private AccesoView    accesoView;
    private AuditoriaView auditoriaView;

    private Button btnAcceso;
    private Button btnAuditoria;

    public MainPanelView(Stage stage, Vigilante vigilante) {
        this.stage     = stage;
        this.vigilante = vigilante;

        root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setTop(buildTopBar());
        root.setCenter(contentArea);
        root.setStyle("-fx-background-color: #0D1117;");

        mostrarAcceso();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");

        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(24, 20, 20, 20));
        Text logo    = new Text("SARS");
        logo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-fill: #1F6FEB;");
        Text logoSub = new Text("v1.0  •  " + vigilante.getTurno());
        logoSub.setStyle("-fx-font-size: 11px; -fx-fill: #8B949E;");
        logoBox.getChildren().addAll(logo, logoSub);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #30363D;");

        Label lblSection = new Label("MENÚ");
        lblSection.setStyle("-fx-font-size: 10px; -fx-text-fill: #8B949E; -fx-padding: 16 20 4 20; -fx-font-weight: bold;");

        btnAcceso = sidebarBtn("⬡  Panel de Control");
        btnAcceso.setOnAction(e -> mostrarAcceso());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox vigilanteBox = new VBox(2);
        vigilanteBox.setPadding(new Insets(12, 16, 16, 16));
        vigilanteBox.setStyle("-fx-background-color: #1C2128; -fx-background-radius: 8; -fx-border-color: #30363D; -fx-border-radius: 8;");
        VBox.setMargin(vigilanteBox, new Insets(0, 12, 4, 12));
        Label lNom   = new Label("👤 " + vigilante.getNomVigilante());
        lNom.setStyle("-fx-font-size: 12px; -fx-text-fill: #E6EDF3; -fx-font-weight: bold;");
        Label lTurno = new Label("Turno: " + vigilante.getTurno());
        lTurno.setStyle("-fx-font-size: 11px; -fx-text-fill: #8B949E;");
        vigilanteBox.getChildren().addAll(lNom, lTurno);

        Button btnLogout = new Button("Cerrar sesión");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #E84040; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 8 20 16 20;");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> cerrarSesion());

        VBox bottomContainer = new VBox(0);
        bottomContainer.setPadding(new Insets(12, 0, 0, 0));
        bottomContainer.getChildren().addAll(vigilanteBox, btnLogout);

        if ("admin".equals(vigilante.getRol())) {
            btnAuditoria = sidebarBtn("⊞  Auditoría");
            btnAuditoria.setOnAction(e -> mostrarAuditoria());
            sidebar.getChildren().addAll(logoBox, sep, lblSection, btnAcceso, btnAuditoria, spacer, bottomContainer);
        } else {
            btnAuditoria = new Button();
            sidebar.getChildren().addAll(logoBox, sep, lblSection, btnAcceso, spacer, bottomContainer);
        }

        return sidebar;
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("top-bar");
        bar.setAlignment(Pos.CENTER_RIGHT);

        Label reloj = new Label();
        reloj.setStyle("-fx-font-size: 13px; -fx-text-fill: #8B949E; -fx-font-family: 'Consolas';");
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            reloj.setText(java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss")));
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        bar.getChildren().add(reloj);
        return bar;
    }

    private void mostrarAcceso() {
        setActivo(btnAcceso, btnAuditoria);
        if (accesoView == null) accesoView = new AccesoView(vigilante);
        contentArea.getChildren().setAll(accesoView.getRoot());
    }

    private void mostrarAuditoria() {
        if (!"admin".equals(vigilante.getRol())) return;
        setActivo(btnAuditoria, btnAcceso);
        if (auditoriaView == null) auditoriaView = new AuditoriaView();
        contentArea.getChildren().setAll(auditoriaView.getRoot());
    }

    private void setActivo(Button activo, Button... otros) {
        if (activo != null) activo.getStyleClass().setAll("sidebar-btn", "sidebar-btn-active");
        for (Button b : otros) {
            if (b != null && b.getText() != null) b.getStyleClass().setAll("sidebar-btn");
        }
    }

    private Button sidebarBtn(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("sidebar-btn");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private void cerrarSesion() {
        javafx.scene.Scene scene = new javafx.scene.Scene(new LoginView(stage).getRoot(), 480, 560);
        scene.getStylesheets().add(getClass().getResource(sars.MainApp.CSS_PATH).toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
    }

    public BorderPane getRoot() { return root; }
}