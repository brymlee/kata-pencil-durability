package brymlee.pencil.internals;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
import static org.apache.commons.lang3.StringUtils.*;

public interface Pencil {

    Paper paper();
    Integer durability();
    Integer maxDurability();

    default Pencil pencil(final Paper paper,
                          final Integer durability){
        final Integer maxDurability = maxDurability();
        return new Pencil(){
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

    default Pencil write(final List<Character> characters){
        if(characters.size() < 1){
            return this;
        }else if(characters.size() == 1){
            return pencil(newPaper(characters), 0)
                .write(ImmutableList.<Character>of());
        }else{
            final Integer durability = newDurability(characters.get(0));
            final List<Character> newCharacters = range(1, characters.size())
                .mapToObj(index -> characters.get(index))
                .collect(toList());
            return pencil(newPaper(characters), durability)
                .write(newCharacters);
        }
    }

    default Pencil write(final String text){
        final char[] charArray = text.toCharArray();
        final List<Character> characters = range(0, charArray.length)
            .mapToObj(index -> charArray[index])
            .collect(toList());
        return write(characters);
    }

    default Pencil write(final String[] text){
        final String joinedText = range(0, text.length)
            .mapToObj(index -> text[index])
            .reduce((i, j) -> i.concat(j))
            .get();
        return write(joinedText);
    }

    default Pencil sharpen(){
        return pencil(paper(), maxDurability());
    }
}
