package padre.re;

import padre.ds.ComBit;
import padre.ds.GenericSet;

/**
 * Core definitions of properties possessed by all parts of a regular expression, as well as static classes
 * implementing those individual parts.
 * Created by Tommy Ettinger on 8/29/2016.
 */
public interface RE {
    /**
     * Gets the set of all chars (in the Unicode BMP) that this can match, as a compressed bitset (ComBit).
     * @return the compressed bitset representing all chars this can match
     */
    ComBit sigma();

    /**
     * True if this will consume further input, false if it is satisfied after consuming one char.
     * @return true if this is greedy, false if it is not
     */
    boolean isGreedy();

    /**
     * True iff the RE is Phi or is a different RE that can be treated as equivalent to Phi because its rules
     * prevent anything from matching.
     * @return true if the RE cannot match anything, false otherwise.
     */
    boolean isPhi();

    /**
     * False if the RE can potentially be unable to match the empty string, true otherwise. isEpsilon is in
     * general more restrictive than posEpsilon, and will only return true for a Choice if that Choice is
     * between two REs that both can match the empty string, and similarly for Star, where it only returns true
     * if the RE repeated by Star can itself match the empty string.
     * @return false if the RE can potentially be unable to match the empty string, true otherwise
     */
    boolean isEpsilon();

    /**
     * Very similar to isEpsilon except in how it handles choice and some Kleene star operations. posEpsilon is
     * in general less restrictive than isEpsilon, returning true for more Choice and Star REs.
     * @return true if the RE can match the empty string, false if it won't match an empty string
     */
    boolean posEpsilon();

    /**
     * Finds unnecessary sections of an RE that slow down processing and cleans them up where possible.
     * @return a new RE that should be equivalent to this in behavior but no more complex
     */
    RE simplify();

    /**
     * Finds the set of RE values that can follow this RE when it is given the char l.
     * @param c the char that should be checked to see what can follow it
     * @return the Set of RE values that can follow this combination of RE and char
     */
    GenericSet<RE> partialDerive(char c);

    /**
     * Used to mark a state that cannot match anything, including the empty string and all chars.
     * Not the golden ratio.
     */
    class Phi implements RE
    {
        public Phi()
        {

        }
        /**
         * Gets the set of all chars (in the Unicode BMP) that this can match, as a compressed bitset (ComBit).
         *
         * @return the compressed bitset representing all chars this can match
         */
        @Override
        public ComBit sigma() {
            return noBits;
        }

        /**
         * True if this will consume further input, false if it is satisfied after consuming one char.
         *
         * @return true if this is greedy, false if it is not
         */
        @Override
        public boolean isGreedy() {
            return false;
        }

        /**
         * True iff the RE is Phi or is a different RE that can be treated as equivalent to Phi because its rules
         * prevent anything from matching.
         *
         * @return true if the RE cannot match anything, false otherwise.
         */
        @Override
        public boolean isPhi() {
            return true;
        }

        /**
         * False if the RE can potentially be unable to match the empty string, true otherwise. isEpsilon is in
         * general more restrictive than posEpsilon, and will only return true for a Choice if that Choice is
         * between two REs that both can match the empty string, and similarly for Star, where it only returns true
         * if the RE repeated by Star can itself match the empty string.
         *
         * @return false if the RE can potentially be unable to match the empty string, true otherwise
         */
        @Override
        public boolean isEpsilon() {
            return false;
        }

        /**
         * Very similar to isEpsilon except in how it handles choice and some Kleene star operations. posEpsilon is
         * in general less restrictive than isEpsilon, returning true for more Choice and Star REs.
         *
         * @return true if the RE can match the empty string, false if it won't match an empty string
         */
        @Override
        public boolean posEpsilon() {
            return false;
        }

        /**
         * Finds unnecessary sections of an RE that slow down processing and cleans them up where possible.
         *
         * @return a new RE that should be equivalent to this in behavior but no more complex
         */
        @Override
        public RE simplify() {
            return this;
        }

        /**
         * Finds the set of RE values that can follow this RE when it is given the char l.
         *
         * @param c the char that should be checked to see what can follow it
         * @return the Set of RE values that can follow this combination of RE and char
         */
        @Override
        public GenericSet<RE> partialDerive(char c) {
            return nilMatch;
        }
    }

