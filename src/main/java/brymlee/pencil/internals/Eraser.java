package brymlee.pencil.internals;

import brymlee.pencil.Pencil;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.IntStream.*;
import static org.apache.commons.lang3.StringUtils.*;

public interface Eraser {
    Pencil pencil();

    default Pencil erase(final String textToErase){
        if(pencil().paper().text().contains(textToErase)){
            return erase(pencil().paper().text().length() - 1, textToErase, "", ImmutableList.of(), pencil().eraserDurability());
        }else{
            return pencil().newPencil(pencil().paper(), pencil().durability(), pencil().length(), pencil().eraserDurability(), pencil().maxDurability());
        }
    }

    static String characterToString(final Character character){
        return character.toString();
    }

    static String concatenate(final String i,
                              final String j){
        return i.concat(j);
    }

    static <T> T passThrough(final T t){
        return t;
    }

    default Function<Integer, Character> toNewCharacterAtIndex(final Integer paperTextIndex,
                                                               final Supplier<Integer> newEraserDurability,
                                                               final Integer eraserDurability,
                                                               final List<Integer> indexBuffer){
        return textToEditIndex -> {
            if(textToEditIndex.equals(paperTextIndex)
            && newEraserDurability.get().equals(eraserDurability)){
                return pencil().paper().text().charAt(textToEditIndex);
            }else if(indexBuffer.stream().anyMatch(j -> textToEditIndex.equals(j))){
                return ' ';
            }else{
                return pencil().paper().text().charAt(textToEditIndex);
            }
        };
    }

    default Pencil erase(final Integer index,
                         final String textToErase,
                         final String buffer,
                         final List<Integer> indexBuffer,
                         final Integer eraserDurability){
        final Supplier<String> newBuffer = () -> buffer.concat(pencil().paper().text().substring(index, index + 1));
        final Supplier<List<Integer>> newIndexBuffer = () -> ImmutableList.<Integer>builder()
            .addAll(indexBuffer)
            .add(index)
            .build();
        final Supplier<Integer> newEraserDurability = () -> pencil().paper().text().charAt(index) == ' ' ? eraserDurability : eraserDurability - 1;
        final Supplier<String> newText = () -> range(0, pencil().paper().text().length())
            .mapToObj(Eraser::passThrough)
            .map(toNewCharacterAtIndex(index, newEraserDurability, eraserDurability, indexBuffer))
            .map(Eraser::characterToString)
            .reduce(Eraser::concatenate)
            .get();
        if(buffer.length() == textToErase.length()
        || index < 0
        || eraserDurability < 1){
            return pencil().newPencil(() -> newText.get(), pencil().durability(), pencil().length(), eraserDurability, pencil().maxDurability());
        }else if(isBufferTextSubsetOfTextToErase(newBuffer.get(), textToErase)){
            return erase(index - 1, textToErase, newBuffer.get(), newIndexBuffer.get(), newEraserDurability.get());
        }else{
            return erase(index - 1, textToErase, "", indexBuffer, eraserDurability);
        }
    }

    default Boolean isBufferTextSubsetOfTextToErase(final String bufferText,
                                                    final String textToErase){
        final Supplier<String> reversedBufferText = () -> reverse(bufferText);
        final Function<Integer, Integer> bufferIndex = index -> reversedBufferText.get().length() - 1 - index;
        final Function<Integer, Integer> textToEraseIndex = index -> textToErase.length() - 1 - index;
        return range(0, bufferText.length())
            .allMatch(index -> reversedBufferText.get().charAt(bufferIndex.apply(index)) == textToErase.charAt(textToEraseIndex.apply(index)));
    }
}
