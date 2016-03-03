package Parking;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Joshua Swain
 * @version 1.0
 * @created 19-Feb-2016 5:52:30 PM
 */

public class GuiView implements Initializable {

    @FXML private TabPane viewTabPane;
    @FXML private Tab webcamTab;
    @FXML private ImageView webcamView;
    @FXML private Text imageLastUpdateText;
    @FXML private DatePicker predictionDateSelector;
    @FXML private ComboBox<?> predictionTimeSelector;
    @FXML private Button predictionButton;
    @FXML private TextField predictionOutputField;
    @FXML private CheckBox checkBocLabelOverlay;
    @FXML private Tab detailTab;
    @FXML private GridPane parkingGridPane;
    @FXML private Circle spotStatusGrid1;
    @FXML private Circle spotStatusGrid2;
    @FXML private Circle spotStatusGrid3;
    @FXML private Circle spotStatusGrid4;
    @FXML private Circle spotStatusGrid5;
    @FXML private Circle spotStatusGrid6;
    @FXML private Circle spotStatusGrid7;
    @FXML private Circle spotStatusGrid8;
    @FXML private Circle spotStatusGrid9;
    @FXML private Circle spotStatusGrid10;
    @FXML private Circle spotStatusGrid11;
    @FXML private Circle spotStatusGrid12;
    @FXML private Circle spotStatusGrid13;
    @FXML private Circle spotStatusGrid14;
    @FXML private Circle spotStatusGrid15;
    @FXML private Circle spotStatusGrid16;
    @FXML private Circle spotStatusGrid17;
    @FXML private Circle spotStatusGrid18;
    @FXML private Circle spotStatusGrid19;
    @FXML private Circle spotStatusGrid20;
    @FXML private Circle spotStatusGrid21;
    @FXML private Circle spotStatusGrid22;
    @FXML private Circle spotStatusGrid23;
    @FXML private Circle spotStatusGrid24;
    @FXML private Circle spotStatusGrid25;
    @FXML private Circle spotStatusGrid26;
    @FXML private Circle spotStatusGrid27;
    @FXML private Circle spotStatusGrid28;
    @FXML private Circle spotStatusGrid29;
    @FXML private Text gridLastUpdateText;
    @FXML private Text currentSpotsAvailableText;

    private Image image;
	
	public void convertImage() {
		Mat mat = WebCommunications.image;
		
		MatOfByte byteMat = new MatOfByte();
		Highgui.imencode(".bmp", mat, byteMat);
		image = new Image(new ByteArrayInputStream(byteMat.toArray()));
	}

	public void finalize() throws Throwable {

	}
	public void initialize(URL arg0, ResourceBundle arg1){
		
	}
} //end GuiView