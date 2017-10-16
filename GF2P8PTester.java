
import java.util.Arrays;
/**
 * Write a description of class GF2P8PTester here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GF2P8PTester
{
    
    public static void main() {
        GF2P8P p1 = new GF2P8P(0x03, 0x01, 0x01, 0x02);
        GF2P8P p2 = new GF2P8P(0x0b, 0x0d, 0x09, 0x0e);
        // System.out.println(p1);
        // System.out.println(p2);
        Utilities.test("(" + p1.toString() + ") * (" + p2.toString() + ")", p1.multiply(p2).toString(), "{1}"); 
        System.out.println(Integer.toUnsignedString((byte) -1, 10));
    }
}
