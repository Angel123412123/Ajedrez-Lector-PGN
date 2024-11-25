package modelo;



// Clase base abstracta para todas las piezas
public abstract class ChessPiece {
    protected String color;
    protected boolean hasMoved;

    public ChessPiece(String color) {
        this.color = color;
        this.hasMoved = false;
    }

    public abstract boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Board board);

    public String getPieceColor() {
        return color;
    }

    public void flagAsMoved() {
        hasMoved = true;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public boolean hasBeenMoved() {
        return hasMoved;  // Retorna el mismo valor que hasMoved()
    }
    public ChessPiece copy() {
        ChessPiece copy = createCopy();
        copy.color = this.color;
        copy.hasMoved = this.hasMoved;
        return copy;
    }
    protected abstract ChessPiece createCopy();

}