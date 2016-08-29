package padre.ds;

import java.util.Arrays;

/**
 * Compressed Bit-set that builds on CharVLA to support checking the on/off status of 65536 bits, but typically
 * uses much less than that amount of memory. Uses a simple, small RLE-based encoding method, similar to but
 * simpler than JavaEWAH, which is more effective at larger maximum sizes.
 * Created by Tommy Ettinger on 8/19/2016.
 */
public class ComBit extends CharVLA {
    public int windowStart, cardinality;

    /**
     * Creates a new ComBit with the given capacity for compressed items.
     *
     * @param capacity the initial capacity of the ComBit, as a guess at the future compressed size (may be 0).
     */
    public ComBit(int capacity) {
        super(capacity+2);
        windowStart = 1;
        size = 0;
        cardinality = 0;
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
        char t, p = a2[0], c = 0;
        for (int i = 0; i < items.length; i++) {
            t = a2[i];
            if (t - p > c) {
                add(c);
                cardinality += c;
                add(t - (p + c));
                p = t;
                c = 1;
            }
            else if (t - p == c) {
                c++;
            }
        }
        add(c);
        cardinality += c;
    }

    public ComBit(ComBit other)
    {
        super(other);
        windowStart = other.windowStart;
        cardinality = other.cardinality;
    }

    public boolean contains(char c) {
        boolean on = false;
        int index = 0;
        char t;
        for (int i = windowStart; i < size; i++, on = !on) {
            t = a[i];
            if(on && (c >= index && (t == 0 || c < index + t)))
            {
                return true;
            }
            index += t;
        }
        return false;
    }

    public char[] contents() {
        char[] ret = new char[cardinality];
        char t, c = 0, d=0;
        boolean on = false;
        for (int i = windowStart; i < size; i++, on = !on) {
            t = a[i];
            if(on)
            {
                if(t == 0)
                {
                    for (int j = c; j < 0x10000; j++) {
                        ret[d++] = c++;
                    }
                    return ret;
                }
                for (int j = 0; j < t; j++) {
                    ret[d++] = c++;
                }
            }
            else {
                c += t;
            }
        }
        return ret;
    }

    public ComBit negate()
    {
        switch (windowStart)
        {
            case 0:
                windowStart++;
                if(a[size-1] == 0)
                    removeChar(size-1);
                else
                    add(0);
                break;
            case 1:
                if(size == 1)
                {
                    windowStart--;
                    add(0);
                }
                else if(a[1] == 0)
                {
                    windowStart++;
                    if(a[size-1] == 0)
                        removeChar(size-1);
                    else
                        add(0);
                }
                else
                {
                    windowStart--;
                    if(a[size-1] == 0)
                        removeChar(size-1);
                    else
                        add(0);
                }
                break;
            default:
                windowStart--;
                if(a[size-1] == 0)
                    removeChar(size-1);
                else
                    add(0);
        }
        cardinality = 0x10000 - cardinality;
        return this;
    }

