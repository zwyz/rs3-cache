package rs3.unpack;

public enum ScriptTrigger {
    OPWORLDMAPELEMENT1(10, Type.MAPELEMENT),
    OPWORLDMAPELEMENT2(11, Type.MAPELEMENT),
    OPWORLDMAPELEMENT3(12, Type.MAPELEMENT),
    OPWORLDMAPELEMENT4(13, Type.MAPELEMENT),
    OPWORLDMAPELEMENT5(14, Type.MAPELEMENT),
    WORLDMAPELEMENTMOUSEOVER(15, Type.MAPELEMENT),
    WORLDMAPELEMENTMOUSELEAVE(16, Type.MAPELEMENT),
    WORLDMAPELEMENTMOUSEREPEAT(17, Type.MAPELEMENT),
    JCOINS_UPDATED(19),
    CUTSCENE_SUBTITLE(20, Type.CUTSCENE),
    LOYALTY_UPDATED(21),
    PROCESS_NPC(22),
    PROCESS_PLAYER(23),
    IF_PROCESS_ACTIVE_NPC(24, Type.INTERFACE),
    IF_PROCESS_ACTIVE_PLAYER(25, Type.INTERFACE),
    IF_PROCESS_ACTIVE_LOC(26, Type.INTERFACE),
    IF_PROCESS_ACTIVE_OBJ(27, Type.INTERFACE),
    CUTSCENE_END(28),
    TWITCH_EVENT(29, Type.TWITCH_EVENT),
    MINIMENU_EVENT(30, Type.MINIMENU_EVENT),
    PROC(73),
    CLIENTSCRIPT(76);

    public final int id;
    public final Type type;

    ScriptTrigger(int id, Type type) {
        this.id = id;
        this.type = type;
    }

    ScriptTrigger(int id) {
        this(id, null);
    }

    public static ScriptTrigger byID(int id) {
        for (var value : values()) {
            if (value.id == id) {
                return value;
            }
        }

        throw new IllegalArgumentException("unknown id " + id);
    }

    public static boolean isValidID(int id) {
        for (var value : values()) {
            if (value.id == id) {
                return true;
            }
        }

        return false;
    }
}
