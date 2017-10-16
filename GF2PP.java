import java.util.*;

public class GF2PP {

    private GF2P[] cfs;

    private static final int TERMS = 4;

    public static final GF2PP ZERO = new GF2PP(GF2P.zeroArray(TERMS) );
    public static final GF2PP A    = new GF2PP(0x02, 0x01, 0x01, 0x03);
    public static final GF2PP AINV = new GF2PP(0x0e, 0x09, 0x0d, 0x0b);
    
    private GF2PP() {
        this(GF2P.zeroArray(TERMS));
    }
    
    public GF2PP(GF2P... cfs) {
        assert cfs.length == TERMS;
        this.cfs = new GF2P[TERMS];
        for (int i = 0 ; i < this.cfs.length ; i++) {
            this.cfs[i] = cfs[i];
        }
    }

    public GF2PP(int... cfs) {
        this.cfs = new GF2P[TERMS];
        for (int i = 0 ; i < this.cfs.length ; i++) {
            this.cfs[i] = new GF2P(cfs[i]);
        }
    }


    public GF2PP(byte... cfs) {
        this.cfs = new GF2P[TERMS];
        for (int i = 0 ; i < this.cfs.length ; i++) {
            this.cfs[i] = new GF2P(cfs[i]);
        }
    }
    
    public byte[] toByteArray() {
        byte[] bytes = new byte[TERMS];
        for (int i = 0 ; i < bytes.length ; i++) {
            bytes[i] = cfs[i].toByte();
        }
        return bytes;
    }
    
    public GF2PP add(GF2PP other) {
        GF2PP sum = new GF2PP();
        for (int i = 0 ; i < sum.cfs.length ; i++) {
            sum.cfs[i] = this.cfs[i].add(other.cfs[i]);
        }
        return sum;
    }

    public GF2PP multiply(GF2PP other) {
        GF2PP product = new GF2PP();
        for (int i = 0 ; i < this.cfs.length ; i++) {
            for (int j = 0, k = (i+j)%4 ; j < other.cfs.length ; j++, k++, k%=4) {
                product.cfs[k] = product.cfs[k].add(this.cfs[i].multiply(other.cfs[j]));
            }
        }
        
        for (int i = 0 ; i < product.cfs.length ; i++) {
            product.cfs[i] = product.cfs[i].modulo(GF2P.MASK);
        }
        return product;
    }

    public String toString() {
        if (this.equals(ZERO)) return "0";
        Deque<String> terms = new ArrayDeque<>(cfs.length);
        String cf;
        for (int i = 0 ; i < cfs.length ; i++) {
            if (cfs[i].equals(ZERO)) continue;
            cf = "{" + cfs[i].toHexString() + "}";
            if (i >= 1) cf += "x";
            if (i >  1) cf += Utilities.superscript(i);
            terms.add(cf);
        }
        return String.join(" + ", terms);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (! (obj instanceof GF2PP)) return false;
        GF2PP other = (GF2PP) obj;
        return Arrays.equals(this.cfs, other.cfs);
    }
    

}
