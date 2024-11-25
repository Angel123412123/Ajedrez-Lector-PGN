package modelo;

public class Knight extends ChessPiece {
    public Knight(String color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Board board) {
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        // Verificar movimiento en L
        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
            return false;
        }

        // Verificar pieza en destino
        ChessPiece targetPiece = board.getPiece(endRow, endCol);
        return targetPiece == null || !targetPiece.getPieceColor().equals(this.color);
    }
    @Override
    protected ChessPiece createCopy() {
        return new Knight(this.color);
    }
}