    class Empty implements RE
    {
        public Empty(){}
        /**
         * Gets the set of all chars (in the Unicode BMP) that this can match, as a compressed bitset (ComBit).
         *
         * @return the compressed bitset representing all chars this can match
         */
        @Override
        public ComBit sigma() {
            return noBits;
        }

        /**
         * True if this will consume further input, false if it is satisfied after consuming one char.
         *
         * @return true if this is greedy, false if it is not
         */
        @Override
        public boolean isGreedy() {
            return false;
        }

        /**
         * True iff the RE is Phi or is a different RE that can be treated as equivalent to Phi because its rules
         * prevent anything from matching.
         *
         * @return true if the RE cannot match anything, false otherwise.
         */
        @Override
        public boolean isPhi() {
            return false;
        }

        /**
         * False if the RE can potentially be unable to match the empty string, true otherwise. isEpsilon is in
         * general more restrictive than posEpsilon, and will only return true for a Choice if that Choice is
         * between two REs that both can match the empty string, and similarly for Star, where it only returns true
         * if the RE repeated by Star can itself match the empty string.
         *
         * @return false if the RE can potentially be unable to match the empty string, true otherwise
         */
        @Override
        public boolean isEpsilon() {
            return true;
        }

        /**
         * Very similar to isEpsilon except in how it handles choice and some Kleene star operations. posEpsilon is
         * in general less restrictive than isEpsilon, returning true for more Choice and Star REs.
         *
         * @return true if the RE can match the empty string, false if it won't match an empty string
         */
        @Override
        public boolean posEpsilon() {
            return true;
        }

        /**
         * Finds unnecessary sections of an RE that slow down processing and cleans them up where possible.
         *
         * @return a new RE that should be equivalent to this in behavior but no more complex
         */
        @Override
        public RE simplify() {
            return this;
        }

        /**
         * Finds the set of RE values that can follow this RE when it is given the char l.
         *
         * @param c the char that should be checked to see what can follow it
         * @return the Set of RE values that can follow this combination of RE and char
         */
        @Override
        public GenericSet<RE> partialDerive(char c) {
            return nilMatch;
        }
    }
    class Single implements RE
    {
        public final char glyph;
        public Single()
        {
            glyph = ' ';
        }
        public Single(char glyph){
            this.glyph = glyph;
        }
        /**
         * Gets the set of all chars (in the Unicode BMP) that this can match, as a compressed bitset (ComBit).
         *
         * @return the compressed bitset representing all chars this can match
         */
        @Override
        public ComBit sigma() {
            return new ComBit(glyph);
        }

        /**
         * True if this will consume further input, false if it is satisfied after consuming one char.
         *
         * @return true if this is greedy, false if it is not
         */
        @Override
        public boolean isGreedy() {
            return false;
        }

        /**
         * True iff the RE is Phi or is a different RE that can be treated as equivalent to Phi because its rules
         * prevent anything from matching.
         *
         * @return true if the RE cannot match anything, false otherwise.
         */
        @Override
        public boolean isPhi() {
            return false;
        }

        /**
         * False if the RE can potentially be unable to match the empty string, true otherwise. isEpsilon is in
         * general more restrictive than posEpsilon, and will only return true for a Choice if that Choice is
         * between two REs that both can match the empty string, and similarly for Star, where it only returns true
         * if the RE repeated by Star can itself match the empty string.
         *
         * @return false if the RE can potentially be unable to match the empty string, true otherwise
         */
        @Override
        public boolean isEpsilon() {
            return false;
        }

        /**
         * Very similar to isEpsilon except in how it handles choice and some Kleene star operations. posEpsilon is
         * in general less restrictive than isEpsilon, returning true for more Choice and Star REs.
         *
         * @return true if the RE can match the empty string, false if it won't match an empty string
         */
        @Override
        public boolean posEpsilon() {
            return false;
        }

        /**
         * Finds unnecessary sections of an RE that slow down processing and cleans them up where possible.
         *
         * @return a new RE that should be equivalent to this in behavior but no more complex
         */
        @Override
        public RE simplify() {
            return this;
        }

