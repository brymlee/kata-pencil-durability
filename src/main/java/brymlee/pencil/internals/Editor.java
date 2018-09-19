package brymlee.pencil.internals;

import brymlee.pencil.Pencil;

import java.util.function.Supplier;

public interface Editor {
    Pencil pencil();
    Integer durability();

    default Character newReplacementCharacter(final Integer startIndex,
                                              final String textToEdit,
                                              final Integer textCount){
        if(hasBeenDegraded()){
            return ' ';
        }else if(currentPaperCharacter(startIndex) == ' '){
            return textToEdit.charAt(textCount);
        }else{
            return '@';
        }
    }

    default Character currentPaperCharacter(final Integer startIndex){
        return pencil().paper().text().charAt(startIndex);
    }

    default Boolean hasBeenDegraded(){
        return pencil().length() <= 0 || pencil().durability() <= 0;
    }

    default String newReplacementCharacters(final Integer startIndex,
                                            final String replacementText,
                                            final String textToEdit,
                                            final Integer textToCount){
        return replacementText + newReplacementCharacter(startIndex, textToEdit, textToCount).toString();
    }

    default Paper newPaper(final Integer startIndex,
                           final String textToEdit,
                           final String replacementText){
        return () -> pencil().paper().text().substring(0, startIndex - textToEdit.length())
                   + replacementText
                   + pencil().paper().text().substring(startIndex);
    }

    default Pencil edit(final Integer startIndex,
                        final Integer textCount,
                        final Integer durability,
                        final String textToEdit,
                        final String replacementText) {
        final Supplier<Paper> newPaper = () -> newPaper(startIndex, textToEdit, replacementText);
        final Supplier<Character> newReplacementCharacter = () -> newReplacementCharacter(startIndex, textToEdit, textCount);
        final Supplier<String> newReplacementCharacters = () -> newReplacementCharacters(startIndex, replacementText, textToEdit, textCount);
        if (textCount >= textToEdit.length()) {
            return Pencil.pencil(newPaper.get(), durability, pencil().length(), pencil().eraserDurability(), pencil().maxDurability());
        } else {
            return edit(startIndex + 1, textCount + 1, pencil().newDurability(newReplacementCharacter.get(), durability), textToEdit, newReplacementCharacters.get());
        }
    }
}
