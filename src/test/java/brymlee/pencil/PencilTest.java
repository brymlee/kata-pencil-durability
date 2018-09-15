package brymlee.pencil;

import brymlee.pencil.internals.Paper;
import brymlee.pencil.internals.PencilInterface;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;
import static java.util.stream.IntStream.*;
import static com.google.common.collect.FluentIterable.*;
import static java.util.stream.Collectors.*;

public class PencilTest {

    private static PencilInterface basicWritingAssertion(final String initialText,
                                                         final String assertableText,
                                                         final Integer durability,
                                                         final Integer length,
                                                         final Integer eraserDurability,
                                                         final String ... textToWrite ){
        final Paper paper = () -> initialText;
        final PencilInterface pencil = Pencil.create(durability, paper, length, eraserDurability).write(textToWrite);
        assertEquals(assertableText, pencil.paper().text());
        return pencil;
    }

    private static PencilInterface basicWritingAssertion(final PencilInterface pencil,
                                                         final String assertableText,
                                                         final String ... textToWrite){
        final PencilInterface newPencil = pencil.write(textToWrite);
        assertEquals(assertableText, newPencil.paper().text());
        return newPencil;
    }

    @Test
    public void canPencilWriteOnABlankPieceOfPaper(){
        basicWritingAssertion("", "Functional programming is very fun.", 100, 1, 0,"Functional programming is very fun.");
    }

    @Test
    public void canPencilWriteOnPaperWithExistingText(){
        basicWritingAssertion("She sells sea shells","She sells sea shells down by the sea shore", 100, 1, 0, " down by the sea shore");
    }

    @Test
    public void canPencilWriteMultipleIntegersOnSheetOfPaper(){
        final List<String> textList = range(0, 11)
            .mapToObj(integer -> Integer.valueOf(integer))
            .map(integer -> integer.toString())
            .collect(toList());
        final String[] text = from(textList).toArray(String.class);
        basicWritingAssertion("", "012345678910", 100, 1, 0, text);
    }

    @Test
    public void canPencilRunOutOfGraphiteAndOnlyWriteSpaces(){
        basicWritingAssertion("", "Tex ", 4, 1, 0, "Text");
    }

    @Test
    public void canPencilBeSharpenedAndBeReusedAfterwards(){
        final PencilInterface pencil = basicWritingAssertion("", "Goo ", 4, 1, 0, "Good").sharpen();
        basicWritingAssertion(pencil, "Goo jell ", "jelly");
    }

    @Test
    public void canPencilBeSharpenedOnlyASetNumberOfTimes(){
        final PencilInterface sharpenedPencil = basicWritingAssertion("", "abcd ", 4, 1, 0, "abcde").sharpen();
        final PencilInterface dullPencil = basicWritingAssertion(sharpenedPencil, "abcd fghi ", "fghij").sharpen();
        basicWritingAssertion(dullPencil, "abcd fghi  ", "k");
    }

    @Test
    public void canPencilEraseInstancesOfMistakes(){
        final PencilInterface pencil = basicWritingAssertion("", "good boy good boy", 50, 1, 1000, "good boy good boy");
        assertEquals("good     good    ", pencil.erase("boy").erase("boy").paper().text());
    }

    @Test
    public void canPencilEraseInstancesOfMistakes_woodchuckExample(){
        final PencilInterface pencil = basicWritingAssertion("", "How much wood would a woodchuck chuck if a woodchuck could chuck wood?", 1000, 1, 1000, "How much wood would a woodchuck chuck if a woodchuck could chuck wood?");
        final PencilInterface firstChuckErased = pencil.erase("chuck");
        assertEquals("How much wood would a woodchuck chuck if a woodchuck could       wood?", firstChuckErased.paper().text());
        final PencilInterface secondChuckErased = firstChuckErased.erase("chuck");
        assertEquals("How much wood would a woodchuck chuck if a wood      could       wood?", secondChuckErased.paper().text());
    }

    @Test
    public void canPencilEraserDegradeWhenErasingTooMuch(){
        final PencilInterface pencil = basicWritingAssertion("", "Buffalo Bill", 1000, 1, 3, "Buffalo Bill");
        assertEquals("Buffalo B   ", pencil.erase("Bill").paper().text());
    }

}
