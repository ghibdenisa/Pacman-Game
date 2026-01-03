package entities;

import java.awt.*;
import world.GameBoard;

public class Food extends Block {
    private static final int FOOD_SIZE=4;
    private static final int FOOD_OFFSET=14;
    private int points;

    public Food(int x, int y, GameBoard game){
        super(x+FOOD_OFFSET, y+FOOD_OFFSET, FOOD_SIZE, FOOD_SIZE, null, game);
        this.points=10;
    }

    public Food(int x, int y, int points, GameBoard game){
        super(x+FOOD_OFFSET, y+FOOD_OFFSET, FOOD_SIZE, FOOD_SIZE, null, game);
        this.points=points;
    }

    public int getPoints(){
        return points;
    }

    public void draw(Graphics g){
        g.setColor(Color.white);
        g.fillRect(this.x, this.y, this.width, this.height);
    }
}
