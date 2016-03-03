package Parking;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

/**
 * @author Joshua Swain
 * @version 1.0
 * @created 19-Feb-2016 5:52:26 PM
 */
public class App extends Application {

	public void start(Stage primaryStage) {
		try {	//load GUI.fxml to new pane, create new scene, put it in primaryStage, all that fun stuff
			Pane mainWindow = (Pane) FXMLLoader.load(getClass().getResource("guiMain.fxml"));
			Scene scene = new Scene(mainWindow);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Parking Pal 1.0");
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception e) {	//catch exceptions while loading GUI.fxml
			System.out.println("Ouch, I've encountered a fatal error! :(\nError loading guiMain.fxml. Check your files and help me feel better!");
		}
	}
	
	public void finalize() throws Throwable {

	}
	
	public static void main(String[] args)
	{
		launch(args);
		
		//WebCommunications web = new WebCommunications() TODO	
	}
}//end App