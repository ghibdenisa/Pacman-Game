package entities;

import java.awt.*;
import java.util.*;
import world.GameBoard;

public class Ghost extends Block {
    private Random rand;
    private char[] directions={'U','D','L','R'};

    public Ghost(int x, int y, int width, int height, Image image, GameBoard game) {
        super(x, y, width, height, image, game);
        this.rand=new Random();
    }

    public void move(int boardWidth, int boardHeight)
    {
        this.x+=this.velocityX;
        this.y+=this.velocityY;

        if (this.x < 0) this.x = 0;
        if (this.y < 0) this.y = 0;
        if (this.x > boardWidth - width) this.x = boardWidth - width;
        if (this.y > boardHeight - height) this.y = boardHeight - height;
   }

    public char chooseDirection(Block pacman, int boardWidth, int boardHeight){
        ArrayList<Character> validDir=new ArrayList<>();
        int tileSize = game.getTileSize();

        for(char dir:directions) {
            if (this.direction != ' ' && dir == getOppositeDirection(this.direction)) continue;

            int testX = x;
            int testY = y;

            if (dir == 'U') testY -= tileSize;
            else if (dir == 'D') testY += tileSize;
            else if (dir == 'R') testX += tileSize;
            else if (dir == 'L') testX -= tileSize;

            if (testX < 0 || testX >= boardWidth || testY < 0 || testY >= boardHeight) {
                continue;
            }

            Block testBlock = new Block(testX + 12, testY + 22, 8, 8, null, game);

            boolean canMove = true;
            for (Block wall : game.getWalls()) {
                if (game.collision(testBlock, wall)) {
                    canMove = false;
                    break;
                }
            }

            if(canMove) {
                validDir.add(dir);
            }
        }

        if (validDir.isEmpty()) {
            return getOppositeDirection(this.direction);
        }

        if (rand.nextInt(100) < 60) {
            return validDir.get(rand.nextInt(validDir.size()));
        }

        char targetDir = getDirectionTowards(pacman);
        if (validDir.contains(targetDir)) return targetDir;

        return validDir.get(rand.nextInt(validDir.size()));
    }

    private char getOppositeDirection(char dir) {
        return switch (dir) {
            case 'U' -> 'D';
            case 'D' -> 'U';
            case 'L' -> 'R';
            case 'R' -> 'L';
            default -> 'U';
        };
    }

    public char getDirectionTowards(Block pacman) {
        int dx = pacman.x - this.x;
        int dy = pacman.y - this.y;
        if (Math.abs(dx) > Math.abs(dy)) {
            return (dx > 0) ? 'R' : 'L';
        } else {
            return (dy > 0) ? 'D' : 'U';
        }
    }

    @Override
    public void updateVelocity(){
        int speed=6;

        velocityX = 0;
        velocityY = 0;

        switch (direction) {
            case 'U' -> velocityY = -speed;
            case 'D' -> velocityY = speed;
            case 'L' -> velocityX = -speed;
            case 'R' -> velocityX = speed;
        }
    }

}
