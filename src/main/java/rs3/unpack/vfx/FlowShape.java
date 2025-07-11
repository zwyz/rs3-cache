package rs3.unpack.vfx;

import rs3.unpack.Vector3;
import rs3.util.Packet;

public class FlowShape {
    public FlowShapeType kind;
    public boolean unknown1;
    public Vector3 position;
    public Vector3 rotation;
    public float length;
    public float height;
    public float width;

    public FlowShape(Packet packet) {
        int kindID = packet.g1();

        kind = switch (kindID) {
            case 0 -> FlowShapeType.NONE;
            case 1 -> FlowShapeType.LINE;
            case 2 -> FlowShapeType.PLANE;
            case 3 -> FlowShapeType.BOX;
            case 4 -> FlowShapeType.DISC;
            case 5 -> FlowShapeType.CONE;
            case 6 -> FlowShapeType.SPHERE;
            case 7 -> FlowShapeType.HEMISPHERE;
            default -> throw new IllegalStateException("unknown kind " + kindID);
        };

        unknown1 = packet.g1() == 1;
        position = new Vector3(packet);
        rotation = new Vector3(packet);

        switch (kind) {
            case NONE -> {
                // no extra data
            }

            case LINE -> {
                length = packet.gFloat();
            }

            case PLANE -> {
                width = packet.gFloat();
                length = packet.gFloat();
            }

            case BOX -> {
                width = packet.gFloat();
                height = packet.gFloat();
                length = packet.gFloat();
            }

            case DISC, SPHERE, HEMISPHERE -> {
                length = packet.gFloat();
            }

            case CONE -> {
                height = packet.gFloat();
                length = packet.gFloat();
                width = packet.gFloat();
            }

            default -> {
                throw new IllegalStateException("unknown type " + kind);
            }
        }
    }

    public enum FlowShapeType {
        NONE,
        LINE,
        PLANE,
        BOX,
        DISC,
        CONE,
        SPHERE,
        HEMISPHERE
    }
}
