package feedreader.utils;

import java.util.Date;
import java.util.TreeSet;

import feedreader.log.Logger;

@Deprecated
public class RangeTimes {

    StringBuilder sb = new StringBuilder();
    TreeSet<Range> ranges = new TreeSet<Range>();
    static final String ANDOPER = " AND ";
    static final int ANDOPERLEN = ANDOPER.length();

    public RangeTimes() {
    }

    public RangeTimes(String timeFilter) {
        // s:1419860380000,e:1419859720000
        // s:1419860380000,e:1419859720000|s:1419860380000,e:1419859720000
        add(timeFilter);
    }

    public int getRangeCount() {
        return ranges.size();
    }

    public TreeSet<Range> getRanges() {
        return ranges;
    }

    // "s:1419860380000,e:1419779640000"
    public final void add(String addFilter) {
        for (String range : addFilter.split("\\|")) {
            Range r = new Range(range);
            if (r.start == 0 || r.end == 0) {
                continue;
            }
            ranges.add(r);
        }
    }

    @Override
    public String toString() {
        return "RangeTimes{ " + ranges + " }";
    }

    public String serialize() {
        sb.setLength(0);
        for (Range r : ranges) {
            sb.append(r.serialize()).append("|");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    public class Range implements Comparable<Range> {

        long start = 0L;
        long end = 0L;

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }

        public Range(String str) {
            // s:1419860380000,e:1419859720000
            String[] ranges = str.split(",");

            for (String r : ranges) {
                // s:1419860380000 or
                // e:1419859720000
                String[] range = r.split(":");
                if (range[0].equals("s")) {
                    start = Long.parseLong(range[1]); // TODO: Parse directly.
                } else if (range[0].equals("e")) {
                    end = Long.parseLong(range[1]);
                } else {
                    Logger.error(RangeTimes.class).log("Unknown range type: ").log(range[0]).end();
                }
            }
        }

        @Override
        public String toString() {
            return "Range{" + "start=" + new Date(start) + ", end=" + new Date(end) + '}';
        }

        /**
         * We don't only compare objects, but if "equal" we copy the highest
         * start and lowest end to the other object.
         *
         * @param o
         * @return
         */
        @Override
        public int compareTo(Range o) {
            if (o.end > start) {
                return 1;
            }
            if (o.start < end) {
                return -1;
            }

            start = (o.start > start) ? o.start : start; // The highest start
            o.start = start;

            end = (o.end < end) ? o.end : end; // The lowest end.
            o.end = end;

            return 0;
        }

        public String serialize() {
            return "s:" + start + ",e:" + end;
        }

        public String toSqlString(String field) {
            sb.setLength(0);
            sb.append(field).append("<=").append(start).append(" AND ").append(field).append(">=").append(end);
            return sb.toString();
        }
    }

}