        /**
         * Finds the set of RE values that can follow this RE when it is given the char l.
         *
         * @param c the char that should be checked to see what can follow it
         * @return the Set of RE values that can follow this combination of RE and char
         */
        @Override
        public GenericSet<RE> partialDerive(char c) {
            return (c == glyph) ? matchEmpty : null;
        }
    }
    class Multiple implements RE
    {
        public final ComBit glyphs;
        public Multiple()
        {
            glyphs = noBits;
        }
        public Multiple(char... glyphs){
            this.glyphs = new ComBit(glyphs);
        }
        /**
         * Gets the set of all chars (in the Unicode BMP) that this can match, as a compressed bitset (ComBit).
         *
         * @return the compressed bitset representing all chars this can match
         */
        @Override
        public ComBit sigma() {
            return glyphs;
        }

        /**
         * True if this will consume further input, false if it is satisfied after consuming one char.
         *
         * @return true if this is greedy, false if it is not
         */
        @Override
        public boolean isGreedy() {
            return false;
        }

        /**
         * True iff the RE is Phi or is a different RE that can be treated as equivalent to Phi because its rules
         * prevent anything from matching.
         *
         * @return true if the RE cannot match anything, false otherwise.
         */
        @Override
        public boolean isPhi() {
            return false;
        }

        /**
         * False if the RE can potentially be unable to match the empty string, true otherwise. isEpsilon is in
         * general more restrictive than posEpsilon, and will only return true for a Choice if that Choice is
         * between two REs that both can match the empty string, and similarly for Star, where it only returns true
         * if the RE repeated by Star can itself match the empty string.
         *
         * @return false if the RE can potentially be unable to match the empty string, true otherwise
         */
        @Override
        public boolean isEpsilon() {
            return false;
        }

        /**
         * Very similar to isEpsilon except in how it handles choice and some Kleene star operations. posEpsilon is
         * in general less restrictive than isEpsilon, returning true for more Choice and Star REs.
         *
         * @return true if the RE can match the empty string, false if it won't match an empty string
         */
        @Override
        public boolean posEpsilon() {
            return false;
        }

        /**
         * Finds unnecessary sections of an RE that slow down processing and cleans them up where possible.
         *
         * @return a new RE that should be equivalent to this in behavior but no more complex
         */
        @Override
        public RE simplify() {
            return this;
        }

        /**
         * Finds the set of RE values that can follow this RE when it is given the char l.
         *
         * @param c the char that should be checked to see what can follow it
         * @return the Set of RE values that can follow this combination of RE and char
         */
        @Override
        public GenericSet<RE> partialDerive(char c) {
            return glyphs.contains(c) ? matchEmpty : null;
        }
    }

    class Any implements RE
    {
        public Any()
        {
        }
        /**
         * Gets the set of all chars (in the Unicode BMP) that this can match, as a compressed bitset (ComBit).
         *
         * @return the compressed bitset representing all chars this can match
         */
        @Override
        public ComBit sigma() {
            return allBits;
        }

        /**
         * True if this will consume further input, false if it is satisfied after consuming one char.
         *
         * @return true if this is greedy, false if it is not
         */
        @Override
        public boolean isGreedy() {
            return false;
        }

        /**
         * True iff the RE is Phi or is a different RE that can be treated as equivalent to Phi because its rules
         * prevent anything from matching.
         *
         * @return true if the RE cannot match anything, false otherwise.
         */
        @Override
        public boolean isPhi() {
            return false;
        }

        /**
         * False if the RE can potentially be unable to match the empty string, true otherwise. isEpsilon is in
         * general more restrictive than posEpsilon, and will only return true for a Choice if that Choice is
         * between two REs that both can match the empty string, and similarly for Star, where it only returns true
         * if the RE repeated by Star can itself match the empty string.
         *
         * @return false if the RE can potentially be unable to match the empty string, true otherwise
         */
        @Override
        public boolean isEpsilon() {
            return false;
        }

        /**
         * Very similar to isEpsilon except in how it handles choice and some Kleene star operations. posEpsilon is
         * in general less restrictive than isEpsilon, returning true for more Choice and Star REs.
         *
         * @return true if the RE can match the empty string, false if it won't match an empty string
         */
        @Override
        public boolean posEpsilon() {
            return false;
        }

