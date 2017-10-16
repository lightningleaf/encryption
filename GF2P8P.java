
public class GF2P8P {
    
    public   GF2P8[] cfs;
    
    private static final int TERMS = 4;
    
    public static final GF2P8P A = new GF2P8P(0x03, 0x01, 0x01, 0x02);
    
    public GF2P8P() {
        cfs = GF2P8.zeroArray(4);
    }
    
    public GF2P8P(GF2P8... cfs) {
        assert cfs.length == 4;
        this.cfs = new GF2P8[4];
        for (int i = 0 ; i < 4 ; i++) {
            this.cfs[i] = cfs[i];
        }
    }
    
    public GF2P8P(int... cfs) {
        this.cfs = new GF2P8[4];
        for (int i = 0 ; i < 4 ; i++) {
            this.cfs[i] = new GF2P8(cfs[i]);
        }
    }
    
    public GF2P8P(byte... cfs) {
        this.cfs = new GF2P8[4];
        for (int i = 0 ; i < 4 ; i++) {
            this.cfs[i] = new GF2P8(cfs[i]);
        }
    }
    
    
    public GF2P8P add(GF2P8P other) {
        GF2P8P sum = new GF2P8P();
        for (int i = 0 ; i < 4 ; i++) {
            sum.cfs[i] = this.cfs[i].add(other.cfs[i]);
        }
        return sum;
    } 
    
    /**
     * 3 : 2 & 6 (0 : 0 & 4)
     * 2 : 1 & 5 (1 : 1 & 5)
     * 1 : 0 & 4 (2 : 2 & 6)
     * 0 : 3     (3 : 3)
     */
    public GF2P8P multiply(GF2P8P other) {
        GF2P8P product = new GF2P8P();
        for (int i = 0 ; i < 4 ; i++) {
            for (int j = 0 ; j < 4 ; j++) {
                int k = Utilities.posmod(i+j+1, 4); 
                // The +1 arises from the following:
                // d0 = a0 * b0 (0 + 0) % 4 = 0
                // d(3-0) = a(3-0) * b(3-0) (2(3-0)) % 4 != (3-0). In ai + bj = dk, i + j = k
                // but both i and j are changed so we have to compensate for that.
                product.cfs[k] = product.cfs[k].add(this.cfs[i].multiply(other.cfs[j]));
                if (k == 3) System.out.println(i + " " + j);
            }
        }
        return product;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String nxt;
        for (int i = 0 ; i < cfs.length ; i++) {
            if (cfs[i].toByte() != 0) {
                switch (i) {
                    case TERMS - 1:
                        nxt = "";
                        break;
                    case TERMS - 2:
                        nxt = "x + ";
                        break;
                    default: 
                        nxt = "x" + Utilities.superscript(cfs.length - i - 1) + " + ";
                }
                sb.append("{" + cfs[i].toHexString() + "}" + nxt);
            }
        }
        return sb.toString().replaceAll(" \\+ $", "");
    }
    
}
