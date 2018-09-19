package brymlee.pencil.internals;

import brymlee.pencil.Pencil;
import com.google.common.collect.ImmutableList;
import java.util.List;

import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;

@FunctionalInterface
public interface Writer {
    Pencil pencil();

    default Writer write(final List<Character> characters){
        if(characters.size() < 1){
            return this;
        }else if(characters.size() == 1){
            final Pencil pencil = Pencil.pencil(pencil().newPaper(characters),
                                                                  pencil().durability(),
                                                                  pencil().length(),
                                                                  pencil().eraserDurability(),
                                                                  pencil().maxDurability());
            final Writer writer = () -> pencil;
            return writer
                .write(ImmutableList.<Character>of());
        }else{
            final Integer durability = pencil().newDurability(characters.get(0), pencil().durability());
            final List<Character> newCharacters = range(1, characters.size())
                .mapToObj(index -> characters.get(index))
                .collect(toList());
            final Pencil pencil = Pencil.pencil(pencil().newPaper(characters),
                                                                  durability,
                                                                  pencil().length(),
                                                                  pencil().eraserDurability(),
                                                                  pencil().maxDurability());
            final Writer writer = () -> pencil;
            return writer
                .write(newCharacters);
        }
    }

    default Pencil write(final String text){
        final char[] charArray = text.toCharArray();
        final List<Character> characters = range(0, charArray.length)
            .mapToObj(index -> charArray[index])
            .collect(toList());
        return write(characters).pencil();
    }

    default Pencil write(final String[] text){
        final String joinedText = range(0, text.length)
            .mapToObj(index -> text[index])
            .reduce((i, j) -> i.concat(j))
            .get();
        return write(joinedText);
    }
}