        /**
         * Finds unnecessary sections of an RE that slow down processing and cleans them up where possible.
         *
         * @return a new RE that should be equivalent to this in behavior but no more complex
         */
        @Override
        public RE simplify() {
            return this;
        }

        /**
         * Finds the set of RE values that can follow this RE when it is given the char l.
         *
         * @param c the char that should be checked to see what can follow it
         * @return the Set of RE values that can follow this combination of RE and char
         */
        @Override
        public GenericSet<RE> partialDerive(char c) {
            return matchEmpty;
        }
    }

    class Choice implements RE
    {
        public final RE[] regs;
        public final boolean greedy;
        private Choice()
        {
            regs = null;
            greedy = true;
        }
        public Choice(boolean greedy, RE... regs){
            this.regs = regs;
            this.greedy = greedy;
        }
        /**
         * Gets the set of all chars (in the Unicode BMP) that this can match, as a compressed bitset (ComBit).
         *
         * @return the compressed bitset representing all chars this can match
         */
        @Override
        public ComBit sigma() {
            int pLen;
            if(regs == null || (pLen = regs.length) <= 0)
                return noBits;
            if(pLen == 1)
                return new ComBit(regs[0].sigma());
            if(pLen == 2)
                return ComBit.union(regs[0].sigma(), regs[1].sigma());
            ComBit working = ComBit.union(regs[0].sigma(), regs[1].sigma());
            for (int i = 2; i < pLen; i++) {
                working = ComBit.union(working, regs[i].sigma());
            }
            return working;
        }

        /**
         * True if this will consume further input, false if it is satisfied after consuming one char.
         *
         * @return true if this is greedy, false if it is not
         */
        @Override
        public boolean isGreedy() {
            return greedy;
        }

        /**
         * True iff the RE is Phi or is a different RE that can be treated as equivalent to Phi because its rules
         * prevent anything from matching.
         *
         * @return true if the RE cannot match anything, false otherwise.
         */
        @Override
        public boolean isPhi() {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return false;
            for (int i = 0; i < rLen; i++) {
                if(!regs[i].isPhi()) return false;
            }
            return true;
        }

        /**
         * False if the RE can potentially be unable to match the empty string, true otherwise. isEpsilon is in
         * general more restrictive than posEpsilon, and will only return true for a Choice if that Choice is
         * between two REs that both can match the empty string, and similarly for Star, where it only returns true
         * if the RE repeated by Star can itself match the empty string.
         *
         * @return false if the RE can potentially be unable to match the empty string, true otherwise
         */
        @Override
        public boolean isEpsilon() {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return false;
            for (int i = 0; i < rLen; i++) {
                if(!regs[i].isEpsilon()) return false;
            }
            return true;
        }

        /**
         * Very similar to isEpsilon except in how it handles choice and some Kleene star operations. posEpsilon is
         * in general less restrictive than isEpsilon, returning true for more Choice and Star REs.
         *
         * @return true if the RE can match the empty string, false if it won't match an empty string
         */
        @Override
        public boolean posEpsilon() {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return false;
            for (int i = 0; i < rLen; i++) {
                if(regs[i].posEpsilon()) return true;
            }
            return false;
        }

        /**
         * Finds unnecessary sections of an RE that slow down processing and cleans them up where possible.
         *
         * @return a new RE that should be equivalent to this in behavior but no more complex
         */
        @Override
        public RE simplify() {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return soloPhi;
            RE[] res = new RE[rLen];
            RE t;
            int j = 0;
            for (int i = 0; i < rLen; i++) {
                t = regs[i].simplify();
                if(!t.isPhi())
                {
                    res[j++] = t;
                }
            }
            RE[] res2 = new RE[j];
            System.arraycopy(res, 0, res2, 0, j);
            return new Choice(greedy, res2);
        }

