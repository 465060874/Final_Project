package parking;

import org.opencv.core.Range;
import org.opencv.core.Scalar;

/**
 * @author Ian McElhenny
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
		isEmpty = false;	//TODO did this work?
	}
	/**
	 * Sprint 1: No implementation
	 */
	public void finalize() throws Throwable 
	{
		
	}
	/**
	 * @author Ian McElhenny
	 * Sets the spots boolean to true for being empty
	 */
	public void setEmpty()
	{
		isEmpty = true;
	}
	
	/**
	 * @author Ian McElhenny
	 * Sets the spots boolean to false for being occupied
	 */
	public void setOccupied()
	{
		isEmpty = false;
	}
	
	//The following are used by image processing
	
	//This sets the pixel range that the spot is in for the overall image.
	/**
	 * @author Ian McElhenny
	 * Sets the location of the spot for image processing
	 * @param x
	 * @param y
	 */
	public void setLocation(Range x, Range y)
	{
		this.xRange = x;
		this.yRange = y;
	}
	
	//This sets the mask upper and lower limits for the spot to process the image
	/**
	 * @author Ian McElhenny
	 * Sets the HSV limits of the spot for image processing
	 * @param lower
	 * @param upper
	 */
	public void setHSVLimits(Scalar lower, Scalar upper)
	{
		this.lowerLim = lower;
		this.upperLim = upper;
	}
	
	//get methods for x and y range
	/**
	 * @author Ian McElhenny
	 * Gets the xRange for the spot
	 * @return xRange
	 */
	public Range getXRange()
	{
		return this.xRange;
	}
	/**
	 * @author Ian McElhenny
	 * Gets the yRange for the spot
	 * @return yRange
	 */
	public Range getYRange()
	{
		return this.yRange;
	}
	//get methods for lower/upper hsv range
	/**
	 * @author Ian McElhenny
	 * Returns the lower HSV scalar
	 * @return lowerLim
	 */
	public Scalar getLowerHsv()
	{
		return this.lowerLim;
	}
	/**
	 * @author Ian McElhenny
	 * Returns the upper HSV scalar
	 * @return upperLim
	 */
	public Scalar getUpperHsv()
	{
		return this.upperLim;
	}
	//get method for status
	/**
	 * @author Ian McElhenny
	 * Returns the status of the spot
	 * 
	 * Boolean True is empty
	 * Boolean False is occupied
	 * @return isEmpty
	 */
	public boolean getStatus()
	{
		return this.isEmpty;
	}
	
	
	
	
	
	
	
	
	
}//end ParkingSpots