package rs3.js5;

import rs3.util.CRC32;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemCacheResourceProvider implements Js5ResourceProvider {
    private final Path path;
    private final Js5ResourceProvider underlying;
    private final byte[] masterIndexData;
    private final byte[][] archiveIndexData;
    private final Js5MasterIndex masterIndex;
    private final Js5ArchiveIndex[] archiveIndex;

    public FileSystemCacheResourceProvider(Path path, Js5ResourceProvider underlying) {
        this.path = path;
        this.underlying = underlying;

        // Load master index (needed for CRCs)
        this.masterIndexData = underlying.get(255, 255, true, 0);
        this.masterIndex = new Js5MasterIndex(Js5Util.decompress(masterIndexData));

        // Load archive indices (needed for CRCs)
        this.archiveIndexData = new byte[masterIndex.getArchiveCount()][];
        this.archiveIndex = new Js5ArchiveIndex[masterIndex.getArchiveCount()];

        for (int archive = 0; archive < masterIndex.getArchiveCount(); archive++) {
            var data = masterIndex.getArchiveData(archive);

            if (data.getCrc() != 0) {
                archiveIndexData[archive] = underlying.get(255, archive, true, 0);
                archiveIndex[archive] = new Js5ArchiveIndex(Js5Util.decompress(archiveIndexData[archive]));
            }
        }
    }

    @Override
    public byte[] get(int archive, int group, boolean urgent, int priority) {
        try {
            if (archive == 255 && group == 255) {
                return masterIndexData;
            } else if (archive == 255) {
                return archiveIndexData[group];
            } else {
                var crc = archiveIndex[archive].groupChecksum[group];
                var file = path.resolve(archive + "/" + group + "_" + Integer.toHexString(crc));

                if (Files.exists(file)) {
                    var data = Files.readAllBytes(file);

                    if (CRC32.crc(data) == crc) {
                        return data;
                    } else {
                        System.err.println("[File System Cache] CRC mismatch on " + archive + "." + group);
                    }
                }

                var data = underlying.get(archive, group, urgent, 0);
                Files.createDirectories(file.getParent());
                Files.write(file, data);
                return data;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
