package parking;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * @author Joshua Swain
 * @version 1.0
 * @created 19-Feb-2016 5:52:26 PM
 */
public class App extends Application {
	public static Stage primaryStage = new Stage();
	public static JFrame frame = new JFrame();
	
	/**
	 * Load GUI structure from FXML and show GUI
	 * 
	 * @param p a Stage object
	 */
	@Override
	public void start(Stage p) {
		// Load and display splash screen (Swing)
		String fileName = "src/main/resources/splash.png";
        ImageIcon icon = new ImageIcon(fileName);
        JLabel label = new JLabel(icon, SwingConstants.CENTER);	//center picture in frame
	    frame.getContentPane().add(label);
	    frame.setUndecorated(true);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.pack();	//conform frame size to image size
	    frame.setLocationRelativeTo(null);	//center on screen
	    frame.setAlwaysOnTop(true);
	    frame.setVisible(true);
	    
	    // Load and display main interface
		try {
			Pane mainWindow = (Pane) FXMLLoader.load(getClass().getResource("guiMain.fxml"));
			Scene scene = new Scene(mainWindow);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Parking Pal 1.0");
			primaryStage.setResizable(false);
			primaryStage.centerOnScreen();
			primaryStage.getIcons().add(new Image(new File("src/main/resources/icon.png").toURI().toString()));
			primaryStage.show();
		} catch (Exception e) {	//catch exceptions while loading GUI.fxml
			System.out.println("Ouch, I've encountered a fatal error! :(\nError loading guiMain.fxml. Check it out and help me feel better!");
			e.printStackTrace();
		}

		// Let splash screen hang out for awhile
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			System.out.println("Quit it, can't you see I'm trying to sleep here!?");
		}
		
		// Close the splash screen
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			System.out.println("Quit it, can't you see I'm trying to sleep here!?");
		}
		frame.setVisible(false);
	}
	
	/**
	 * Main method, initializes program
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
}//end App