package padre.ds;

import java.util.Arrays;

/**
 * Compressed Bit-set that builds on CharVLA to support checking the on/off status of 65536 bits, but typically
 * uses much less than that amount of memory. Uses a simple, small RLE-based encoding method, similar to but
 * simpler than JavaEWAH, which is more effective at larger maximum sizes.
 * Created by Tommy Ettinger on 8/19/2016.
 */
public class ComBit extends CharVLA {
    public int windowStart;

    /**
     * Creates a new ComBit with the given capacity for compressed items.
     *
     * @param capacity the initial capacity of the ComBit, as a guess at the future compressed size (may be 0).
     */
    public ComBit(int capacity) {
        super(capacity+2);
        windowStart = 1;
        size = 0;
        add(0);
    }

    /**
     * Creates a new array list with 14 capacity.
     */
    protected ComBit() {
        this(14);
    }

    /**
     * Creates a new ComBit and ensures the char values present in items will be contained in the ComBit.
     * @param items an array or vararg whose elements will be used to fill the ComBit.
     */
    public ComBit(char... items) {
        this(items.length * 2);
        if(items.length <= 0)
            return;
        char[] a2 = new char[items.length];
        System.arraycopy(items, 0, a2, 0, items.length);
        Arrays.sort(a2);
        add(a2[0]);
        char t, p = a2[0], c = '\0';
        for (int i = 0; i < items.length; i++) {
            t = a2[i];
            if(t - p == c) {
                c++;
            }
            else
            {
                add(c);
                add(t - (p + c));
                p = t;
                c = '\1';
            }
        }
        add(c);

    }

    public boolean contains(char c) {
        boolean on = false;
        int index = 0;
        char t;
        for (int i = windowStart; i < size; i++, on = !on) {
            t = a[i];
            if(on && c < index + t)
            {
                return true;
            }
            index += t;
        }
        return false;
    }
}
