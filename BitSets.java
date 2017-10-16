
import java.util.BitSet;
import java.util.Arrays;
public class BitSets {

    public static void main() {

        BitSet bits = BitSet.valueOf(new long[] {1});
        System.out.println(bits.toLongArray() == bits.toLongArray());
    }

    public static BitSet leftShift(BitSet bits, int n) {
        if (n < 0) throw new IllegalArgumentException("Can't shift by a negative value: " + n);
        if (n >= 64) throw new IllegalArgumentException("Can't shift >=64: " + n);

        long[] words = bits.toLongArray(); // little-endian
        // Do the shift
        for (int i = 0; i < words.length - 1; i++) {
            words[i+1] |= words[i] >>> n; // carry
            words[i] <<= n; // Shift current word
        }
        BitSet carry = BitSet.valueOf(new long[] {words[words.length - 1] >>> n});
        words[words.length - 1] <<= n;

        BitSet shifted = BitSet.valueOf(words);
        int placeValOffset = shifted.length();
        for (int i : (Iterable<Integer>) carry.stream()::iterator) {
            shifted.set(placeValOffset+i);
        }
        return shifted;
    }

    public static Iterable<Integer> asIterable(BitSet bits) {
        return (Iterable<Integer>) bits.stream()::iterator;
    }

    public static byte rotateLeft(byte b) {
        return (byte) ( 
            (b<<1) | (b>>>7) & 1
        ); 
    }

}