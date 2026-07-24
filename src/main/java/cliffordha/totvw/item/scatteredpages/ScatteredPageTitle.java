package cliffordha.totvw.item.scatteredpages;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ScatteredPageTitle {

    // group related items together and comment references to a page if needed

    PLAYER_STATS(-2, "PLAYER STATS"),

    SCATTERED_PAGE(-1, "Scattered Page"),

    SP_0(0,"Unknown Page"),
    SP_1000(1000,"Multi-test Page"),


    SP_1001(1001,"An Alchemist's Torn Anecdotes"),
    SP_1002(1002,SP_1001.getTitle() + "#2"),
    SP_1003(1003,SP_1001.getTitle() + "#3"),
    SP_1004(1004,SP_1001.getTitle() + "#4"),

    SP_1005(1005,"A Torn Page"),

    SP_1006(1006, "A Hunter's Torn Anecdotes"),

    SP_1007(1007,"A Poem"),
    SP_1008(1008,"A Poem"),
    SP_1009(1009,"A Poem"),
    SP_1010(1010,"A Poem"),
    SP_1011(1011,"A Poem"),
    SP_1012(1012,"A Poem"),

    LODESTONE_WIND_CORE_MANUAL(333, "Lodestone Wind Core Manual"),
    ;

    private final int id;
    private final String title;

    ScatteredPageTitle(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }

    private static final Map<Integer, ScatteredPageTitle> BY_ID =
            Arrays.stream(values()).collect(Collectors.toMap(ScatteredPageTitle::getId, e -> e));

    public static Optional<ScatteredPageTitle> fromId(int id) {
        return Optional.ofNullable(BY_ID.get(id));
    }
}
