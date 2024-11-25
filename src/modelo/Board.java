package modelo;

public class Board {
    private ChessPiece[][] squares;

    public Board() {
        squares = new ChessPiece[8][8];
        setupBoard();
    }

    private void setupBoard() {
        // Setup white pieces
        squares[0][0] = new Rook("white");
        squares[0][1] = new Knight("white");
        squares[0][2] = new Bishop("white");
        squares[0][3] = new Queen("white");
        squares[0][4] = new King("white");
        squares[0][5] = new Bishop("white");
        squares[0][6] = new Knight("white");
        squares[0][7] = new Rook("white");
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new Pawn("white");
        }

        // Setup black pieces
        squares[7][0] = new Rook("black");
        squares[7][1] = new Knight("black");
        squares[7][2] = new Bishop("black");
        squares[7][3] = new Queen("black");
        squares[7][4] = new King("black");
        squares[7][5] = new Bishop("black");
        squares[7][6] = new Knight("black");
        squares[7][7] = new Rook("black");
        for (int i = 0; i < 8; i++) {
            squares[6][i] = new Pawn("black");
        }
    }


        public boolean movePiece(int startRow, int startCol, int endRow, int endCol) {
            ChessPiece piece = squares[startRow][startCol];
            if (piece == null) {
                System.out.println("No piece at starting position " + startRow + "," + startCol);
                return false;
            }

            // Verificar si el movimiento está dentro del tablero
            if (endRow < 0 || endRow > 7 || endCol < 0 || endCol > 7) {
                System.out.println("Target position out of bounds");
                return false;
            }

            // Verificar si hay una pieza del mismo color en el destino
            if (squares[endRow][endCol] != null &&
                    squares[endRow][endCol].getPieceColor().equals(piece.getPieceColor())) {
                System.out.println("Cannot capture own piece");
                return false;
            }

            // Verificar si el movimiento es válido para la pieza específica
            if (piece.isValidMove(startRow, startCol, endRow, endCol, this)) {
                squares[endRow][endCol] = piece;
                squares[startRow][startCol] = null;
                piece.flagAsMoved(); // Marcar la pieza como movida, importante para enroques y peones
                return true;
            } else {
                System.out.println("Invalid move for piece at " + startRow + "," + startCol +
                        " to " + endRow + "," + endCol);
                return false;
            }
        }



    public boolean attemptCastling(String color, String type) {
        System.out.println("Attempting castling for " + color + " " + type);

        int row = color.equals("white") ? 0 : 7;
        King king = (King) squares[row][4];

        // Verificar rey
        if (king == null || king.hasBeenMoved()) {
            System.out.println("King cannot castle - null or has moved");
            return false;
        }

        // Configurar posiciones según tipo de enroque
        int kingDestCol, rookStartCol, rookDestCol;
        if (type.equals("corto")) {
            kingDestCol = 6;
            rookStartCol = 7;
            rookDestCol = 5;
        } else if (type.equals("largo")) {
            kingDestCol = 2;
            rookStartCol = 0;
            rookDestCol = 3;
        } else {
            System.out.println("Invalid castling type");
            return false;
        }

        // Verificar torre
        Rook rook = (Rook) squares[row][rookStartCol];
        if (rook == null || rook.hasBeenMoved()) {
            System.out.println("Rook cannot castle - null or has moved");
            return false;
        }

        // Verificar casillas intermedias
        if (!checkEmptySquares(row, kingDestCol)) {
            System.out.println("Path not clear for castling");
            return false;
        }

        // Verificar que las casillas no están bajo ataque
        if (checkSquaresUnderAttack(row, kingDestCol, color)) {
            System.out.println("Castling path under attack");
            return false;
        }

        // Ejecutar enroque
        boolean success = executeCastling(color, row, kingDestCol, rookStartCol, rookDestCol);
        if (success) {
            System.out.println("Castling successfully executed");
        }
        return success;
    }


    private boolean checkEmptySquares(int row, int kingDestCol) {
        int start = Math.min(4, kingDestCol);
        int end = Math.max(4, kingDestCol);

        for (int col = start + 1; col < end; col++) {
            if (squares[row][col] != null) {
                return false;
            }
        }
        return true;
    }

    private boolean checkSquaresUnderAttack(int row, int kingDestCol, String color) {
        int start = Math.min(4, kingDestCol);
        int end = Math.max(4, kingDestCol);

        for (int col = start; col <= end; col++) {
            if (isSquareUnderAttack(row, col, color)) {
                return true;
            }
        }
        return false;
    }

    private boolean executeCastling(String color, int row, int kingDestCol, int rookStartCol, int rookDestCol) {
        try {
            // Mover rey
            squares[row][kingDestCol] = squares[row][4];
            squares[row][4] = null;

            // Mover torre
            squares[row][rookDestCol] = squares[row][rookStartCol];
            squares[row][rookStartCol] = null;

            // Marcar piezas como movidas
            ((King) squares[row][kingDestCol]).flagAsMoved();
            ((Rook) squares[row][rookDestCol]).flagAsMoved();

            return true;
        } catch (Exception e) {
            System.err.println("Error executing castling");
            e.printStackTrace();
            return false;
        }
    }








    public Board copy() {
        Board newBoard = new Board();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (squares[i][j] != null) {
                    newBoard.squares[i][j] = squares[i][j].copy();
                }
            }
        }
        return newBoard;
    }

    public void copyFrom(Board other) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (other.squares[i][j] != null) {
                    this.squares[i][j] = other.squares[i][j].copy();
                } else {
                    this.squares[i][j] = null;
                }
            }
        }
    }

    public ChessPiece getPiece(int row, int col) {
        return squares[row][col];
    }

    public boolean isSquareUnderAttack(int row, int col, String color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = squares[i][j];
                if (piece != null && !piece.getPieceColor().equals(color)) {
                    if (piece.isValidMove(i, j, row, col, this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}