        /**
         * Finds the set of RE values that can follow this RE when it is given the char l.
         *
         * @param c the char that should be checked to see what can follow it
         * @return the Set of RE values that can follow this combination of RE and char
         */
        @Override
        public GenericSet<RE> partialDerive(char c) {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return nilMatch;
            GenericSet<RE> res = regs[0].partialDerive(c);
            for (int i = 1; i < rLen; i++) {
                res.addAll(regs[i].partialDerive(c));
            }
            return res;
        }
    }
    class Sequence implements RE
    {
        public final RE[] regs;
        private Sequence()
        {
            regs = null;
        }
        public Sequence(RE... regs){
            this.regs = regs;
        }
        /*
        public Sequence(RE reg0, RE... regs){
            int rLen;
            if(regs == null || (rLen = regs.length) == 0)
                this.regs = new RE[]{reg0};
            else
            {
                this.regs = new RE[rLen+1];
                this.regs[0] = reg0;
                System.arraycopy(regs, 0, this.regs, 1, rLen);
            }
        }
        */
        /**
         * Gets the set of all chars (in the Unicode BMP) that this can match, as a compressed bitset (ComBit).
         *
         * @return the compressed bitset representing all chars this can match
         */
        @Override
        public ComBit sigma() {
            int pLen;
            if(regs == null || (pLen = regs.length) <= 0)
                return noBits;
            if(pLen == 1)
                return new ComBit(regs[0].sigma());
            if(pLen == 2)
                return ComBit.union(regs[0].sigma(), regs[1].sigma());
            ComBit working = ComBit.union(regs[0].sigma(), regs[1].sigma());
            for (int i = 2; i < pLen; i++) {
                working = ComBit.union(working, regs[i].sigma());
            }
            return working;

        }

        /**
         * True if this will consume further input, false if it is satisfied after consuming one char.
         *
         * @return true if this is greedy, false if it is not
         */
        @Override
        public boolean isGreedy() {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return false;
            for (int i = 0; i < rLen; i++) {
                if(regs[i].isGreedy()) return true;
            }
            return false;
        }

        /**
         * True iff the RE is Phi or is a different RE that can be treated as equivalent to Phi because its rules
         * prevent anything from matching.
         *
         * @return true if the RE cannot match anything, false otherwise.
         */
        @Override
        public boolean isPhi() {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return false;
            for (int i = 0; i < rLen; i++) {
                if(regs[i].isPhi()) return true;
            }
            return false;
        }

        /**
         * False if the RE can potentially be unable to match the empty string, true otherwise. isEpsilon is in
         * general more restrictive than posEpsilon, and will only return true for a Choice if that Choice is
         * between two REs that both can match the empty string, and similarly for Star, where it only returns true
         * if the RE repeated by Star can itself match the empty string.
         *
         * @return false if the RE can potentially be unable to match the empty string, true otherwise
         */
        @Override
        public boolean isEpsilon() {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return false;
            for (int i = 0; i < rLen; i++) {
                if(regs[i].isEpsilon()) return true;
            }
            return false;
        }

        /**
         * Very similar to isEpsilon except in how it handles choice and some Kleene star operations. posEpsilon is
         * in general less restrictive than isEpsilon, returning true for more Choice and Star REs.
         *
         * @return true if the RE can match the empty string, false if it won't match an empty string
         */
        @Override
        public boolean posEpsilon() {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return false;
            for (int i = 0; i < rLen; i++) {
                if(regs[i].posEpsilon()) return true;
            }
            return false;
        }

        /**
         * Finds unnecessary sections of an RE that slow down processing and cleans them up where possible.
         *
         * @return a new RE that should be equivalent to this in behavior but no more complex
         */
        @Override
        public RE simplify() {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return soloPhi;
            RE[] res = new RE[rLen];
            RE t;
            int j = 0;
            for (int i = 0; i < rLen; i++) {
                t = regs[i].simplify();
                if(!t.isEpsilon())
                {
                    res[j++] = t;
                }
            }
            RE[] res2 = new RE[j];
            System.arraycopy(res, 0, res2, 0, j);
            return new Sequence(res2);
        }

