package modelo;

public class Queen extends ChessPiece {
    public Queen(String color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Board board) {
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        // Movimiento válido: diagonal, horizontal o vertical
        if (!(rowDiff == colDiff || startRow == endRow || startCol == endCol)) {
            return false;
        }

        // Verificar piezas en el camino
        int rowDirection = Integer.compare(endRow, startRow);
        int colDirection = Integer.compare(endCol, startCol);

        int currentRow = startRow + rowDirection;
        int currentCol = startCol + colDirection;

        while (currentRow != endRow || currentCol != endCol) {
            if (board.getPiece(currentRow, currentCol) != null) {
                return false;
            }
            currentRow += rowDirection != 0 ? rowDirection : 0;
            currentCol += colDirection != 0 ? colDirection : 0;
        }

        // Verificar pieza en destino
        ChessPiece targetPiece = board.getPiece(endRow, endCol);
        return targetPiece == null || !targetPiece.getPieceColor().equals(this.color);
    }
    @Override
    protected ChessPiece createCopy() {
        return new Queen(this.color);
    }
}
