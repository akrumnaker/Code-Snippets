package steganography;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SteganographyWriteReadTest{
	
	/**
	 * This test requires three arguments to run.
	 * args[0] = the path of the image file that will be used to create the new image
	 * 			 with the message inside
	 * args[1] = the path of the file containing the message that will be placed inside
	 * 			 the image
	 * args[2] = the path and name of the image file that will contain the message 
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedImage img = null;
		BufferedImage codedImg = null;
		try{
			img = ImageIO.read(new File(args[0]));
			String msg = StegImageIO.readFile(new File(args[1]));
			if(img != null && msg != null){
				System.out.println("WRITING.........");
				codedImg = StegImageIO.writeImage(img, msg);
				if(codedImg != null){
					try{
						File outputFile = new File(args[2]);
						ImageIO.write(codedImg, "png", outputFile);
						System.out.println("........DONE WRITING\n\n");
						String decodedMsg = "";
						if(codedImg != null){
							System.out.println("GETTING MESSAGE.........");
							decodedMsg = StegImageIO.readImage(codedImg);
							System.out.println(decodedMsg);
							System.out.println(".......DONE GETTING MESSAGE");
						}
						if(msg.equals(decodedMsg)){
							System.out.println("The message was recovered properly.");
						}
					}catch (IOException e){
						System.err.println("IIOException occurred while trying to write the image " + args[2]);
					}
				}else{
					System.err.println("writeImage: The message could not be written.");
				}
			}else{
				System.err.println("java.io.FileNotFoundException occurred while trying to read the file " + args[1]);
			}
		}catch (IOException e){
			System.err.println("java.io.IIOException occurred while trying to read the image " + args[0]);
		}
	}	
}
