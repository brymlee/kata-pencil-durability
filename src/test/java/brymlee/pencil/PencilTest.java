package brymlee.pencil;

import brymlee.pencil.internals.Paper;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;
import static java.util.stream.IntStream.*;
import static com.google.common.collect.FluentIterable.*;
import static java.util.stream.Collectors.*;

public class PencilTest {

    private static brymlee.pencil.internals.Pencil basicWritingAssertion(final String initialText,
                                                                         final String assertableText,
                                                                         final Integer durability,
                                                                         final Integer length,
                                                                         final String ... textToWrite ){
        final Paper paper = () -> initialText;
        final brymlee.pencil.internals.Pencil pencil = Pencil.create(durability, paper, length).write(textToWrite);
        assertEquals(assertableText, pencil.paper().text());
        return pencil;
    }

    private static brymlee.pencil.internals.Pencil basicWritingAssertion(final brymlee.pencil.internals.Pencil pencil,
                                                                         final String assertableText,
                                                                         final String ... textToWrite){
        final brymlee.pencil.internals.Pencil newPencil = pencil.write(textToWrite);
        assertEquals(assertableText, newPencil.paper().text());
        return newPencil;
    }

    @Test
    public void canPencilWriteOnABlankPieceOfPaper(){
        basicWritingAssertion("", "Functional programming is very fun.", 100, 1, "Functional programming is very fun.");
    }

    @Test
    public void canPencilWriteOnPaperWithExistingText(){
        basicWritingAssertion("She sells sea shells","She sells sea shells down by the sea shore", 100, 1," down by the sea shore");
    }

    @Test
    public void canPencilWriteMultipleIntegersOnSheetOfPaper(){
        final List<String> textList = range(0, 11)
            .mapToObj(integer -> Integer.valueOf(integer))
            .map(integer -> integer.toString())
            .collect(toList());
        final String[] text = from(textList).toArray(String.class);
        basicWritingAssertion("", "012345678910", 100, 1, text);
    }

    @Test
    public void canPencilRunOutOfGraphiteAndOnlyWriteSpaces(){
        basicWritingAssertion("", "Tex ", 4, 1, "Text");
    }

    @Test
    public void canPencilBeSharpenedAndBeReusedAfterwards(){
        final brymlee.pencil.internals.Pencil pencil = basicWritingAssertion("", "Goo ", 4, 1, "Good").sharpen();
        basicWritingAssertion(pencil, "Goo jell ", "jelly");
    }

    @Test
    public void canPencilBeSharpenedOnlyASetNumberOfTimes(){
        final brymlee.pencil.internals.Pencil sharpenedPencil = basicWritingAssertion("", "abcd ", 4, 1, "abcde").sharpen();
        final brymlee.pencil.internals.Pencil dullPencil = basicWritingAssertion(sharpenedPencil, "abcd fghi ", "fghij").sharpen();
        basicWritingAssertion(dullPencil, "abcd fghi  ", "k");
    }

}