    public static ComBit union(ComBit left, ComBit right)
    {
        if(left.cardinality == 0)
            return new ComBit(right);
        if(right.cardinality == 0)
            return new ComBit(left);
        ComBit packing = new ComBit(64);
        boolean on = false, onLeft = false, onRight = false;
        int idx = 0, skip = 0, elemLeft = left.windowStart, elemRight = right.windowStart, totalLeft = 0, totalRight = 0,
                lsz = left.size, rsz = right.size, card = 0, covered = 0;
        char[] la = left.a, ra = right.a;
        while ((elemLeft < lsz || elemRight < rsz) && idx <= 0xffff) {
            if(onLeft && elemLeft >= lsz - 1 && la[lsz - 1] == 0)
            {
                break;
            }
            else if (elemLeft >= lsz) {
                totalLeft = 0x20000;
                onLeft = false;
            }
            else if(totalLeft <= idx) {
                totalLeft += la[elemLeft];
            }
            if(onRight && elemRight >= rsz - 1 && ra[rsz - 1] == 0)
            {
                break;
            }
            else if(elemRight >= rsz) {
                totalRight = 0x20000;
                onRight = false;
            }
            else if(totalRight <= idx) {
                totalRight += ra[elemRight];
            }
            // 300, 5, 6, 8, 2, 4
            // 290, 12, 9, 1
            // =
            // 290, 15, 6, 8, 2, 4
            // 290 off in both, 10 in right, 2 in both, 3 in left, 6 off in both, 1 on in both, 7 on in left, 2 off in
            //     both, 4 on in left
            if(totalLeft < totalRight)
            {
                onLeft = !onLeft;
                skip += totalLeft - idx;
                idx = totalLeft;
                if(on != (onLeft || onRight)) {
                    if (!(!onLeft && elemLeft >= lsz - 1 && la[lsz - 1] == 0)) {
                        packing.add(skip);
                        covered += skip;
                        if (on) card += skip;
                        skip = 0;
                        on = !on;
                    }
                }
                elemLeft++;
            }
            else if(totalLeft == totalRight)
            {
                onLeft = !onLeft;
                onRight = !onRight;
                skip += totalLeft - idx;
                idx = totalLeft;
                if(on != (onLeft || onRight)) {
                    if (!(!onRight && elemRight >= rsz - 1 && ra[rsz - 1] == 0) &&
                            !(!onLeft && elemLeft >= lsz - 1 && la[lsz - 1] == 0)) {
                        packing.add(skip);
                        covered += skip;
                        if (on) card += skip;
                        skip = 0;
                        on = !on;
                    }
                }
                elemLeft++;
                elemRight++;

            }
            else
            {
                onRight = !onRight;
                skip += totalRight - idx;
                idx = totalRight;
                if(on != (onLeft || onRight)) {
                    if (!(!onRight && elemRight >= rsz - 1 && ra[rsz - 1] == 0)) {
                        packing.add(skip);
                        covered += skip;
                        if (on) card += skip;
                        skip = 0;
                        on = !on;
                    }
                }
                elemRight++;
            }
        }
        if((elemLeft >= lsz - 1 && la[lsz - 1] == 0) || (elemRight >= rsz - 1 && ra[rsz - 1] == 0))
        {
            card += 0x10000 - covered;
            packing.add(0);
        }
        packing.cardinality = card;
        return packing;
    }
    public static ComBit intersection(ComBit left, ComBit right)
    {
        if(left.cardinality == 0 || right.cardinality == 0)
            return new ComBit(1);
        ComBit packing = new ComBit(64);
        boolean on = false, onLeft = false, onRight = false;
        int idx = 0, skip = 0, elemLeft = left.windowStart, elemRight = right.windowStart, totalLeft = 0, totalRight = 0,
                lsz = left.size, rsz = right.size, card = 0, covered = 0;
        char[] la = left.a, ra = right.a;
        while ((elemLeft < lsz && elemRight < rsz) && idx <= 0xffff) {
            if(onLeft && elemLeft >= lsz - 1 && la[lsz - 1] == 0)
            {
                totalLeft = 0x10000;
                onLeft = true;
            }
            else if (elemLeft >= lsz) {
                totalLeft = 0x20000;
                onLeft = false;
            }
            else if(totalLeft <= idx) {
                totalLeft += la[elemLeft];
            }

            if(onRight && elemRight >= rsz - 1 && ra[rsz - 1] == 0)
            {
                totalRight = 0x10000;
                onRight = true;
            }
            else if(elemRight >= rsz) {
                totalRight = 0x20000;
                onRight = false;
            }
            else if(totalRight <= idx) {
                totalRight += ra[elemRight];
            }
            // 300, 5, 6, 8, 2, 4
            // 290, 12, 9, 1
            // =
            // 290, 15, 6, 8, 2, 4
            // 290 off in both, 10 in right, 2 in both, 3 in left, 6 off in both, 1 on in both, 7 on in left, 2 off in
            //     both, 4 on in left
            if(totalLeft < totalRight)
            {
                onLeft = !onLeft;
                skip += totalLeft - idx;
                idx = totalLeft;
                if(on != (onLeft && onRight)) {
                    packing.add(skip);
                    covered += skip;
                    if(on) card += skip;
                    skip = 0;
                    on = !on;
                }
                elemLeft++;
            }
            else if(totalLeft == totalRight)
            {
                onLeft = !onLeft;
                onRight = !onRight;
                skip += totalLeft - idx;
                idx = totalLeft;
                if(on != (onLeft && onRight)) {
                    if(totalLeft == 0x10000) {
                        card += 0x10000 - covered;
                        packing.add(0);
                        break;
                    }
                    packing.add(skip);
                    covered += skip;
                    if(on) card += skip;
                    skip = 0;
                    on = !on;
                }
                elemLeft++;
                elemRight++;

            }
            else
            {
                onRight = !onRight;
                skip += totalRight - idx;
                idx = totalRight;
                if(on != (onLeft && onRight)) {
                    packing.add(skip);
                    covered += skip;
                    if(on) card += skip;
                    skip = 0;
                    on = !on;
                }
                elemRight++;
            }
        }
        packing.cardinality = card;
        return packing;
    }

    public ComBit copy()
    {
        return new ComBit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComBit that = (ComBit) o;
        if (cardinality != that.cardinality) return false;
        int s = size(), ts = that.size(), ws = windowStart, tws = that.windowStart;
        if (s - ws != ts - tws) return false;
        final char[] a1 = a;
        final char[] a2 = that.a;
        while (s-- != ws && ts-- != tws)
            if (a1[s] != a2[ts]) return false;
        return true;
    }

    public boolean equals(final ComBit that) {
        if (that == this) return true;
        if(cardinality != that.cardinality) return false;
        int s = size(), ts = that.size(), ws = windowStart, tws = that.windowStart;
        if (s - ws != ts - tws) return false;
        final char[] a1 = a;
        final char[] a2 = that.a;
        while (s-- != ws && ts-- != tws)
            if (a1[s] != a2[ts]) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + windowStart;
        result = 31 * result + cardinality;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ComBit{");
        sb.append("cardinality=").append(cardinality).append(",compressed=[");
        if(windowStart < size) {
            sb.append((int) a[windowStart]);
            for (int i = windowStart + 1; i < size; i++) {
                sb.append(',');
                sb.append((int) a[i]);
            }
        }
        return sb.append("]}").toString();
    }
}
