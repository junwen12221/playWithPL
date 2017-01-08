package c;

interface Named {
    public String name();
    public int order();
}

enum Planets implements Named {
    Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, Neptune;
    // name() is implemented automagically.
    public int order() { return ordinal()+1; }
}