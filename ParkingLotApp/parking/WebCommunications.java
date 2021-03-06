package parking;
//import java.awt.FlowLayout;
//import java.awt.Image;
import java.awt.Point;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
//import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;
//import javax.swing.JFrame;
//import javax.swing.JLabel;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
//import org.opencv.core.Range;
//import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javafx.scene.image.Image;

/**
 * @author Ian McElhenny, Tim Christovitch, Joshua Swain
 * @version 1.0
 * @created 19-Feb-2016 5:52:38 PM
 */
public class WebCommunications 
{

	public static FrameGrabber grabber;
	private static Frame frame;

	private ParkingLotGrid parkingLot;
	//public static Mat imageToProcess;
	Mat img;
	Mat crop;
	//Mat blur = null;
    //Mat hsv = null;
    Mat mask = null;
    Mat gray;
    
    Point start;
    Point end;
    
    public static File imageForGUIMadness = new File("src/main/resources/getImageResult.jpg");
	//public static boolean grabFail = false;
	private static BufferedImage imageToShow;
    
	/**
	 * Blank constructor for WebCommunication.java
	 */
	public WebCommunications()
	{
		parkingLot = new ParkingLotGrid();
		parkingLot.setGridSize(28);
	}

	/**
	 * Non-Functional: Sprint 1
	 */
	public void finalize() throws Throwable 
	{

	}
	
	/**
	 * @author Tim Christovitch
	 * @exception
	 * Gets image from website and calls save function
	 */
	public static void getImage() throws Exception 
	{
		/*Use this if your library path is giving you trouble, i.e.:
		 * Exception in thread "main" java.lang.UnsatisfiedLinkError: no opencv_java2411 in java.library.path
		 * 
		 * Use directory of .dll file on machine...
		 */
		//System.load("C:/Users/tchristovich/Documents/opencv/build/java/x64/opencv_java2411.dll");
		
		/*
		 * JavaCV (required for image pull)
		 * http://search.maven.org/remotecontent?filepath=org/bytedeco/javacv/1.1/javacv-1.1-bin.zip
		 */
		
		/* Use this if you know that your library path is configured correctly */
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		grabber = new FFmpegFrameGrabber("http://construction1.db.erau.edu/mjpg/video.mjpg"); 
	    grabber.setFormat("mjpeg");
	    System.out.println("Making connection...");
	    grabber.start();
	    
	    saveImage();
	}
	
	/**
	 * @author Tim Christovitch
	 * Saves the image that is pulled from the website to a file
	 * @throws org.bytedeco.javacv.FrameGrabber.Exception 
	 * @throws IOException 
	 */
	public static void saveImage() throws org.bytedeco.javacv.FrameGrabber.Exception, IOException 
	{
		frame = grabber.grab();
		Java2DFrameConverter javaconverter = new Java2DFrameConverter(); 
		imageToShow = javaconverter.convert(frame);
		ImageIO.write(imageToShow, "jpg", imageForGUIMadness);
	}

	/**
	 * @author Ian McElhenny
	 * @return parkingLot
	 * This method returns the local copy of the object parking lot grid for use by 
	 * the logging function to log the current status of the parking spots
	 */
	public ParkingLotGrid getParkingGrid()
	{
		return this.parkingLot;
	}

