package parking;

import javafx.scene.image.Image;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author Joshua Swain
 * @version 1.0
 * @created 19-Feb-2016 5:52:30 PM
 */

public class GuiView implements Initializable {

	// GUI objects loaded from guiMain.fxml
    @FXML private Text imageLastUpdateText, currentSpotsAvailableText, thereAreCurrently, spotsAvailable;
    @FXML private TextField predictionOutputField;
    @FXML private JFXButton buttonPredict, buttonHelp, buttonQuit;
    @FXML private JFXToggleButton toggleLabelOverlay, gridToggle;
    @FXML private GridPane parkingGridPane;
    @FXML private AnchorPane controlPane;
    @FXML private Rectangle mainArea, spot1, spot2, spot3, spot4, spot5, spot6, spot7, spot8, spot9,
    	spot10, spot11, spot12, spot13, spot14, spot15, spot16, spot17, spot18, spot19, spot20,
    	spot21, spot22, spot23, spot24, spot25, spot26, spot27, spot28;
    @FXML private ImageView carIcon1, carIcon2, carIcon3, carIcon4, carIcon5, carIcon6, carIcon7,
    	carIcon8, carIcon9, carIcon10, carIcon11, carIcon12, carIcon13, carIcon14, carIcon15,
    	carIcon16, carIcon17, carIcon18, carIcon19, carIcon20, carIcon21, carIcon22, carIcon23,
    	carIcon24, carIcon25, carIcon26, carIcon27, carIcon28, imageRoad, imageTree, spotLabelOverlay,
    	webcamView;
    
    // Class Variables
    private Image image;						//The image object to be displayed in the webcam view
    private Timer updateTimer;					//Timer object used to pull images
    private Timer processTimer;					//Timer object used to process images
    private Timer animateTimer;					//Timer object used to animate GUI
    private File imageFile;						//File object used to store current image
    private WebCommunications web 
    	= new WebCommunications();				//WebCommunications object used for magic
    private int numEmpty = -1;					//Number of empty spots
    private boolean isProcessing = false;		//Triggers camera view to stop updating when image being processed
    private ImageView[] cuteCars;				//Array holding all car icons, used in updateGrid()
    private Rectangle[] spots;					//Array of spot areas, used for tool-tips
    public static double STAGE_INIT = 0;		//Initial height value for App.primaryStage, set in initialize()
    private boolean grabFail = false;
    public static boolean toggleControl = false;	//Used to signal animating control panel
    public static boolean controlsHidden = true;	//Used to signal that control panel is hidden
    public static boolean controlsShown = false;	//Used to signal that control panel is showing
    public static boolean viewSwitch = true;		//Used to signal animating view switch
    
    // TWEAKABLE VALUES
    private final long	 	UPDATE_DELAY 		= 3000,		//How long to wait to start pulling images
    						UPDATE_RATE 		= 50,		//How often to pull new image (50 = 20 ideal fps)
    						PROCESS_DELAY 		= 4000,		//How long to wait to start processing images
    						PROCESS_RATE 		= 4000,		//How often to process image
    						CAMERA_SLEEP_RATE	= 250,		//How long to wait to process next image after interrupting camera update
    						ANIMATE_DELAY		= 2500,		//How long to wait to start animateTimer
    						ANIMATE_RATE		= 10;		//Length of step used to animate GUI
    private final double 	VIEW_CHANGE_RATE	= 0.02,		//Size of opacity change step used in view transitions
    						ICON_CHANGE_RATE	= 0.02;		//Size of opacity change step used in grid view transitions
    private final int 		PANEL_THROW			= 120,		//Distance control panel moves to hide/show controls
    						PANEL_STEP			= 2;		//Step size used to animate control panel movement - Must divide evenly into PANEL_THROW!
   
    private final SimpleDateFormat TIME_FORMAT 
    	= new SimpleDateFormat("HH:mm:ss z");			//Format of time-stamp
    
