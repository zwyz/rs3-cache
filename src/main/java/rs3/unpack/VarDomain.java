package rs3.unpack;

public enum VarDomain {
    PLAYER(0),
    NPC(1),
    CLIENT(2),
    WORLD(3),
    REGION(4),
    OBJECT(5),
    CLAN(6),
    CLAN_SETTING(7),
    CONTROLLER(8),
    PLAYER_GROUP(9),
    SHARED(10),
    ;

    public final int id;

    VarDomain(int id) {
        this.id = id;
    }

    public static VarDomain byID(int id) {
        for (var domain : values()) {
            if (domain.id == id) {
                return domain;
            }
        }

        throw new IllegalStateException("unknown var domain " + id);
    }
}
