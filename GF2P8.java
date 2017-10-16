
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

/**
 * Galois field GF(2^8). 
 * 
 * Polynomials:
 * 1) With degree at most 7 (8 terms) and integer cfs modulo 2.
 * 2) Modulo an irreducible polynomial. For AES, the polynomial is x^8 + x^4 + x^3 + x + 1.
 * 
 * Addition and subtraction are defined atypically, in terms of the binary representation of GF(2^8).
 * They both are equivalent to a bitwise XOR. This means that modulo relies on XOR, not subtraction.
 * (Explore long division of polynomials?)
 */ 
public class GF2P8 {

    /**
     * Coefficients of the polynomial.
     */
    public boolean[] cfs;

    /**
     * Length of a byte in terms of bits.
     */
    private static final int BYTE_LENGTH = 8;

    /**
     * Standard mask for AES.
     */
    public static final int M = 0b100011011; // {1b} + 1 << 8

    public static final GF2P8 ZERO = new GF2P8(0);
    public static final GF2P8 ONE = new GF2P8(1);

    public static void main() {
        GF2P8 p = new GF2P8((byte) 0x57);
        //System.out.println(p);
        GF2P8 q = new GF2P8((byte) 0x83);
        //System.out.println(q);
        System.out.println(p.multiply(q));

        assert new GF2P8(0x57).xTime().equals(0xae);
        assert new GF2P8(0x57).xTime().xTime().equals(0x47);
        assert false;
    }

    /**
     * Constructs an array of zeros.
     */
    public static GF2P8[] zeroArray(int len) {
        GF2P8[] zeros = new GF2P8[len];
        for (int i = 0 ; i < len ; i++) {
            zeros[i] = ZERO;
        }
        return zeros;
    }

    public enum Representation {
        POLYNOMIAL,
        BINARY,
        HEX
    }

    private static Representation repr = Representation.POLYNOMIAL;
    public static void setStringRepresentation(Representation repr) {
        GF2P8.repr = repr;
    }

    public GF2P8() {
        cfs = new boolean[BYTE_LENGTH];
    }

    // private GF2P8(boolean[] cfs) {
    // this();
    // if (cfs.length <= this.cfs.length) {
    // System.arraycopy(cfs, 0, this.cfs, this.cfs.length - cfs.length, cfs.length);
    // }
    // else {
    // Utilities.boolsToLong(cfs);
    // }
    // }

    public GF2P8(boolean[] cfs) {
        this(Utilities.boolsToLong(cfs));
    }

    public GF2P8(long cfs) {
        this(modulus(cfs));
    }

    public GF2P8(int cfs) {
        this(modulus(cfs));
    }

    public GF2P8(byte cfs) {
        this();
        for (int i = 0, mask = 1 << 7; i < 8 ; i++, mask >>= 1) {
            this.cfs[i] = (cfs & mask) != 0;
        }
    }

    /**
     * Adds this field element to another. Equipvalent to the XOR operation.
     */
    public GF2P8 add(GF2P8 other) {
        GF2P8 sum = new GF2P8();
        for (int i = 0 ; i < 8 ; i++) {
            sum.cfs[i] = this.cfs[i] ^ other.cfs[i];
        }
        return sum;
    }

    public GF2P8 add(byte other) {
        return this.add(new GF2P8(other));
    }

    /**
     * Multiplies this field element against another, taking the modulus if necessary.
     */
    public GF2P8 multiply(GF2P8 other) {
        boolean[] product = new boolean[15];
        for (int i = 0 ; i < 8 ; i++) {
            for (int j = 0 ; j < 8 ; j++) {
                product[i+j] ^= this.cfs[i] && other.cfs[j];
            }            
        }
        // return modulus(Utilities.boolsToLong(product));
        return new GF2P8(product);
    }

    public GF2P8 multiply(byte other) {
        return this.multiply(new GF2P8(other));
    }

    public GF2P8 xTime() {
        int mask = cfs[0] ? 0x1b : 0;
        return new GF2P8((byte) ((toByte() << 1) ^ mask));
    }
    
    public GF2P8 xTime(int cnt) {
        GF2P8 product = this;
        while (cnt-- > 0) product = product.xTime();
        return product;
    }

    public GF2P8 inverse() {
        GF2P8[] eq1 = {ONE, ZERO, this};
        GF2P8[] eq2 = {ZERO, ONE, new GF2P8(M)};
        GF2P8[] t;
        GF2P8 d;  
        while (! eq1[2].equals(ZERO)) {
            d = eq2[2].divide(eq1[2]);
            eq2[0] = eq2[0].add(d.multiply(eq1[0]));
            eq2[1] = eq2[1].add(d.multiply(eq1[1]));
            eq2[2] = eq2[2].add(d.multiply(eq1[2]));
            t = eq2;
            eq2 = eq1;
            eq1 = t;
        }
        return eq2[0];
    }

    private static int highestBitPosition(long bits) {
        return 64-Long.numberOfLeadingZeros(bits);
    }
    
    private static int indexOf(boolean[] bs, boolean b) {
        int i = bs.length;
        while (i > 0 && bs[--i] != b);
        return i;
    }

    private static int highestBitPosition(GF2P8 bits) {
        return 8-indexOf(bits.cfs, true);
    }

    private static byte modulus(long bits) {
        int maskHighBitPos = highestBitPosition(M);
        while (highestBitPosition(bits) >= maskHighBitPos) {
            bits ^= M << highestBitPosition(bits) - maskHighBitPos;
        }
        return (byte) bits;
    }
    
    

    public GF2P8 divide(GF2P8 divisor) {
        return null;
    }

    public String toString() {
        switch (repr) {
            case POLYNOMIAL:
            return toPolynomialString();
            case BINARY:
            return toBinaryString();
            case HEX:
            return toHexString();
            default:
            throw new UnsupportedOperationException();
        }
    }

    public String toBinaryString() {
        return Integer.toBinaryString(toInt());
    }

    public String toHexString() {
        return Integer.toHexString(toInt());
    }

    public String toPolynomialString() {
        StringBuilder sb = new StringBuilder();
        String nxt;
        for (int i = 0 ; i < cfs.length ; i++) {
            if (cfs[i]) {
                switch (i) {
                    case BYTE_LENGTH - 1:
                    nxt = "1";
                    break;
                    case BYTE_LENGTH - 2:
                    nxt = "x + ";
                    break;
                    default: 
                    nxt = "x" + Utilities.superscript(cfs.length - i - 1) + " + ";
                }
                sb.append(nxt);
            }
        }
        return sb.length() == 0 ? "0" : sb.toString().replaceAll(" \\+ $", "");
    }

    public byte toByte() {
        return (byte) toInt();
    }

    public int toInt() {
        int i = 0;
        for (boolean b : cfs) {
            i = (i << 1) + (b ? 1 : 0);
        }
        return i;
    }

    public boolean equals(Object obj) {
        System.out.println(obj instanceof Byte);
        if (obj == null) return false;
        if (! (obj instanceof GF2P8) && ! (obj instanceof Number)) return false;
        GF2P8 other = obj instanceof GF2P8 ? (GF2P8) obj : new GF2P8(Utilities.safeByteCast((Number) obj));
        return Arrays.equals(this.cfs, other.cfs);
    }
}