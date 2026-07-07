package cliffordha.totvw.item.scatteredpages;

public enum ScatteredPageTitle {
    SP_1000("Test Title"),
    SP_1001("A Stranger's Torn Anecdotes"),
    SP_1002("A Research Paper"),
    ;

    private final String title;

    ScatteredPageTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
