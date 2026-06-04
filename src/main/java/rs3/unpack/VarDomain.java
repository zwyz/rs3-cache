package rs3.unpack;

public enum VarDomain {
    PLAYER(0, Type.VAR_PLAYER, Type.VAR_PLAYER_BIT),
    NPC(1, Type.VAR_NPC, Type.VAR_NPC_BIT),
    CLIENT(2, Type.VAR_CLIENT, Type.VAR_CLIENT_BIT),
    WORLD(3, Type.VAR_WORLD, Type.VAR_WORLD_BIT),
    REGION(4, Type.VAR_REGION, Type.VAR_REGION_BIT),
    OBJECT(5, Type.VAR_OBJECT, Type.VAR_OBJECT_BIT),
    CLAN(6, Type.VAR_CLAN, Type.VAR_CLAN_BIT),
    CLAN_SETTING(7, Type.VAR_CLAN_SETTING, Type.VAR_CLAN_SETTING_BIT),
    CONTROLLER(8, Type.VAR_CONTROLLER, Type.VAR_CONTROLLER_BIT),
    PLAYER_GROUP(9, Type.VAR_PLAYER_GROUP, Type.VAR_PLAYER_GROUP_BIT),
    GLOBAL(10, Type.VAR_GLOBAL, Type.VAR_GLOBAL_BIT);

    public final int id;
    public final Type type;
    public final Type bittype;

    VarDomain(int id, Type type, Type bittype) {
        this.id = id;
        this.type = type;
        this.bittype = bittype;
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
