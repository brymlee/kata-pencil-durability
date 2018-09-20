package brymlee.pencil;

import brymlee.pencil.internals.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.*;
import static com.google.common.collect.FluentIterable.*;

public interface Pencil {

    Paper paper();
    Integer durability();
    Integer maxDurability();
    Integer length();
    Integer eraserDurability();

    static Pencil newPencilStatic(final Paper paper,
                                  final Integer durability,
                                  final Integer length,
                                  final Integer eraserDurability,
                                  final Integer maxDurability){
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

    default Pencil newPencil(final Paper paper,
                             final Integer durability,
                             final Integer length,
                             final Integer eraserDurability,
                             final Integer maxDurability){
        return newPencilStatic(paper, durability, length, eraserDurability, maxDurability);
    }

    default Pencil newPencil(final Integer durability,
                             final Integer length,
                             final Integer eraserDurability,
                             final Integer maxDurability){
        return newPencil(() -> "", durability, length, eraserDurability, maxDurability);
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

    default Pencil write(final List<String> text){
        final Writer writer = () -> this;
        return writer.write(from(text).toArray(String.class));
    }

    default Pencil sharpen(){
        final Sharpener sharpener = () -> this;
        return sharpener.sharpen();
    }

    default Pencil erase(final String textToErase){
        final Eraser eraser = () -> this;
        return eraser.erase(textToErase);
    }

    default Pencil edit(final Integer startIndex,
                        final String textToEdit){
        final Pencil pencil = this;
        return Editor
            .newEditor(pencil, durability())
            .edit(startIndex, 0, durability(), textToEdit, "");

    }

    static Pencil runWithMain(final List<String> arguments) {
        return PencilCliApplication.runWithMain(arguments);
    }
}
