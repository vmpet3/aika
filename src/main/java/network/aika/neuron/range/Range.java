/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.aika.neuron.range;

import network.aika.Document;
import network.aika.Model;
import network.aika.Utils;
import network.aika.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import static network.aika.neuron.range.Position.Operator;


/**
 * The class {@code Range} specifies a text range (begin char pos, end char pos) within a given document.
 *
 * @author Lukas Molzberger
 */
public class Range {

    public static Comparator<Range> BEGIN_COMP = (r1, r2) -> {
        int r = Position.compare(r1.begin, r2.begin);
        if(r != 0) return r;
        return Position.compare(r1.end, r2.end);
    };

    public static Comparator<Range> END_COMP = (r1, r2) -> {
        int r = Position.compare(r1.end, r2.end);
        if(r != 0) return r;
        return Position.compare(r1.begin, r2.begin);
    };


    public final Position begin;
    public final Position end;


    public Range(Document doc, Integer begin, Integer end) {
        this(
                doc.lookupFinalPosition(begin),
                doc.lookupFinalPosition(end)
        );
    }

    public Range(Position begin, Position end) {
        this.begin = begin;
        this.end = end;
    }


    public Position getBegin(boolean invert) {
        return invert ? end : begin;
    }


    public Position getEnd(boolean invert) {
        return invert ? begin : end;
    }


    public Range invert(boolean inv) {
        return inv ? new Range(end, begin) : this;
    }


    public boolean isEmpty() {
        return length() == 0;
    }


    public Integer length() {
        if(begin == null || end == null) return Integer.MAX_VALUE;

        return begin.getDistance(end);
    }


    public boolean equals(Range r) {
        return begin.compare(Operator.EQUALS, r.begin) && end.compare(Operator.EQUALS, r.end);
    }


    public boolean equalsIgnoreNull(Range r) {
        if(begin != null && r.begin != null && !begin.compare(Operator.EQUALS, r.begin)) {
            return false;
        }
        if(end != null && r.end != null && !end.compare(Operator.EQUALS, r.end)) {
            return false;
        }

        return true;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        sb.append(begin);
        sb.append(",");
        sb.append(end);
        sb.append(")");

        return sb.toString();
    }


    public static int compare(Range ra, Range rb, boolean inv) {
        int a = Position.compare(ra.getBegin(inv), rb.getBegin(inv));
        if(a != 0) return a;
        int b = Position.compare(ra.getEnd(inv), rb.getEnd(inv));
        return b;
    }


    public static int compare(Range ra, Range rb) {
        int a = Position.compare(ra.begin, rb.begin);
        if(a != 0) return a;
        int b = Position.compare(ra.end, rb.end);
        return b;
    }


    public static class Relation implements Writable, Comparable<Relation> {
        private static SortedMap<Relation, Relation> map = new TreeMap();

        public static Relation EQUALS = create(Operator.EQUALS, Operator.EQUALS);
        public static Relation BEGIN_EQUALS = create(Operator.EQUALS, Operator.NONE);
        public static Relation END_EQUALS = create(Operator.NONE, Operator.EQUALS);
        public static Relation BEGIN_TO_END_EQUALS = create(Operator.NONE, Operator.EQUALS, Operator.NONE , Operator.NONE);
        public static Relation END_TO_BEGIN_EQUALS = create(Operator.NONE, Operator.NONE, Operator.NONE , Operator.EQUALS);
        public static Relation CONTAINS = create(Operator.LESS_THAN_EQUAL, Operator.GREATER_THAN_EQUAL);
        public static Relation CONTAINED_IN = create(Operator.GREATER_THAN_EQUAL, Operator.LESS_THAN_EQUAL);
        public static Relation OVERLAPS = create(Operator.NONE, Operator.LESS_THAN, Operator.NONE, Operator.GREATER_THAN);
        public static Relation NONE = create(Operator.NONE, Operator.NONE);
        public static Relation BETWEEN = create(Operator.GREATER_THAN, Operator.LESS_THAN);
        public static Relation BEFORE = create(Operator.NONE, Operator.NONE, Operator.NONE , Operator.LESS_THAN_EQUAL);
        public static Relation AFTER = create(Operator.NONE, Operator.NONE, Operator.NONE , Operator.GREATER_THAN_EQUAL);

        public Operator beginToBegin = Operator.NONE;
        public Operator beginToEnd = Operator.NONE;
        public Operator endToEnd = Operator.NONE;
        public Operator endToBegin = Operator.NONE;

        public Relation() {}

        private Relation(Operator beginToBegin, Operator beginToEnd, Operator endToEnd, Operator endToBegin) {
            this.beginToBegin = beginToBegin;
            this.beginToEnd = beginToEnd;
            this.endToEnd = endToEnd;
            this.endToBegin = endToBegin;
        }


        public static Relation create(Operator beginToBegin, Operator beginToEnd, Operator endToEnd, Operator endToBegin) {
            return lookup(new Relation(beginToBegin, beginToEnd, endToEnd, endToBegin));
        }


        public static Relation create(Operator beginToBegin, Operator endToEnd) {
            return create(beginToBegin, Operator.NONE, endToEnd, Operator.NONE);
        }


        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("BB:" + beginToBegin.name() + ",");
            sb.append("BE:" + beginToEnd.name() + ",");
            sb.append("EB:" + endToBegin.name() + ",");
            sb.append("EE:" + endToEnd.name());
            return sb.toString();
        }


        public boolean compare(Range ra, Range rb) {
            return ra.begin.compare(beginToBegin, rb.begin) &&
                    ra.begin.compare(beginToEnd, rb.end) &&
                    ra.end.compare(endToEnd, rb.end) &&
                    ra.end.compare(endToBegin, rb.begin);
        }


        public Relation invert() {
            return new Relation(beginToBegin.invert(), endToBegin.invert(), endToEnd.invert(), beginToEnd.invert());
        }


        @Override
        public int compareTo(Relation rr) {
            int r = beginToBegin.compareTo(rr.beginToBegin);
            if(r != 0) return r;
            r = beginToEnd.compareTo(rr.beginToEnd);
            if(r != 0) return r;
            r = endToEnd.compareTo(rr.endToEnd);
            if(r != 0) return r;
            r = endToBegin.compareTo(rr.endToBegin);
            return r;
        }


        public static Relation lookup(Relation r) {
            Relation rr = map.get(r);
            if(rr == null) {
                rr = r;
                map.put(r, r);
            }
            return rr;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeByte(beginToBegin.getId());
            out.writeByte(beginToEnd.getId());
            out.writeByte(endToEnd.getId());
            out.writeByte(endToBegin.getId());
        }

        public static Relation read(DataInput in, Model m) throws IOException {
            Relation r = new Relation();
            r.readFields(in, m);
            return lookup(r);
        }

        @Override
        public void readFields(DataInput in, Model m) throws IOException {
            beginToBegin = Operator.getById(in.readByte());
            beginToEnd = Operator.getById(in.readByte());
            endToEnd = Operator.getById(in.readByte());
            endToBegin = Operator.getById(in.readByte());
        }
    }
}