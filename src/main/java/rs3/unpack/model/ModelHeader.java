package rs3.unpack.model;

import rs3.util.Packet;

// (position, normal, tangent, texture, label, ???, colour, alpha, facelabel)
public class ModelHeader {
    public int[] vertexX;
    public int[] vertexY;
    public int[] vertexZ;
    public int[] vertexNormalX;
    public int[] vertexNormalY;
    public int[] vertexNormalZ;
    public int[] vertexTangentX;
    public int[] vertexTangentY;
    public int[] vertexTangentZ;
    public int[] vertexTangentW;
    public int[] vertexU;
    public int[] vertexV;
    public int[] vertexLabel;
    public int[][] vertexUnknown1;
    public int[][] vertexUnknown2;
    public int[] vertexColours;
    public int[] vertexTrans;
    public int[] vertexFaceLabel;

    public ModelHeader(Packet packet) {
        var flags = packet.g4sLE();
        var hasVertices = (flags & 1) != 0;
        var hasFaceAlpha = (flags & 2) != 0;
        var hasFaceLabels = (flags & 4) != 0;
        var hasVertexLabels = (flags & 8) != 0;
        var hidden = (flags & 16) != 0;
        var hasSkin = (flags & 32) != 0;

        var vertexCount = packet.g4sLE();

        vertexX = new int[vertexCount];
        vertexY = new int[vertexCount];
        vertexZ = new int[vertexCount];

        for (var i = 0; i < vertexX.length; i++) {
            vertexX[i] = packet.g2LE();
            vertexY[i] = packet.g2LE();
            vertexZ[i] = packet.g2LE();
        }

        vertexNormalX = new int[vertexCount];
        vertexNormalY = new int[vertexCount];
        vertexNormalZ = new int[vertexCount];

        for (var i = 0; i < vertexX.length; i++) {
            vertexNormalX[i] = packet.g1();
            vertexNormalY[i] = packet.g1();
            vertexNormalZ[i] = packet.g1();
        }

        vertexTangentX = new int[vertexCount];
        vertexTangentY = new int[vertexCount];
        vertexTangentZ = new int[vertexCount];
        vertexTangentW = new int[vertexCount];

        for (var i = 0; i < vertexCount; i++) {
            vertexTangentX[i] = packet.g1();
            vertexTangentY[i] = packet.g1();
            vertexTangentZ[i] = packet.g1();
            vertexTangentW[i] = packet.g1();
        }

        vertexU = new int[vertexCount];
        vertexV = new int[vertexCount];

        for (var vertex = 0; vertex < vertexCount; vertex++) {
            vertexU[vertex] = packet.g2LE();
            vertexV[vertex] = packet.g2LE();
        }

        if (hasVertexLabels) {
            vertexLabel = new int[vertexCount];

            for (var i = 0; i < vertexCount; i++) {
                vertexLabel[i] = packet.g2LE();
            }
        }

        if (hasSkin) {
            vertexUnknown1 = new int[vertexCount][];
            vertexUnknown2 = new int[vertexCount][];

            for (var i = 0; i < vertexCount; i++) {
                var count = packet.g2LE();
                vertexUnknown1[i] = new int[count];

                for (var j = 0; j < count; j++) {
                    vertexUnknown1[i][j] = packet.g2LE();
                }

                var count2 = packet.g2LE();
                vertexUnknown2[i] = new int[count2];

                for (var j = 0; j < count2; j++) {
                    vertexUnknown2[i][j] = packet.g1();
                }
            }
        }

        if (hasVertices) {
            vertexColours = new int[vertexCount];

            for (var i = 0; i < vertexCount; i++) {
                vertexColours[i] = packet.g2LE();
            }
        }

        vertexTrans = new int[vertexCount];

        for (var i = 0; i < vertexCount; i++) {
            vertexTrans[i] = packet.g1();
        }

        if (hasFaceLabels) {
            vertexFaceLabel = new int[vertexCount];

            for (var i = 0; i < vertexCount; i++) {
                vertexFaceLabel[i] = packet.g2LE();
            }
        }
    }
}
