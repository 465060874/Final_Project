package Parking;

import org.opencv.core.Range;
import org.opencv.core.Scalar;

/**
 * @author michaelh
 * @version 1.0
 * @created 19-Feb-2016 5:52:32 PM
 */
public class ParkingSpots 
{
	private Scalar lowerLim = new Scalar(0,0,0);
	private Scalar upperLim = new Scalar(0,0,0);

	private Range xRange = new Range(0,0);
	private Range yRange = new Range(0,0);

	private boolean isEmpty;

	public ParkingSpots()
	{
		
	}

	public void finalize() throws Throwable 
	{
		
	}
	
	public void setEmpty()
	{
		isEmpty = true;
	}
	
	
	public void setOccupied()
	{
		isEmpty = false;
	}
	
	//The following are used by image processing
	
	//This sets the pixel range that the spot is in for the overall image
	public void setLocation(Range x, Range y)
	{
		this.xRange = x;
		this.yRange = y;
	}
	
	//This sets the mask upper and lower limits for the spot to process the image
	public void setHSVLimits(Scalar lower, Scalar upper)
	{
		this.lowerLim = lower;
		this.upperLim = upper;
	}
	
	//get methods for x and y range
	public Range getXRange()
	{
		return this.xRange;
	}
	public Range getYRange()
	{
		return this.yRange;
	}
	//get methods for lower/upper hsv range
	public Scalar getLowerHsv()
	{
		return this.lowerLim;
	}
	public Scalar getUpperHsv()
	{
		return this.upperLim;
	}
	
	
	
	
	
	
	
	
	
	
}//end ParkingSpots