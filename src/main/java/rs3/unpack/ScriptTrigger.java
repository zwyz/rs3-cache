package rs3.unpack;

public enum ScriptTrigger {
    OPWORLDMAPELEMENT1(10, Type.MAPELEMENT, true),
    OPWORLDMAPELEMENT2(11, Type.MAPELEMENT, true),
    OPWORLDMAPELEMENT3(12, Type.MAPELEMENT, true),
    OPWORLDMAPELEMENT4(13, Type.MAPELEMENT, true),
    OPWORLDMAPELEMENT5(14, Type.MAPELEMENT, true),
    WORLDMAPELEMENTMOUSEOVER(15, Type.MAPELEMENT, true),
    WORLDMAPELEMENTMOUSELEAVE(16, Type.MAPELEMENT, true),
    WORLDMAPELEMENTMOUSEREPEAT(17, Type.MAPELEMENT, true),
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
    CLIENTSCRIPT(76),
    LOCSELECT_MOUSEOVER(77, true); // input: [coord] output: []

    public final int id;
    public final Type type;
    public final boolean category;

    ScriptTrigger(int id, Type type, boolean category) {
        this.id = id;
        this.type = type;
        this.category = category;
    }

    ScriptTrigger(int id) {
        this(id, null, false);
    }

    ScriptTrigger(int id, Type type) {
        this(id, type, false);
    }

    ScriptTrigger(int id, boolean category) {
        this(id, null, category);
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