	// TODO PROCESS IMAGE REV 2
	/**
	 * This method processes a given image name in the file storage area. 
	 * @author Ian McElhenny
	 * @param filename
	 */
	public void processImageRev2(String filename)
	{
		int erosion_size = 7;
		int x = 5;
		int y = 5;
		int spotAvg = 0;
		int boundT = 51;
		int boundBT = 30;
		int boundBB = 30;
		int bound;
		int ctrl = 98;
		int tweak = 0;
		int currentCtrlAvg;
		boolean oldStatus;
		boolean didChange = false;
		ParkingSpots[] spotArray = parkingLot.getSpotArray();
		PredictionModel predictionModel = new PredictionModel();
		
		
		//load opencv library
		System.loadLibrary("opencv_java2411");
		
		//Load image from file
		img = Highgui.imread("src/main/resources/" + filename);
		
		//Image image2 = Mat2BufferedImage(img);
	    //displayImage(image2);
	    
		Size size = new Size(img.width(), img.height());
		gray = Mat.zeros(size , 0);	
		
		//convert color space to gray
		Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
	    
		//put a gaussian blur on the img
		Imgproc.GaussianBlur(img, img, new Size(x, y), 9);
	    
        //Erode the image
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
		Imgproc.erode(img, img, element);
	    
		//equalize the histogram
		Imgproc.equalizeHist(img, img);
		
		//create a copy of gray image
		gray = img;
		
		//Logic to decide if spot is open or nah
		for(int i = 0; i <= parkingLot.getSpotArray().length-1; i++)
		{		
			//Crop to the Nth spot
			crop = img.submat(spotArray[i].getYRange(), spotArray[i].getXRange());
			
			//get average for masking (index 4 is for bottom lot, 0 is for top lot)
			currentCtrlAvg = getGrayAvg(parkingLot.getStartPoint(i), parkingLot.getEndPoint(i), img);
			if(i < 9)
			{
				bound = boundT;
			}
			else if(i > 8 && i < 22)
			{
				bound = boundBT;//top of bottom lot
			}
			else
			{
				bound = boundBB;//bottom of bottom lot
			}
			
			//get average gray pixel value of spot
			spotAvg = getAvg(crop, 0);//index is 2 for hsv and 0 for gray

			//Check old status of spot to tell if it changed
			oldStatus = parkingLot.getStatus(i);		
			
			//decide if spot is open or taken
			if(spotAvg > currentCtrlAvg - bound)//spot is open (255 is white)
			{
				if(oldStatus == false)
				{
					didChange = true;
				}
				System.out.println("Spot: " + (i+1) + " is open");
				parkingLot.setStatus(i, true);//spot empty
			}
			else //spot taken (0 is black)
			{
				if(oldStatus == true)
				{
					didChange = true;
				}
				System.out.println("Spot: " + (i+1) + " is taken");
				parkingLot.setStatus(i, false);//spot occupied
			}
			System.out.println("Spot Average:" + spotAvg);
			System.out.println("Current Average - bound:" + (currentCtrlAvg - bound));
		}
		//Image image1 = Mat2BufferedImage(img);
	    //displayImage(image1);
		//Log results if the spot status changed
		if(didChange)
		{
			try {
				predictionModel.addToHistory();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("IOException: eror calling predictionModel.addToHistory()");
				e.printStackTrace();
			}
		}
	}
	
	//returns a scalar of the average value of the three indexes of sent image. Image is full image, start and end are points on that image.
	/**
	 * This method returns a scalar of the average value of the three indexes of sent image. Image is full image, start and end are points on that image.
	 * @author Ian McElhenny
	 * @param start
	 * @param end
	 * @param hsv
	 * @return
	 */
	public Scalar getHsvAvg(Point start, Point end, Mat hsv)
	{
		int x = (int)(end.getX()-start.getX());
		int y = (int)(end.getY()-start.getY());
		int one = 0;
		int two = 0;
		int three = 0;
		int count = 0;
		
		for(int i = 0; i <= x; i++)
		{
			for(int j = 0; j <= y; j++)
			{
				one = (one + (int)hsv.get((int)(j + start.getY()), (int)(i + start.getX()))[0]);
				two = (two + (int)hsv.get((int)(j + start.getY()), (int)(i + start.getX()))[1]);
				three = (three + (int)hsv.get((int)(j + start.getY()), (int)(i + start.getX()))[2]);
				count++;
			}
			
		}
		one = one/count;
		two = two/count;
		three = three/count;
		
//		System.out.println("Average");
//		System.out.println("HSV 1: " + one);
//		System.out.println("HSV 2: " + two);
//		System.out.println("HSV 3: " + three + "\n\n");
		return new Scalar(one, two, three);
		
	}
	
