package uk.co.sleonard.unison.gui;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.support.WaitUntilSupport;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FirstTestFX extends ApplicationTest {

	@Override
	public void start(Stage stage) throws Exception {

		FXMLLoader loader = new FXMLLoader();
		ClassLoader classLoader = getClass().getClassLoader();
		loader.setLocation(classLoader.getResource("fxml/SplashScreenLayout.fxml"));
		Pane splashPane = (Pane) loader.load();
		Scene splashScene = new Scene(splashPane);
		stage.setScene(splashScene);
		stage.show();

		SplashScreenFX controller = loader.getController();
		// controller.setMainApp(this);
		controller.load();
	}

	@Test
	public void teste() {
		WaitUntilSupport until = new WaitUntilSupport();
		try {
			until.wait(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
