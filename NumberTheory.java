import static java.lang.Math.*;
public class NumberTheory {
        /**
     * Computes y such that xy = 1 (mod m).
     */
    public static int modinv(int x, int m) {
        int[] bezout = egcd(x, m);
        if (bezout[2] != 1) {
            return -1;
        }
        return posmod(bezout[0], m);
    }

    /**
     * Computes the positive remainder of x / m. 
     * 
     * I.e., if x % m < 0 then this returns x % m + m.
     */
    public static int posmod(int x, int m) {
        return ((x%m) + m) % m;
    }

    /**
     * Computes a set of coefficients (a,b) for Bezout's identity, ax + by = gcd(x, y).
     */
    public static int[] egcd(int x, int y) {
        int[] eq1 = {1, 0, x};
        int[] eq2 = {0, 1, y};
        int[] t;
        int d;
        while (eq1[2] != 0) {
            d = eq2[2] / eq1[2];
            eq2[0] -= d * eq1[0];
            eq2[1] -= d * eq1[1];
            eq2[2] -= d * eq1[2];
            t = eq2;
            eq2 = eq1;
            eq1 = t;
        }
        return eq2;
    }

    public static int gcd(int x, int y) {
        return x > y ? gcd_recur(x, y) : gcd_recur(y, x);
    }

    public static int gcd_recur(int m, int n) {
        if (n == 0) return m;
        return gcd_recur(n, m%n);
    }

    public static int modexp(int b, int e, int m) {
        int p = 1;
        b %= m;
        multiplyExact(m-1, m-1); // check that, if b = m-1, it will not oveflow
        while (e > 0) {
            if (e % 2 == 1) {
                e -= 1;
                p *= b;
                p %= m;
            }
            e /= 2;
            b *= b;
            b %= m;
        }
        return p;
    }
}