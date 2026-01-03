package graphics;

import entities.Block;
import entities.Food;
import entities.Ghost;

import java.awt.*;
import java.util.*;

public class GameRenderer {
    private int boardWidth;
    private int boardHeight;
    private int tileSize;
    private ImageLoader imageLoader;

    public GameRenderer(int boardWidth, int boardHeight, int tileSize, ImageLoader imageLoader) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.tileSize = tileSize;
        this.imageLoader = imageLoader;
    }

    public void draw(Graphics g, Block pacman, HashSet<Ghost> ghosts, HashSet<Block> walls,
                     HashSet<Food> foods, int score, int lives, boolean gameOver,
                     boolean isPaused, Image pauseImage, boolean pauseButtonAnimating) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for(Ghost ghost:ghosts)
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

        for(Block wall:walls)
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

        for(Food food:foods)
            food.draw(g);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        int lifeSize=28;
        int lifeX=tileSize/2;
        int lifeY=tileSize/2-lifeSize+12;

        for(int i=0; i<lives; i++)
            g.drawImage(imageLoader.getImage("pacmanRight"), lifeX+i*lifeSize-10, lifeY, lifeSize, lifeSize, null);

        if(gameOver)
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        else
        {
            int scoreX=lifeX+lives*lifeSize;
            int scoreY=tileSize/2+6;
            g.drawString(" Score: " + String.valueOf(score), scoreX, scoreY);
        }

        if(isPaused)
        {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, boardWidth, boardHeight);

            int imgW = 200;
            int imgH = 70;
            int imgX = (boardWidth - imgW) / 2;
            int imgY = (boardHeight - imgH) / 2;

            g.drawImage(pauseImage, imgX, imgY, imgW, imgH, null);

            if(pauseButtonAnimating)
            {
                g.setColor(new Color(0, 0, 0, 80));
                g.fillRoundRect(imgX, imgY, imgW, imgH, 12, 12);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String resumeText = "Click the image or press P to continue";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(resumeText);

            g.drawString(resumeText, (boardWidth - textWidth) / 2, imgY + 70 + 30);
        }
    }
}
