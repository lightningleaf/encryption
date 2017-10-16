import java.security.SecureRandom;
import java.util.Random;
import java.math.BigInteger;
import static java.lang.Math.*;
import java.util.function.BiFunction;
import java.util.Arrays;
/**
 * Generates keys for RSA encryption.
 * 
 * RSA gives each user two keys:
 *      1. public,  can be freely shared
 *      2. private, should never be shared
 * This is why RSA is referred to as an <i>asymmetric<i> cryptographic algorithm - it relies
 * on an asymmetry of knowledge between the sender and receiver.
 * 
 * The keys are simply two integers, conventionally denoted d (public) and e (private). They 
 * arise from the equation de ≡ 1 (mod φ(n)), where n is a semiprime and φ the totient 
 * function, i.e., the count of values below n coprime to n.
 * 
 * If m < n is an integer representing a message, then the sender can calculate c = m^e % n. 
 * To decrypt it, the receiver uses d, the unique positive integer (excepting multiples) such that m = c^d % n.
 * 
 * To crack the cipher, an attack must factor n so that they can recover d from de ≡ 1 (mod φ(n)). 
 * (φ(n) = (p-1)(q-1), where pq = n.) No fast (polynomial-time) factoring algorithm exists yet.
 * 
 * There are several weaknesses to "textbook" RSA, as implemented in this class. For example, it is trivial
 * for modern computers to brute-force decipher by finding i such that (m + i * n) ^ (1/e) = m. Also,
 * this class merely performs a letter-by-letter substitution cipher, which can be easily broken by frequency
 * analysis, just like a Caesar cipher. This class should be taken as a proof of concept of RSA, not a viable
 * encryption scheme.
 * 
 * 
 * https://simple.wikipedia.org/wiki/RSA_(algorithm)
 * https://www.reddit.com/r/crypto/comments/42mx48/brute_force_textbook_rsa/
 */
public class RSAEncryption
{
    // instance variables - replace the example below with your own
    protected int p, q, n, t, e, d, dmp1, dmq1, iqmp;

    private static void print(String text) {
        System.out.println(text);
    }

    /**
     * Constructor for objects of class RSAEncryption
     */
    public RSAEncryption() {
        generateKeys();
    }

    public void generateKeys() {
        Random rng = new SecureRandom();

        do {
            p = new BigInteger(12, 20, rng).intValueExact();
            do {q = new BigInteger(12, 20, rng).intValueExact();} while (p == q);
        } while (p < 1000 || q < 1000);
        n = multiplyExact(p, q);
        t = multiplyExact(p-1, q-1);
        // generateExp();
        {int g;
            do {
                e = rng.nextInt(t-2)+2; // 1 <  e < t
                do {
                    e /= (g = NumberTheory.gcd(e, t)); // we don't divide t so e may still have factors after dividing by the NumberTheory.gcd, e.g., e = 2^4 * 3^1 * 11 int g;and t = 2^2 * 3^5 * 7
                } while (g != 1);
            } while (e == 1);}
        d = NumberTheory.modinv(e, t);
        if (d == -1) throw new IllegalStateException("Something went wrong!");
        dmp1 = d % (p-1);
        dmq1 = d % (q-1);
        iqmp = NumberTheory.modinv(q, p);
    }

    public int encrypt(int m) {
        return NumberTheory.modexp(m, e, n);
    }

    public int decrypt(int c) {
        return NumberTheory.modexp(c, d, n);
    }

    public String encrypt(String s) {
        return s.chars()
        .map(this::encrypt)
        .boxed()
        .reduce("", (s1, i) -> s1 + (char) (int) i, 
            (s1, s2) -> s1 + s2);
        // return new String(s.chars().map(this::encrypt)
    }

    public String decrypt(String s) {
        return s.chars()
        .map(this::decrypt)
        .boxed()
        .reduce("", (s1, i) -> s1 + (char) (int) i, 
            (s1, s2) -> s1 + s2);
        // return new String(s.chars().map(this::encrypt)
    }

    public boolean chk(int m) {
        return m == decrypt(encrypt(m));
    }

    public String toString() {
        return String.format("p=%s, q=%s, n=%s, t=%s, e=%s, d=%s", p, q, n, t, e, d);
    }
}