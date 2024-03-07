package rs3.unpack.script;

import rs3.unpack.Type;

import java.util.List;

import static rs3.unpack.Type.*;

public enum JavaScriptCommand {
    REQUEST_PROFANITY_FILTER(1, List.of(BOOLEAN)),
    SET_LAST_JOINED_CHAT(2, List.of(STRING)),
    IS_MUTED(3, List.of(BOOLEAN)),
    FRIEND_CHAT_NAME(4, List.of(STRING)),
    FRIEND_MIN_ENTER_RANK(5, List.of(INT)),
    FRIEND_MIN_TALK_RANK_REPLY(6, List.of(INT)),
    FRIEND_MIN_KICK_RANK_REPLY(7, List.of(INT)),
    GET_CLAN_STATUS(8, List.of(BOOLEAN, BOOLEAN)),
    REQUEST_ALL_CHAT_FILTERS(9, List.of(INT, INT, INT)),
    REQUEST_PRIVATE_CHAT_FILTER(10, List.of(INT)),
    REQUEST_FRIEND_CHAT_FILTER(11, List.of(INT)),
    REQUEST_CLAN_CHAT_FILTER(12, List.of(INT)),
    GET_CHAT_CROWN_IMAGE_ID(13, List.of(INT)),
    REQUEST_GE_SINGLE_SLOT(14, List.of(INT, OBJ, STRING, STRING, INT, INT, INT, INT, INT, INT, INT, BOOLEAN, OBJ, INT, OBJ, INT)),
    GE_UPDATE_NOTIFICATION(15, List.of(INT, INT, INT)),
    IS_TRADE_RESTRICTED(16, List.of(BOOLEAN)),
    REQUEST_DDS_LIST_ITEM(17, List.of(INT, STRING, INT, INT, INT, BOOLEAN, INT)),
    REQUEST_DD_INFO(18, List.of(BOOLEAN, INT, STRING, INT, INT, INT, BOOLEAN, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING)),
    REQUEST_BANK_TABS(19, List.of(INT, INT, INT, INT, INT, INT, INT, INT, STRING, STRING, STRING, STRING, STRING, STRING, STRING, STRING, INT, INT)),
    REQUEST_BANK_SLOT(20, List.of(INT, OBJ, STRING, INT, BOOLEAN, BOOLEAN, INT)),
    SET_BANK_DETAILS(21, List.of(INV, INT, INV)),
    REQUEST_BANK_SEARCH(22, List.of(INT, OBJ, INT, INT)),
    GET_ITEM_DETAILS(23, List.of(OBJ, STRING, STRING, INT, BOOLEAN, BOOLEAN, INT, BOOLEAN)),
    MAKE_BUY_OFFER(24, List.of(BOOLEAN, INT)),
    MAKE_SELL_OFFER(25, List.of(BOOLEAN, INT)),
    MESSAGE(26, List.of(INT, STRING)),
    ABORT_OFFER(27, List.of(BOOLEAN)),
    COLLECT_SLOT(28, List.of()),
    PIN_ENTERED(29, List.of(BOOLEAN, STRING, INT, BOOLEAN, INT)),
    PIN_STATUS(30, List.of(BOOLEAN, INT, INT, INT)),
    REQUEST_GE_HISTORY(31, List.of(OBJ, INT, INT, STRING, INT, BOOLEAN, OBJ, INT, INT, STRING, INT, BOOLEAN, OBJ, INT, INT, STRING, INT, BOOLEAN, OBJ, INT, INT, STRING, INT, BOOLEAN, OBJ, INT, INT, STRING, INT, BOOLEAN)),
    REQUEST_GE_COMPLETED_TRANSACTION_COUNT(32, List.of(INT)),
    IS_2FACTOR_AUTH_ENABLED(33, List.of(BOOLEAN)),
    IS_COMAPP_TRADING_ENABLED(34, List.of(BOOLEAN)),
    CAN_ACCESS_GE_BUY_SELL(35, List.of(BOOLEAN));

    public final int id;
    public final List<Type> params;

    JavaScriptCommand(int id, List<Type> params) {
        this.id = id;
        this.params = params;
    }

    public static JavaScriptCommand byID(int id) {
        for (var value : values()) {
            if (value.id == id) {
                return value;
            }
        }

        throw new IllegalArgumentException("id " + id);
    }
}
