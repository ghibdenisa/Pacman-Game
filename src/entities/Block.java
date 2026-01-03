package entities;

import java.awt.*;
import world.GameBoard;

public class Block {
        public int x;
        public int y;
        public int width;
        public int height;
        public Image image;

        public int startX;
        public int startY;

        public char direction='U';
        public int velocityX=0;
        public int velocityY=0;

        protected GameBoard game;

        public Block(int x, int y, int width, int height, Image image, GameBoard game) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
            this.startX = x;
            this.startY = y;
            this.game = game;
        }

    public void updateDirection(char direction){
        char prevDirection=this.direction;
        this.direction = direction;
        updateVelocity();
        this.x += this.velocityX;
        this.y += this.velocityY;
        for(Block wall: game.getWalls()){
            if(game.collision(this, wall))
            {
                this.x-=this.velocityX;
                this.y-=this.velocityY;
                this.direction=prevDirection;
                updateVelocity();
            }
        }
    }

    public void updateVelocity(){
        int speed=game.getTileSize();
        if(this.direction=='U'){
            this.velocityX=0;
            this.velocityY=-speed/4;
        }
        else if(this.direction=='D'){
            this.velocityX=0;
            this.velocityY=speed/4;
        }
        else if(this.direction=='L'){
            this.velocityX=-speed/4;
            this.velocityY=0;
        }
        else if(this.direction=='R'){
            this.velocityX=speed/4;
            this.velocityY=0;
        }
    }

    public void reset()
    {
        this.x=this.startX;
        this.y=this.startY;
    }
}
