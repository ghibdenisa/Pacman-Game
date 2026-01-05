package core;

import entities.*;
import world.GameBoard;
import graphics.ImageLoader;
import java.awt.*;
import java.util.*;

public class GameLogic {
    private int tileSize;
    private int boardWidth;
    private int boardHeight;
    private GameBoard game;
    private ImageLoader imageLoader;
    private Random rand;

    private char pendingDirection = ' ';
    private int pendingFrames = 0;
    private final int MAX_PENDING_FRAMES = 4;

    private boolean isEating = false;
    private int eatFrame = 0;
    private final int EAT_ANIMATION_SPEED = 1;

    public GameLogic(int tileSize, int boardWidth, int boardHeight, GameBoard game, ImageLoader imageLoader) {
        this.tileSize = tileSize;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.game = game;
        this.imageLoader = imageLoader;
        this.rand = new Random();
    }

    public void setPendingDirection(char pendingDirection) {
        this.pendingDirection = pendingDirection;
    }

    public void move(Block pacman, HashSet<Ghost> ghosts, HashSet<Block> walls, HashSet<Food> foods, GameState gameState) {
        int speed=8;

        if(isEating)
        {
            eatFrame++;
            if(eatFrame >= EAT_ANIMATION_SPEED)
            {
                isEating=false;
                eatFrame=0;
                updatePacmanImage(pacman);
            }
        }

        boolean atCenter=(pacman.x % tileSize == 0 && pacman.y % tileSize == 0);

        if(pendingDirection != ' ')
        {
            if(canMove(pacman, pendingDirection, walls))
            {
                boolean isOpposite = (pacman.direction == 'U' && pendingDirection == 'D') ||
                        (pacman.direction == 'D' && pendingDirection == 'U') ||
                        (pacman.direction == 'L' && pendingDirection == 'R') ||
                        (pacman.direction == 'R' && pendingDirection == 'L');

                if (isOpposite || atCenter) {
                    pacman.x = Math.round((float) pacman.x / tileSize) * tileSize;
                    pacman.y = Math.round((float) pacman.y / tileSize) * tileSize;

                    pacman.updateDirection(pendingDirection);
                    pendingDirection = ' ';

//                    if(!isEating) updatePacmanImage(pacman);
//                    pendingDirection = ' ';
                }
            }
        }

        pacman.velocityX = 0;
        pacman.velocityY = 0;

        switch (pacman.direction) {
            case 'U' -> pacman.velocityY = -speed;
            case 'D' -> pacman.velocityY = speed;
            case 'L' -> pacman.velocityX = -speed;
            case 'R' -> pacman.velocityX = speed;
        }

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for(Block wall:walls)
        {
            if(game.collision(pacman, wall))
            {
                pacman.x-=pacman.velocityX;
                pacman.y-=pacman.velocityY;

                pacman.x = Math.round((float) pacman.x / tileSize) * tileSize;
                pacman.y = Math.round((float) pacman.y / tileSize) * tileSize;
                pacman.velocityX = 0;
                pacman.velocityY = 0;
                break;
            }
        }

        if(pacman.x + pacman.width < 0) pacman.x = boardWidth;
        else if(pacman.x > boardWidth) pacman.x = -pacman.width;

        if(!isEating) updatePacmanImage(pacman);

        for(Ghost ghost:ghosts)
        {
            ghost.move(tileSize, boardWidth, boardHeight);

            boolean hitWall = false;
            for (Block wall : walls) {
                if (game.collision(ghost, wall)) {
                    ghost.x = Math.round((float) (ghost.x - ghost.velocityX) / tileSize) * tileSize;
                    ghost.y = Math.round((float) (ghost.y - ghost.velocityY) / tileSize) * tileSize;
                    hitWall = true;
                    break;
                }
            }

            boolean atIntersection = (ghost.x % tileSize == 0 && ghost.y % tileSize == 0);

            if (hitWall || atIntersection) {
                ghost.x = Math.round((float) ghost.x / tileSize) * tileSize;
                ghost.y = Math.round((float) ghost.y / tileSize) * tileSize;

                char newDir = ghost.chooseDirection(pacman, boardWidth, boardHeight);
                ghost.updateDirection(newDir);
                ghost.updateVelocity();

                if (newDir != ghost.direction) {
                    ghost.updateDirection(newDir);
                    ghost.updateVelocity();
                }
            }

            if(game.collision(pacman, ghost))
            {
                gameState.loseLife();
                if(gameState.isGameOver())
                    return;
                resetPositions(pacman, ghosts);
                return;
            }
        }

        Food foodEaten=null;
        for(Food food:foods)
        {
            if(game.collision(pacman, food))
            {
                foodEaten=food;
                gameState.addScore(food.getPoints());

                isEating=true;
                eatFrame=0;
                pacman.image=imageLoader.getImage("pacmanClosed");
            }
        }
        if(foodEaten!=null)
            foods.remove(foodEaten);
    }

    public boolean canMove(Block pacman, char dir, HashSet<Block> walls)
    {
        int testX=pacman.x;
        int testY=pacman.y;

        testX = Math.round((float) testX / tileSize) * tileSize;
        testY = Math.round((float) testY / tileSize) * tileSize;

        if(dir=='U')
            testY-=tileSize/4;
        else if(dir=='D')
            testY+=tileSize/4;
        else if(dir=='R')
            testX-=tileSize/4;
        else if(dir=='L')
            testX+=tileSize/4;

        Block testBlock = new Block(testX + 8, testY + 8, 16, 16, null, game);

        for(Block wall:walls)
            if(game.collision(testBlock,wall))
                return false;

        return true;
    }

    public void autoAlign(Block pacman, char newDirection)
    {
        boolean changingAxis=
                ((pacman.direction=='L' || pacman.direction=='R') && (newDirection=='U' || newDirection=='D')) ||
                        ((pacman.direction=='U' || pacman.direction=='D') && (newDirection=='L' || newDirection=='R'));

        if(changingAxis)
        {
            if(newDirection=='U' || newDirection=='D')
            {
                int targetX=pacman.x+tileSize/2;
                int diff=Math.abs(pacman.x-targetX);
                if(diff<=12)
                    pacman.x=targetX;
            }
            else
            {
                int targetY=pacman.y+tileSize/2;
                int diff=Math.abs(pacman.y-targetY);
                if(diff<=12)
                    pacman.y=targetY;
            }
        }
    }

    public void updatePacmanImage(Block pacman) {
        if(pacman.direction == 'U')
            pacman.image = imageLoader.getImage("pacmanUp");
        else if(pacman.direction == 'D')
            pacman.image = imageLoader.getImage("pacmanDown");
        else if(pacman.direction == 'L')
            pacman.image = imageLoader.getImage("pacmanLeft");
        else if(pacman.direction == 'R')
            pacman.image = imageLoader.getImage("pacmanRight");
    }

    public void resetPositions(Block pacman, HashSet<Ghost> ghosts)
    {
        pacman.reset();
        pacman.velocityX=0;
        pacman.velocityY=0;

        char[] directions={'U', 'D', 'R', 'L'};
        for(Ghost ghost:ghosts)
        {
            ghost.reset();
            char newDirection=directions[rand.nextInt(4)];
            ghost.updateDirection(newDirection);
            ghost.updateVelocity();
        }
    }

    public boolean isEating(){
        return isEating;
    }
}
