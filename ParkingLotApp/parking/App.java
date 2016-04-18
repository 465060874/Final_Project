package parking;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * @author Joshua Swain
 * @version 1.0
 * @created 19-Feb-2016 5:52:26 PM
 */
public class App extends Application {
	public static Stage primaryStage = new Stage();
	public static JFrame frame = new JFrame();
	public static Pane mainWindow;
	public static Scene scene;
	
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
			mainWindow = (Pane) FXMLLoader.load(getClass().getResource("newGuiMain.fxml"));
			scene = new Scene(mainWindow);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Parking Pal 1.0");
			primaryStage.setResizable(false);
			primaryStage.centerOnScreen();
			primaryStage.getIcons().add(new Image(new File("src/main/resources/icon.png").toURI().toString()));
			primaryStage.hide();
		} catch (Exception e) {	//catch exceptions while loading GUI.fxml
			System.out.println("Ouch, I've encountered a fatal error! :(\nError loading guiMain.fxml. Check it out and help me feel better!");
			e.printStackTrace();
		}

		// Let splash screen hang out for awhile
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			System.out.println("Quit it, can't you see I'm trying to sleep here!?");
		}
		
		primaryStage.show();
		
		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {	//EventHandler for mouse events
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY) GuiView.toggleControl = true;
				if (event.getButton() == MouseButton.SECONDARY) GuiView.viewSwitch = !GuiView.viewSwitch;
			}
		});
		/*
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
					case C:	GuiView.toggleControl = true; break;
					case V:	GuiView.viewSwitch = !GuiView.viewSwitch; break;
				}
			}
		});*/
		
		GuiView.STAGE_INIT = App.primaryStage.getHeight();	//used to determine how the window will change size when animated
		
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