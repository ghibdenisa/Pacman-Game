package world;

import entities.Block;
import entities.Food;
import entities.Ghost;
import graphics.ImageLoader;

import java.util.*;

public class MapLoader {
    private int tileSize;
    private int rowCnt;
    private int colCnt;
    private ImageLoader imageLoader;
    private GameBoard game;

    public MapLoader(int tileSize, int rowCnt, int colCnt, ImageLoader imageLoader,  GameBoard game) {
        this.tileSize = tileSize;
        this.rowCnt = rowCnt;
        this.colCnt = colCnt;
        this.imageLoader = imageLoader;
        this.game = game;
    }

    public MapData loadMap(Level level){
        HashSet<Block> walls=new HashSet<>();
        HashSet<Food>  foods=new HashSet<>();
        HashSet<Ghost> ghosts=new HashSet<>();
        Block pacman=null;

        String[] tileMap=level.getTileMap();

        for(int r=0; r<rowCnt; r++)
        {
            for(int c=0; c<colCnt; c++)
            {
                String row=tileMap[r];
                char tileMapChar=row.charAt(c);

                int x=c*tileSize;
                int y=r*tileSize;

                if(tileMapChar=='X')
                {
                    Block wall=new Block(x, y, tileSize, tileSize, imageLoader.getImage("wall"), game);
                    walls.add(wall);
                }
                else if(tileMapChar=='b')
                {
                    Ghost ghost=new Ghost(x, y, tileSize, tileSize, imageLoader.getImage("blueGhost"), game);
                    ghosts.add(ghost);

                    Food food=new Food(x, y, game);
                    foods.add(food);
                }
                else if(tileMapChar=='o')
                {
                    Ghost ghost=new Ghost(x, y, tileSize, tileSize, imageLoader.getImage("orangeGhost"), game);
                    ghosts.add(ghost);

                    Food food=new Food(x, y, game);
                    foods.add(food);
                }
                else if(tileMapChar=='p')
                {
                    Ghost ghost=new Ghost(x, y, tileSize, tileSize, imageLoader.getImage("pinkGhost"), game);
                    ghosts.add(ghost);

                    Food food=new Food(x, y, game);
                    foods.add(food);
                }
                else if(tileMapChar=='r')
                {
                    Ghost ghost=new Ghost(x, y, tileSize, tileSize, imageLoader.getImage("redGhost"), game);
                    ghosts.add(ghost);

                    Food food=new Food(x, y, game);
                    foods.add(food);
                }
                else if(tileMapChar=='P')
                    pacman=new Block(x, y, tileSize, tileSize, imageLoader.getImage("pacmanRight"), game);
                else if(tileMapChar==' ')
                {
                    Food food=new Food(x, y, game);
                    foods.add(food);
                }
            }
        }
        return new MapData(walls, foods, ghosts, pacman);
    }

    public static class MapData{
        public final HashSet<Block> walls;
        public final HashSet<Food> foods;
        public final HashSet<Ghost> ghosts;
        public final Block pacman;

        public MapData(HashSet<Block> walls, HashSet<Food> foods, HashSet<Ghost> ghosts, Block pacman) {
            this.walls = walls;
            this.foods = foods;
            this.ghosts = ghosts;
            this.pacman = pacman;
        }
    }
}
