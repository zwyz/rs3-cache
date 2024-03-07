package rs3.js4;

import rs3.util.Packet;

public class Jagfile {
    private byte[] buffer;
    private int fileCount;
    private int[] fileHash;
    private int[] fileUnpackedSize;
    private int[] filePackedSize;
    private int[] fileOffset;
    private boolean unpacked;

    public Jagfile(byte[] src) {
        this.load(src);
    }

    private void load(byte[] src) {
        var data = new Packet(src);
        var unpackedSize = data.g3();
        var packedSize = data.g3();

        if (packedSize == unpackedSize) {
            this.buffer = src;
            this.unpacked = false;
        } else {
            var temp = new byte[unpackedSize];
            BZip2.read(temp, unpackedSize, src, packedSize, 6);
            this.buffer = temp;

            data = new Packet(this.buffer);
            this.unpacked = true;
        }

        this.fileCount = data.g2();
        this.fileHash = new int[this.fileCount];
        this.fileUnpackedSize = new int[this.fileCount];
        this.filePackedSize = new int[this.fileCount];
        this.fileOffset = new int[this.fileCount];

        var pos = data.pos + this.fileCount * 10;

        for (var i = 0; i < this.fileCount; i++) {
            this.fileHash[i] = data.g4s();
            this.fileUnpackedSize[i] = data.g3();
            this.filePackedSize[i] = data.g3();
            this.fileOffset[i] = pos;
            pos += this.filePackedSize[i];
        }
    }


    public byte[] read(String name, byte[] dst) {
        var hash = 0;
        var upper = name.toUpperCase();
        for (var i = 0; i < upper.length(); i++) {
            hash = hash * 61 + upper.charAt(i) - 32;
        }

        for (var i = 0; i < this.fileCount; i++) {
            if (this.fileHash[i] == hash) {
                if (dst == null) {
                    dst = new byte[this.fileUnpackedSize[i]];
                }

                if (this.unpacked) {
                    if (this.fileUnpackedSize[i] >= 0) {
                        System.arraycopy(this.buffer, this.fileOffset[i], dst, 0, this.fileUnpackedSize[i]);
                    }
                } else {
                    BZip2.read(dst, this.fileUnpackedSize[i], this.buffer, this.filePackedSize[i], this.fileOffset[i]);
                }

                return dst;
            }
        }

        return null;
    }
}