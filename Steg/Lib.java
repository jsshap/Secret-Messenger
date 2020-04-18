import java.util.LinkedList;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class Lib{

    public static LinkedList<Integer> pullSpecifiedBitsOfSpecificColors(BufferedImage image, int[] colors, int[] whichBits){
        //int[] bits should be [2,1] for two least sig 2 bits and [1] for LSB
        //[3,2,1] for least sig 3 bits
        //This method takes in a list of color channels and a least of bits (3rd lsb, 2nd lsb, 1st lsb)
        //and returns a list of those bits

        //Pull LSBs of one color channel. R=0, G=1, B=2. Input array must be in increasing order
        //like in pullLSBsOfSpecificColors
        int width = image.getWidth();
        int height = image.getHeight();
        LinkedList<Integer> bits = new LinkedList<Integer>();
        WritableRaster raster = image.getRaster();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int[] pixels = raster.getPixel(c, r, (int[]) null);
                for (int i : colors){
                    for (int j: whichBits){
                        bits.add((pixels[i] & ((int) Math.pow(2,j-1)))>>(j-1)); 
                    }
                }
            }
        }
        return bits;    
    }


    public static LinkedList<Integer> convertBitsToBytes(LinkedList<Integer> bits){
        //Make a copy of bits, so it is not modified outside this method. only alter the copy
        LinkedList<Integer> localBits = new LinkedList<Integer>();
        for(Integer i: bits){localBits.add(i);}

        LinkedList<Integer> bytes = new LinkedList<Integer>();
        while (!localBits.isEmpty()){
            int nextByte=0;
            for (int i=0; i<8; i++){
                
                nextByte+=localBits.removeFirst()*((int) Math.pow(2, 7-i));
                
            }
            bytes.add(nextByte);
        }
        return bytes;

    }


}

    
