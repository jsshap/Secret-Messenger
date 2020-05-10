import java.util.LinkedList;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.WritableRaster;
import java.util.Scanner;
import java.util.Random;

public class HideAndFindText {

    public static Scanner input = new Scanner(System.in);
    public static int[][] cryptoTable = new int[256][2];

    public static void main(String[] args) {
        if (args.length != 2) {
            showUsage();
        }
        if (args[0].toLowerCase().equals("usage")) {
            showUsage();
        }
        String file = args[0];
        makeTable();

        try {
            if (args[1].equals("Hide")) {
                addText(file);
            } else if (args[1].equals("Reveal")) {
                revealText(file);
            } else {
                showUsage();
            }
        } catch (Exception e) {
            System.out.println("Error with files.");
            showUsage();
        }
    }

    public static void revealText(String fileName) throws Exception {
        BufferedImage carrier = ImageIO.read(new File(fileName));
        LinkedList<Integer> bits = Lib.pullSpecifiedBitsOfSpecificColors(carrier, new int[] { 2 }, new int[] { 1 });
        LinkedList<Integer> bytes = Lib.convertBitsToBytes(bits);
        LinkedList<Integer> decrypted = decrypt(bytes);
        for (Integer i : decrypted) {
            if ((char) i.intValue() == '¥') {
                System.out.println();
                return;
            }
            System.out.print((char) i.intValue());

        }
    }

    public static void addText(String fileName) throws Exception {

        BufferedImage carrier = ImageIO.read(new File(fileName));

        String s = input.nextLine();
        s += '¥';

        LinkedList<Integer> chars = new LinkedList<Integer>();
        for (int i = 0; i < s.length(); i++) {
            chars.add((int) s.charAt(i));
        }

        LinkedList<Integer> encrypted = encrypt(chars);

        LinkedList<Integer> bits = new LinkedList<Integer>();
        for (Integer i : encrypted) {
            for (int j = 7; j > -1; j--) {
                bits.add(i >> j & 1);
            }
        }
        int width = carrier.getWidth();
        int height = carrier.getHeight();
        WritableRaster raster = carrier.getRaster();
        for (int yy = 0; yy < height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                if (bits.isEmpty())
                    break;
                int bit = bits.removeFirst();
                pixels[2] = pixels[2] & 254;
                pixels[2] = pixels[2] | bit;
                raster.setPixel(xx, yy, pixels);
            }
            if (bits.isEmpty())
                break;
        }
        ImageIO.write(carrier, "png", new File("hiddenText.png"));

    }

    public static void makeTable() {
        Random rand = new Random(18);
        for (int i = 0; i < 256; i++) {
            cryptoTable[i][0] = i;
        }
        boolean[] added = new boolean[256];
        int numAdded = 0;
        int toAdd;
        while (numAdded < 256) {
            toAdd = rand.nextInt(256);
            if (!added[toAdd]) {// If toAdd has not been added yet
                cryptoTable[numAdded][1] = toAdd;
                numAdded++;
                added[toAdd] = true;
            }

        }

    }

    public static LinkedList<Integer> decrypt(LinkedList<Integer> encryptedBytes) {
        LinkedList<Integer> plaintext = new LinkedList<Integer>();
        for (Integer i : encryptedBytes) {
            for (int k = 0; k < 256; k++) {
                if (cryptoTable[k][1] == i) {
                    plaintext.add(cryptoTable[k][0]);
                    break;
                }
            }
        }
        return plaintext;

    }

    public static LinkedList<Integer> encrypt(LinkedList<Integer> plaintext) {
        LinkedList<Integer> encrypted = new LinkedList<Integer>();

        for (Integer i : plaintext) {
            encrypted.add(cryptoTable[i][1]);
        }
        return encrypted;

    }

    public static void showUsage() {
        System.out.println("Usage:");
        System.out.println("Java HideAndFindText [filename] Hide <- [inputfile]");
        System.out.println("Java HideAndFindText [image].png Reveal -> [outputfile]");
        System.exit(0);
    }

}
