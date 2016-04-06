package parking;

import javafx.scene.image.Image;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;

/**
 * @author Joshua Swain
 * @version 1.0
 * @created 19-Feb-2016 5:52:30 PM
 */

public class GuiView implements Initializable {

	@FXML private MenuItem menuClose;
	@FXML private MenuItem menuAbout;
	@FXML private MenuItem menuSaveFrame;
    @FXML private TabPane viewTabPane;
    @FXML private Tab webcamTab;
    @FXML private ImageView webcamView;
    @FXML private Text imageLastUpdateText;
    @FXML private DatePicker predictionDateSelector;
    @FXML private ComboBox<?> predictionTimeSelector;
    @FXML private Button predictionButton;
    @FXML private TextField predictionOutputField;
    @FXML private CheckBox checkBoxLabelOverlay;
    @FXML private Tab detailTab;
    @FXML private GridPane parkingGridPane;
    @FXML private ImageView carIcon1;
    @FXML private ImageView carIcon2;
    @FXML private ImageView carIcon3;
    @FXML private ImageView carIcon4;
    @FXML private ImageView carIcon5;
    @FXML private ImageView carIcon6;
    @FXML private ImageView carIcon7;
    @FXML private ImageView carIcon8;
    @FXML private ImageView carIcon9;
    @FXML private ImageView carIcon10;
    @FXML private ImageView carIcon11;
    @FXML private ImageView carIcon12;
    @FXML private ImageView carIcon13;
    @FXML private ImageView carIcon14;
    @FXML private ImageView carIcon15;
    @FXML private ImageView carIcon16;
    @FXML private ImageView carIcon17;
    @FXML private ImageView carIcon18;
    @FXML private ImageView carIcon19;
    @FXML private ImageView carIcon20;
    @FXML private ImageView carIcon21;
    @FXML private ImageView carIcon22;
    @FXML private ImageView carIcon23;
    @FXML private ImageView carIcon24;
    @FXML private ImageView carIcon25;
    @FXML private ImageView carIcon26;
    @FXML private ImageView carIcon27;
    @FXML private ImageView carIcon28;
    @FXML private Text currentSpotsAvailableText;
    @FXML private Pane paneSpotLabelOverlay;

    // Class Variables
    private Image image;		//the image object to be displayed in the webcam view
    private Timer camTimer;		//Timer object used to update webcam view
    private Task<Void> update;	//Task used for image processing and grid view display
    private Thread thread;		//Thread to run image processing task in
    private File imageFile;		//File object used to pull webcam image
    private WebCommunications web = new WebCommunications();	//WebCommunications object used for magic
    private int numEmpty = -1;	//Number of empty spots
    
    // Date and Time
	SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd");
	SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss z");
    
	/**
	 * Non-Function: Sprint 1
	 */
	public void finalize() throws Throwable {

	}
	
