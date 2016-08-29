package padre.ds;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Tommy Ettinger on 8/20/2016.
 */
public class ComBitTest {
    @Test
    public void testContents()
    {
        ComBit cb = new ComBit("ACDEGsDjmhk".toCharArray());
        System.out.println(cb.contents());
    }
    @Test
    public void testContains()
    {
        ComBit cb = new ComBit("ACDEGsDjmhk".toCharArray());
        assertTrue(cb.contains('A'));
        assertFalse(cb.contains('B'));
        assertTrue(cb.contains('C'));
        assertTrue(cb.contains('D'));
        assertTrue(cb.contains('E'));
        assertFalse(cb.contains('F'));
        assertTrue(cb.contains('G'));
        assertFalse(cb.contains('H'));
        assertFalse(cb.contains('I'));
        assertFalse(cb.contains('J'));
    }
    @Test
    public void testNegate()
    {
        ComBit cb = new ComBit("ACDEGsDjmhk".toCharArray()).negate();
        assertFalse(cb.contains('A'));
        assertTrue(cb.contains('B'));
        assertFalse(cb.contains('C'));
        assertFalse(cb.contains('D'));
        assertFalse(cb.contains('E'));
        assertTrue(cb.contains('F'));
        assertFalse(cb.contains('G'));
        assertTrue(cb.contains('H'));
        assertTrue(cb.contains('I'));
        assertTrue(cb.contains('J'));
    }

    @Test
    public void testUnion()
    {
        ComBit caps = new ComBit("ACDEGDAA".toCharArray()), lows = new ComBit("sgjmhk".toCharArray()),
                combined = new ComBit("ACDEGDAAsgjmhk".toCharArray());
        assertTrue(ComBit.union(caps, lows).equals(combined));
        caps.negate();
        combined = new ComBit("ACDEGDAA".toCharArray()).negate();
        assertTrue(ComBit.union(caps, lows).equals(combined));
        assertTrue(ComBit.union(caps, lows).negate().equals(combined.negate()));
    }

    @Test
    public void testIntersection()
    {
        ComBit caps = new ComBit("ACDEGDAAzx".toCharArray()), lows = new ComBit("sgjmhkxyz".toCharArray()),
                combined = new ComBit("xz".toCharArray());
        assertTrue(ComBit.intersection(caps, lows).equals(combined));
        caps.negate();
        combined = new ComBit("sgjmhky".toCharArray());
        ComBit inter = ComBit.intersection(caps, lows);
        assertTrue(inter.equals(combined));
        inter.negate();
        assertTrue(inter.equals(combined.negate()));
        assertTrue(inter.negate().equals(combined.negate()));
    }

}
