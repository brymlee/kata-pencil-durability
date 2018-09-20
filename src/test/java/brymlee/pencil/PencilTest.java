package brymlee.pencil;

import brymlee.pencil.internals.Paper;
import org.junit.Test;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static brymlee.pencil.Pencil.*;
import static org.junit.Assert.*;
import static java.util.stream.IntStream.*;
import static com.google.common.collect.FluentIterable.*;
import static java.util.stream.Collectors.*;
import static java.util.Arrays.*;

public class PencilTest {

    private static Pencil basicWritingAssertion(final String initialText,
                                                final String assertableText,
                                                final Integer durability,
                                                final Integer length,
                                                final Integer eraserDurability,
                                                final String ... textToWrite ){
        final Paper paper = () -> initialText;
        final Pencil pencil = newPencilStatic(paper, durability, length, eraserDurability, durability).write(asList(textToWrite));
        assertEquals(assertableText, pencil.paper().text());
        return pencil;
    }

    private static Pencil basicWritingAssertion(final Pencil pencil,
                                                final String assertableText,
                                                final String ... textToWrite){
        final Pencil newPencil = pencil.write(asList(textToWrite));
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
        final Pencil pencil = basicWritingAssertion("", "Goo ", 4, 1, 0, "Good").sharpen();
        basicWritingAssertion(pencil, "Goo jell ", "jelly");
    }

    @Test
    public void canPencilBeSharpenedOnlyASetNumberOfTimes(){
        final Pencil sharpenedPencil = basicWritingAssertion("", "abcd ", 4, 1, 0, "abcde").sharpen();
        final Pencil dullPencil = basicWritingAssertion(sharpenedPencil, "abcd fghi ", "fghij").sharpen();
        basicWritingAssertion(dullPencil, "abcd fghi  ", "k");
    }

    @Test
    public void canPencilEraseInstancesOfMistakes(){
        final Pencil pencil = basicWritingAssertion("", "good boy good boy", 50, 1, 1000, "good boy good boy");
        assertEquals("good     good    ", pencil.erase("boy").erase("boy").paper().text());
    }

    @Test
    public void canPencilEraseInstancesOfMistakes_woodchuckExample(){
        final Pencil pencil = basicWritingAssertion("", "How much wood would a woodchuck chuck if a woodchuck could chuck wood?", 1000, 1, 1000, "How much wood would a woodchuck chuck if a woodchuck could chuck wood?");
        final Pencil firstChuckErased = pencil.erase("chuck");
        assertEquals("How much wood would a woodchuck chuck if a woodchuck could       wood?", firstChuckErased.paper().text());
        final Pencil secondChuckErased = firstChuckErased.erase("chuck");
        assertEquals("How much wood would a woodchuck chuck if a wood      could       wood?", secondChuckErased.paper().text());
    }

    @Test
    public void canPencilEraserDegradeWhenErasingTooMuch(){
        final Pencil pencil = basicWritingAssertion("", "Buffalo Bill", 1000, 1, 3, "Buffalo Bill");
        assertEquals("Buffalo B   ", pencil.erase("Bill").paper().text());
    }

    @Test
    public void canPencilEditExistingTextWithoutAlwaysAppending(){
        final Pencil pencil = basicWritingAssertion("", "An       a day keeps the doctor away", 1000, 1, 0, "An       a day keeps the doctor away");
        assertEquals("An onion a day keeps the doctor away", pencil.edit(3, "onion").paper().text());
    }

    @Test
    public void canPencilBeRunOffOfMain_createPencilThenDoBasicWrite() throws InvocationTargetException, IllegalAccessException {
        assertEquals("hello", Pencil.runWithMain(asList("newPencil", "1000", "1", "0", "0", "and",
                                                                 "write", "hello"))
            .paper()
            .text());
    }

    @Test
    public void canPencilBeRunOffOfMain_createPencilThenDoWriteThenDoErase() throws InvocationTargetException, IllegalAccessException {
        assertEquals("good    ", Pencil.runWithMain(asList("newPencil", "1000", "1", "10", "10", "and",
                                                                    "write", "good job", "and",
                                                                    "erase", "job"))
            .paper()
            .text());
    }

}
