package brymlee.pencil;

import static java.util.stream.IntStream.*;

@FunctionalInterface
public interface Pencil {
    Paper paper();

    default Pencil write(final String text){
        final Paper paper = () -> paper().text().concat(text);
        return () -> paper;
    }

    default Pencil write(final String[] text){
        final String joinedText = range(0, text.length)
            .mapToObj(index -> text[index])
            .reduce((i, j) -> i.concat(j))
            .get();
        return write(joinedText);
    }
}
