package alexman.regexgenerator;

public interface BackReference {

    static Construct N_TH_GROUP(int n) {
        return () -> "\\" + n;
    }

    static Construct NAMED_GROUP(String name) {
        return () -> "\\k<" + name + ">";
    }
}
