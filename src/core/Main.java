package core;

import world.Level;
import world.MapLoader;
import graphics.ImageLoader;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        int rowCnt=22;
        int colCnt=19;
        int tileSize=32;
        int boardWidth=colCnt*tileSize;
        int boardHeight=rowCnt*tileSize;

        JFrame frame=new JFrame("Pacman");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageLoader imageLoader=new ImageLoader();

        JLayeredPane layeredPane=new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(boardWidth,boardHeight));

        MapLoader mapLoader=new MapLoader(tileSize, rowCnt, colCnt, imageLoader, null);

        Generic menu=new Generic(boardWidth, boardHeight, tileSize, imageLoader, mapLoader, null);

        menu.setOnStartAction(() ->{
            PacMan pacmanGame=new PacMan(Level.level1());
            pacmanGame.setBounds(0,0,boardWidth,boardHeight);
            layeredPane.add(pacmanGame, JLayeredPane.DEFAULT_LAYER);

            layeredPane.remove(menu);

            pacmanGame.startGame();
            layeredPane.repaint();
            pacmanGame.requestFocusInWindow();
        });

        menu.setBounds(0,0,boardWidth,boardHeight);
        layeredPane.add(menu, JLayeredPane.PALETTE_LAYER);

        frame.add(layeredPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        menu.requestFocusInWindow();
    }
}