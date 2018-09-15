package brymlee.pencil.internals;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Stack;

import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
import static org.apache.commons.lang3.StringUtils.*;

public interface PencilInterface {

    Paper paper();
    Integer durability();
    Integer maxDurability();
    Integer length();

    default PencilInterface pencil(final Paper paper,
                                   final Integer durability,
                                   final Integer length){
        final Integer maxDurability = maxDurability();
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
        };
    }

    default Integer newDurability(final Character character){
        if(character.toString().trim().equals("")){
            return durability();
        }else if(isNumeric(character.toString())
              || isAllLowerCase(character.toString())){
            return durability() - 1;
        }else if(isAllUpperCase(character.toString())){
            return durability() - 2;
        }else if(!isAlphanumeric(character.toString())) {
            return durability() - 1;
        }else{
            throw new IllegalArgumentException("This pencil cannot write the character inputted. You must only write spaces or alphanumeric characters.");
        }
    }

    default Paper newPaper(final List<Character> characters){
        final Supplier<Paper> paperWithAddedSpace = () -> () -> paper().text().concat(" ");
        final Boolean hasDegradated = !newDurability(characters.get(0)).equals(durability());
        final Boolean isCompletelyDegradated = Integer.valueOf(0).equals(durability());
        if(isCompletelyDegradated){
            return paperWithAddedSpace.get();
        }else if(hasDegradated){
            return () -> paper().text().concat(characters.get(0).toString());
        }else{
            return paperWithAddedSpace.get();
        }
    }

    default PencilInterface write(final List<Character> characters){
        if(characters.size() < 1){
            return this;
        }else if(characters.size() == 1){
            return pencil(newPaper(characters), 0, length())
                .write(ImmutableList.<Character>of());
        }else{
            final Integer durability = newDurability(characters.get(0));
            final List<Character> newCharacters = range(1, characters.size())
                .mapToObj(index -> characters.get(index))
                .collect(toList());
            return pencil(newPaper(characters), durability, length())
                .write(newCharacters);
        }
    }

    default PencilInterface write(final String text){
        final char[] charArray = text.toCharArray();
        final List<Character> characters = range(0, charArray.length)
            .mapToObj(index -> charArray[index])
            .collect(toList());
        return write(characters);
    }

    default PencilInterface write(final String[] text){
        final String joinedText = range(0, text.length)
            .mapToObj(index -> text[index])
            .reduce((i, j) -> i.concat(j))
            .get();
        return write(joinedText);
    }

    default PencilInterface sharpen(){
        if(length() > 0){
            return pencil(paper(), maxDurability(), length() - 1);
        }else{
            return pencil(paper(), durability(), length());
        }
    }

    default PencilInterface erase(final String textToErase){
        if(paper().text().contains(textToErase)){
            return erase(paper().text().length() - 1, textToErase, "", ImmutableList.of());
        }else{
            return pencil(paper(), durability(), length());
        }
    }

    default PencilInterface erase(final Integer index,
                                  final String textToErase,
                                  final String buffer,
                                  final List<Integer> indexBuffer){
        final Supplier<String> newBuffer = () -> buffer.concat(paper().text().substring(index, index + 1));
        final Supplier<List<Integer>> newIndexBuffer = () -> ImmutableList.<Integer>builder()
            .addAll(indexBuffer)
            .add(index)
            .build();
        final Supplier<String> newText = () -> range(0, paper().text().length())
            .mapToObj(i -> i)
            .map(i -> {
                if(indexBuffer.stream().anyMatch(j -> i.equals(j))){
                    return ' ';
                }else{
                    return paper().text().charAt(i);
                }
            }).map(character -> character.toString())
            .reduce((i, j) -> i.concat(j))
            .get();
        if(buffer.length() == textToErase.length()
        || index < 0){
            return pencil(() -> newText.get(), durability(), length());
        }else if(isBufferTextSubsetOfTextToErase(newBuffer.get(), textToErase)){
            return erase(index - 1, textToErase, newBuffer.get(), newIndexBuffer.get());
        }else{
            return erase(index - 1, textToErase, "", indexBuffer);
        }
    }

    static Boolean isBufferTextSubsetOfTextToErase(final String bufferText, final String textToErase){
        final Supplier<String> reversedBufferText = () -> reverse(bufferText);
        final Function<Integer, Integer> bufferIndex = index -> reversedBufferText.get().length() - 1 - index;
        final Function<Integer, Integer> textToEraseIndex = index -> textToErase.length() - 1 - index;
        return range(0, bufferText.length())
            .allMatch(index -> reversedBufferText.get().charAt(bufferIndex.apply(index)) == textToErase.charAt(textToEraseIndex.apply(index)));
    }
}