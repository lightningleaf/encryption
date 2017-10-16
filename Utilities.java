import java.util.Map;
import java.util.HashMap;
public class Utilities {
    private static final Map<Integer, Character> superscripts = new HashMap<>();
    static {
        superscripts.put(0, '\u2070');
        superscripts.put(1, '\u00B9');
        superscripts.put(2, '\u00B2');
        superscripts.put(3, '\u00B3');
        superscripts.put(4, '\u2074');
        superscripts.put(5, '\u2075');
        superscripts.put(6, '\u2076');
        superscripts.put(7, '\u2077');
        superscripts.put(8, '\u2078');
        superscripts.put(9, '\u2079');
    }
    
    public static void test(String prompt, String ans, String actual) {
        System.out.printf("%s -> %s, actual %s. %s\n", prompt, ans, actual, ans.equals(actual) ? "PASSED" : "FAILED");
    }

    /**
     * Converts an integer into a string of superscripted numerals representing it.
     */
    public static String superscript(int n) {
        if (n < 0) throw new UnsupportedOperationException("Negatives not supported: " + n);
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(superscripts.get(n%10));
            n /= 10;
        } while (n > 0);
        return sb.reverse().toString();
    }
    
    
    public static int joinBytes(byte[] bs) {
        int i = 0;
        for (byte b : bs) {
            i = (i << 8) + b;
        }
        return i;
    }

    /**
     * Converts an array of booleans into a long by treating the booleans as bits.
     * 
     * The number is constructed in read order.
     */
    public static long boolsToLong(boolean[] bs) {
        long l = 0;
        for (boolean b : bs) {
            l = (l << 1) + (b ? 1 : 0);
        }
        //System.out.println(Long.toBinaryString(l));
        return l;
    }

    public static int posmod(int x, int m) {
        return ((x%m) + m) % m;
    }

    private static byte MAX_UNSIGNED_BYTE = ~ ((byte) 0);
    public static byte safeByteCast(int i) {
        if (Integer.compareUnsigned(i, MAX_UNSIGNED_BYTE) > 0) {
            throw new IllegalArgumentException("Lossy conversion from int to byte: " + i);
        }
        return (byte) i;
    }

    public static byte safeByteCast(long l) {
        if (Long.compareUnsigned(l, MAX_UNSIGNED_BYTE) > 0) {
            throw new IllegalArgumentException("Lossy conversion from int to byte: " + l);
        }
        return (byte) l;
    }

    public static byte safeByteCast(Number n) {
        if (n instanceof Long) {
            return safeByteCast((long) n);
        }
        else if (n instanceof Integer | n instanceof Short) {
            return safeByteCast((int) n);
        }
        else if (n instanceof Byte) {
            return (byte) n;
        }
        else {
            throw new IllegalArgumentException("Can't convert to byte: " + n.getClass());
        }
    }

    public static String coefficientsToPolynomial(Object[] os) {
        return null;
    }

}
