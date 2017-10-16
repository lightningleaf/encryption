import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.BitSet;
/**
 * Write a description of class AESEncryption here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class AESEncryption {
    /**
     * Cipher key.
     */
    private static final byte[][] K = {{(byte) 0x2b, (byte) 0x7e, (byte) 0x15, (byte) 0x16}, 
            {(byte) 0x28, (byte) 0xae, (byte) 0xd2, (byte) 0xa6}, 
            {(byte) 0xab, (byte) 0xf7, (byte) 0x15, (byte) 0x88}, 
            {(byte) 0x09, (byte) 0xcf, (byte) 0x4f, (byte) 0x3c}};

    /**
     * Number of columns (32-bit words) comprising the state.
     * Also the length of the input and output blocks in terms of words.
     * block length / 32
     */
    private static final int Nb = 4;

    /**
     * Number of 32-bit words comprising the cipher key.
     */
    private static final int Nk = 4; // 6, 8

    /**
     * Number of rounds. A function of Nk and Nb. 
     */
    private static final int Nr = 10; // 12, 14

    /**
     * Intermediate encryption (or decryption) result.
     */
    private byte[][] state;

    private byte[] in;

    private static final BitSet C = BitSet.valueOf(new byte[] {(byte) 0x63});

    private static final byte[] sbox;
    private static final byte[] isbox;
    private static final int[] rcon;
    static {
        sbox = new byte[((byte) 0xf+1) * ((byte) 0xf+1)];
        for (int i = 0 ; i < sbox.length ; i++) {
            sbox[i] = sbox_((byte) i);
        }
        
        isbox = new byte[sbox.length];
        for (int i = 0 ; i < isbox.length ; i++) {
            isbox[sbox[i] & 0xFF] = (byte) i;
        }

        rcon = new int[ Nb * (Nr + 1) / Nk];
        for (int i = 1 ; i < rcon.length ; i++) {
            rcon[i] = rcon_(i);
        }
    }

    public AESEncryption(byte[] in) {
        this.in = in;
        // this.state = in;
        state = new byte[4][Nb];
        for (int r = 0 ; r < 4 ; r++) {
            for (int c = 0 ; c < Nb ; c++) {
                state[r][c] = in[r + 4*c];
            }
        }
    }

    public byte[][] cipher() {
        int[] ws = keyExpansion(K);
        addRoundKey(ws, 0);
        for (int round = 1 ; round < Nr ; round++) {
            subBytes();
            shiftRows();
            mixColumns();
            addRoundKey(ws, round);
        }
        subBytes();
        shiftRows();
        addRoundKey(ws, Nr);

        return state;
    }

    public byte[][] decipher() {
        int[] ws = keyExpansion(K);
        addRoundKey(ws, Nr);
        for (int round = Nr-1 ; round >= 1 ; round--) {
            invShiftRows();
            invSubBytes();
            addRoundKey(ws, round);
            invMixColumns();
        }

        invShiftRows();
        invSubBytes();
        addRoundKey(ws, 0);
        
        return state;
    }

    private byte[] getCol(byte[][] bss, int col) {
        byte[] bs = new byte[bss.length];
        for (int r = 0 ; r < bs.length ; r++) {
            bs[r] = bss[r][col];
        }
        return bs;
    }

    private void setCol(byte[][] bss, int col, byte[] bs) {
        for (int r = 0 ; r < bs.length ; r++) {
            bss[r][col] = bs[r];
        }
    }

    /**
     * Adds a round key to the state in either the encryption or decryption. Uses an XOR operation.
     */
    public void addRoundKey(int[] ws) {
        for (int c = 0 ; c < Nb ; c++) {
            setCol(state, c, 
                Words.bytesOf(Words.joinBytes(getCol(state, c)) ^ ws[c]));
        }
    }

    /**
     * Adds a round key to the state in either the encryption or decryption. Uses an XOR operation.
     */
    public void addRoundKey(int[] ws, int round) {
        for (int c = 0, l = round*Nb ; c < Nb ; c++, l++) {
            setCol(state, c, 
                Words.bytesOf(Words.joinBytes(getCol(state, c)) ^ ws[l]));
        }
    }

    /**
     * Mixes the data of the columns of the state (independently of one another) to produce new 
     * columns.
     */
    public void mixColumns() {
        for (int c = 0 ; c < Nb ; c++) {
            byte[] col = getCol(state, c);
            GF2PP s = new GF2PP(col);
            GF2PP s_ = GF2PP.A.multiply(s);
            setCol(state, c, s_.toByteArray());
        }
    }
    
    public void invMixColumns() {
        for (int c = 0 ; c < Nb ; c++) {
            byte[] col = getCol(state, c);
            GF2PP s = new GF2PP(col);
            GF2PP s_ = GF2PP.AINV.multiply(s);
            setCol(state, c, s_.toByteArray());
        }
    }

    /**
     * Cyclically shifts the last three rows of the state by different offsets.
     */
    public void shiftRows() {
        for (int r = 0 ; r < 4 ; r++) {
            byte[] s = state[r].clone();
            for (int c = 0 ; c < Nb ; c++) {
                state[r][c] = s[(c + r) % Nb];
            }
        }
    }

    public void invShiftRows() {
        for (int r = 0 ; r < 4 ; r++) {
            byte[] s = state[r].clone();
            for (int c = 0 ; c < Nb ; c++) {
                state[r][(c + r) % Nb] = s[c];
            }
        }
    }

    public void subBytes() {
        for (int r = 0 ; r < 4 ; r++) {
            for (int c = 0 ; c < Nb ; c++) {
                state[r][c] = sbox(state[r][c]);
            }
        }
    }
    
    public void invSubBytes() {
        for (int r = 0 ; r < 4 ; r++) {
            for (int c = 0 ; c < Nb ; c++) {
                state[r][c] = isbox(state[r][c]);
            }
        }
    }

    public static byte sbox(byte b) {
        return sbox[b & 0xFF];
    }
    
    public static byte isbox(byte b) {
        return isbox[b & 0xFF];
    }

    public static byte sbox_(byte bits) {
        byte s = new GF2P(bits).inverse(GF2P.MASK).toByte();
        byte res = 0;
        for (int i = 0 ; i < 5 ; i++) {
            res ^= s;
            s = BitSets.rotateLeft(s);
        }
        return (byte) (res ^ 0x63);
    }

    public static byte sbox__(byte bits) {
        BitSet b = new GF2P(bits).inverse(GF2P.MASK).getCfs();
        BitSet b_ = (BitSet) b.clone();
        for (int i = 0 ; i < 8 ; i++) {            
            b_.set(i, b.get(i) ^ b.get((i+4)%8) 
                               ^ b.get((i+5)%8) 
                               ^ b.get((i+6)%8) 
                               ^ b.get((i+7)%8)
                               ^ C.get(i)
            );
        }
        return new GF2P(b_).toByte();
    }

    // j = 8 - i i = 8 - j

    /**
     * The AES algorithm takes the Cipher Key, K, and performs a Key Expansion routine to generate a key schedule. 
     * 
     * The Key Expansion generates a total of Nb (Nr + 1) words: the algorithm requires an initial set of Nb words,  
     * and each of the Nr rounds requires Nb  words of key data. 
     * 
     * The resulting key schedule consists of a linear array of 4-byte words, denoted [w_i], with i in the range 0 <= i < Nb(Nr + 1).
     */
    public static int[] keyExpansion(byte[][] key) {
        int[] ws = new int[Nb * (Nr + 1)];
        int temp;
        for (int i = 0 ; i < Nk ; i++) {
            ws[i] = Words.joinBytes(key[i]);
        }

        for (int i = Nk ; i < Nb * (Nr + 1) ; i++) {
            temp = ws[i-1];
            if (i % Nk == 0) {
                temp = subWord(rotWord(temp)) ^ rcon(i/Nk);
            }
            else if (Nk > 6 && i % Nk == 4) { // false for AES128   
                temp = subWord(temp);
            }
            ws[i] = ws[i-Nk] ^ temp;
        }
        return ws;
    }

    private int[] keyExpansion() {
        int[] ws = new int[Nb * (Nr + 1)];
        int temp;
        for (int i = 0 ; i < Nk ; i++) {
            ws[i] = Words.joinBytes(K[i]);
        }

        for (int i = Nk ; i < Nb * (Nr + 1) ; i++) {
            temp = ws[i-1];
            if (i % Nk == 0) {
                temp = subWord(rotWord(temp)) ^ rcon(i/Nk);
            }
            else if (Nk > 6 && i % Nk == 4) { // false for AES128   
                temp = subWord(temp);
            }
            ws[i] = ws[i-Nk] ^ temp;
        }
        return ws;
    }

    public static int rcon(int i) {
        return rcon[i];
    }

    private static int rcon_(int i) {
        return Words.joinBytes(new byte[] {GF2P.X.pow(i-1).modulo(GF2P.MASK).toByte(), 0, 0, 0});    
    }

    /**
     * Applies the S-box to each of the four bytes of a word independently.
     */
    public static int subWord(int w) {
        byte[] bytes = Words.bytesOf(w);
        for (int i = 0 ; i < 4 ; i++) {
            bytes[i] = sbox(bytes[i]);
        }
        return Words.joinBytes(bytes);
    }

    /**
     * Cyclically permutes a four-byte word.
     */
    public static int rotWord(int w) {
        byte[] bytes = Words.bytesOf(w);
        byte temp = bytes[0]; 
        for (int i = 0 ; i < 3 ; i++) {
            bytes[i] = bytes[i+1];
        }
        bytes[3] = temp;
        return Words.joinBytes(bytes);
    }
}