	// Event handler to close application using Red X TODO javadoc
	public void closeRedX() {
		App.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@SuppressWarnings("deprecation")
			@Override
			public void handle(WindowEvent t) {
				//stop the camView thread
				camTimer.cancel();
				
				//stop the processing thread
				thread.stop();
				
				//stop swinging
				App.frame.dispose();
				
				//stop the frame grabber
				try {
					WebCommunications.grabber.stop();
				} catch (Exception ed) {
					System.out.println("Tim is a terrible developer.");
				}
				
				//delete image file
				boolean done = false;
				while (!done) {
					done = WebCommunications.imageForGUIMadness.delete();
					System.out.println(done);
				}
				
				//terminate program
				Platform.exit();
			}
		});
	}
	
	// Close application using File menu TODO javadoc
	@SuppressWarnings("deprecation")
	public void closeFromMenu() {
		menuClose.setOnAction(e -> {	//<File-Close>
			//stop the camView thread
			camTimer.cancel();
			
			//stop the processing thread
			thread.stop();
			
			//stop swinging
			App.frame.dispose();
			
			//stop the frame grabber
			try {
				WebCommunications.grabber.stop();
			} catch (Exception ed) {
				System.out.println("Tim is a terrible developer.");
			}
			
			//delete image file
			boolean done = false;
			while (!done) {
				done = WebCommunications.imageForGUIMadness.delete();
				System.out.println(done);
			}
			
			//terminate program
			Platform.exit();
		});
	}
	
	// Event handlers for menu items TODO javadoc
	public void clearGrid() {
		carIcon1.setVisible(false);
		carIcon2.setVisible(false);
		carIcon3.setVisible(false);
		carIcon4.setVisible(false);
		carIcon5.setVisible(false);
		carIcon6.setVisible(false);
		carIcon7.setVisible(false);
		carIcon8.setVisible(false);
		carIcon9.setVisible(false);
		carIcon10.setVisible(false);
		carIcon11.setVisible(false);
		carIcon12.setVisible(false);
		carIcon13.setVisible(false);
		carIcon14.setVisible(false);
		carIcon15.setVisible(false);
		carIcon16.setVisible(false);
		carIcon17.setVisible(false);
		carIcon18.setVisible(false);
		carIcon19.setVisible(false);
		carIcon20.setVisible(false);
		carIcon21.setVisible(false);
		carIcon22.setVisible(false);
		carIcon23.setVisible(false);
		carIcon24.setVisible(false);
		carIcon25.setVisible(false);
		carIcon26.setVisible(false);
		carIcon27.setVisible(false);
		carIcon28.setVisible(false);
	}
	
	// TODO javadoc
	public void updateCamView() {
		//Initial image pull, also opens connection to webcam
		try {
			
			WebCommunications.getImage();
		} catch (Exception e) {
			System.out.println("Boo get :(");
		}
		
		// timer task to update webcam feed TODO make this cleaner; property binding?
		camTimer = new Timer();
		camTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//System.out.println("I'm alive!");
				try {	//pull image
					WebCommunications.saveImage();
				} catch (Exception e) {
					System.out.println("Boo save :(");
				}
				imageFile = WebCommunications.imageForGUIMadness;			//pull image file from system TODO change path
				image = new Image(imageFile.toURI().toString());			//create Image from File
				webcamView.setImage(image);									//display Image in GUI
				imageLastUpdateText.setText(time.format(Calendar.getInstance().getTime()) 	//update update time
						+ " " + date.format(Calendar.getInstance().getTime()));
			}
		}, 3000, 30);	// change webcam view update interval here!
	}
	
	// This is Ian's happy place. TODO javadoc
	// Thread to control image processing and grid view display
	public void updateGridView() {
		final long UPDATE_INTERVAL = 1000;		//TODO change processing interval here
		update = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				//First time in the thread, wait for image to be grabbed
				boolean done = false;
				if (!done) {
					Thread.sleep(4000);
				}
				done = true;
				
				while (true) {	//loop forever
					Thread.sleep(UPDATE_INTERVAL);
					System.out.println("Ding, fries are done.");
					
					//web.processImage("getImageResult.jpg");
					
					updateMessage(Double.toString(Math.random()));	//triggers listener to update GUI
				}
			}
		};
		
		// Execute simulation thread
		thread = new Thread(update);
		thread.setDaemon(true);
		thread.start();
		
		// Listener to update GUI after each iteration
		update.messageProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				updateGrid();
				updateNumEmpty();
			}
		});
	}
	
	/*
	 * TODO implement this, fool!
	 * Uses results of image processing to update icons in grid view
	 */
	public void updateGrid() {
		System.out.println("The wheels on the bus go round and round"); //TEST
		
		//TODO change icon visibility based on spot status
	}
	
	public void updateNumEmpty() {
		System.out.println("Two wrongs don't make a right, but two rights make a U-turn!"); //TEST
		
		//TODO assign number of empty spots to numEmpty
		
		if (numEmpty < 0) currentSpotsAvailableText.setText("NaN");
		else currentSpotsAvailableText.setText(Integer.toString(numEmpty));
		
		if (numEmpty <= 5) currentSpotsAvailableText.setFill(Color.RED);
		else if (numEmpty <= 10) currentSpotsAvailableText.setFill(Color.GOLD);
		else currentSpotsAvailableText.setFill(Color.LIME);
	}
	
	/**
	 * Event handlers, listeners, and other GUI-related actions
	 */
	public void initialize(URL arg0, ResourceBundle arg1){

		closeRedX();
		closeFromMenu();
		clearGrid();
		
		// Property binding for spot label overlay checkbox
		paneSpotLabelOverlay.visibleProperty().bind(checkBoxLabelOverlay.selectedProperty());
		
		updateCamView();
		updateGridView();
	}
} //end GuiView