import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        int rowCnt=21;
        int colCnt=19;
        int tileSize=32;
        int boardWidth=colCnt*tileSize;
        int boardHeight=rowCnt*tileSize;

        JFrame frame=new JFrame("Pacman");
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame=new PacMan();
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }
}