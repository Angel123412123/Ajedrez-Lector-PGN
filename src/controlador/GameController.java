package controlador;

import vista.GameInterface;
import modelo.Board;
import modelo.*;
import java.util.List;
import java.util.ArrayList;

public class GameController {
    private Board currentBoard;
    private GameInterface gameUI;
    private ChessNotationProcessor notationProcessor;
    private List<String> moveHistory;
    private int currentMoveIndex;
    private String activeColor;
    private List<BoardState> boardStates;

    public GameController(Board board, GameInterface gui, ChessNotationProcessor processor) {
        this.currentBoard = board;
        this.gameUI = gui;
        this.notationProcessor = processor;
        this.currentMoveIndex = 0;
        this.activeColor = "black";
        this.boardStates = new ArrayList<>();
        saveBoardState();
    }

    private void saveBoardState() {
        boardStates.add(new BoardState(currentBoard, activeColor));
    }

    private static class BoardState {
        private final Board board;
        private final String activeColor;

        public BoardState(Board board, String activeColor) {
            this.board = board.copy();
            this.activeColor = activeColor;
        }
    }

    public void loadGameMoves(String gameText) {
        notationProcessor.parseGameNotation(gameText);
        moveHistory = notationProcessor.getMoveList();
        currentMoveIndex = 0;
    }

    public Board getGameBoard() {
        return currentBoard;
    }

    public boolean executeNextMove() {
        if (moveHistory == null || currentMoveIndex >= moveHistory.size()) {
            return false;
        }

        String move = moveHistory.get(currentMoveIndex);
        System.out.println("Executing move: " + move);

        if (processMove(move)) {
            currentMoveIndex++;
            saveBoardState();
            gameUI.refreshBoard();
            return true;
        } else {
            System.err.println("Invalid move: " + move);
            return false;
        }
    }

    public boolean executePreviousMove() {
        if (currentMoveIndex <= 0 || boardStates.size() <= 1) {
            return false;
        }

        boardStates.remove(boardStates.size() - 1);
        BoardState previousState = boardStates.get(boardStates.size() - 1);
        currentBoard.copyFrom(previousState.board);
        activeColor = previousState.activeColor;
        currentMoveIndex--;

        gameUI.refreshBoard();
        return true;
    }

    public void resetGame() {
        currentBoard = new Board();
        currentMoveIndex = 0;
        moveHistory = null;
        activeColor = "black";
        boardStates.clear();
        saveBoardState();
        gameUI.refreshBoard();
    }

    private boolean processMove(String move) {
        move = move.replaceAll("[+#]", ""); // Remover símbolos de jaque y jaque mate
        System.out.println("Processing move: " + move + " for color: " + activeColor);

        try {
            // Enroques
            if (move.equals("O-O")) {
                return handleCastling("corto");
            } else if (move.equals("O-O-O")) {
                return handleCastling("largo");
            }

            // Identificar componentes del movimiento
            char piece = Character.isUpperCase(move.charAt(0)) ? move.charAt(0) : 'P';
            String destination;
            boolean isCapture = move.contains("x");

            // Extraer destino
            if (piece == 'P') {
                destination = move.length() > 2 ? move.substring(move.length() - 2) : move;
            } else {
                destination = move.substring(move.length() - 2);
            }

            // Convertir coordenadas de destino
            int targetRow = 8 - (destination.charAt(1) - '0');
            int targetCol = destination.charAt(0) - 'a';

            // Buscar pieza que puede realizar el movimiento
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    ChessPiece currentPiece = currentBoard.getPiece(row, col);

                    if (currentPiece != null &&
                            currentPiece.getPieceColor().equals(activeColor) &&
                            getPieceChar(currentPiece) == piece) {

                        if (currentPiece.isValidMove(row, col, targetRow, targetCol, currentBoard)) {
                            if (currentBoard.movePiece(row, col, targetRow, targetCol)) {
                                switchActiveColor();
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing move: " + move);
            e.printStackTrace();
        }
        return false;
    }




    private boolean handleCastling(String type) {
        try {
            String color = activeColor;
            boolean castlingSuccess = currentBoard.attemptCastling(color, type);

            if (castlingSuccess) {
                // Importante: Cambiar el turno después de un enroque exitoso
                switchActiveColor();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




    private char getPieceChar(ChessPiece piece) {
        if (piece instanceof King) return 'K';
        if (piece instanceof Queen) return 'Q';
        if (piece instanceof Rook) return 'R';
        if (piece instanceof Bishop) return 'B';
        if (piece instanceof Knight) return 'N';
        if (piece instanceof Pawn) return 'P';
        return '?';
    }

    private void switchActiveColor() {
        activeColor = activeColor.equals("black") ? "white" : "black";
        System.out.println("Active color switched to: " + activeColor);
    }
}
