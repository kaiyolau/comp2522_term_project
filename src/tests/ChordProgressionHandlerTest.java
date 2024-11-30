import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChordProgressionHandlerTest {
    private ChordProgressionHandler chordHandler;

    @BeforeEach
    void setUp() throws IOException {
        chordHandler = ChordProgressionHandler.getInstance();
    }



    @Test
    void testTransposeChord_InvalidChord() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                chordHandler.transposeChord("Z#m", "C", "D"));
        assertEquals("Invalid chord: Z#m", exception.getMessage());
    }

    @Test
    void testProcessChordLine() {
        String originalLine = "C G Am F";
        ChordProgressionHandler.ChordLine chordLine =
                chordHandler.new ChordLine(originalLine, 1);

        String processedLine = chordHandler.processChordLine(chordLine, "C", "D");
        assertEquals("D A Bm G", processedLine);
    }

    @Test
    void testProcessContent() {
        List<String> content = List.of(
                "key: C",
                "This is a lyric line"
        );

        List<String> processedContent = chordHandler.processContent(content, "D");

        assertEquals("D key", processedContent.get(0));
        assertEquals("This is a lyric line", processedContent.get(1));
    }
}
