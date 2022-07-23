package alexman.regexgenerator;

public interface Construct {

    default Construct APPEND(Construct other) {
        return () -> this.toRegex() + other.toRegex();
    }

    String toRegex();

}
