
import java.util.Arrays;
/**
 * Write a description of class RSAEncryptionTester here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class RSAEncryptionTester {
    public static void main(String[] args) {
        assert NumberTheory.modexp(4, 13, 497) == 445 : NumberTheory.modexp(4, 13, 497);
        assert Arrays.equals(NumberTheory.egcd(180, 150), new int[]{1, -1, 30});
        assert Arrays.equals(NumberTheory.egcd(238470, 209347), new int[]{-74069, 84373, 1});
        RSAEncryption enc = new RSAEncryption();
        for (int i = 1 ; i < enc.n ; i++) {
            assert enc.chk(i) : i + "\n" + enc;
        }
        System.out.println(enc.encrypt("The quick brown fox jumped over the lazy dog."));
        System.out.println(enc.decrypt(enc.encrypt("The quick brown fox jumped over the lazy dog.")));
        assert false;
    }
}
