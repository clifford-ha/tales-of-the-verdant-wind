package cliffordha.totvw.item.scatteredpages;

public enum TextFormatterEnum {
    BOLD("§l"),
    ITALIC("§o"),
    UNDERLINED("§n"),
    STRIKETHROUGH("§m"),
    ;

    private final String marker;

    TextFormatterEnum(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return this.marker;
    }
}
