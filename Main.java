import java.util.Arrays;
import java.util.stream.*;
public class Main {
    public static void main() {
        
        byte[][] key = new byte[][] {
            {(byte) 0x2b, (byte) 0x7e, (byte) 0x15, (byte) 0x16},
            {(byte) 0x28, (byte) 0xae, (byte) 0xd2, (byte) 0xa6},
            {(byte) 0xab, (byte) 0xf7, (byte) 0x15, (byte) 0x88},
            {(byte) 0x09, (byte) 0xcf, (byte) 0x4f, (byte) 0x3c}
        };
        
        byte[] in = {
            (byte) 0x32, (byte) 0x43, (byte) 0xf6, (byte) 0xa8, 
            (byte) 0x88, (byte) 0x5a, (byte) 0x30, (byte) 0x8d, 
            (byte) 0x31, (byte) 0x31, (byte) 0x98, (byte) 0xa2, 
            (byte) 0xe0, (byte) 0x37, (byte) 0x07, (byte) 0x34
        };
        AESEncryption enc = new AESEncryption(in);
        // String ans = Arrays.deepToString(enc.cipher());
        
        // Stream<byte[]> s = Arrays.stream(enc.cipher());
        // Stream<IntStream> t = s.map(bs -> IntStream.range(0, bs.length).map(i -> bs[i]));
        // Stream<String[]> r = t.map(bs -> bs.mapToObj(Integer::toHexString).toArray(String[]::new));
        byte[][] ans = enc.cipher();
        String[][] s = new String[ans.length][ans[0].length];
        for (int i = 0 ; i < s.length ; i++) { for (int j = 0 ; j < s[i].length ; j++) {
            s[i][j] = Integer.toHexString(ans[i][j] & 0xFF);
        }}
        System.out.println(Arrays.deepToString(s));
        
        ans = enc.decipher();
        s = new String[ans.length][ans[0].length];
        for (int i = 0 ; i < s.length ; i++) { for (int j = 0 ; j < s[i].length ; j++) {
            s[i][j] = Integer.toHexString(ans[i][j] & 0xFF);
        }}
        System.out.println(Arrays.deepToString(s));
        //enc.cipher();
        // System.out.println(Arrays.deepToString(enc.cipher()));
        // int[] roundKeys = AESEncryption.keyExpansion(key);
        // System.out.println(String.join(", ", Arrays.stream(roundKeys).mapToObj(Integer::toHexString).toArray(String[]::new)));
        // System.out.println();
    }
}