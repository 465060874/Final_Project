package parking;

import javafx.scene.image.Image;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
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
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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

	// GUI objects loaded from guiMain.fxml
    @FXML private ImageView webcamView;
    @FXML private Text imageLastUpdateText;
    @FXML private DatePicker predictionDateSelector;
    @FXML private ComboBox<?> predictionTimeSelector;
    @FXML private Button predictionButton;
    @FXML private Button buttonHelp;
    @FXML private Button buttonQuit;
    @FXML private TextField predictionOutputField;
    @FXML private CheckBox checkBoxLabelOverlay;
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
    @FXML public static ImageView imageRoad;
    @FXML public static ImageView imageTree;
    @FXML private Text currentSpotsAvailableText;
    @FXML private Pane paneSpotLabelOverlay;
    @FXML private Text thereAreCurrently;
    @FXML private Text spotsAvailable;

    // Class Variables
    private Image image;										//The image object to be displayed in the webcam view
    private Timer camTimer;										//Timer object used to update webcam view
    private Task<Void> processingTask;							//Task used for image processing and grid view display
    private Thread processingThread;							//Thread to run image processing task in
    private File imageFile;										//File object used to pull webcam image
    private WebCommunications web = new WebCommunications();	//WebCommunications object used for magic
    private int numEmpty = -1;									//Number of empty spots
    private boolean isProcessing = false;						//Triggers camera view to stop updating when image being processed
    /*private ImageView[] cuteCars = {
    		carIcon1, carIcon2, carIcon3, carIcon4, carIcon5, carIcon6, carIcon7,
    		carIcon8, carIcon9, carIcon10, carIcon11, carIcon12, carIcon13, carIcon14,
    		carIcon15, carIcon16, carIcon17, carIcon18, carIcon19, carIcon20, carIcon21,
    		carIcon22, carIcon23, carIcon24, carIcon25, carIcon26, carIcon27, carIcon28
    };*/				//TODO why doesn't this behave
    
    // TWEAKABLE VALUES
    private final long CAM_DELAY 			= 3000;		//How long to wait to start pulling images
    private final long CAM_UPDATE_RATE 		= 30;		//How often to pull new image
    private final long PROCESS_DELAY 		= 0;		//How long to wait to start processing images
    private final long PROCESS_RATE 		= 4000;		//How often to process image
    private final long CAMERA_SLEEP_RATE	= 100;		//How long to wait to process next image after interrupting camera update
    
    // Date and Time
	SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd");
	SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss z");
    
	/**
	 * Called to terminate program when red X clicked in the main interface
	 */
	public void closeRedX() {
		App.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@SuppressWarnings("deprecation")
			@Override
			public void handle(WindowEvent t) {
				camTimer.cancel();			//stop the camView thread
				processingThread.stop();	//stop the processing thread
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
				System.out.println(done);	//TODO test line
				
				Platform.exit();	//terminate program
			}
		});
	}
	
	/**
	 * Called to terminate program when "Quit" is selected
	 */
	@SuppressWarnings("deprecation")
	public void closeFromButton() {
		buttonQuit.setOnAction(e -> {
			camTimer.cancel();			//stop the camView thread
			processingThread.stop();	//stop the processing thread
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
			System.out.println(done);	//TODO test line
			
			Platform.exit();	//terminate program
		});
	}

	/**
	 * TODO Tim
	 * Shows built-in program instructions
	 */
	public void showUserGuide() {
		buttonHelp.setOnAction(e -> {
			//TODO User Guide goes here
			System.out.println("Tim's not done yet.");
		});
	}
	
	/**
	 * Hides all car icons in the Grid View
	 */
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
		
		/*for (int i = 0; i < cuteCars.length; i ++) {		// TODO why doesn't this behave
			cuteCars[i].setVisible(false);			
		}*/
	}
	
	/**
	 * Contains the Timer and associated task for updating the live camera feed
	 */
	public void updateCamView() {
		//initial image pull, opens connection to camera feed
		try {
			WebCommunications.getImage();
		} catch (Exception e) {
			System.out.println("Boo get :(");	//TODO test line
			imageLastUpdateText.setText("ERROR: check network");
		}
		
		//timer task to update live feed
		camTimer = new Timer();
		camTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//System.out.println(WebCommunications.grabFail);		//test line
				try {	//pull image
					if (WebCommunications.grabFail) {
						if (!isProcessing) WebCommunications.getImage();	//get new image unless processing is occurring
					}
					else {
						if (!isProcessing) WebCommunications.saveImage();	//get new image unless processing is occurring
					}
				} catch (Exception e) {
					System.out.println("Boo save :(");	//TODO test line
				}
				imageFile = WebCommunications.imageForGUIMadness;			//pull image file from system
				image = new Image(imageFile.toURI().toString());			//create Image from File
				webcamView.setImage(image);									//display Image in GUI
				if (WebCommunications.grabFail) {		//if can't get a new image, let user know 
					imageLastUpdateText.setText("ERROR: check network");
					webcamView.setVisible(false);
				}
				else {
					imageLastUpdateText.setText(time.format(Calendar.getInstance().getTime()) 	//update update time
							+ " " + date.format(Calendar.getInstance().getTime()));
					webcamView.setVisible(true);
				}
				
			}
		}, CAM_DELAY, CAM_UPDATE_RATE);
	}
	
	/**
	 * Contains thread for image processing and updating the grid view
	 */
	public void updateGridView() {
		processingTask = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				//first time in the thread, wait for image to be grabbed
				boolean done = false;
				if (!done) {
					Thread.sleep(PROCESS_DELAY);
				}
				done = true;
				
				while (true) {	//loop forever
					Thread.sleep(PROCESS_RATE);
					
					isProcessing = true;	//stop saving new images until current image processed
					Thread.sleep(CAMERA_SLEEP_RATE);
					if (imageFile.exists()) {
						System.out.println("Ding, fries are done (calling web.processImage())");	//TODO test line
						web.processImageRev2("getImageResult.jpg");	//TODO get this working
					}
					
					isProcessing = false;	//start saving new images now that current image is processed
					
					updateMessage(Double.toString(Math.random()));	//triggers listener to update GUI
				}
			}
		};
		
		//execute thread
		processingThread = new Thread(processingTask);
		processingThread.setDaemon(true);
		processingThread.start();
		
		//listener to update GUI after each cycle
		processingTask.messageProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				//clearGrid();		//clear the grid so latest results are shown
				updateGrid();		//updates icons visible in the grid
				updateNumEmpty();	//updates total empty spot display
			}
		});
	}
	
	/*
	 * Uses results of image processing to update icons in grid view
	 */
	public void updateGrid() {
		//System.out.println("The wheels on the bus go round and round"); //test line
		
		//change icon visibility based on spot status
		if (!web.getParkingGrid().getSpotArray()[0].getStatus())
			carIcon1.setVisible(true);
		else
			carIcon1.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[1].getStatus())
			carIcon2.setVisible(true);
		else
			carIcon2.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[2].getStatus())
			carIcon3.setVisible(true);
		else
			carIcon3.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[3].getStatus())
			carIcon4.setVisible(true);
		else
			carIcon4.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[4].getStatus())
			carIcon5.setVisible(true);
		else
			carIcon5.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[5].getStatus())
			carIcon6.setVisible(true);
		else
			carIcon6.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[6].getStatus())
			carIcon7.setVisible(true);
		else
			carIcon7.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[7].getStatus())
			carIcon8.setVisible(true);
		else
			carIcon8.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[8].getStatus())
			carIcon9.setVisible(true);
		else
			carIcon9.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[9].getStatus())
			carIcon10.setVisible(true);
		else
			carIcon10.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[10].getStatus())
			carIcon11.setVisible(true);
		else
			carIcon11.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[11].getStatus())
			carIcon12.setVisible(true);
		else
			carIcon12.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[12].getStatus())
			carIcon13.setVisible(true);
		else
			carIcon13.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[13].getStatus())
			carIcon14.setVisible(true);
		else
			carIcon14.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[14].getStatus())
			carIcon15.setVisible(true);
		else
			carIcon15.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[15].getStatus())
			carIcon16.setVisible(true);
		else
			carIcon16.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[16].getStatus())
			carIcon17.setVisible(true);
		else
			carIcon17.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[17].getStatus())
			carIcon18.setVisible(true);
		else
			carIcon18.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[18].getStatus())
			carIcon19.setVisible(true);
		else
			carIcon19.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[19].getStatus())
			carIcon20.setVisible(true);
		else
			carIcon20.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[20].getStatus())
			carIcon21.setVisible(true);
		else
			carIcon21.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[21].getStatus())
			carIcon22.setVisible(true);
		else
			carIcon22.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[22].getStatus())
			carIcon23.setVisible(true);
		else
			carIcon23.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[23].getStatus())
			carIcon24.setVisible(true);
		else
			carIcon24.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[24].getStatus())
			carIcon25.setVisible(true);
		else
			carIcon25.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[25].getStatus())
			carIcon26.setVisible(true);
		else
			carIcon26.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[26].getStatus())
			carIcon27.setVisible(true);
		else
			carIcon27.setVisible(false);
		if (!web.getParkingGrid().getSpotArray()[27].getStatus())
			carIcon28.setVisible(true);
		else
			carIcon28.setVisible(false);
		
		/*for (int i = 0; i < web.getParkingGrid().getSpotArray().length; i ++) {
			if (!web.getParkingGrid().getSpotArray()[i].getStatus()) {
				cuteCars[i].setVisible(true);
			}
			else {
				cuteCars[i].setVisible(false);
			}
		}*/				//TODO why doesn't this behave
	}
	
	/**
	 * Updates GUI output for the total number of empty spots
	 */
	public void updateNumEmpty() {
		//System.out.println("Two wrongs don't make a right, but two rights make a U-turn!"); //test line
		
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
	 * Event handlers, listeners, and other GUI-related actions
	 */
	public void initialize(URL arg0, ResourceBundle arg1){

		//setToolTips();
		closeRedX();
		closeFromButton();
		showUserGuide();
		clearGrid();
		
		//property binding for spot label overlay check-box
		paneSpotLabelOverlay.visibleProperty().bind(checkBoxLabelOverlay.selectedProperty());
		Platform.runLater(new Runnable() {		//focus on check-box
	        @Override
	        public void run() {
	        	checkBoxLabelOverlay.requestFocus();
	        }
	    });
		
		updateCamView();	//executes camTimer
		updateGridView();	//executes processingThread
	}
} //end GuiView