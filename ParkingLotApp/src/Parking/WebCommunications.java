package Parking;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * @author Ian McElhenny, Tim Christovitch, Joshua Swain
 * @version 1.0
 * @created 19-Feb-2016 5:52:38 PM
 */
public class WebCommunications {

	private String error;
	
	public static Mat image;	// added to allow GuiView access to image TODO clean this up

	public WebCommunications()
	{
		processImage();
	}

	public void finalize() throws Throwable {

	}
	public void getImage(){

	}

	public void processImage()
	{
		////////////////////////
		//Initialize Variables//
		////////////////////////
		Range xRange = new Range(405, 450);
		Range yRange = new Range(280, 335);
		Mat crop;
		Mat blur = null;
	    Mat hsv = null;
	    Mat mask = null;
	    Scalar lower = new Scalar(0,0,0);
	    Scalar upper = new Scalar(125,40,110); //Based on spot 6 open in bottom open jpg
	    int black = 0;
	    int white = 0;
	    double ratio = 0;

	    
		System.loadLibrary("opencv_java2411");
		
	    //Load image from file
		Mat img = Highgui.imread("src/main/resources/bottomOpen.JPG");
		image = img;	// added to allow GuiView access to image TODO clean this up
		
		//LOOP:
			//Crop to the Nth spot: cropN = img[y:y+h, x:x+w]
			crop = img.submat(yRange, xRange);
			
			//Create a Blur and hsv matrix same size and type ase crop for bilatereral filter return
			Size size = new Size(crop.width(), crop.height());
			blur = Mat.zeros(size , 0);
			hsv = Mat.zeros(size , 0);
			
			//bilaterally filter the image: blurCropN = cv2.bilateralFilter(crop, 20, 75, 75)
			Imgproc.bilateralFilter(crop, blur, 20, 75, 75);
			
			//Convert color space to HSV
			Imgproc.cvtColor(blur, hsv, Imgproc.COLOR_RGB2HSV);
			
			//Mask img with upper and lower limits
			mask = Mat.zeros(size , 0);
			Core.inRange(hsv, lower, upper, mask);

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
				System.out.println("Open");
			}
			else if(ratio < 0.5)
			{
				System.out.println("Taken");
			}
			
		//GOTO top of loop

		 // Save the visualized detection.
//		    String filename = "faceDetection.png";
//		    System.out.println(String.format("Writing %s", filename));
//		    Highgui.imwrite(filename, crop);
		
		    Image image1 = Mat2BufferedImage(mask);
		    displayImage(image1);
		    
		    /////////
		    //Notes//
		    /////////
//			byte buff[] = new byte[(int) (mask.total() * mask.channels())];
//			mask.get(0, 0, buff);
//			System.out.print(hsv.get(40, 15)[2]); //[110, 37, 104]

	}
	public BufferedImage Mat2BufferedImage(Mat m)
	{
		// Fastest code
		// output can be assigned either to a BufferedImage or to an Image

		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
		    type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels()*m.cols()*m.rows();
		byte [] b = new byte[bufferSize];
		m.get(0,0,b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);  
		return image;
		}
	
	public void displayImage(Image img2) 
	{
    	//BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
    	ImageIcon icon=new ImageIcon(img2);
    	JFrame frame=new JFrame();
    	frame.setLayout(new FlowLayout());        
    	frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
    	JLabel lbl=new JLabel();
    	lbl.setIcon(icon);
    	frame.add(lbl);
    	frame.setVisible(true);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	}
}//end WebCommunications