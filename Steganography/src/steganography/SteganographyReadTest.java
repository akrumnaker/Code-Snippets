package steganography;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SteganographyReadTest {

	/**
	 * This test requires three arguments to run.
	 * args[0] = the path of the image file that contains a message
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedImage test = null;
		try{
			test = ImageIO.read(new File(args[0]));
			String decode = StegImageIO.readImage(test);
			System.out.println(decode);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

}