        /**
         * Finds the set of RE values that can follow this RE when it is given the char l.
         *
         * @param c the char that should be checked to see what can follow it
         * @return the Set of RE values that can follow this combination of RE and char
         */
        @Override
        public GenericSet<RE> partialDerive(char c) {
            int rLen;
            if(regs == null || (rLen = regs.length) <= 0)
                return nilMatch;
            GenericSet<RE> fin = new GenericSet<RE>(64), res;
            if(rLen == 1)
            {
                return regs[0].partialDerive(c);
            }
            boolean[] eps = new boolean[rLen - 1];
            for (int i = 0; i < rLen - 1; i++) {
                eps[i] = regs[i].posEpsilon();
                res = regs[i].partialDerive(c);
                for (RE re : res) {
                    RE[] tre = new RE[rLen];
                    System.arraycopy(regs, 0, tre, 1, rLen-1);
                    tre[0] = re;
                    fin.add(new Sequence(tre));
                }
            }
            for (int i = 0; i < rLen-1 && eps[i]; i++) {
                if(i >= rLen - 2)
                    fin.addAll(regs[rLen-1].partialDerive(c));
                else
                {
                    res = regs[i+1].partialDerive(c);
                    for (RE re : res) {
                        RE[] tre = new RE[rLen - 1 - i];
                        System.arraycopy(regs, 0, tre, 1, rLen - 2 - i);
                        tre[0] = re;
                        fin.add(new Sequence(tre));
                    }

                }
            }
            return fin;
        }
    }


    class Star implements RE
    {
        public final RE re;
        public final boolean greedy;
        private Star()
        {
            re = soloAny;
            greedy = true;
        }
        public Star(boolean greedy, RE re){
            this.re = re;
            this.greedy = greedy;
        }
        /**
         * Gets the set of all chars (in the Unicode BMP) that this can match, as a compressed bitset (ComBit).
         *
         * @return the compressed bitset representing all chars this can match
         */
        @Override
        public ComBit sigma() {
            if(re == null)
                return noBits;
            return re.sigma();
        }

        /**
         * True if this will consume further input, false if it is satisfied after consuming one char.
         *
         * @return true if this is greedy, false if it is not
         */
        @Override
        public boolean isGreedy() {
            return greedy;
        }

        /**
         * True iff the RE is Phi or is a different RE that can be treated as equivalent to Phi because its rules
         * prevent anything from matching.
         *
         * @return true if the RE cannot match anything, false otherwise.
         */
        @Override
        public boolean isPhi() {
            return false;
        }

        /**
         * False if the RE can potentially be unable to match the empty string, true otherwise. isEpsilon is in
         * general more restrictive than posEpsilon, and will only return true for a Choice if that Choice is
         * between two REs that both can match the empty string, and similarly for Star, where it only returns true
         * if the RE repeated by Star can itself match the empty string.
         *
         * @return false if the RE can potentially be unable to match the empty string, true otherwise
         */
        @Override
        public boolean isEpsilon() {
            if(re == null || re.isPhi())
                return true;
            return re.isEpsilon();
        }

        /**
         * Very similar to isEpsilon except in how it handles choice and some Kleene star operations. posEpsilon is
         * in general less restrictive than isEpsilon, returning true for more Choice and Star REs.
         *
         * @return true if the RE can match the empty string, false if it won't match an empty string
         */
        @Override
        public boolean posEpsilon() {
            return true;
        }

        /**
         * Finds unnecessary sections of an RE that slow down processing and cleans them up where possible.
         *
         * @return a new RE that should be equivalent to this in behavior but no more complex
         */
        @Override
        public RE simplify() {
            if(re == null)
                return soloPhi;
            return new Star(greedy, re.simplify());
        }

        /**
         * Finds the set of RE values that can follow this RE when it is given the char l.
         *
         * @param c the char that should be checked to see what can follow it
         * @return the Set of RE values that can follow this combination of RE and char
         */
        @Override
        public GenericSet<RE> partialDerive(char c) {
            if(re == null)
                return nilMatch;
            GenericSet<RE> res = re.partialDerive(c), t = new GenericSet<RE>(res.size);
            for (RE r : res)
            {
                t.add(new Sequence(r, this));
            }
            return t;
        }
    }

    ComBit noBits = ComBit.none(), allBits = ComBit.all();
    RE soloPhi = new Phi(), soloEmpty = new Empty(), soloAny = new Any();
    GenericSet<RE> matchEmpty = GenericSet.with(soloEmpty), nilMatch = new GenericSet<RE>(0);
}
