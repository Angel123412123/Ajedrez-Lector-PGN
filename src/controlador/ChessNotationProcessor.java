package controlador;
import java.util.ArrayList;
import java.util.List;

public class ChessNotationProcessor {
    private List<String> moveSequence;

    public ChessNotationProcessor() {
        this.moveSequence = new ArrayList<>();
    }

    public void parseGameNotation(String gameText) {
        moveSequence.clear();

        String[] textLines = gameText.split("\n");
        StringBuilder moveBuilder = new StringBuilder();

        for (String currentLine : textLines) {
            if (!currentLine.startsWith("[")) {
                moveBuilder.append(currentLine).append(" ");
            }
        }

        String cleanedText = moveBuilder.toString()
                .replaceAll("\\d+\\.\\s*", "")
                .replaceAll("\\s+", " ")
                .trim();

        String[] individualMoves = cleanedText.split("\\s+");

        for (String move : individualMoves) {
            if (move.matches("1-0|0-1|1/2-1/2")) {
                continue;
            }

            if (isCastlingMove(move)) {
                moveSequence.add(move);
            } else {
                moveSequence.add(move);
            }
        }
    }

    private boolean isCastlingMove(String move) {
        return move.equals("O-O") || move.equals("O-O-O");
    }

    public List<String> getMoveList() {
        return moveSequence;
    }
}