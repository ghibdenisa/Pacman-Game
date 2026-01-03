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

    private int ghostMoveCnt = 0;

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

        if(pendingDirection != ' ' && pendingDirection != pacman.direction)
        {
            pendingFrames++;

            if(canMove(pacman, pendingDirection, walls))
            {
                autoAlign(pacman, pendingDirection);
                pacman.updateDirection(pendingDirection);
                if(!isEating)
                    updatePacmanImage(pacman);
                //updatePacmanImage();
                pendingDirection= ' ';
                pendingFrames=0;
            }
            else if(pendingFrames >= MAX_PENDING_FRAMES)
            {
                pendingDirection= ' ';
                pendingFrames=0;
            }
        }
        pacman.x+=pacman.velocityX;
        pacman.y+=pacman.velocityY;

        if(pacman.x+pacman.width < 0)
            pacman.x=boardWidth;
        else if(pacman.x > boardWidth)
            pacman.x=-pacman.width;

        for(Block wall:walls)
        {
            if(game.collision(pacman, wall))
            {
                pacman.x-=pacman.velocityX;
                pacman.y-=pacman.velocityY;
                break;
            }
        }

        for(Ghost ghost:ghosts)
        {
            if(game.collision(pacman, ghost))
            {
                gameState.loseLife();
                if(gameState.isGameOver())
                    return;
                resetPositions(pacman, ghosts);
            }

            ghost.move(tileSize, boardWidth, boardHeight);

            boolean needsNewDirection=false;
            for(Block wall:walls)
            {
                if(game.collision(ghost, wall))
                {
                    ghost.x-=ghost.velocityX;
                    ghost.y-=ghost.velocityY;
                    needsNewDirection=true;
                    break;
                }
            }

            if(needsNewDirection || ghost.needsNewDirection(ghostMoveCnt))
            {
                char newDirection = ghost.chooseDirection(pacman, boardWidth);
                ghost.updateDirection(newDirection);
            }
        }

        ghostMoveCnt++;

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
        foods.remove(foodEaten);
    }

    public boolean canMove(Block pacman, char dir, HashSet<Block> walls)
    {
        int testX=pacman.x;
        int testY=pacman.y;

        if(dir=='U')
            testY-=tileSize/4;
        else if(dir=='D')
            testY+=tileSize/4;
        else if(dir=='R')
            testX-=tileSize/4;
        else if(dir=='L')
            testX+=tileSize/4;

        Block testBlock = new Block(testX, testY, pacman.width, pacman.height, null, game);

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
        }
    }

    public boolean isEating(){
        return isEating;
    }
}
