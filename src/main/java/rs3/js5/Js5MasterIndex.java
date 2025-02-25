package rs3.js5;

import rs3.Unpack;
import rs3.util.Packet;

public class Js5MasterIndex {
    private final Js5MasterIndexArchiveData[] archiveInfo;

    public Js5MasterIndex(byte[] data) {
        var packet = new Packet(data);
        var archiveCount = 0;

        if (Unpack.VERSION < 456) {
            archiveCount = data.length / 4;
        } else if (Unpack.VERSION < 605) {
            archiveCount = data.length / 8;
        } else {
            archiveCount = packet.g1();
        }

        archiveInfo = new Js5MasterIndexArchiveData[archiveCount];

        for (var i = 0; i < archiveCount; i++) {
            var crc = packet.g4s();
            var version = 0;
            var groupCount = 0;
            var unknown = 0;
            var whirlpool = (byte[]) null;

            if (Unpack.VERSION >= 456) {
                version = packet.g4s();
            }

            if (Unpack.VERSION >= 818) {
                groupCount = packet.g4s();
                unknown = packet.g4s();
            }

            if (Unpack.VERSION >= 605) {
                whirlpool = packet.gdata(64);
            }

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
