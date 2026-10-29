package cliffordha.totvw.item.pages;

public enum FTextStyle {
    BOLD("§l"),
    ITALIC("§o"),
    UNDERLINED("§n"),
    STRIKETHROUGH("§m"),
    ;

    private final String marker;

    FTextStyle(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return this.marker;
    }
}
