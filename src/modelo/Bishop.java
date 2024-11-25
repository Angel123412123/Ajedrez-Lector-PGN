package modelo;
public class Bishop extends ChessPiece {

    public Bishop(String color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Board board) {
        // Verificar movimiento diagonal
        if (Math.abs(startRow - endRow) == Math.abs(startCol - endCol)) {
            int rowDirection = (endRow - startRow) > 0 ? 1 : -1;
            int colDirection = (endCol - startCol) > 0 ? 1 : -1;

            int row = startRow + rowDirection;
            int col = startCol + colDirection;

            while (row != endRow && col != endCol) {
                if (board.getPiece(row, col) != null) {
                    return false; // Hay una pieza en el camino
                }
                row += rowDirection;
                col += colDirection;
            }
            return true;
        }
        return false;
    }

    @Override
    protected ChessPiece createCopy() {
        return new Bishop(this.color);
    }
}
