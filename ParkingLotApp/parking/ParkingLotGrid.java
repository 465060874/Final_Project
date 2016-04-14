package parking;

import java.awt.Point;

import org.opencv.core.Range;
import org.opencv.core.Scalar;

/**
 * @author Ian McElhenny
 * @version 1.0
 * @created 19-Feb-2016 5:52:31 PM
 */
public class ParkingLotGrid 
{
	private int totalSpots = 28; //Starting count with 1 not zero.
	private boolean isFull;
	private ParkingSpots[] myGrid;
	private String timeSinceUpdated;
	public ParkingSpots m_ParkingSpots;	

	//Hard Coded array of Ranges for all the spots, if we make it were this is done through the GUI this isnt needed anymore
	private Range[] xRange = {  new Range(195, 218), new Range(224, 258),new Range(262, 301),new Range(299, 341),new Range(387, 412),new Range(425, 455),
								new Range(455, 487), new Range(484, 528),new Range(520, 550),new Range(191, 232),new Range(250, 275),new Range(285, 315),
								new Range(325, 360), new Range(375, 400),new Range(412, 440),new Range(455, 485),new Range(490, 515),new Range(530, 550),
								new Range(560, 580), new Range(595, 615),new Range(622, 638),new Range(655, 670),new Range(107, 157),new Range(185, 222),
								new Range(230, 280), new Range(292, 340),new Range(370, 395),new Range(394, 471)};
	
	private Range[] yRange = {  new Range(220, 259), new Range(229, 260),new Range(227, 262),new Range(230, 264),new Range(233, 266),new Range(233, 267),
								new Range(235, 270), new Range(240, 269),new Range(243, 273),new Range(276, 315),new Range(282, 329),new Range(282, 333),
								new Range(284, 334), new Range(290, 333),new Range(295, 330),new Range(285, 334),new Range(289, 334),new Range(291, 334),
								new Range(295, 330), new Range(293, 335),new Range(295, 330),new Range(310, 330),new Range(401, 480),new Range(408, 478),
								new Range(420, 478), new Range(413, 470),new Range(415, 479),new Range(395, 478)};
	
	private Scalar lower = new Scalar(0, 0, 0);
	private Scalar spot0_8 = new Scalar(170, 62, 245);
	private Scalar spot9_27 = new Scalar(120,45,170);

	//Copied values starting at 1 :6->7,8,9,10; 12->13; 15-16; 22->23; 27->28
	private Scalar[] lowerArray = {
			new Scalar(0,0,90), new Scalar(0,0,105), new Scalar(0,0,105), new Scalar(0,0,105), new Scalar(0,0,110), new Scalar(0,0,70), 
			new Scalar(0,0,70), new Scalar(0,0,70), new Scalar(0,0,70), new Scalar(0,0,70), new Scalar(0,0,50), new Scalar(0,0,65), 
			new Scalar(0,0,65), new Scalar(0,0,60), new Scalar(0,0,70), new Scalar(0,0,70), new Scalar(0,0,50), new Scalar(0,0,80), 
			new Scalar(0,0,80), new Scalar(0,0,80), new Scalar(0,0,90), new Scalar(0,0,85), new Scalar(0,0,85), new Scalar(0,0,60), 
			new Scalar(0,0,80), new Scalar(0,0,70), new Scalar(0,0,80), new Scalar(0,0,80)};
	
	//Copied values starting at 1 :6->7,8,9,10; 12->13; 15-16; 22->23; 27->28
	private Scalar[] upperArray = {
			new Scalar(90,90,130), new Scalar(150,7,140), new Scalar(140,10,130), new Scalar(150,15,130), new Scalar(150,20,125), new Scalar(140,20,130), 
			new Scalar(140,20,130), new Scalar(140,20,130), new Scalar(140,20,130), new Scalar(140,20,130), new Scalar(150,30,85), new Scalar(150,8,80), 
			new Scalar(150,8,80), new Scalar(170,60,130), new Scalar(170,10,85), new Scalar(170,10,85), new Scalar(140,50,120), new Scalar(150,18,110), 
			new Scalar(170,15,110), new Scalar(165,20,115), new Scalar(150,15,110), new Scalar(150,10,100), new Scalar(150,10,100), new Scalar(140,40,110), 
			new Scalar(140,40,100), new Scalar(150,30,90), new Scalar(150,20,90), new Scalar(150,20,90)};

	// Outlines the control area for adjusting to weather and light change conditions
		private Point[] controlAreaStart = {
				//Top Lot
				new Point(197, 198), new Point(279, 203), new Point(385, 211), new Point(463, 220), 
				//Bottom Lot
				new Point(170, 356), new Point(190, 337), new Point(285, 339)
		};
		private Point[] controlAreaEnd = {
				//Top Lot
				new Point(243, 220), new Point(345, 219), new Point(470, 227), new Point(558, 232), 
				//Bottom Lot
				new Point(210, 384), new Point(251, 375), new Point(337, 382)
		};
	
	/**
	 * @author Ian McElhenny
	 * Blank Constructor
	 */
	public ParkingLotGrid()
	{
		
	}

	/**
	 * @author Ian McElhenny
	 * @param size
	 */
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
	/**
	 * @author Ian McElhenny
	 * Populates the spots in the grid by location
	 */
	public void populateGrid()
	{
		for(int i = 0; i <= this.myGrid.length - 1; i++)
		{
			this.myGrid[i].setLocation(xRange[i], yRange[i]);
		}
		setHSVIndividual();
	}
	
	//Gives a certain number spot a location of pixels in the picture for all spots in the photo
	/**
	 * @author Ian McElhenny
	 * Populates the hsv lower and upper limit per spot of grid
	 */
	public void setHSVIndividual()
	{
		for(int i = 0; i <= this.myGrid.length - 1; i++)
		{
			this.myGrid[i].setHSVLimits(this.lowerArray[i], this.upperArray[i]);
		}
	}
	
	
	//Set the hsv value for each spot (TEMP)
	/**
	 * Temporary Test method
	 */
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
	/**
	 * @author Ian McElhenny
	 * @param spotNumber
	 * @param status
	 * Sets the status of the spot by sending a boolean and a spot number True is empty
	 */
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
	
	/**
	 * This is used to get the status of the spot, true is empty false is taken
	 * @param spotNumber
	 * @return status of spot
	 */
	public boolean getStatus(int spotNumber)
	{
		return myGrid[spotNumber].getStatus();
	}
	
	//Return array of parking spots
	/**
	 * Used to give my object of ParkingSpots to the PredictionModel for logging each spot
	 * @return ParkingSpots[]
	 */
	public ParkingSpots[] getSpotArray()
	{
		return this.myGrid;
	}
	
	
	//A file checking method???-Ian
	/**
	 * Sprint 1: Not implemented
	 */
	public void updateGrid()
	{

	}
	
	//Returns the start point for the control area selected by int i
	public Point getStartPoint(int i)
	{
		return this.controlAreaStart[i];
	}
	//Returns the end point for the control area selected by int i
	public Point getEndPoint(int i)
	{
		return this.controlAreaEnd[i];
	}
}//end ParkingLotGrid