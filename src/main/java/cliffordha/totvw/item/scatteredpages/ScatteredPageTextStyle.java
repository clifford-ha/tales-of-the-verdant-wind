package cliffordha.totvw.item.scatteredpages;

public enum ScatteredPageTextStyle {
    BOLD("§l"),
    ITALIC("§o"),
    UNDERLINED("§n"),
    STRIKETHROUGH("§m"),
    ;

    private final String marker;

    ScatteredPageTextStyle(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return this.marker;
    }
}
