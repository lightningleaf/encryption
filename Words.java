

public class Words {

    public static byte[] bytesOf(int i) {
        return bytesOf(i, true);
    }

    public static byte[] bytesOf(int i, boolean littleEndian) {
        if (littleEndian) {
            return new byte[] {(byte)(i>>>24),
                               (byte)(i>>>16),
                               (byte)(i>>> 8),  
                               (byte) i 
            };
        }
        return new byte[] {(byte)i,
                           (byte)(i>>>8),
                           (byte)(i>>>16),  
                           (byte)(i>>>24)     
        };
    }
    
    public static byte getByte(int w, int i) {
        return (byte) (w >>> (8*i));
    }

    public static int joinBytes(byte[] bs) {
        return joinBytes(bs, true);
    }

    public static int joinBytes(byte[] bs, boolean littleEndian) {
        // When packing signed bytes into an int, each byte needs to be masked off because it is sign-extended to 32 bits
        if (littleEndian) {
            return bs[0]<<24 | (bs[1] & 0xFF)<<16
                             | (bs[2] & 0xFF)<< 8
                             | (bs[3] & 0xFF)    ;
        }
        return bs[0] | (bs[1] & 0xFF)<< 8
                     | (bs[2] & 0xFF)<<16
                     | (bs[3] & 0xFF)<<24;
    }
}