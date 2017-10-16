import java.util.*;

/** Polynomials
 * 
 * Addition and subtraction are defined atypically, in terms of the binary representation of GF(2^8).
 * They both are equivalent to a bitwise XOR. This means that modulo relies on XOR, not subtraction.
 * (Explore long division of polynomials?)
 */ 
public class GF2P {

    /**
     * Coefficients of the polynomial.
     */
    private BitSet cfs;

    /**
     * Reducing polynomial for AES - Rijindael's field.
     */
    public static final GF2P MASK = new GF2P(0b100011011); // {1b} + 1 << 8

    public static final GF2P ZERO = new GF2P(0);
    public static final GF2P ONE = new GF2P(1);
    public static final GF2P X = new GF2P(2);

    /**
     * Constructs an array of zeros.
     */
    public static GF2P[] zeroArray(int len) {
        GF2P[] zeros = new GF2P[len];
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
        GF2P.repr = repr;
    }

    public GF2P(long cfs) {
        this(BitSet.valueOf(new long[] {cfs}));
    }
    
    public GF2P(byte cfs) {
        this(BitSet.valueOf(new byte[] {cfs}));
    }

    public GF2P(BitSet cfs) {
        this.cfs = (BitSet) cfs.clone();
    }
    
    public BitSet getCfs() {
        return (BitSet) this.cfs.clone();
    }

    public int highestPower() {
        return cfs.length() - 1;
    }

    public GF2P add(GF2P other) {
        return add(other.cfs);
    }

    public GF2P add(BitSet other) {
        BitSet sum = (BitSet) this.cfs.clone();
        sum.xor(other); 
        return new GF2P(sum);
    }

    public GF2P multiply(GF2P other) {
        return new GF2P(
            this.cfs.stream().parallel()
            .flatMap(x -> 
                    other.cfs.stream()
                    .map(y -> x+y))
            .collect(BitSet::new, BitSet::flip, BitSet::xor)
        );
    }
    
    public GF2P pow(int exp) {
        if (exp < 0) throw new IllegalArgumentException("Can't raise by negative: " + exp);
        GF2P product = ONE;
        GF2P base = this;
        while (exp > 0) {
            if (exp % 2 == 1) {
                product = product.multiply(base);
                exp--;
            }
            base = base.multiply(base);
            exp /= 2;
        }
        return product;
    }

    public GF2P divide(GF2P divisor) {
        return ediv(divisor)[0];
    }

    public GF2P modulo(GF2P divisor) {
        return ediv(divisor)[1];
    }
    
    public GF2P inverse(GF2P divisor) {
        if (this.equals(ZERO) || divisor.equals(ZERO)) return ZERO;
        return egcd(divisor)[0];
    }

    public GF2P[] ediv(GF2P divisor) {
        GF2P dividend = this;
        GF2P quotient = ZERO;   
        while (dividend.highestPower() >= divisor.highestPower()) {
            quotient = quotient.add(BitSets.leftShift(ONE.cfs, dividend.highestPower() - divisor.highestPower()));
            dividend = dividend.add(divisor.multiply(quotient));
        }
        return new GF2P[] {quotient, dividend};
    }

    public GF2P[] egcd(GF2P other) {
        GF2P[] eq1 = {ONE, ZERO, this};
        GF2P[] eq2 = {ZERO, ONE, other};
        GF2P[] t;
        GF2P d;
        while (! eq1[2].equals(ZERO)) {
            d = eq2[2].divide(eq1[2]);
            eq2[0] = eq2[0].add(d.multiply(eq1[0]));
            eq2[1] = eq2[1].add(d.multiply(eq1[1]));
            eq2[2] = eq2[2].add(d.multiply(eq1[2]));
            t = eq2;
            eq2 = eq1;
            eq1 = t;
        }
        return eq2;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof GF2P)) return false;
        GF2P other = (GF2P) obj;
        return this.cfs.equals(other.cfs);
    }
    
    @Override
    public String toString() {
        if (this.equals(ZERO)) return "0";
        Deque<String> terms = new ArrayDeque<>(cfs.length());

        for (int i : BitSets.asIterable(cfs)) {
            switch (i) {
                case 0:
                terms.push("1");
                break;
                case 1:
                terms.push("x");
                break;
                default: 
                terms.push("x" + Utilities.superscript(i));
            }
        }
        return String.join(" + ", terms);
    }
    
    public String toHexString() {
        return cfs.length() == 0 ? "0" : Long.toHexString(cfs.toLongArray()[0]);
    }
    
    public byte toByte() {
        byte[] bytes = cfs.toByteArray();
        switch (bytes.length) {
            case 0:
            return (byte) 0;
            case 1:
            return bytes[0];
            default:
            throw new ArithmeticException("Can't fit into a single byte: " + Arrays.toString(bytes));
        }
    }
}