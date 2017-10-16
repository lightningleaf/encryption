
import java.util.Arrays;
/**
 * Write a description of class UtilitiesTest here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class UtilitiesTest
{
    
    public static void main() {
        byte[] bs = {0x63, 0x63, 0x63, 0x63};
        Utilities.test("joinBytes(" + Arrays.toString(bs) + ")", zeroPaddedBinaryString(Utilities.joinBytes(bs)),
            "01100011011000110110001101100011");
    }
    
    private static String zeroPaddedBinaryString(int i) {
        return String.format("%32s", Integer.toBinaryString(i)).replace(' ', '0');
    }
}