	//return the average gray pixel given a gray matrix and a start and end point for the region. Image is full size image.
	/**
	 * This method will return the average gray pixel given a gray matrix and a start and end point for the region. Image is full size image.
	 * @author Ian McElhenny
	 * @param start
	 * @param end
	 * @param gray
	 * @return average (int)
	 */
	public int getGrayAvg(Point start, Point end, Mat gray)
	{
		int x = (int)(end.getX()-start.getX());
		int y = (int)(end.getY()-start.getY());
		int one = 0;
		int count = 0;
		
		for(int i = 0; i <= x; i++)
		{
			for(int j = 0; j <= y; j++)
			{
				one = (one + (int)gray.get((int)(j + start.getY()), (int)(i + start.getX()))[0]); //row, column
				count++; 
			}
			
		}
		one = one/count;
//		System.out.println("Average Gray: " + one);
		return one;
		
	}
	
	
	//returns the average of the submat given, and index controls if it is hsv after gray scale(2) or just gray scale(0).
	/**
	 * This method returns the average of the submat given. Index controls if it is hsv after gray scale(2) or just gray scale(0).
	 * @author Ian McElhenny
	 * @param img
	 * @param index
	 * @return average (int)
	 */
	public int getAvg(Mat img, int index)
	{
		int x = img.height();
		int y = img.width();
		int one = 0;
		int count = 0;
		
		for(int i = 0; i < x; i++)
		{
			for(int j = 0; j < y; j++)
			{
				one = (one + (int)img.get(i, j)[index]);
				count++;
			}
			
		}
		one = one/count;
//		System.out.println("Average");
//		System.out.println("Average: " + one + "\n\n");
		return one;
		
	}
	//Takes a img of the parking lot, subdivides it into spots, process each spot in a for loop then 
	/**
	 * @author Ian McElhenny
	 * @param filename
	 * 
	 * Reads an image from a file name, then processes the image
	 */
/*
	public void processImage(String filename)
	{
		System.loadLibrary("opencv_java2411");

		//Load image from file
		Mat img = Highgui.imread("src/main/resources/" + filename);
		
		////////////////////////
		//Initialize Variables//
		////////////////////////
		Mat crop;
		Mat blur = null;
		Mat hsv = null;
		Mat mask = null;
		int black = 0;
		int white = 0;
		double ratio = 0;
		ParkingSpots[] spotArray = parkingLot.getSpotArray();

		
		//loop through array or parking lot and process each spot
		for(int i = 0; i <= parkingLot.getSpotArray().length-1; i++)
		{
			//Set black and white count to zero from last run
			black = 0;
			white = 0;
			
			//Crop to the Nth spot
			crop = img.submat(spotArray[i].getYRange(), spotArray[i].getXRange());
			
			//Create a Blur, hsv, and mask matrix same size and type as crop for bilateral filter return, hsv return and mask return
			Size size = new Size(crop.width(), crop.height());
			blur = Mat.zeros(size , crop.type());	//might need to change zero
			hsv = Mat.zeros(size , crop.type());
			mask = Mat.zeros(size , crop.type());

			//bilaterally filter the image
			Imgproc.bilateralFilter(crop, blur, 20, 50, 50);
			
			//Convert color space to HSV
			Imgproc.cvtColor(blur, hsv, Imgproc.COLOR_RGB2HSV);
			
			//Mask img with upper and lower limits
			Core.inRange(hsv, spotArray[i].getLowerHsv(), spotArray[i].getUpperHsv(), mask);

			//Count the white pixels and black pixels
			for(int x = 0; x <= mask.size().width - 1; x++)
			{
				for(int y = 0; y <= mask.size().height - 1; y++)
				{
					if(mask.get(y, x)[0] == 0.0)
					{
						black++;
					}
					else if(mask.get(y, x)[0] == 255.0)
					{
						white++;
					}
				}
			}
			
			System.out.println(black + ", " + white);
			
			//Make decision about status of spot
			ratio = (double)white/(white+black);
			if(ratio > 0.5)
			{
				System.out.println("Spot: " + (i+1) + " is open");
				parkingLot.setStatus(i, true);//spot empty
			}
			else if(ratio < 0.5)
			{
				System.out.println("Spot: " + (i+1) + " is taken");
				parkingLot.setStatus(i, false);//spot occupied
			}
			
//			Image image1 = Mat2BufferedImage(crop);
//		    displayImage(image1);
//			Image image2 = Mat2BufferedImage(mask);
//		    displayImage(image2);
		}
//		Image image3 = Mat2BufferedImage(img);
//	    displayImage(image3);
	}
	*/
//	//copy to test individual spots (TEMP)
//	public void processImage(int i)
//	{
//		System.loadLibrary("opencv_java2411");
//
//		//Load image from file
//		Mat img = Highgui.imread("src/main/resources/ParkingOpen.JPG");
//		
//		////////////////////////
//		//Initialize Variables//
//		////////////////////////
//		Mat crop;
//		Mat blur = null;
//		Mat hsv = null;
//		Mat mask = null;
//		int black = 0;
//		int white = 0;
//		double ratio = 0;
//		ParkingLotGrid parkingLot = new ParkingLotGrid();
//		//This should be dynamic but is hard coded for now.
//		parkingLot.setGridSize(28);//I, ian, counted 28 spots that i think we can process effectively in the area
//		ParkingSpots[] spotArray = parkingLot.getSpotArray();
//
//		
//	
//		//Crop to the Nth spot
//		crop = img.submat(spotArray[i].getYRange(), spotArray[i].getXRange());
//		
//		//Create a Blur, hsv, and mask matrix same size and type as crop for bilateral filter return, hsv return and mask return
//		Size size = new Size(crop.width(), crop.height());
//		blur = Mat.zeros(size , 0);
//		hsv = Mat.zeros(size , 0);
//		mask = Mat.zeros(size , 0);
//
//		//bilaterally filter the image
//		Imgproc.bilateralFilter(crop, blur, 20, 75, 75);
//		
//		//Convert color space to HSV
//		Imgproc.cvtColor(blur, hsv, Imgproc.COLOR_RGB2HSV);
//		
//		//Mask img with upper and lower limits
//		Core.inRange(hsv, spotArray[i].getLowerHsv(), spotArray[i].getUpperHsv(), mask);
//		
//		//Count the white pixels and black pixels
//		for(int x = 0; x <= mask.size().width - 1; x++)
//		{
//			for(int y = 0; y <= mask.size().height - 1; y++)
//			{
//				if(mask.get(y, x)[0] == 0.0)
//				{
//					black++;
//				}
//				else if(mask.get(y, x)[0] == 255.0)
//				{
//					white++;
//				}
//			}
//		}
//		
//		System.out.println(black + ", " + white);
//		
//		//Make decision about status of spot
//		ratio = (double)white/(white+black);
//		if(ratio > 0.5)
//		{
//			System.out.println("Spot: " + (i+1) + " is open");
//			parkingLot.setStatus(i, true);//spot empty
//		}
//		else if(ratio < 0.5)
//		{
//			System.out.println("Spot: " + (i+1) + " is taken");
//			parkingLot.setStatus(i, false);//spot occupied
//		}
//		Image image1 = Mat2BufferedImage(crop);
//	    displayImage(image1);
//		Image image2 = Mat2BufferedImage(mask);
//	    displayImage(image2);
//	}
//	
//		
//	//(TEMP)
//	public void procImage()
//	{
//		////////////////////////
//		//Initialize Variables//
//		////////////////////////
//		Range xRange = new Range(405, 450);
//		Range yRange = new Range(280, 335);
////		Mat crop;
////		Mat blur = null;
////	    Mat hsv = null;
////	    Mat mask = null;
//	    Scalar lower = new Scalar(0,0,0);
//	    //to make the upper liit better look at three always open spots and average the value of the spots to keep a good upper limit
//	    Scalar upper = new Scalar(100,100,100); //Based on spot 6 open in bottom open jpg
//	    int black = 0;
//	    int white = 0;
//	    double ratio = 0;
//	    
//		System.loadLibrary("opencv_java2411");
//	    img = Highgui.imread("src/main/resources/ParkingOpen.JPG");
//
//	    //Load image from file
////		Mat img = Highgui.imread("src/main/resources/bottomOpen.JPG");
//		
//		//LOOP:
//			//Crop to the Nth spot: cropN = img[y:y+h, x:x+w]
//			crop = img.submat(yRange, xRange);
//			
//			//Create a Blur and hsv matrix same size and type ase crop for bilatereral filter return
//			Size size = new Size(crop.width(), crop.height());
//			blur = Mat.zeros(size , 0);
//			hsv = Mat.zeros(size , 0);
//			
//			//bilaterally filter the image: blurCropN = cv2.bilateralFilter(crop, 20, 75, 75)
//			Imgproc.bilateralFilter(crop, blur, 20, 75, 75);
//			
//			//Convert color space to HSV
//			Imgproc.cvtColor(blur, hsv, Imgproc.COLOR_RGB2HSV);
//			
//			//Mask img with upper and lower limits
//			mask = Mat.zeros(size , 0);
//			Core.inRange(hsv, lower, upper, mask);
//
//			//Count the white pixels and black pixels
//			for(int x = 0; x <= mask.size().width - 1; x++)
//			{
//				for(int y = 0; y <= mask.size().height - 1; y++)
//				{
//					if(mask.get(y, x)[0] == 0.0)
//					{
//						black++;
//					}
//					else if(mask.get(y, x)[0] == 255.0)
//					{
//						white++;
//					}
//				}
//			}
//			
//			System.out.println(black + ", " + white);
//			
//			//Make decision about status of spot
//			ratio = (double)white/(white+black);
//			if(ratio > 0.5)
//			{
//				System.out.println("Open");
//			}
//			else if(ratio < 0.5)
//			{
//				System.out.println("Taken");
//			}
//			
//		//GOTO top of loop
//
//			
//			
//			
//			///////////////////////
//			//Display Image(Temp)//
//			///////////////////////		
//			
//		 // Save the visualized detection.
////		    String filename = "faceDetection.png";
////		    System.out.println(String.format("Writing %s", filename));
////		    Highgui.imwrite(filename, crop);
//		
//		    Image image1 = Mat2BufferedImage(mask);
//		    displayImage(image1);
//		    
//		    /////////
//		    //Notes//
//		    /////////
////			byte buff[] = new byte[(int) (mask.total() * mask.channels())];
////			mask.get(0, 0, buff);
////			System.out.print(hsv.get(40, 15)[2]); //[110, 37, 104]
//
//	}
//	
//	
//	
//	/////////////
//	//Temporary//
//	/////////////
//	public BufferedImage Mat2BufferedImage(Mat m)
//	{
//		// Fastest code
//		// output can be assigned either to a BufferedImage or to an Image
//
//		int type = BufferedImage.TYPE_BYTE_GRAY;
//		if ( m.channels() > 1 ) {
//		    type = BufferedImage.TYPE_3BYTE_BGR;
//		}
//		int bufferSize = m.channels()*m.cols()*m.rows();
//		byte [] b = new byte[bufferSize];
//		m.get(0,0,b); // get all the pixels
//		BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
//		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//		System.arraycopy(b, 0, targetPixels, 0, b.length);  
//		return image;
//	}
//	JFrame frame;
//	public void displayImage(Image img2) 
//	{
//    	//BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
//    	ImageIcon icon=new ImageIcon(img2);
//    	frame=new JFrame();
//    	frame.setLayout(new FlowLayout());        
//    	frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
//    	JLabel lbl=new JLabel();
//    	lbl.setIcon(icon);
//    	lbl.addMouseListener(this);
//    	frame.add(lbl);
//    	frame.setVisible(true);
//    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	}
//	
//	
//	
//	///////////////////
//	//Mouse listeners//
//	///////////////////
//	@Override
//	public void mouseClicked(MouseEvent e) 
//	{
//		frame.dispose();
////	       System.out.println("HSV 1: " + hsv.get(e.getX(), e.getY())[0]);
////	       System.out.println("HSV 2: " + hsv.get(e.getX(), e.getY())[1]);
////	       System.out.println("HSV 3: " + hsv.get(e.getX(), e.getY())[2]);
//
////	       System.out.println(e.getPoint());
//    }
//
//	@Override
//	public void mouseEntered(MouseEvent arg0) 
//	{
//		//  Auto-generated method stub
//		
//	}
//
//	@Override
//	public void mouseExited(MouseEvent arg0) 
//	{
//		//  Auto-generated method stub
//		
//	}
//
//	
//	public void mousePressed(MouseEvent e) 
//	{
////		System.out.println("Entered ");
////   System.out.println(e.getPoint());
//		
////		start = e.getPoint();
//		
//	}
//
//	
//	public void mouseReleased(MouseEvent e) 
//	{
////		System.out.println("Exited");
////   System.out.println(e.getPoint());
////		end = e.getPoint();
////		
////		getHsvMax(start, end, hsv);
////		getHsvAvg(start, end, hsv);
//
//	}
//	
//	
//	//For a given matrix of pixels and an area from start (x1, y1 pixel) to end (x2, y2 pixel) it tells the largest and smallest value of the pixels.
//	public void getHsvMax(Point start, Point end, Mat hsv)
//	{
//		int x = (int)(end.getX()-start.getX());
//		int y = (int)(end.getY()-start.getY());
//		int oneh = 0;
//		int twoh = 0;
//		int threeh = 0;
//		int onel = 255;
//		int twol = 255;
//		int threel = 255;
//		
//		for(int i = 0; i <= x; i++)
//		{
//			for(int j = 0; j <= y; j++)
//			{
//				//high
//				if( (int)hsv.get(i, j)[0] > oneh)
//				{
//					oneh = (int)hsv.get(i, j)[0];
//
//				}
//				if((int)hsv.get(i, j)[1] > twoh)
//				{
//					twoh = (int)hsv.get(i, j)[1];
//
//				}
//				if((int)hsv.get(i, j)[2] > threeh)
//				{
//					threeh = (int)hsv.get(i, j)[2];
//				}
//				//low
//				if( (int)hsv.get(i, j)[0] < onel)
//				{
//					onel = (int)hsv.get(i, j)[0];
//
//				}
//				if((int)hsv.get(i, j)[1] < twol)
//				{
//					twol = (int)hsv.get(i, j)[1];
//
//				}
//				if((int)hsv.get(i, j)[2] < threel)
//				{
//					threel = (int)hsv.get(i, j)[2];
//				}
//			}
//			
//		}
//		System.out.println("MAX");
//		System.out.println("HSV 1: " + oneh);
//		System.out.println("HSV 2: " + twoh);
//		System.out.println("HSV 3: " + threeh);
//		
//		System.out.println("Min");
//		System.out.println("HSV 1: " + onel);
//		System.out.println("HSV 2: " + twol);
//		System.out.println("HSV 3: " + threel);
//		
//	}
//	
//	//For a given matrix of pixels and an area from start (x1, y1 pixel) to end (x2, y2 pixel) it tells the avg value of the pixels.
//	public void getHsvAvg(Point start, Point end, Mat hsv)
//	{
//		int x = (int)(end.getX()-start.getX());
//		int y = (int)(end.getY()-start.getY());
//		int one = 0;
//		int two = 0;
//		int three = 0;
//		
//		for(int i = 0; i <= x; i++)
//		{
//			for(int j = 0; j <= y; j++)
//			{
//				one = (one + (int)hsv.get(i, j)[0])/2;
//				two = (two + (int)hsv.get(i, j)[1])/2;
//				three = (three + (int)hsv.get(i, j)[2])/2;
//			}
//			
//		}
//		System.out.println("Average");
//		System.out.println("HSV 1: " + one);
//		System.out.println("HSV 2: " + two);
//		System.out.println("HSV 3: " + three + "\n\n");
//		
//		
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	

	
	
	
	
}//end WebCommunications