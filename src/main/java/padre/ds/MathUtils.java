package padre.ds;

/**
 * Created by Tommy Ettinger on 9/4/2016.
 */
public class MathUtils {
    /**
     * Generates a pseudo-random long when passed a different state each time; state should usually be
     * incremented and assigned at the call site with (specifically) {@code state += 0x9E3779B97F4A7C15L}.
     * @param state long; should be incremented and assigned at the call site with {@code state += 0x9E3779B97F4A7C15L}
     * @return a pseudo-random long
     */
    public static long nextLong(long state) {
        state = (state ^ (state >>> 30)) * 0xBF58476D1CE4E5B9L;
        state = (state ^ (state >>> 27)) * 0x94D049BB133111EBL;
        return state ^ (state >>> 31);
    }
    /**
     * Generates a pseudo-random int that is less than bound and at least equal to 0 when passed a different state each
     * time; state should usually be incremented and assigned at the call site with (specifically)
     * {@code state += 0x9E3779B97F4A7C15L}, and bound should always be positive.
     * @param state long; should be incremented and assigned at the call site with {@code state += 0x9E3779B97F4A7C15L}
     * @param bound a positive exclusive upper bound for the random int
     * @return a pseudo-random int that is between 0 (inclusive) and bound (exclusive)
     */
    public static int nextInt(long state, int bound)
    {
        state = (state ^ (state >>> 30)) * 0xBF58476D1CE4E5B9L;
        state = (state ^ (state >>> 27)) * 0x94D049BB133111EBL;
        return (int)((bound * ((state ^ (state >>> 31)) & 0x7FFFFFFFL)) >>> 31);
    }
    public static int nextPowerOfTwo(int n)
    {
        int highest = Integer.highestOneBit(n);
        return  (highest == Integer.lowestOneBit(n)) ? highest : highest << 1;
    }
}
