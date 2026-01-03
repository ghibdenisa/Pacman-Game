package entities;

import java.awt.*;
import java.util.*;
import world.GameBoard;

public class Ghost extends Block {
    private Random rand;
    private char[] directions={'U','D','L','R'};
    private final int DIRECTION_CHANGE_INTERVAL=10;

    public Ghost(int x, int y, int width, int height, Image image, GameBoard game) {
        super(x, y, width, height, image, game);
        this.rand=new Random();
    }

    public void move(int tileSize, int boardWidth, int boardHeight)
    {
        if(this.y==tileSize*9 && (this.direction!='L' && this.direction!='R'))
        {
            if(rand.nextInt(100) < 80)
                updateDirection('U');
            else
                updateDirection('D');
        }

        this.x+=this.velocityX;
        this.y+=this.velocityY;

        int TUNNEL_Y=tileSize*10;

        if(this.y == TUNNEL_Y) {
            if (this.x + this.width < 0)
                this.x = boardWidth;
            else if (this.x > boardWidth)
                this.x = -this.width;
        }

        if(this.y < tileSize)
            this.y=tileSize;
        if(this.y > boardHeight-this.height)
            this.y = boardHeight-this.height;
    }

    public boolean needsNewDirection(int ghostMoveCnt){
        return ghostMoveCnt%DIRECTION_CHANGE_INTERVAL==0;
    }

    public char chooseDirection(Block pacman, int boardWidth){
        ArrayList<Character> validDir=new ArrayList<>();

        for(char dir:directions) {
            if(this.y<=game.getTileSize() && dir=='U')
                continue;
            if ((this.direction == 'U' && dir == 'D') ||
                    (this.direction == 'D' && dir == 'U') ||
                    (this.direction == 'L' && dir == 'R') ||
                    (this.direction == 'R' && dir == 'L')) {
                continue;
            }

            int testX = this.x;
            int testY = this.y;
            int tileSize=game.getTileSize();

            if (dir == 'U')
                testY -= tileSize / 4;
            else if (dir == 'D')
                testY += tileSize / 4;
            else if (dir == 'R')
                testX += tileSize / 4;
            else if (dir == 'L')
                testX -= tileSize / 4;

            Block testBlock = new Block(testX, testY, this.width, this.height, null, game);

            boolean canMove = true;
            for (Block wall : game.getWalls()) {
                if (game.collision(testBlock, wall)) {
                    canMove = false;
                    break;
                }
            }

            if (canMove && testX > -this.width && testX < boardWidth+this.width)
                validDir.add(dir);
        }

        if(validDir.isEmpty())
        {
            if(this.direction == 'U') return 'D';
            if(this.direction == 'D') return 'U';
            if(this.direction == 'L') return 'R';
            if(this.direction == 'R') return 'L';
        }

        if(rand.nextInt(100) < 45 && !validDir.isEmpty())
        {
            int dx = pacman.x-this.x;
            int dy = pacman.y-this.y;

            if(Math.abs(dx) > Math.abs(dy))
            {
                char preferredDir = dx > 0 ? 'R' : 'L';
                if(validDir.contains(preferredDir))
                {
                    return preferredDir;
                }
            }
            else {
                char preferredDir = dy > 0 ? 'D' : 'U';
                if (validDir.contains(preferredDir)) {
                    return preferredDir;
                }
            }
        }

        return validDir.get(rand.nextInt(validDir.size()));
    }

}
