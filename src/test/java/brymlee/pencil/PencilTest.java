package brymlee.pencil;

import brymlee.pencil.internals.Paper;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;
import static java.util.stream.IntStream.*;
import static com.google.common.collect.FluentIterable.*;
import static java.util.stream.Collectors.*;

public class PencilTest {

    private static void basicWritingAssertion(final String initialText,
                                              final String assertableText,
                                              final Integer degradation,
                                              final String ... textToWrite ){
        final Paper paper = () -> initialText;
        final Pencil pencil = Pencil.create(degradation, paper);
        assertEquals(assertableText, pencil.write(textToWrite).paper().text());
    }

    @Test
    public void canPencilWriteOnABlankPieceOfPaper(){
        basicWritingAssertion("", "Functional programming is very fun.", 100, "Functional programming is very fun.");
    }

    @Test
    public void canPencilWriteOnPaperWithExistingText(){
        basicWritingAssertion("She sells sea shells","She sells sea shells down by the sea shore",100," down by the sea shore");
    }

    @Test
    public void canPencilWriteMultipleIntegersOnSheetOfPaper(){
        final List<String> textList = range(0, 11)
            .mapToObj(integer -> Integer.valueOf(integer))
            .map(integer -> integer.toString())
            .collect(toList());
        final String[] text = from(textList).toArray(String.class);
        basicWritingAssertion("", "012345678910", 100, text);
    }

    @Test
    public void canPencilRunOutOfGraphiteAndOnlyWriteSpaces(){
        basicWritingAssertion("", "Tex ", 4, "Text");
    }
}
