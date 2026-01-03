package world;

import entities.Block;
import java.util.*;

public interface GameBoard {
    HashSet<Block> getWalls();
    int getTileSize();
    boolean collision(Block a, Block b);
}
