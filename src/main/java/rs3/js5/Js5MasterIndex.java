package rs3.js5;

import rs3.util.Packet;

public class Js5MasterIndex {
    private final Js5MasterIndexArchiveData[] archiveInfo;

    public Js5MasterIndex(byte[] data) {
        var packet = new Packet(data);
        var archiveCount = packet.g1();

        archiveInfo = new Js5MasterIndexArchiveData[archiveCount];

        for (var i = 0; i < archiveCount; i++) {
            var crc = packet.g4s();
            var version = packet.g4s();
            var groupCount = packet.g4s();
            var unknown = packet.g4s();
            var whirlpool = packet.gdata(64);
            archiveInfo[i] = new Js5MasterIndexArchiveData(crc, groupCount, version, unknown, whirlpool);
        }

        // TODO: check signature
    }

    public int getArchiveCount() {
        return archiveInfo.length;
    }

    public Js5MasterIndexArchiveData getArchiveData(int archive) {
        return archiveInfo[archive];
    }
}
