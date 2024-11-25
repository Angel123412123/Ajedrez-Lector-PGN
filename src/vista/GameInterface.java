package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import controlador.*;
import modelo.*;

public class GameInterface extends JFrame {
    private JPanel boardPanel;
    private JPanel controlPanel;
    private JButton[] gameControls;
    private JTextArea pgnTextArea;
    private JLabel[][] squares;
    private GameController controller;

    // Definición de colores personalizados para un diseño más elegante
    private static final Color DARK_SQUARE = new Color(75, 115, 153);    // Azul oscuro elegante
    private static final Color LIGHT_SQUARE = new Color(234, 233, 210);  // Crema claro
    private static final Color BUTTON_COLOR = new Color(45, 45, 45);     // Gris oscuro para botones
    private static final Color BUTTON_TEXT = new Color(255, 255, 255);   // Texto blanco para contraste
    private static final Color PANEL_BACKGROUND = new Color(30, 30, 30); // Fondo oscuro

    public GameInterface() {
        initializeFrame();
        initializeController();
        createComponents();
        layoutComponents();
        styleComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(PANEL_BACKGROUND);
    }

    private void initializeController() {
        Board board = new Board();
        ChessNotationProcessor notationProcessor = new ChessNotationProcessor();
        controller = new GameController(board, this, notationProcessor);
    }

    private void createComponents() {
        // Crear tablero
        boardPanel = new JPanel(new GridLayout(8, 8, 1, 1));
        squares = new JLabel[8][8];

        // Panel de control con diseño más moderno
        controlPanel = new JPanel(new BorderLayout(10, 10));
        gameControls = new JButton[4];
        gameControls[0] = createStyledButton("Load PGN", e -> loadGameMoves());
        gameControls[1] = createStyledButton("Previous", e -> executePreviousMove());
        gameControls[2] = createStyledButton("Next", e -> executeNextMove());
        gameControls[3] = createStyledButton("Reset", e -> resetGame());

        // Área de PGN con estilo moderno
        pgnTextArea = new JTextArea(8, 25);
        pgnTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        pgnTextArea.setBackground(new Color(45, 45, 45));
        pgnTextArea.setForeground(Color.WHITE);
        pgnTextArea.setCaretColor(Color.WHITE);
    }

    private JButton createStyledButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Sans-serif", Font.BOLD, 12));
        button.addActionListener(listener);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        return button;
    }

    private void layoutComponents() {
        // Configuración del tablero
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = new JLabel();
                squares[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                squares[i][j].setOpaque(true);
                squares[i][j].setBackground((i + j) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                squares[i][j].setBorder(null);
                boardPanel.add(squares[i][j]);
            }
        }

        // Panel de control
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        for (JButton button : gameControls) {
            buttonPanel.add(button);
        }
        buttonPanel.setBackground(PANEL_BACKGROUND);

        // Área de PGN
        JScrollPane scrollPane = new JScrollPane(pgnTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Panel lateral
        JPanel sidePanel = new JPanel(new BorderLayout(10, 10));
        sidePanel.setBackground(PANEL_BACKGROUND);
        sidePanel.add(scrollPane, BorderLayout.CENTER);
        sidePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Añadir padding alrededor del tablero
        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setBackground(PANEL_BACKGROUND);
        boardContainer.add(boardPanel);

        // Layout final
        add(boardContainer, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        // Añadir padding general
        ((JPanel)getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void styleComponents() {
        boardPanel.setBorder(BorderFactory.createLineBorder(DARK_SQUARE, 3));
        refreshBoard();
    }

    public void refreshBoard() {
        Board board = controller.getGameBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board.getPiece(i, j);
                squares[i][j].setIcon(null);
                if (piece != null) {
                    String imagePath = getPieceImagePath(piece);
                    if (imagePath != null) {
                        ImageIcon icon = new ImageIcon(imagePath);
                        Image scaledImage = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                        squares[i][j].setIcon(new ImageIcon(scaledImage));
                    }
                }
            }
        }
    }

    private String getPieceImagePath(ChessPiece piece) {
        String color = piece.getPieceColor();
        String baseDir = "src/Recursos/";

        if (piece instanceof King)
            return baseDir + (color.equals("white") ? "Rey.png" : "ReyNegro.png");
        if (piece instanceof Queen)
            return baseDir + (color.equals("white") ? "Reina.png" : "ReinaNegra.png");
        if (piece instanceof Rook)
            return baseDir + (color.equals("white") ? "Torre.png" : "TorreNegra.png");
        if (piece instanceof Bishop)
            return baseDir + (color.equals("white") ? "Alfil.png" : "AlfilNegro.png");
        if (piece instanceof Knight)
            return baseDir + (color.equals("white") ? "Caballo.png" : "CaballoNegro.png");
        if (piece instanceof Pawn)
            return baseDir + (color.equals("white") ? "Ficha Peon.png" : "Ficha PeonNegra.png");
        return null;
    }

    private void loadGameMoves() {
        String pgnText = pgnTextArea.getText().trim();
        if (pgnText.isEmpty()) {
            showStyledMessage("Por favor, ingrese el texto PGN.");
            return;
        }
        controller.loadGameMoves(pgnText);
        showStyledMessage("Movimientos cargados exitosamente.");
    }

    private void executePreviousMove() {
        if (!controller.executePreviousMove()) {
            showStyledMessage("No hay más movimientos para retroceder.");
        }
    }
    private void executeNextMove() {
        if (!controller.executeNextMove()) {
            showStyledMessage("No hay más movimientos para ejecutar.");
        }
    }

    private void resetGame() {
        controller.resetGame();
        pgnTextArea.setText("");
    }

    private void showStyledMessage(String message) {
        UIManager.put("OptionPane.background", PANEL_BACKGROUND);
        UIManager.put("Panel.background", PANEL_BACKGROUND);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);

        JOptionPane.showMessageDialog(
                this,
                message,
                "Chess Lector",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new GameInterface());
    }
}