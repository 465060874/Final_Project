package parking;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class PredictionWindow implements Initializable {
	
	// GUI objects loaded from guiPredict.fxml
	@FXML private JFXDatePicker dateChooser, timeChooser;
    @FXML private Text predictionText;
    @FXML private JFXButton buttonGo;
    
    /**
     * TODO
     */
    public void predict() {
    	System.out.println("Abracadabra!");
    }
    
    public void initialize(URL arg0, ResourceBundle arg1) {
    	buttonGo.setOnAction(e -> predict());
    }
}
