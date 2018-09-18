package brymlee.pencil.internals;

import com.google.common.collect.ImmutableList;
import java.util.List;

import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;

@FunctionalInterface
public interface WriteTemplate {
    PencilInterface pencil();

    default WriteTemplate write(final List<Character> characters){
        if(characters.size() < 1){
            return this;
        }else if(characters.size() == 1){
            final PencilInterface pencil = PencilInterface.pencil(pencil().newPaper(characters),
                                                                  pencil().durability(),
                                                                  pencil().length(),
                                                                  pencil().eraserDurability(),
                                                                  pencil().maxDurability());
            final WriteTemplate writeTemplate = () -> pencil;
            return writeTemplate
                .write(ImmutableList.<Character>of());
        }else{
            final Integer durability = pencil().newDurability(characters.get(0), pencil().durability());
            final List<Character> newCharacters = range(1, characters.size())
                .mapToObj(index -> characters.get(index))
                .collect(toList());
            final PencilInterface pencil = PencilInterface.pencil(pencil().newPaper(characters),
                                                                  durability,
                                                                  pencil().length(),
                                                                  pencil().eraserDurability(),
                                                                  pencil().maxDurability());
            final WriteTemplate writeTemplate = () -> pencil;
            return writeTemplate
                .write(newCharacters);
        }
    }

    default PencilInterface write(final String text){
        final char[] charArray = text.toCharArray();
        final List<Character> characters = range(0, charArray.length)
            .mapToObj(index -> charArray[index])
            .collect(toList());
        return write(characters).pencil();
    }

    default PencilInterface write(final String[] text){
        final String joinedText = range(0, text.length)
            .mapToObj(index -> text[index])
            .reduce((i, j) -> i.concat(j))
            .get();
        return write(joinedText);
    }
}
