package padre.re;

import padre.ds.IntMap;
import padre.ds.LongVLA;
import padre.ds.OMap;

/**
 * Created by Tommy Ettinger on 9/3/2016.
 */
public interface InnerPattern {

    /**
     * Returns true if this Pattern is greedy.
     * @return true if this is greedy, false otherwise.
     */
    boolean isGreedy();

    /**
     * Gets the RE that is the core of the implementation for this InnerPattern.
     * @return the backing RE.
     */
    RE strip();

    /**
     * Gets a binder (a synonym for this method's return type) from this InnerPattern.
     * @return A "map" of Int keys to primitive lists of long values representing ranges
     */
    IntMap<LongVLA> toBinder();
    /**
     * Really complicated.
     * @return an ordered Map of InnerPattern keys to IncompleteUpdate values that can be used by LeftToRight
     */
    OMap<InnerPattern, IncompleteUpdate> partialDerive0();

    /**
     * Returns true if any component of this InnerPattern is a variable and as such needs to bind indices to ranges.
     * @return true if this InnerPattern is a variable or contains one
     */
    boolean hasBinder();
    /**
     * True iff the InnerPattern is Phi or is a different InnerPattern that can be treated as equivalent to Phi because
     * its rules prevent anything from matching.
     * @return true if the InnerPattern cannot match anything, false otherwise.
     */
    boolean isPhi();

    /**
     * False if the InnerPattern can potentially be unable to match the empty string, true otherwise.
     * @return false if the InnerPattern can potentially be unable to match the empty string, true otherwise
     */
    boolean isEpsilon();

    /**
     * Gets a simplified version of this InnerPattern, if possible.
     * @return a simplified version of this InnerPattern (a copy), or possibly this object (not a copy)
     */
    InnerPattern simplify();

    class IncompleteUpdate
    {
        public int currentKey, updatePosition = 0;
        public IntMap<LongVLA> binder = null;

        public IncompleteUpdate() {
            this.currentKey = 0;
        }
        public IncompleteUpdate(int currentKey) {
            this.currentKey = currentKey;
        }

        public IncompleteUpdate(int currentKey, int updatePosition) {
            this.currentKey = currentKey;
            this.updatePosition = updatePosition;
        }

        public IncompleteUpdate(int currentKey, int updatePosition, IntMap<LongVLA> binder) {
            this.currentKey = currentKey;
            this.updatePosition = updatePosition;
            this.binder = binder;
        }

        public IntMap<LongVLA> update()
        {
            if(binder == null)
                return null;
            LongVLA found = binder.get(currentKey);
            if(found == null)
                binder.put(currentKey, LongVLA.single(((long)updatePosition << 32) | updatePosition));
            else if(found.isEmpty())
                found.add(((long)updatePosition << 32) | updatePosition);
            else
            {
                long first = found.getLong(0);
                if((first & 0xFFFFFFFFL) == updatePosition - 1)
                    found.set(0, first + 1L);
                else
                    found.add(0, ((long)updatePosition << 32) | updatePosition);
            }
            return binder;
        }

    }
}
