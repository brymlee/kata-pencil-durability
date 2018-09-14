package brymlee.pencil;

@FunctionalInterface
public interface Pencil {
    Paper paper();

    default Pencil write(final String text){
        final Paper paper = () -> paper().text().concat(text);
        return () -> paper;
    }
}
