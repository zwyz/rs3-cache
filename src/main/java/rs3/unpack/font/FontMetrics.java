package rs3.unpack.font;

import rs3.util.Packet;

import java.util.ArrayList;
import java.util.List;

public class FontMetrics {

    public FontSourceType sourceType;
    public int sourcePackID;
    public int pixelSize;
    public List<FontGlyphInfo> glyphInfo;
    public int fontSheetWidth;
    public int fontSheetHeight;
    public List<FontSheetPosition> fontSheetPosition;
    public int baseLine;
    public int upperCaseAscent;
    public int byte3049;
    public int maxAscent;
    public int maxDescent;
    public int scale;
    public FontKerningData kerningData;

    public FontMetrics(Packet packet) {
        sourceType = FontSourceType.lookup(packet.g1());
        switch (sourceType) {
            case SPRITE_BITMAP, SPRITE_FONTSHEET -> {
                loadSpriteFontMetricsFromPacket(packet);
            }
            case VECTOR -> {
                loadVectorFontMetricsFromPacket(packet);
            }
            default -> throw new IllegalArgumentException("Invalid font source type: " + sourceType);
        }
    }


    private void loadSpriteFontMetricsFromPacket(Packet packet) {
        boolean complexKerning = packet.g1() == 1;
        sourcePackID = sourceType == FontSourceType.SPRITE_FONTSHEET ? packet.g4s() : -1;
        glyphInfo = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) {
            glyphInfo.add(new FontGlyphInfo());
        }
        for (FontGlyphInfo glyphInfo : glyphInfo) {
            glyphInfo.width = packet.g1();
        }
        for (FontGlyphInfo glyphInfo : glyphInfo) {
            glyphInfo.height = packet.g1();
        }
        for (FontGlyphInfo glyphInfo : glyphInfo) {
            glyphInfo.bearingY = packet.g1();
        }
        fontSheetWidth = packet.g2();
        fontSheetHeight = packet.g2();
        fontSheetPosition = new ArrayList<>(256);
        for (int i = 0; i < 256; ++i) {
            fontSheetPosition.add(new FontSheetPosition());
        }
        for (int i = 0; i < 256; ++i) {
            fontSheetPosition.get(i).x = packet.g2();
        }
        for (int i = 0; i < 256; ++i) {
            fontSheetPosition.get(i).y = packet.g2();
        }
        baseLine = 0;
        if (complexKerning) {
            loadComplexKerning(packet);
        } else {
            baseLine = packet.g1();
        }
        upperCaseAscent = packet.g1();
        byte3049 = packet.g1();
        maxAscent = packet.g1();
        maxDescent = packet.g1();
        scale = packet.g1();
    }

    private void loadComplexKerning(Packet packet) {
        FontKerningData kerningData = this.kerningData = new FontKerningData();
        kerningData.rightKern = new byte[256][];
        for (int glyph1 = 0; glyph1 < 256; ++glyph1) {
            byte[] kerns = kerningData.rightKern[glyph1] = new byte[256];
            int kern = 0;
            for (int glyph2 = 0; glyph2 < 256; ++glyph2) {
                kern += packet.g1s();
                kerns[glyph2] = (byte) kern;
            }
        }
        kerningData.leftKern = new byte[256][];
        for (int glyph1 = 0; glyph1 < 256; ++glyph1) {
            byte[] kerns = kerningData.leftKern[glyph1] = new byte[256];
            int kern = 0;
            for (int glyph2 = 0; glyph2 < 256; ++glyph2) {
                kern += packet.g1s();
                kerns[glyph2] = (byte) kern;
            }
        }
    }

    private void loadVectorFontMetricsFromPacket(Packet packet) {
        sourcePackID = packet.g4s();
        pixelSize = packet.g1();
    }

    public static class FontGlyphInfo {
        public int width;
        public int height;
        public int bearingY;
    }

    public static class FontSheetPosition {
        public int x;
        public int y;
    }

    public static class FontKerningData {
        public byte[][] leftKern;
        public byte[][] rightKern;
    }

    public enum FontSourceType {
        SPRITE_BITMAP,
        SPRITE_FONTSHEET,
        VECTOR,
        INVALID;

        private static final FontSourceType[] entries = FontSourceType.values();

        public static FontSourceType lookup(int id) {
            if (id < 0 || id >= entries.length) {
                throw new IllegalArgumentException("Invalid font source type id: " + id);
            }
            return entries[id];
        }
    }
}
