package Parking;

import org.opencv.core.Range;
import org.opencv.core.Scalar;

/**
 * @author michaelh
 * @version 1.0
 * @created 19-Feb-2016 5:52:31 PM
 */
public class ParkingLotGrid 
{

	private boolean isFull;
	private ParkingSpots[] myGrid;
	private String timeSinceUpdated;
	public ParkingSpots m_ParkingSpots;
	private int totalSpots = 28; //Starting count with 1 not zero.

	//Hard Coded array of Ranges for all the spots, if we make it were this is done through the GUI this isnt needed anymore
	private Range[] xRange = {  new Range(195, 218), new Range(224, 258),new Range(262, 301),new Range(299, 341),new Range(373, 422),new Range(414, 455),
								new Range(447, 493), new Range(484, 528),new Range(520, 558),new Range(191, 232),new Range(237, 281),new Range(279, 328),
								new Range(319, 370), new Range(360, 415),new Range(402, 458),new Range(433, 501),new Range(477, 538),new Range(513, 570),
								new Range(544, 602), new Range(581, 634),new Range(608, 659),new Range(635, 690),new Range(107, 157),new Range(173, 222),
								new Range(228, 286), new Range(283, 350),new Range(339, 412),new Range(394, 471)};
	
	private Range[] yRange = {  new Range(220, 259), new Range(229, 260),new Range(227, 262),new Range(230, 264),new Range(233, 266),new Range(233, 267),
								new Range(235, 270), new Range(240, 269),new Range(243, 273),new Range(276, 328),new Range(278, 329),new Range(282, 333),
								new Range(284, 334), new Range(284, 333),new Range(285, 335),new Range(285, 334),new Range(289, 334),new Range(291, 334),
								new Range(289, 335), new Range(293, 335),new Range(292, 330),new Range(291, 330),new Range(401, 480),new Range(408, 478),
								new Range(415, 478), new Range(413, 479),new Range(403, 479),new Range(395, 478)};
	
	private Scalar lower = new Scalar(0, 0, 0);
	private Scalar spot0_8 = new Scalar(170, 62, 245);
	private Scalar spot9_27 = new Scalar(120,45,170);

	
	public ParkingLotGrid()
	{
		
	}

	public void finalize() throws Throwable 
	{

	}
	
	public void setGridSize(int size)
	{
		this.myGrid = new ParkingSpots[totalSpots];
		for(int i = 0; i <= totalSpots-1; i++)
			{
				this.myGrid[i] = new ParkingSpots();
			}
		populateGrid();//this call being here is temp unless we make this a hardcoded parking lot
	}
	
	//Gives a certain number spot a location of pixels in the picture for all spots in the photo
	public void populateGrid()
	{
		for(int i = 0; i <= this.myGrid.length - 1; i++)
		{
			this.myGrid[i].setLocation(xRange[i], yRange[i]);
		}
		setHSV();
	}
	
	//Set the hsv value for each spot
	public void setHSV()
	{
		for(int i = 0; i <= 8; i++)
		{
			this.myGrid[i].setHSVLimits(this.lower, this.spot0_8);;
		}
		for(int i = 9; i <= this.myGrid.length - 1; i++)
		{
			this.myGrid[i].setHSVLimits(this.lower, this.spot9_27);;
		}
	}
	
	//set the spot status: True is empty
	public void setStatus(int spotNumber, boolean status)
	{
		if(status)
		{
			myGrid[spotNumber].setEmpty();
		}
		else
		{
			myGrid[spotNumber].setOccupied();
		}
		
	}
	
	//Return array of parking spots
	public ParkingSpots[] getSpotArray()
	{
		return this.myGrid;
	}
	
	
	//A file checking method???-Ian
	public void updateGrid()
	{

	}
}//end ParkingLotGrid