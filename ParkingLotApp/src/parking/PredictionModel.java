package parking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Hunter Michael
 * @version 1.0
 * @created 19-Feb-2016 5:52:33 PM
 */
public class PredictionModel {

	private File historicalData;

	public PredictionModel()
	{

	}
	
	/**
	 * Sprint 1: No implementation
	 */
	public void finalize() throws Throwable 
	{

	}
	
	/**
	 * @author Hunter Michael
	 * @throws IOException
	 * creates a text file titled by date and appends the document with a time stamp 
	 * and whether each spot is filled
	 */
	public void addToHistory() throws IOException{
		//Get string name of file to write too
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
		Date date = new Date();
		String dateString = dateFormat.format(date)+".txt";

		//get time stamp to append to file
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
		Date time = new Date();
		String timeString = timeFormat.format(time);

		//if exists then append file else create new file and write to it
		historicalData = new File(dateString);
		if(historicalData.exists() && !historicalData.isDirectory()) { 
			FileWriter fw = new FileWriter(historicalData, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			
			
			//here is where we need to format the input 
			WebCommunications webCommunications = new WebCommunications();
			webCommunications.getParkingGrid();
			//after the time stamp
			ParkingLotGrid parkingGrid = webCommunications.getParkingGrid();
			ParkingSpots[] spots = parkingGrid.getSpotArray();
			for(int i = 0; i <= spots.length-1; i++)
			{
				//the array will be converted into a string here
			}
			out.println(timeString);
			out.close();
		}
		else{
			historicalData.createNewFile();
			FileWriter fw = new FileWriter(historicalData);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			//here is where we need to format the input text to include an array after the time stamp
			out.println(timeString);
			out.close();
		}
	}

	/**
	 * Sprint 1: No implementation
	 * @return
	 */
	public double makePrediction()
	{
		return 0;
	}
}//end PredictionModel