package rs3.js5;

public final class Js5MasterIndexArchiveData {
    private final int crc;
    private final int groupCount;
    private final int version;
    private final int unknown;
    private final byte[] whirlpool;

    public Js5MasterIndexArchiveData(int crc, int groupCount, int version, int unknown, byte[] whirlpool) {
        this.crc = crc;
        this.groupCount = groupCount;
        this.version = version;
        this.unknown = unknown;
        this.whirlpool = whirlpool;
    }

    public int getCrc() {
        return crc;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public int getVersion() {
        return version;
    }

    public int getUnknown() {
        return unknown;
    }

    public byte[] getWhirpool() {
        return whirlpool;
    }
}
