/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package padre.ds;

import java.io.Serializable;
import java.util.Arrays;

/** A resizable, ordered or unordered variable-length int array. Avoids boxing that occurs with ArrayList of Integer.
 * If unordered, this class avoids a memory copy when removing elements (the last element is moved to the removed
 * element's position).
 * <br>
 * Was called IntArray in libGDX; to avoid confusion with the fixed-length primitive array type, VLA (variable-length
 * array) was chosen as a different name.
 * Copied from LibGDX by Tommy Ettinger on 10/1/2015.
 * @author Nathan Sweet */
public class IntVLA implements Serializable, Cloneable {
    private static final long serialVersionUID = -2948161891082748626L;

    public int[] items;
    public int size;

    /** Creates an ordered array with a capacity of 16. */
    public IntVLA() {
        this(16);
    }

    /** Creates an ordered array with the specified capacity. */
    public IntVLA(int capacity) {
        items = new int[capacity];
    }

    /** Creates a new array containing the elements in the specific array. The new array will be ordered if the specific array is
     * ordered. The capacity is set to the number of elements, so any subsequent elements added will cause the backing array to be
     * grown. */
    public IntVLA(IntVLA array) {
        size = array.size;
        items = new int[size];
        System.arraycopy(array.items, 0, items, 0, size);
    }

    /** Creates a new ordered array containing the elements in the specified array. The capacity is set to the number of elements,
     * so any subsequent elements added will cause the backing array to be grown. */
    public IntVLA(int[] array) {
        this(array, 0, array.length);
    }

    /** Creates a new array containing the elements in the specified array. The capacity is set to the number of elements, so any
     * subsequent elements added will cause the backing array to be grown.
     * @param array the int array to copy from
     * @param startIndex the first index in array to copy from
     * @param count the number of ints to copy from array into this IntVLA
     */
    public IntVLA(int[] array, int startIndex, int count) {
        this(count);
        size = count;
        System.arraycopy(array, startIndex, items, 0, count);
    }

    public void add (int value) {
        int[] items = this.items;
        if (size == items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
        items[size++] = value;
    }
    
    public void addAll (IntVLA array) {
        addAll(array, 0, array.size);
    }

    public void addAll (IntVLA array, int offset, int length) {
        if (offset + length > array.size)
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
        addAll(array.items, offset, length);
    }

    public void addAll (int... array) {
        addAll(array, 0, array.length);
    }

    public void addAll (int[] array, int offset, int length) {
        int[] items = this.items;
        int sizeNeeded = size + length;
        if (sizeNeeded > items.length) items = resize(Math.max(8, (int)(sizeNeeded * 1.75f)));
        System.arraycopy(array, offset, items, size, length);
        size += length;
    }

    public void addRange (int start, int end) {
        int[] items = this.items;
        int sizeNeeded = size + end - start;
        if (sizeNeeded > items.length) items = resize(Math.max(8, (int)(sizeNeeded * 1.75f)));
        for(int r = start, i = size; r < end; r++, i++)
        {
            items[i] = (int)r;
        }
        size += end - start;
    }

    public void addFractionRange (int start, int end, int fraction) {
        int[] items = this.items;
        int sizeNeeded = size + (end - start) / fraction + 2;
        if (sizeNeeded > items.length) items = resize(Math.max(8, (int)(sizeNeeded * 1.75f)));
        for(int r = start, i = size; r < end; r = fraction * ((r / fraction) + 1), i++, size++)
        {
            items[i] = r;
        }
    }

    public int get (int index) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        return items[index];
    }

