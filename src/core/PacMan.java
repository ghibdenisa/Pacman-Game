package core;

import entities.*;
import world.*;
import graphics.*;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener, MouseListener, GameBoard {

    private int rowCnt=22;
    private int colCnt=19;
    private int tileSize=32;
    private int boardWidth=colCnt*tileSize;
    private int boardHeight=rowCnt*tileSize;

    private Image pauseImage;

    private HashSet<Block> walls;
    HashSet<Food> foods;
    HashSet<Ghost> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] direction={'U','D','L','R'};
    boolean gameOver=false;
    boolean isPaused=false;

    float pauseScale = 1.0f;
    boolean scaleUp = true;

    boolean pauseButtonAnimating = false;
    int pauseAnimFrame = 0;
    final int PAUSE_ANIM_FRAMES = 4;

    private ImageLoader imageLoader;
    private MapLoader mapLoader;
    private Level crtLevel;
    private GameRenderer renderer;
    private GameState gameState;
    private GameLogic gameLogic;

    public PacMan(Level level){
        this.crtLevel=level;

        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);
        addMouseListener(this);

        imageLoader = new ImageLoader();
        mapLoader=new MapLoader(tileSize, rowCnt, colCnt, imageLoader, this);
        renderer=new GameRenderer(boardWidth, boardHeight, tileSize, imageLoader);
        gameState=new GameState();
        gameLogic=new GameLogic(tileSize, boardWidth, boardHeight, this, imageLoader);

        pauseImage=imageLoader.getImage("pause");

        loadMap();
        int dirIndex=0;
        for(Block ghost:ghosts){
            ghost.updateDirection(direction[dirIndex%4]);
            dirIndex++;
        }
        gameLoop=new Timer(50,this);
        gameLoop.start();
    }

    public void startGame(){
        if(!gameLoop.isRunning()){
            gameLoop.start();
            this.requestFocusInWindow();
        }
    }

    public void loadMap(){
        MapLoader.MapData mapData=mapLoader.loadMap(crtLevel);

        this.walls=mapData.walls;
        this.foods=mapData.foods;
        this.ghosts=mapData.ghosts;
        this.pacman=mapData.pacman;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        renderer.draw(g, pacman, ghosts, walls, foods, gameState.getScore(),
                gameState.getLives(), gameState.isGameOver(),
                gameState.isPaused(), pauseImage, pauseButtonAnimating);
    }

    public void move()
    {
        gameLogic.move(pacman, ghosts, walls, foods, gameState);
        if(foods.isEmpty()){
            loadMap();
            gameLogic.resetPositions(pacman, ghosts);
        }
    }

    public HashSet<Block> getWalls()
    {
        return walls;
    }

    public int getTileSize(){
        return tileSize;
    }

    public boolean collision(Block a, Block b)
    {
        return a.x < b.x+b.width &&
                a.x+a.width > b.x &&
                a.y < b.y+b.height &&
                a.y+a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!gameState.isPaused()) {
            move();
            pauseScale=1.0f;
        }
        else
        {
            if(scaleUp)
                pauseScale += 0.01f;
            else
                pauseScale -= 0.01f;

            if(pauseScale >= 1.1f)
                scaleUp = false;
            if(pauseScale <= 0.95f)
                scaleUp = true;
        }

        if(pauseButtonAnimating)
        {
            pauseAnimFrame++;

            if(pauseAnimFrame >= PAUSE_ANIM_FRAMES)
            {
                pauseButtonAnimating=false;
                isPaused=false;
            }
        }

        repaint();
        if(gameState.isGameOver()){
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_P && !gameOver)
            gameState.togglePaused();

        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);

        if(e.getKeyCode() == KeyEvent.VK_UP)
            gameLogic.setPendingDirection('U');
        else if(e.getKeyCode() == KeyEvent.VK_DOWN)
            gameLogic.setPendingDirection('D');
        else if(e.getKeyCode() == KeyEvent.VK_LEFT)
            gameLogic.setPendingDirection('L');
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            gameLogic.setPendingDirection('R');

        if(gameState.isGameOver()){
            loadMap();
            gameLogic.resetPositions(pacman, ghosts);
            gameState.reset();
            gameLoop.start();
        }

        if(e.getKeyCode()==KeyEvent.VK_UP)
            pacman.updateDirection('U');
        else if(e.getKeyCode()==KeyEvent.VK_DOWN)
            pacman.updateDirection('D');
        else if(e.getKeyCode()==KeyEvent.VK_LEFT)
            pacman.updateDirection('L');
        else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
            pacman.updateDirection('R');

        if(!gameLogic.isEating())
            gameLogic.updatePacmanImage(pacman);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        int baseW = 200;
        int baseH = 70;

        int drawW = (int)(baseW * pauseScale);
        int drawH = (int)(baseH * pauseScale);

        int imgX = (boardWidth - drawW) / 2;
        int imgY = (boardHeight - drawH) / 2;

        if(isPaused &&
                mouseX >= imgX && mouseX <= imgX + drawW &&
                mouseY >= imgY && mouseY <= imgY + drawH)
        {
            pauseButtonAnimating = true;
            pauseAnimFrame = 0;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
