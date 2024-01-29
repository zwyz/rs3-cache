package rs3.unpack;

import rs3.util.Packet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SpriteUnpacker {
    public static List<String> unpack(int id, byte[] data) {
        var packet = new Packet(data);
        packet.pos = data.length - 2;
        var b = packet.g2();
        var format = b >> 15;
        var count = b & 0x7fff;

        if (format == 0) {
            packet.pos = data.length - 7 - count * 8;
            var sizeX = packet.g2();
            var sizeY = packet.g2();
            var paletteSize = (packet.g1() & 255) + 1;

            var subX = new int[count];
            var subY = new int[count];
            var subSizeX = new int[count];
            var subSizeY = new int[count];

            for (var i = 0; i < count; ++i) {
                subX[i] = packet.g2();
            }

            for (var i = 0; i < count; ++i) {
                subY[i] = packet.g2();
            }

            for (var i = 0; i < count; ++i) {
                subSizeX[i] = packet.g2();
            }

            for (var i = 0; i < count; ++i) {
                subSizeY[i] = packet.g2();
            }

            packet.pos = data.length - 7 - count * 8 - (paletteSize - 1) * 3;
            var palette = new int[paletteSize];

            for (var i = 1; i < paletteSize; ++i) {
                palette[i] = packet.g3();

                if (palette[i] == 0) {
                    palette[i] = 1;
                }
            }

            packet.pos = 0;

            for (var i = 0; i < count; ++i) {
                var pixels = new int[sizeX * sizeY];
                var flags = packet.g1();

                if ((flags & 2) == 0) {
                    if ((flags & 1) == 0) {
                        for (var y = subY[i]; y < subY[i] + subSizeY[i]; ++y) {
                            for (var x = subX[i]; x < subX[i] + subSizeX[i]; ++x) {
                                pixels[sizeX * y + x] = palette[packet.g1()];
                            }
                        }
                    } else {
                        for (var x = subX[i]; x < subX[i] + subSizeX[i]; ++x) {
                            for (var y = subY[i]; y < subY[i] + subSizeY[i]; ++y) {
                                pixels[sizeX * y + x] = palette[packet.g1()];
                            }
                        }
                    }
                } else {
                    if ((flags & 1) == 0) {
                        for (var y = subY[i]; y < subY[i] + subSizeY[i]; ++y) {
                            for (var x = subX[i]; x < subX[i] + subSizeX[i]; ++x) {
                                pixels[sizeX * y + x] = palette[packet.g1()];
                            }
                        }

                        for (var y = subY[i]; y < subY[i] + subSizeY[i]; ++y) {
                            for (var x = subX[i]; x < subX[i] + subSizeX[i]; ++x) {
                                pixels[sizeX * y + x] &= 0xffffff;
                                pixels[sizeX * y + x] |= packet.g1() << 24;
                            }
                        }
                    } else {
                        for (var x = subX[i]; x < subX[i] + subSizeX[i]; ++x) {
                            for (var y = subY[i]; y < subY[i] + subSizeY[i]; ++y) {
                                pixels[sizeX * y + x] = palette[packet.g1()];
                            }
                        }

                        for (var x = subX[i]; x < subX[i] + subSizeX[i]; ++x) {
                            for (var y = subY[i]; y < subY[i] + subSizeY[i]; ++y) {
                                pixels[sizeX * y + x] &= 0xffffff;
                                pixels[sizeX * y + x] |= packet.g1() << 24;
                            }
                        }
                    }
                }

                save(id, i, sizeX, sizeY, pixels);
            }
        } else {
            packet.pos = 0;
            var version = packet.g1();

            if (version == 0) {
                var flags = packet.g1();
                var alpha = flags == 1;
                var sizeX = packet.g2();
                var sizeY = packet.g2();

                for (var i = 0; i < count; ++i) {
                    var pixels = new int[sizeX * sizeY];

                    for (var j = 0; j < sizeX * sizeY; ++j) {
                        pixels[j] = 0xff000000 | packet.g3();
                        if (pixels[j] == 0xffff00ff) {
                            pixels[j] = 0;
                        }
                    }

                    if (alpha) {
                        for (var j = 0; j < sizeX * sizeY; ++j) {
                            pixels[j] &= 0xffffff;
                            pixels[j] |= packet.g1() << 24;
                        }
                    }

                    save(id, i, sizeX, sizeY, pixels);
                }
            } else if (version == 1) {
                throw new UnsupportedOperationException();
            } else {
                throw new RuntimeException();
            }
        }

        return List.of();
    }

    private static void save(int id, int subid, int sizeX, int sizeY, int[] pixels) {
        try (var out = Files.newOutputStream(Path.of("spritedump/" + id + "_" + subid + ".png"))) {
            var data = new DataBufferInt(pixels, pixels.length);
            var raster = Raster.createPackedRaster(data, sizeX, sizeY, sizeX, new int[]{0xff0000, 0xff00, 0xff}, null);
            var colorModel = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
            var image = new BufferedImage(colorModel, raster, false, null);
            ImageIO.write(image, "png", out);
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
