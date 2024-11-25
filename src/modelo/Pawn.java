package modelo;

public class Pawn extends ChessPiece {
    public Pawn(String color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Board board) {
        int direction = color.equals("white") ? 1 : -1;
        int rowDiff = endRow - startRow;
        int colDiff = Math.abs(endCol - startCol);

        // Movimiento hacia adelante
        if (colDiff == 0) {
            // Movimiento simple
            if (rowDiff == direction) {
                return board.getPiece(endRow, endCol) == null;
            }
            // Primer movimiento doble
            if (!hasMoved && rowDiff == 2 * direction) {
                return board.getPiece(endRow, endCol) == null &&
                        board.getPiece(startRow + direction, startCol) == null;
            }
        }
        // Captura diagonal
        else if (colDiff == 1 && rowDiff == direction) {
            ChessPiece targetPiece = board.getPiece(endRow, endCol);
            return targetPiece != null && !targetPiece.getPieceColor().equals(this.color);
        }

        return false;
    }
    @Override
    protected ChessPiece createCopy() {
        return new Pawn(this.color);
    }
}