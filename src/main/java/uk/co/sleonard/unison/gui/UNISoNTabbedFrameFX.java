package uk.co.sleonard.unison.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import uk.co.sleonard.unison.UNISoNControllerFX;

/**
 * The class UNISoNTabbedFrameFX
 * 
 * The class UNISoNTabbedFrameFX is responsible by union of three Stages:
 * DownloadNewsPanel, MessageStoreViewer and PajekPanel.
 * 
 * Order: 1st Show splash screen is called. <br/>
 * 2nd Splash screen call load() to call initRootLayout and initialize
 * UNISoNController and use setInstance to get unisonController instance and set
 * inside UNISoNControllerFX static variable. <br/>
 * 3rd Method load() inside Splash screen call showRootLayout to show rootLayout
 * and other screens (I need improve this architecture). <br/>
 * 
 * @author Stephen <github@leonarduk.com> and adapted to JavaFX by Elton
 *         <elton_12_nunes@hotmail.com>
 * @since 18-jun-2016
 */
public class UNISoNTabbedFrameFX extends Application {

	private Stage primaryStage;
	private Stage aboutDialogStage;
	private BorderPane rootLayout;
	private TabPane tabs;
	private UNISoNControllerFX unisonController;

	/** The Constant dbDriver. */
	private final static String dbDriver = "org.hsqldb.jdbcDriver";
	/** The Constant dbUser. */
	private final static String dbUser = "sa";
	/** The Constant DB_URL. */
	private final static String DB_URL = "jdbc:hsqldb:file:DB/projectDB";
	public static final String GUI_ARGS[] = { "-driver", UNISoNTabbedFrameFX.dbDriver, "-url",
			UNISoNTabbedFrameFX.DB_URL, "-user", UNISoNTabbedFrameFX.dbUser, "-noexit" };

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Loading...");
		try {
			showSplashScreen();
		} catch (IOException e1) {
			System.err.println("ERROR - " + e1.getMessage());
			e1.printStackTrace();
		}

		this.primaryStage.setOnCloseRequest(e -> Platform.exit());
	}

	private void showSplashScreen() throws IOException {

		FXMLLoader loader = new FXMLLoader();
		ClassLoader classLoader = getClass().getClassLoader();
		loader.setLocation(classLoader.getResource("fxml/SplashScreenLayout.fxml"));
		Pane splashPane = (Pane) loader.load();
		Scene splashScene = new Scene(splashPane);
		this.primaryStage.setScene(splashScene);
		this.primaryStage.setTitle("Loading...");
		this.primaryStage.show();

		SplashScreenFX controller = loader.getController();
		controller.setMainApp(this);
		controller.load();
	}

	public void initRootLayout() {

		try {
			// Load the FXML File.
			FXMLLoader loader = new FXMLLoader();
			ClassLoader classLoader = getClass().getClassLoader();
			loader.setLocation(classLoader.getResource("fxml/RootLayout.fxml"));
			this.rootLayout = (BorderPane) loader.load();
			this.tabs = (TabPane) this.rootLayout.getChildren().get(2);

			this.unisonController = (UNISoNControllerFX) loader.getController();
			this.unisonController.setUnisonTabbedFrameFX(this);
			this.unisonController.setInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void showRootLayout() {
		// Show scene
		Scene scene = new Scene(this.rootLayout);
		this.primaryStage = new Stage();
		this.primaryStage.setScene(scene);
		this.primaryStage.setTitle("UNISoN");
		this.primaryStage.show();

		showDownloadNewsPanel();
		showMessageStoreViewer();
		showPajekPanel();
		loadAboutDialog();

	}

	// Shows downloadNewsPanel inside RootLayout.
	private void showDownloadNewsPanel() {
		try {
			// Load the FXML File.
			FXMLLoader loader = new FXMLLoader();
			ClassLoader classLoader = getClass().getClassLoader();
			loader.setLocation(classLoader.getResource("fxml/DownloadNewsPanelLayout.fxml"));
			AnchorPane downloadNewsPanel = (AnchorPane) loader.load();
			this.tabs.getTabs().get(0).setContent(downloadNewsPanel);

			this.unisonController.setUnisonTabbedFrameFX(this);
			this.unisonController.setDownloadNewsPanelFX(loader.getController());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Shows messageStoreViewer inside RootLayout.
	private void showMessageStoreViewer() {
		try {
			// Load the FXML File.
			FXMLLoader loader = new FXMLLoader();
			ClassLoader classLoader = getClass().getClassLoader();
			loader.setLocation(classLoader.getResource("fxml/MessageStoreViewerLayout.fxml"));
			AnchorPane messageStoreViewer = (AnchorPane) loader.load();
			this.tabs.getTabs().get(1).setContent(messageStoreViewer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Shows PajekPanel inside RootLayout.
	private void showPajekPanel() {
		try {
			// Load the FXML File.
			FXMLLoader loader = new FXMLLoader();
			ClassLoader classLoader = getClass().getClassLoader();
			loader.setLocation(classLoader.getResource("fxml/PajekPanelLayout.fxml"));
			AnchorPane messageStoreViewer = (AnchorPane) loader.load();
			this.tabs.getTabs().get(2).setContent(messageStoreViewer);
			PajekPanelFX controller = loader.getController();
			controller.setStage(this.primaryStage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Loads about dialog **/
	private void loadAboutDialog() {
		try {
			// Load the FXML File.
			FXMLLoader loader = new FXMLLoader();
			ClassLoader classLoader = getClass().getClassLoader();
			loader.setLocation(classLoader.getResource("fxml/AboutDialogLayout.fxml"));
			Pane paneAboutDialog = (Pane) loader.load();
			Scene aboutScene = new Scene(paneAboutDialog);
			this.aboutDialogStage = new Stage();
			this.aboutDialogStage.setScene(aboutScene);
			this.aboutDialogStage.setTitle("About UNISoN");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Show about dialog **/
	public void showAboutDialog() {
		this.aboutDialogStage.show();
	}

	public Stage getPrimStage() {
		return this.primaryStage;
	}

	public UNISoNControllerFX getUnisonController() {
		return this.unisonController;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
