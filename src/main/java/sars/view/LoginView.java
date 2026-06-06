package sars.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sars.dao.VigilanteDAO;
import sars.model.Vigilante;

public class LoginView {

    private final VBox root;

    public LoginView(Stage stage) {

        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0D1117;");
        root.setPrefSize(480, 560);

        VBox card = new VBox(20);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(360);

        VBox headerBox = new VBox(4);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setMaxWidth(Double.MAX_VALUE);

        ImageView logoImg = new ImageView();
        try {
            logoImg.setImage(new Image(getClass().getResourceAsStream("/images/logo.png")));
            logoImg.setFitWidth(180);
            logoImg.setFitHeight(180);
            logoImg.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }

        Text logo = new Text("SARS");
        logo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: #1F6FEB; -fx-padding: 4 0 0 0;");

        Text subtitle = new Text("SMART-ACCESS RESIDENTIAL SYSTEM");
        subtitle.setStyle("-fx-font-size: 11px; -fx-fill: #00A3A3; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        headerBox.getChildren().addAll(logoImg, logo, subtitle);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #30363D;");

        Label lblUser = new Label("Usuario");
        lblUser.getStyleClass().add("form-label");
        TextField txtUser = new TextField();
        txtUser.getStyleClass().add("text-field");
        txtUser.setPromptText("Ingresa tu usuario");

        Label lblPass = new Label("Contraseña");
        lblPass.getStyleClass().add("form-label");
        PasswordField txtPass = new PasswordField();
        txtPass.getStyleClass().add("text-field");
        txtPass.setPromptText("••••••••");

        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill: #E84040; -fx-font-size: 12px;");

        Button btnLogin = new Button("Iniciar sesión");
        btnLogin.getStyleClass().add("btn-primary");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(42);

        Runnable doLogin = () -> {
            String u = txtUser.getText().trim();
            String p = txtPass.getText().trim();
            if (u.isEmpty() || p.isEmpty()) {
                lblError.setText("Completa todos los campos.");
                return;
            }
            try {
                VigilanteDAO dao = new VigilanteDAO();
                Vigilante vig = dao.login(u, p);
                if (vig != null) {
                    MainPanelView panel = new MainPanelView(stage, vig);
                    javafx.scene.Scene scene = new javafx.scene.Scene(panel.getRoot(), 1200, 720);
                    scene.getStylesheets().add(getClass().getResource(sars.MainApp.CSS_PATH).toExternalForm());
                    stage.setScene(scene);
                    stage.setResizable(true);
                    stage.setMinWidth(900);
                    stage.setMinHeight(600);
                } else {
                    lblError.setText("Usuario o contraseña incorrectos.");
                    txtPass.clear();
                }
            } catch (Exception ex) {
                lblError.setText("Error de conexión: " + ex.getMessage());
                ex.printStackTrace();
            }
        };

        btnLogin.setOnAction(e -> doLogin.run());
        txtPass.setOnAction(e -> doLogin.run());

        card.getChildren().addAll(headerBox, sep, lblUser, txtUser, lblPass, txtPass, lblError, btnLogin);
        root.getChildren().add(card);
    }

    public VBox getRoot() { return root; }
}