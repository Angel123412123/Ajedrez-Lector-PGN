package modelo;

public class King extends ChessPiece {
    private boolean hasMoved;  // Añadimos esta variable de instancia

    public King(String color) {
        super(color);
        this.hasMoved = false;  // Inicializamos en false en el constructor
    }

    // Añadimos el método getter
    public boolean hasBeenMoved() {
        return hasMoved;
    }

    // Añadimos el método setter
    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Board board) {
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        // Movimiento normal del Rey
        if (rowDiff <= 1 && colDiff <= 1) {
            ChessPiece targetPiece = board.getPiece(endRow, endCol);
            return targetPiece == null || !targetPiece.getPieceColor().equals(this.color);
        }

        // Enroque
        if (!hasMoved && rowDiff == 0 && colDiff == 2) {
            // Verificar si hay una torre en la posición correcta
            int rookCol = endCol > startCol ? 7 : 0;
            ChessPiece rook = board.getPiece(startRow, rookCol);

            if (rook instanceof Rook && !((Rook)rook).hasBeenMoved()) {
                // Verificar si hay piezas entre el rey y la torre
                int direction = endCol > startCol ? 1 : -1;
                for (int col = startCol + direction; col != rookCol; col += direction) {
                    if (board.getPiece(startRow, col) != null) {
                        return false;
                    }
                }
                // Verificar si el rey está en jaque o si las casillas del enroque están atacadas
                for (int col = startCol; col != endCol + direction; col += direction) {
                    if (board.isSquareUnderAttack(startRow, col, color)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    @Override
    protected ChessPiece createCopy() {
        return new King(this.color);
    }
}