package steganography;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class StegImageIO {

	private static final int RB_BIT_MASK = 252;
	private static final int G_BIT_MASK = 248;
	private static final int RB_MASK = 3;
	private static final int G_MASK = 7;
	
	private final static Charset ENCODING = StandardCharsets.UTF_8; 
	
	/**
	 * This method takes in a parameter color of type Color. The method will then extract
	 * the red, green, and blue values of the color. Using these values, the least significant
	 * bits will be retrieved, and the bits will be combined together with bitwise shifting.
	 * This combined value will be stored as a char, and the method will return that char.
	 * @param color
	 * @return char that is contained in the Color
	 */
	public static char colorToChar(Color color){
		int red = color.getRed();
		int blue = color.getBlue();
		int green = color.getGreen();

		// Apply the designated masks to the red, green, blue values to obtain the 
		// desired least significant digits
		int rLeastSig = red & RB_MASK;
		int gLeastSig = green & G_MASK;
		int bLeastSig = blue & RB_MASK;
		
		// Get the character that was tied to the 2 least significant digits from red and blue
		// as well as the 3 least significant digits from green
		// bitwise shift rLeastSig 5 places and gLeastSig 3 places
		// bitwise OR these two values with the bLeastSig in order to get the character
		int ch = (rLeastSig << 5) | (gLeastSig << 2) | bLeastSig;
		char character = (char) ch;
		
		return character;
	}
	
	/**
	 * This method takes in two parameters: a char c, and a Color color. The char c will
	 * be stored as an int in order to break down the bits that comprised the char. The
	 * int will be broken down into blue, green, and red values using bitwise AND as well
	 * as bitwise shift right. These values will then be added to their corresponding color
	 * in order to create a new color. The color that is made is then returned.
	 * @param c
	 * @param color
	 * @return the Color that contains the char
	 */
	public static Color charToColor(char c, Color color){
		int character = c;
		// Break down character into the red, green, and blue bits
		int blue = character & RB_MASK;
		character = character >> 2;
		int green = character & G_MASK;
		character = character >> 3;
		int red = character & RB_MASK;
		color = new Color((color.getRed() & RB_BIT_MASK) + red,
						  (color.getGreen() & G_BIT_MASK) + green,
						  (color.getBlue() & RB_BIT_MASK) + blue);
		return color;
	}
	
	/**
	 * This method takes in a BufferedImage img which holds the encoded image. The method
	 * retrieves the starting row and skip value in order to get the message stored in the
	 * image. The method will loop through the pixels starting at the starting row and skipping
	 * skip pixels and retrieve the character stored in the pixel. The character will be added to
	 * a String. When the character that is read is the null character '\0', the end of the message
	 * has been reached, and the method will stop going through the image. The String will then
	 * be returned. 
	 * @param img
	 * @return the String containing the message from the image
	 */
	public static String readImage(BufferedImage img){
		String msg = "";
		boolean eof = false;
		int width = img.getWidth();
		int height = img.getHeight();
		// Get the starting row value in the first pixel
		Color tempColor = getColor(img, 0, 0);
		char tempChar = colorToChar(tempColor);
		int startRow = tempChar;
//		// Get the skip value in the second pixel
		tempColor = getColor(img, 1, 0);
		tempChar = colorToChar(tempColor);
		int skip = tempChar;
		for(int row = startRow; row < height && !eof; row++){
			for(int col = 0; col < width && !eof; col++){
				// if the skip value is greater than 0
				if(skip > 0){
					if(col % skip == 0){
						Color color = getColor(img, col, row);
						char character = colorToChar(color);
						if(character != '\0'){
							msg += character;
						}else{
							eof = true;
						}
					}else{
						continue;
					}
				}else{
					// the skip value is 0, so get each consecutive pixel
					Color color = getColor(img, col, row);
					char character = colorToChar(color);
					if(character != '\0'){
						// add the character to the msg string
						msg += character;
					}else{
						// the end of the message has been reached
						eof = true;
					}
				}
			}
		}
		msg += '\0';
		return msg;
	}
	
	/**
	 * This method takes in a BufferedImage img and a String msg. A new BufferedImage
	 * will be created by setting it equal to img. The method will then call calclulateStartingRow
	 * to determine the starting row and number of pixels to skip. If startingVals[0] == -1,
	 * return null. Otherwise, set the first pixel to contain the starting row of the
	 * message and the second pixel to contain the number of pixels to skip. The method
	 * will then go through the image beginning at the starting row and placing a char
	 * any time the col % skip == 0. Once the null character at the end of msg has been
	 * added, the method will stop looping and return the new BufferedImage. 
	 * @param img
	 * @param msg
	 * @return the BufferedImage containing the message or null
	 */
	public static BufferedImage writeImage(BufferedImage img, String msg){
		BufferedImage codedImg = img;
		int index = 0;
		int length = msg.length();
		int width = img.getWidth();
		int height = img.getHeight();
		int[] startingVals;
		startingVals = calculateStartingVals(length, width, height);
		if(startingVals[0] == -1){
			return null;
		}
//		// Set the starting row value in the first pixel
		Color tempColor = getColor(img, 0, 0);
		char tempChar = (char)startingVals[0];
		tempColor = charToColor(tempChar, tempColor);
		int tempColorVal = tempColor.getRGB();
		codedImg.setRGB(0, 0, tempColorVal);
//		// Set the skip value in the second pixel
		tempColor = getColor(img, 1, 0);
		tempChar = (char)startingVals[1];
		tempColor = charToColor(tempChar, tempColor);
		tempColorVal = tempColor.getRGB();
		codedImg.setRGB(1, 0, tempColorVal);
		for(int row = startingVals[0]; row < height && index < length; row++){
			for(int col = 0; col < width && index < length; col++){
				if(startingVals[1] == 0){
					Color color = getColor(img, col, row);
					char character = msg.charAt(index);
					index++;
					color = charToColor(character, color);
					int colorVal = color.getRGB();
					codedImg.setRGB(col, row, colorVal);
				}else{
					//continue;
					if(col % startingVals[1] == 0){
						Color color = getColor(img, col, row);
						char character = msg.charAt(index);
						index++;
						color = charToColor(character, color);
						int colorVal = color.getRGB();
						codedImg.setRGB(col, row, colorVal);
					}
				}
			}
		}
		return codedImg;
	}
	
	/**
	 * This method takes in a BufferedImage img and two ints: the column
	 * and row of the pixel in the image. The RGB value of the pixel will 
	 * be retrieved, and the individual values will be obtained using bitwise
	 * AND. The red, green, and blue values will then be used to create a
	 * new Color color which will then be returned. 
	 * @param img
	 * @param col
	 * @param row
	 * @return the Color of the pixel
	 */
	public static Color getColor(BufferedImage img, int col, int row){
		int rgb = img.getRGB(col, row);
		int red = (rgb & 0x00ff0000) >> 16;
		int green = (rgb & 0x0000ff00) >> 8;
		int blue = (rgb & 0x000000ff);
		Color color = new Color(red, green, blue);
		return color;
	}
	
	/**
	 * This method takes in a File file, and it uses a Scanner to read
	 * the contents of file. The Scanner will read the file line by line
	 * while it still has another line, and it will append it to the String
	 * msg. When there is nothing left to read, the null character is appended
	 * to the end of msg to signal that it is the end of the message. The
	 * method will then return msg.
	 * @param file
	 * @return the String msg if and only if the file could be read, otherwise it returns null 
	 */
	public static String readFile(File file){
		Scanner fileReader = null;
		try {
			fileReader = new Scanner(file, ENCODING.name());
			String msg = "";
			while(fileReader.hasNextLine()){
				msg += fileReader.nextLine();
				msg += "\n";
			}
			msg += "\0";
			fileReader.close();
			return msg;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method takes in three ints: the message length and the image width and height.
	 * The method calls the calculateSkippedPixels method to get the skip value. If the skip
	 * value is -1, the method will set startingVals[0] = -1 and return it. Otherwise, skip is
	 * stored in startingVals[1]. The method will then determine the new length of the message
	 * based on the value of skip. After that is done, the number of rows needed to store the
	 * message is then computed via a while loop comparing the new length to the width. This
	 * value is then used to determine the starting row of the message which is then stored
	 * in startingVals[0]. Once complete, the method returns startingVals.
	 * @param msgLength
	 * @param width
	 * @param height
	 * @return an int[] of size 2
	 */
	private static int[] calculateStartingVals(int msgLength, int width, int height){
		// startingVals is an int array of size 2. The first value is the starting row
		// and the second value is the number of pixels that are skipped between chars
		int[] startingVals = new int[2];
		int skip = calculateSkippedPixels(msgLength, width, height);
		// if skip = -1, message cannot be stored, set startingVals[0] = -1 and return startingVals
		if(skip == -1){
			startingVals[0] = -1;
			return startingVals;
		}
		startingVals[1] = skip;
		// starting point will be placed in the first pixel
		int newLength;
		if(skip > 0){
			newLength = msgLength * skip + 2;
		}else{
			newLength = msgLength + 2;
		}
		// need two lines at least to store message
		// first line will only have the starting row location and number of skipped pixels (row[0])
		// message will start on second row (row[1])
		int numRows = 2;
		while(newLength > width){
			// calculate any additional rows needed for the message
			numRows++;
			newLength -= width;
		}
		// if the number of rows needed is greater than the height, the message cannot be stored
		// set startingVals[0] = -1 and return startingVals
		if(numRows > height){
			startingVals[0] = -1;
			return startingVals;
		}else{
			// if the number of rows > 2^7 - 1 (127), set startingVal[0] = 1
			if(numRows > 127){
				startingVals[0] = 1;
			}else{
				// if the message cannot be stored twice inside the image
				if(numRows * 2 > height){
					// set startingVals[0] = the number of rows needed - 2 * numRows - height
					startingVals[0] = numRows - (numRows * 2 - height);
				}else{
					// otherwise, set startingVals[0] = the number of rows needed
					startingVals[0] = numRows;
				}
			}
		}
		return startingVals;
	}
	
	/**
	 * This method takes in three ints: the messageLength and the image width and height.
	 * Based off the total number of pixels in the image, (height * width), the number of times
	 * the message can be stored will be determined. If the number of times it can be stored is
	 * less than 4, skip is set to that number - 1. Otherwise, skip is set to 3. The method then
	 * returns skip.
	 * @param msgLength
	 * @param width
	 * @param height
	 * @return -1 or the value of skip
	 */
	private static int calculateSkippedPixels(int msgLength, int width, int height){
		int skip = 0;
		
		// Skip will be placed in the second pixel of the image
		int totalChars = width * height;
		if(msgLength + width > totalChars){
			return -1;
		}
		// msgLength has width added to it to account for the first row not being used
		// for the message itself
		int numTimesMsgCanBeStored = (int)((double)totalChars / (msgLength + width));
		// skip is set to numTimesMsgCanBeStored - 1 because it will be used in a modulo calculation
		if(numTimesMsgCanBeStored < 4){
			skip = numTimesMsgCanBeStored - 1;
		}else{
			skip = 3;
		}
		return skip;
	}
	
}
