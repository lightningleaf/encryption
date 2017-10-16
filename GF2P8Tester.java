
public class GF2P8Tester {
    
    public static void main() {
        GF2P8.setStringRepresentation(GF2P8.Representation.HEX);
        GF2P8 p1 = new GF2P8(0x57);
        GF2P8 p2 = new GF2P8(0x83);
        GF2P8 p3 = new GF2P8(0x13);
        Utilities.test(p1 + " + " + p2, p1.add(p2)+"", Integer.toHexString(0xd4));
        Utilities.test(p1 + " * " + p2, p1.multiply(p2)+"", Integer.toHexString(0xc1));
        Utilities.test(p1 + " * " + p3, p1.multiply(p3)+"", Integer.toHexString(0xfe));
        Utilities.test(p1 + ".xtime()", p1.xTime()+"", Integer.toHexString(0xae));
        
        
        GF2P8 p4 = new GF2P8(1<<4 + 1);
        GF2P8 p5 = new GF2P8(GF2P8.M);
        Utilities.test(p5 + ".divide(" + p4 + ")", p5.divide(p4)+"", new GF2P8(1<<2).toString());
    }
}    