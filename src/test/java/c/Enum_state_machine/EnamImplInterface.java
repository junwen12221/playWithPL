package c.Enum_state_machine;

/**
 * Created by islabukhin on 29.09.16.
 */
public interface EnamImplInterface {
    public String name();

    public int order();
}

enum Planets implements EnamImplInterface {
    Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, Neptune;

    // name() is implemented automagically.
    public int order() {
        return ordinal() + 1;
    }

}

 class EnamImplInterfaceRunner{
    public static void main(String[] args) {
        System.out.println(Planets.Mars.order()+"/"+Planets.Mars.name());
    }
}