    public void set (int index, int value) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        items[index] = value;
    }

    /**
     * Adds value to the item in the IntVLA at index. Calling it "add" would overlap with the collection method.
     * @param index
     * @param value
     */
    public void incr (int index, int value) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        items[index] += value;
    }

    public void mul (int index, int value) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        items[index] *= value;
    }

    public void insert (int index, int value) {
        if (index > size) throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + size);
        int[] items = this.items;
        if (size == items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
        System.arraycopy(items, index, items, index + 1, size - index);
        size++;
        items[index] = value;
    }

    public void swap (int first, int second) {
        if (first >= size) throw new IndexOutOfBoundsException("first can't be >= size: " + first + " >= " + size);
        if (second >= size) throw new IndexOutOfBoundsException("second can't be >= size: " + second + " >= " + size);
        int[] items = this.items;
        int firstValue = items[first];
        items[first] = items[second];
        items[second] = firstValue;
    }

    /**
     * Given an array or varargs of replacement indices for the values of this IntVLA, reorders this so the first item
     * in the returned version is the same as {@code get(ordering[0])} (with some care taken for negative or too-large
     * indices), the second item in the returned version is the same as {@code get(ordering[1])}, etc.
     * <br>
     * Negative indices are considered reversed distances from the end of ordering, so -1 refers to the same index as
     * {@code ordering[ordering.length - 1]}. If ordering is smaller than this IntVLA, only the indices up to the
     * length of ordering will be modified. If ordering is larger than this IntVLA, only as many indices will be
     * affected as this IntVLA's size, and reversed distances are measured from the end of this IntVLA instead of the
     * end of ordering. Duplicate values in ordering will produce duplicate values in the returned IntVLA.
     * <br>
     * This method modifies this IntVLA in-place and also returns it for chaining.
     *
     * @param ordering an array or varargs of int indices, where the nth item in ordering changes the nth item in this
     *                 IntVLA to have the value currently in this IntVLA at the index specified by the value in ordering
     * @return this for chaining, after modifying it in-place
     */
    public IntVLA reorder (int... ordering) {
        int ol;
        if (ordering == null || (ol = Math.min(size, ordering.length)) == 0)
            return this;
        int[] items = this.items, alt = new int[ol];
        for (int i = 0; i < ol; i++) {
            alt[i] = items[(ordering[i] % ol + ol) % ol];
        }
        System.arraycopy(alt, 0, items, 0, ol);
        return this;
    }

    public boolean contains (int value) {
        int i = size - 1;
        int[] items = this.items;
        while (i >= 0)
            if (items[i--] == value) return true;
        return false;
    }

    public int indexOf (int value) {
        int[] items = this.items;
        for (int i = 0, n = size; i < n; i++)
            if (items[i] == value) return i;
        return -1;
    }

    public int lastIndexOf (int value) {
        int[] items = this.items;
        for (int i = size - 1; i >= 0; i--)
            if (items[i] == value) return i;
        return -1;
    }

    public boolean removeValue (int value) {
        int[] items = this.items;
        for (int i = 0, n = size; i < n; i++) {
            if (items[i] == value) {
                removeIndex(i);
                return true;
            }
        }
        return false;
    }

    /** Removes and returns the item at the specified index. */
    public int removeIndex (int index) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        int[] items = this.items;
        int value = items[index];
        size--;
        System.arraycopy(items, index + 1, items, index, size - index);
        return value;
    }

    /** Removes the items between the specified indices, inclusive. */
    public void removeRange (int start, int end) {
        if (end >= size) throw new IndexOutOfBoundsException("end can't be >= size: " + end + " >= " + size);
        if (start > end) throw new IndexOutOfBoundsException("start can't be > end: " + start + " > " + end);
        int[] items = this.items;
        int count = end - start + 1;
        System.arraycopy(items, start + count, items, start, size - (start + count));
        size -= count;
    }

    /** Removes from this array all of elements contained in the specified array.
     * @return true if this array was modified. */
    public boolean removeAll (IntVLA array) {
        int size = this.size;
        int startSize = size;
        int[] items = this.items;
        for (int i = 0, n = array.size; i < n; i++) {
            int item = array.get(i);
            for (int ii = 0; ii < size; ii++) {
                if (item == items[ii]) {
                    removeIndex(ii);
                    size--;
                    break;
                }
            }
        }
        return size != startSize;
    }


    /** Moves the item at the specified index to the first index and returns it. */
    public int moveToFirst (int index) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        int[] items = this.items;
        int value = items[index];
        if(index == 0) return value;
        System.arraycopy(items, 0, items, 1, index);
        items[0] = value;
        return value;
    }

    /** Moves the item at the specified index to the last index and returns it. */
    public int moveToLast (int index) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        int[] items = this.items;
        int value = items[index];
        if(index == size - 1) return value;
        System.arraycopy(items, index + 1, items, index, size - index - 1);
        items[size - 1] = value;
        return value;
    }

    /** Removes and returns the last item. */
    public int pop () {
        return items[--size];
    }

    /** Returns the last item. */
    public int peek () {
        return items[size - 1];
    }

    /** Returns the first item. */
    public int first () {
        if (size == 0) throw new IllegalStateException("IntVLA is empty.");
        return items[0];
    }

    public void clear () {
        size = 0;
    }

    /** Reduces the size of the backing array to the size of the actual items. This is useful to release memory when many items have
     * been removed, or if it is known that more items will not be added.
     * @return {@link #items} */
    public int[] shrink () {
        if (items.length != size) resize(size);
        return items;
    }

    /** Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many
     * items to avoid multiple backing array resizes.
     * @return {@link #items} */
    public int[] ensureCapacity (int additionalCapacity) {
        int sizeNeeded = size + additionalCapacity;
        if (sizeNeeded > items.length) resize(Math.max(8, sizeNeeded));
        return items;
    }

    protected int[] resize (int newSize) {
        int[] newItems = new int[newSize];
        int[] items = this.items;
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    public int[] asInts () {
        int[] newItems = new int[size];
        int[] items = this.items;
        for (int i = 0; i < size; i++) {
            newItems[i] = items[i] & 0xffff;
        }
        return newItems;
    }

    public void sort () {
        Arrays.sort(items, 0, size);
    }

    public void reverse () {
        int[] items = this.items;
        for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
            int ii = lastIndex - i;
            int temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /** Reduces the size of the array to the specified size. If the array is already smaller than the specified size, no action is
     * taken. */
    public void truncate (int newSize) {
        if (size > newSize) size = newSize;
    }

    public int[] toArray () {
        int[] array = new int[size];
        System.arraycopy(items, 0, array, 0, size);
        return array;
    }

    public IntVLA copy()
    {
        return new IntVLA(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        try {
            IntVLA nx = (IntVLA) super.clone();
            nx.items = new int[items.length];
            System.arraycopy(items, 0, nx.items, 0, items.length);
            return nx;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e + (e.getMessage() != null ? "; " + e.getMessage() : ""));
        }
    }
    @Override
    public int hashCode () {
        int[] items = this.items;
        int h = 1;
        for (int i = 0, n = size; i < n; i++)
            h = h * 31 + items[i];
        return h;
    }

    @Override
	public boolean equals (Object object) {
        if (object == this) return true;
        if (!(object instanceof IntVLA)) return false;
        IntVLA array = (IntVLA)object;
        int n = size;
        if (n != array.size) return false;
        for (int i = 0; i < n; i++)
            if (items[i] != array.items[i]) return false;
        return true;
    }

    @Override
	public String toString () {
        if (size == 0) return "[]";
        int[] items = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(items[0]);
        for (int i = 1; i < size; i++) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    public String toString (String separator) {
        if (size == 0) return "";
        int[] items = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append(items[0]);
        for (int i = 1; i < size; i++) {
            buffer.append(separator);
            buffer.append(items[i]);
        }
        return buffer.toString();
    }

    /** @see #IntVLA(int[]) */
    public static IntVLA with (int... array) {
        return new IntVLA(array);
    }
}