	/**
	 * Called to terminate program when red X clicked in the main interface
	 */
	public void closeRedX() {
		App.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				updateTimer.cancel();		//stop updateTimer
				processTimer.cancel();		//stop processTimer
				animateTimer.cancel();		//stop animateTimer
				App.frame.dispose();		//stop swinging
				
				//stop the frame grabber
				try {
					WebCommunications.grabber.stop();
				} catch (Exception ed) {
					System.out.println("Tim is a terrible developer.");
				}
				
				//delete image file
				boolean done = false;
				for (int i = 0; i < 100; i ++) {	//this may take several tries
					done = WebCommunications.imageForGUIMadness.delete();
					if (done) {
						break;	//stop trying after it works
					}
				}
				
				Platform.exit();	//terminate program
			}
		});
	}
	
	/**
	 * Called to terminate program when "Quit" is selected
	 */
	public void closeFromButton() {
		buttonQuit.setOnAction(e -> {
			updateTimer.cancel();		//stop updateTimer
			processTimer.cancel();		//stop processTimer
			animateTimer.cancel();		//stop animateTimer
			App.frame.dispose();		//stop swinging
			
			//stop the frame grabber
			try {
				WebCommunications.grabber.stop();
			} catch (Exception ed) {
				System.out.println("Tim is a terrible developer.");
			}
			
			//delete image file
			boolean done = false;
			for (int i = 0; i < 100; i ++) {	//this may take several tries
				done = WebCommunications.imageForGUIMadness.delete();
				if (done) {
					break;	//when it works, stop trying
				}
			}
			
			Platform.exit();	//terminate program
		});
	}

	/**
	 * TODO Tim
	 * Shows built-in program instructions
	 */
	public void showUserGuide() {
		buttonHelp.setOnAction(e -> {
			System.out.println("Tim's done.");
			showHelpPanel();
		});
	}
	
	/**
	 * Opens prediction interface
	 */
	public void showPredictWindow() {
		Stage predictStage = new Stage();
		
		buttonPredict.setOnAction(e -> {
			try {
				Pane predictPane = (Pane) FXMLLoader.load(getClass().getResource("guiPredict.fxml"));
				Scene predictScene = new Scene(predictPane);
				predictStage.setScene(predictScene);
				predictStage.setTitle("");
				predictStage.setResizable(false);
				predictStage.show();
			} catch (Exception ex) {	//catch exceptions while loading GUI
				System.out.println("Ouch, I've encountered a fatal error! :(\nError loading guiPredict.fxml. Check it out and help me feel better!");
				ex.printStackTrace();
			}
		});
		
		predictStage.setX(predictStage.getX() - 500);
	}
	
	/**
	 * TODO
	 * Sets tool-tips for relevant GUI nodes
	 */
	public void setToolTips() {
		spots = new Rectangle[] {
			spot1, spot2, spot3, spot4, spot5, spot6, spot7, spot8, spot9, spot10, 
		    spot11, spot12, spot13, spot14, spot15, spot16, spot17, spot18, spot19,
		    spot20, spot21, spot22, spot23, spot24, spot25, spot26, spot27, spot28
		};
		
		//for main window
		Tooltip clickTip = new Tooltip("Left click: toggle controls - Right click: toggle view");
		Tooltip.install(mainArea, clickTip);
		
		//for buttons
		buttonHelp.setTooltip(new Tooltip("Show User Guide"));
		buttonPredict.setTooltip(new Tooltip("Open prediction tool"));
		buttonQuit.setTooltip(new Tooltip("You should know what this button does ..."));
		
		//TODO should this move?
		for (int i = 0; i < spots.length; i ++) {
			Tooltip.install(spots[i], new Tooltip("Spot " + (i+1)));
		}
	}
	
	/**
	 * Contains the Timer and associated task for pulling a new image and refreshing the GUI
	 */
	public void update() {
		//initial image pull, opens connection to camera feed
		try {
			WebCommunications.getImage();
		} catch (Exception e) {
			imageLastUpdateText.setText("ERROR: check network");
		}
		
		//timer task to update live feed
		updateTimer = new Timer();
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {	//pull image
					if (!isProcessing) {
						if (grabFail) WebCommunications.getImage();	//get new image unless processing is occurring
						else WebCommunications.saveImage();
					}
					grabFail = false; //reset
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("I failed here!");	//TODO still can't handle losing connection during execution. thread stops
					grabFail = true;
				}
				
				if (grabFail) {		//if can't get a new image, let user know 
					imageLastUpdateText.setText(" CHECKNETWORK");
					webcamView.setVisible(false);
				}
				else {
					imageFile = WebCommunications.imageForGUIMadness;			//pull image file from system
					image = new Image(imageFile.toURI().toString());			//create Image from File
					webcamView.setImage(image);									//display Image in GUI
					
					imageLastUpdateText.setText(TIME_FORMAT.format(
							Calendar.getInstance().getTime())); 		//update update time
					webcamView.setVisible(true);
				}
			}
		}, UPDATE_DELAY, UPDATE_RATE);
	}
	
	/**
	 * Contains the Timer and associated task for processing images
	 */
	public void process() {
		processTimer = new Timer();
		processTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				isProcessing = true;	//stop saving new images until current image is processed
				try {
					Thread.sleep(CAMERA_SLEEP_RATE);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (imageFile.exists()) {
					System.out.println("Ding, fries are done (calling web.processImage())");	//TODO test line
					web.processImageRev2("getImageResult.jpg");	//TODO get this working
				}
				isProcessing = false;	//start saving new images now that current image is processed
			}
		}, PROCESS_DELAY, PROCESS_RATE);
	}
	
	/**
	 * Contains Timer and associated task for animating GUI elements
	 */
	public void animate() {
		animateTimer = new Timer();
		animateTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//control for smooth transition between views
				if (viewSwitch) {
					double opacity = parkingGridPane.getOpacity();
					if (opacity > 0) parkingGridPane.setOpacity(opacity -= VIEW_CHANGE_RATE);
					mainArea.setVisible(true);
				} else {
					double opacity = parkingGridPane.getOpacity();
					if (opacity < 1) parkingGridPane.setOpacity(opacity += VIEW_CHANGE_RATE);
					mainArea.setVisible(false);
				}
				
				//control for smooth show/hide of spot labels
				if (toggleLabelOverlay.isSelected()) {
					double opacity = spotLabelOverlay.getOpacity();
					if (opacity < 1) {
						spotLabelOverlay.setOpacity(opacity += VIEW_CHANGE_RATE);
					}
				}
				else {
					double opacity = spotLabelOverlay.getOpacity();
					if (opacity > 0) {
						spotLabelOverlay.setOpacity(opacity -= VIEW_CHANGE_RATE);
					}
				}
				
				updateGrid();		//control for smooth transitions in grid view
				updateNumEmpty();	//updates total empty spot display
				
				//control showing and hiding of control panel
				if (toggleControl) {
					toggleControls();
				}
			}
		}, ANIMATE_DELAY, ANIMATE_RATE);
	}
	
	/*
	 * Uses results of image processing to update icons in grid view
	 */
	public void updateGrid() {
		cuteCars = new ImageView[] {
	    		carIcon1, carIcon2, carIcon3, carIcon4, carIcon5, carIcon6, carIcon7,
	    		carIcon8, carIcon9, carIcon10, carIcon11, carIcon12, carIcon13, carIcon14,
	    		carIcon15, carIcon16, carIcon17, carIcon18, carIcon19, carIcon20, carIcon21,
	    		carIcon22, carIcon23, carIcon24, carIcon25, carIcon26, carIcon27, carIcon28
	    };
		
		for (int i = 0; i < web.getParkingGrid().getSpotArray().length; i ++) {
			if (!web.getParkingGrid().getSpotArray()[i].getStatus()) {
				//cuteCars[i].setVisible(true);
				fadeIn(cuteCars[i]);
			}
			else {
				//cuteCars[i].setVisible(false);
				fadeOut(cuteCars[i]);
			}
		}
	}

	/**
	 * Provides smooth fade in transition for car icons in the grid view
	 * @param car the car icon to be transitioned
	 */
	public void fadeIn(ImageView car) {
		double opacity = car.getOpacity();
		if (opacity < 1) {
			car.setOpacity(opacity += ICON_CHANGE_RATE);
		}
	}
	
	/**
	 * Provides smooth fade out transition for car icons in the grid view
	 * @param car the car icon to be transitioned
	 */
	public void fadeOut(ImageView car) {
		double opacity = car.getOpacity();
		if (opacity > 0) {
			car.setOpacity(opacity -= ICON_CHANGE_RATE);
		}
	}
	
	/**
	 * Updates GUI output for the total number of empty spots
	 */
	public void updateNumEmpty() {
		//assign number of empty spots to numEmpty
		numEmpty = 0;
		for (ParkingSpots spot: web.getParkingGrid().getSpotArray()) {
			if (spot.getStatus())
				numEmpty ++;
		}
		
		//display numEmpty
		if (numEmpty < 0) currentSpotsAvailableText.setText("NaN");
		else currentSpotsAvailableText.setText(Integer.toString(numEmpty));
		
		//color output
		if (numEmpty <= 5) currentSpotsAvailableText.setFill(Color.RED);
		else if (numEmpty <= 10) currentSpotsAvailableText.setFill(Color.GOLD);
		else currentSpotsAvailableText.setFill(Color.LIME);
		
		//correct grammar
		if (numEmpty == 1) {
			thereAreCurrently.setText("There is currently");
			spotsAvailable.setText("spot available");
		}
		else {
			thereAreCurrently.setText("There are currently");
			spotsAvailable.setText("spots available");
		}
		
	}
	
	/**
	 * Provides animation to show and hide the control panel
	 */
	public void toggleControls() {
		if (controlsShown) {	//if the controls are showing, hide them
			App.primaryStage.setHeight(App.primaryStage.getHeight() - PANEL_STEP);	//step down primaryStage
			
			if (App.primaryStage.getHeight() == STAGE_INIT) {	//continue stepping until fully hidden
				controlsShown = false;
				controlsHidden = true;
				toggleControl = false;	//wait for next toggle command
			}
		} else if (controlsHidden){	//if the controls are hidden, show them
			App.primaryStage.setHeight(App.primaryStage.getHeight() + PANEL_STEP);	//step up primaryStage
			
			if (App.primaryStage.getHeight() == STAGE_INIT + PANEL_THROW) {		//continue stepping until fully shown
				controlsShown = true;
				controlsHidden = false;
				toggleControl = false;	//wait for next toggle command
			}
		}
	}
	
	/**
	 * Event handlers, listeners, and other GUI-related actions
	 */
	public void initialize(URL arg0, ResourceBundle arg1){
		closeRedX();
		closeFromButton();
		showUserGuide();
		showPredictWindow();
		setToolTips();
		update();		//executes updateTimer
		process();		//executes processTimer
		animate();		//executes animateTimer
	}
	
	/**
	 * Shows the help panel for the application
	 */
	public void showHelpPanel(){
		    Platform.runLater(new Runnable() {
		       public void run() {             
		           new HelpPanel().start(new Stage());
		       }
		    });
	}
} //end GuiView