package brymlee.pencil;

import org.junit.Test;

import static org.junit.Assert.*;

public class PencilTest {
    @Test
    public void canPencilWriteOnABlankPieceOfPaper(){
        final Paper paper = () -> "";
        final Pencil pencil = () -> paper;
        final String text = "Functional programming is very fun.";
        assertEquals(text, pencil.write(text).paper().text());
    }
}
