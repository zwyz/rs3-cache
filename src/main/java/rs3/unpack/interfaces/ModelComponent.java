package rs3.unpack.interfaces;

import rs3.Unpack;
import rs3.util.Packet;

public class ModelComponent extends Component {
    public int model;
    public int modelactive = -1;
    public boolean hasmodelorigin;
    public boolean hasmodelorigin_z;
    public int modelorigin_x;
    public int modelorigin_y;
    public int modelorigin_z;
    public int modelangle_x;
    public int modelangle_y;
    public int modelangle_z;
    public int modelzoom;
    public int modelanim;
    public int modelanimactive = -1;
    public boolean modelprecisezoom;
    public boolean modelorthog;
    public boolean modelnodepth;
    public int unknown100;
    public int unknown101;
    public boolean unknown103;
    public int modelobjwidth;
    public int modelobjheight;

    @Override
    protected void decodeSpecific(Packet packet) {
        if (version == -2) {
            if (Unpack.VERSION < 400) {
                model = packet.g2special();
                modelactive = packet.g2special();
                modelanim = packet.g2special();
                modelanimactive = packet.g2special();
            } else {
                model = packet.g2null();
                modelactive = packet.g2null();
                modelanim = packet.g2null();
                modelanimactive = packet.g2null();
            }
            modelzoom = packet.g2();
            modelangle_x = packet.g2();
            modelangle_y = packet.g2();
            return;
        }

        model = Unpack.VERSION < 681 ? packet.g2null() : packet.gSmart2or4null();

        if (Unpack.VERSION < 619) {
            hasmodelorigin = true;
            modelorigin_x = packet.g2s();
            modelorigin_y = packet.g2s();
            modelangle_x = packet.g2();
            modelangle_y = packet.g2();
            modelangle_z = packet.g2();
            modelzoom = packet.g2();
            modelanim = Unpack.VERSION < 681 ? packet.g2null() : packet.gSmart2or4null();
            modelorthog = packet.g1() == 1;

            if (Unpack.VERSION >= 493) {
                unknown100 = packet.g2();
            }

            if (Unpack.VERSION >= 501) {
                unknown101 = packet.g2();
                unknown103 = packet.g1() == 1;
            }
        } else {
            var flags = packet.g1();
            modelprecisezoom = (flags & 2) != 0;
            modelorthog = (flags & 4) != 0;
            modelnodepth = (flags & 8) != 0;

            if ((flags & 1) != 0) {
                hasmodelorigin = true;
                modelorigin_x = packet.g2s();
                modelorigin_y = packet.g2s();
                modelangle_x = packet.g2();
                modelangle_y = packet.g2();
                modelangle_z = packet.g2();
                modelzoom = packet.g2();
            } else if ((flags & 2) != 0) {
                hasmodelorigin = true;
                hasmodelorigin_z = true;
                modelorigin_x = packet.g2s();
                modelorigin_y = packet.g2s();
                modelorigin_z = packet.g2s();
                modelangle_x = packet.g2();
                modelangle_y = packet.g2();
                modelangle_z = packet.g2();
                modelzoom = packet.g2();
            }

            modelanim = Unpack.VERSION < 681 ? packet.g2null() : packet.gSmart2or4null();
        }

        if (widthmode != 0) {
            modelobjwidth = packet.g2();
        }

        if (heightmode != 0) {
            modelobjheight = packet.g2();
        }
    }
}
