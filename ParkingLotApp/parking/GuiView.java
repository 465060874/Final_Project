package parking;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
    @FXML private Text gridLastUpdateText;
    @FXML private Text currentSpotsAvailableText;
    @FXML private Pane paneSpotLabelOverlay;

    // Class Variables
    private Image image;		//the image object to be displayed in the webcam view
    private Timer timer;		//Timer object used to update webcam view
    private File imageFile;		//File object used to pull webcam image
    
    // Date and Time
	SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd");
	SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss z");
    
	/**
	 * Takes the public Mat object image from WebCommunications and converts it to a JavaFX Image object
	 */
	public void convertImage() {
		Mat mat = WebCommunications.image;
		
		MatOfByte byteMat = new MatOfByte();
		Highgui.imencode(".bmp", mat, byteMat);
		image = new Image(new ByteArrayInputStream(byteMat.toArray()));
	}
	
	/**
	 * Non-Function: Sprint 1
	 */
	public void finalize() throws Throwable {

	}
	
	/**
	 * Event handlers, listeners, and other GUI-related actions
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public void initialize(URL arg0, ResourceBundle arg1){
		// Event handler to close application using Red X
		App.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				timer.cancel();
				try {
					WebCommunications.grabber.stop();
				} catch (Exception ed) {
					System.out.println("Tim is a terrible developer.");
				}
				Platform.exit();
			}
		});

		// Event handlers for menu items
		menuClose.setOnAction(e -> {	//<File-Close>
			timer.cancel();
			try {
				WebCommunications.grabber.stop();
			} catch (Exception ed) {
				System.out.println("Tim is a terrible developer.");
			}
			Platform.exit();
		});
		
		// Property binding for spot label overlay checkbox
		paneSpotLabelOverlay.visibleProperty().bind(checkBoxLabelOverlay.selectedProperty());
		
		// Clear the grid on startup
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
		
		//Initial image pull, also opens connection to webcam
		try {
			WebCommunications.getImage();
		} catch (Exception e) {
			System.out.println("Boo get :(");
		}
		
		// timer task to update webcam feed TODO make this cleaner; property binding?
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//System.out.println("test");										//test code to verify update interval TODO remove
				try {															//TEST pull image
					WebCommunications.saveImage();
				} catch (Exception e) {
					System.out.println("Boo save :(");
				}
				imageFile = new File("src/main/resources/getImageResult.JPG");		//pull image file from system TODO change path
				image = new Image(imageFile.toURI().toString());				//create Image from File
				webcamView.setImage(image);										//display Image in GUI
				imageLastUpdateText.setText(time.format(Calendar.getInstance().getTime()) 	//update update time
						+ " " + date.format(Calendar.getInstance().getTime()));
			}
		}, 0, 30);	// change webcam view update interval here!
		
		/* This is Ian's happy place.
		WebCommunications web = new WebCommunications();
		web.processImage("ParkingOpen.JPG");*/
	}
} //end GuiView