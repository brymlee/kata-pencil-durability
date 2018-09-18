package brymlee.pencil.internals;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
import static org.apache.commons.lang3.StringUtils.*;

public interface PencilInterface {

    Paper paper();
    Integer durability();
    Integer maxDurability();
    Integer length();
    Integer eraserDurability();

    static PencilInterface pencil(final Paper paper,
                                  final Integer durability,
                                  final Integer length,
                                  final Integer eraserDurability,
                                  final Integer maxDurability){
        return new PencilInterface(){
            @Override
            public Paper paper() {
                return paper;
            }

            @Override
            public Integer durability() {
                return durability;
            }

            @Override
            public Integer maxDurability() {
                return maxDurability;
            }

            @Override
            public Integer length() {
                return length;
            }

            @Override
            public Integer eraserDurability() {
                return eraserDurability;
            }
        };
    }

    default Integer newDurability(final Character character,
                                 final Integer durability){
        if(character.toString().trim().equals("")){
            return durability;
        }else if(isNumeric(character.toString())
              || isAllLowerCase(character.toString())){
            return durability - 1;
        }else if(isAllUpperCase(character.toString())){
            return durability - 2;
        }else if(!isAlphanumeric(character.toString())) {
            return durability - 1;
        }else{
            throw new IllegalArgumentException("This pencil cannot write the character inputted. You must only write spaces or alphanumeric characters.");
        }
    }

    default Paper newPaper(final List<Character> characters){
        final Supplier<Paper> paperWithAddedSpace = () -> () -> paper().text().concat(" ");
        final Boolean hasDegradated = !newDurability(characters.get(0), durability()).equals(durability());
        final Boolean isCompletelyDegradated = Integer.valueOf(0).equals(durability());
        if(isCompletelyDegradated){
            return paperWithAddedSpace.get();
        }else if(hasDegradated){
            return () -> paper().text().concat(characters.get(0).toString());
        }else{
            return paperWithAddedSpace.get();
        }
    }


    default PencilInterface write(final String[] text){
        final WriteTemplate writeTemplate = () -> this;
        return writeTemplate.write(text);
    }

    default PencilInterface sharpen(){
        if(length() > 0){
            return pencil(paper(), maxDurability(), length() - 1, eraserDurability(), maxDurability());
        }else{
            return pencil(paper(), durability(), length(), eraserDurability(), maxDurability());
        }
    }

    default PencilInterface erase(final String textToErase){
        if(paper().text().contains(textToErase)){
            return erase(paper().text().length() - 1, textToErase, "", ImmutableList.of(), eraserDurability());
        }else{
            return pencil(paper(), durability(), length(), eraserDurability(), maxDurability());
        }
    }

    default PencilInterface erase(final Integer index,
                                  final String textToErase,
                                  final String buffer,
                                  final List<Integer> indexBuffer,
                                  final Integer eraserDurability){
        final Supplier<String> newBuffer = () -> buffer.concat(paper().text().substring(index, index + 1));
        final Supplier<List<Integer>> newIndexBuffer = () -> ImmutableList.<Integer>builder()
            .addAll(indexBuffer)
            .add(index)
            .build();
        final Supplier<Integer> newEraserDurability = () -> paper().text().charAt(index) == ' ' ? eraserDurability : eraserDurability - 1;
        final Supplier<String> newText = () -> range(0, paper().text().length())
            .mapToObj(i -> i)
            .map(i -> {
                if(i.equals(index)
                && newEraserDurability.get().equals(eraserDurability)){
                    return paper().text().charAt(i);
                }else if(indexBuffer.stream().anyMatch(j -> i.equals(j))){
                    return ' ';
                }else{
                    return paper().text().charAt(i);
                }
            }).map(character -> character.toString())
            .reduce((i, j) -> i.concat(j))
            .get();
        if(buffer.length() == textToErase.length()
        || index < 0
        || eraserDurability < 1){
            return pencil(() -> newText.get(), durability(), length(), eraserDurability, maxDurability());
        }else if(isBufferTextSubsetOfTextToErase(newBuffer.get(), textToErase)){
            return erase(index - 1, textToErase, newBuffer.get(), newIndexBuffer.get(), newEraserDurability.get());
        }else{
            return erase(index - 1, textToErase, "", indexBuffer, eraserDurability);
        }
    }

    static Boolean isBufferTextSubsetOfTextToErase(final String bufferText,
                                                   final String textToErase){
        final Supplier<String> reversedBufferText = () -> reverse(bufferText);
        final Function<Integer, Integer> bufferIndex = index -> reversedBufferText.get().length() - 1 - index;
        final Function<Integer, Integer> textToEraseIndex = index -> textToErase.length() - 1 - index;
        return range(0, bufferText.length())
            .allMatch(index -> reversedBufferText.get().charAt(bufferIndex.apply(index)) == textToErase.charAt(textToEraseIndex.apply(index)));
    }

    default PencilInterface edit(final Integer startIndex,
                                 final String textToEdit){
        final PencilInterface pencil = this;
        return new EditTemplate(){
            @Override
            public PencilInterface pencil() {
                return pencil;
            }

            @Override
            public Integer durability() {
                return durability();
            }
        }.edit(startIndex, 0, durability(), textToEdit, "");
    }
}
