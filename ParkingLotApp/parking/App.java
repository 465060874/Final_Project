package parking;

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
public class App extends Application 
{
	public static Stage primaryStage = new Stage();
	
	/**
	 * Load GUI structure from FXML and show GUI
	 * 
	 * @param p a Stage object
	 */
	@Override
	public void start(Stage p) {
		/* TODO fix splash screen
		try {	//load guiSplash.fxml
			//p.initStyle(StageStyle.UNDECORATED);
			p.centerOnScreen();
			p.setOpacity(0.9);
			p.setAlwaysOnTop(true);
			Pane mainWindow = (Pane) FXMLLoader.load(getClass().getResource("guiSplash.fxml"));
			Scene scene = new Scene(mainWindow);
			p.setScene(scene);
			p.setResizable(false);
			p.show();
		} catch (Exception e) {
			System.out.println("Ouch, I've encountered a fatal error! :(\nError loading guiSplash.fxml. Check it out and help me feel better!");
		}*/
		
		try {	//load GUI.fxml to new pane, create new scene, put it in primaryStage, all that fun stuff
			Pane mainWindow = (Pane) FXMLLoader.load(getClass().getResource("guiMain.fxml"));
			Scene scene = new Scene(mainWindow);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Parking Pal 1.0");
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception e) {	//catch exceptions while loading GUI.fxml
			System.out.println("Ouch, I've encountered a fatal error! :(\nError loading guiMain.fxml. Check it out and help me feel better!");
		}

		// let splash screen hang out for awhile
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println("Quit it, can't you see I'm trying to sleep here!?");
		}
		
		// close the splash screen
		p.close();
	}
	
	/**
	 * 
	 * Sprint 1: No implementation
	 */
	public void finalize() throws Throwable {

	}
	
	/**
	 * Main method, initializes program
	 * @param args
	 */
	public static void main(String[] args)
	{
		launch(args);
	}
	
}//end App