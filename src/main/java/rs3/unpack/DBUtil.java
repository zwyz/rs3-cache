package rs3.unpack;

import rs3.Unpack;

public class DBUtil {
    public static int getPackedColumn(int table, int column) {
        if (Unpack.VERSION < 912) {
            return table << 8 | column;
        } else {
            return table << 12 | column << 4;
        }
    }

    public static int getTable(int packed) {
        if (Unpack.VERSION < 912) {
            return packed >>> 8;
        } else {
            return packed >>> 12;
        }
    }

    public static int getColumn(int packed) {
        if (Unpack.VERSION < 912) {
            return packed & 255;
        } else {
            return (packed >>> 4) & 255;
        }
    }

    public static int getTupleIndex(int packed) {
        if (Unpack.VERSION < 912) {
            return -1;
        } else {
            return (packed & 15) - 1;
        }
    }
}
