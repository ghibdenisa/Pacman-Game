package core;

import entities.*;
import graphics.ImageLoader;
import world.MapLoader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;

public class Generic extends JPanel implements MouseListener, KeyListener {
    private Image titleImg;
    private Image startBtnImg;
    private int boardWidth;
    private int boardHeight;
    private int tileSize;
    private Runnable onStartAction;

    private boolean isPressing=false;
    private Rectangle startRect;

    private HashSet<Block> walls;
    private HashSet<Ghost> ghosts;
    private Block pacman;

    public Generic(int width, int height, int tileSize, ImageLoader loader,
                   MapLoader mapLoader, Runnable onStartAction) {
        this.boardWidth = width;
        this.boardHeight = height;
        this.tileSize=tileSize;
        this.onStartAction = onStartAction;

        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        setFocusable(true);
        addMouseListener(this);
        addKeyListener(this);

        titleImg=loader.getImage("PACMAN");
        startBtnImg=loader.getImage("start");

        int bntW=200;
        int bntH=70;
        startRect=new Rectangle((width-bntW)/2, (int)(height*0.61), bntW, bntH);

        MapLoader.MapData map=mapLoader.loadMap(world.Level.generic());
        this.walls=map.walls;
        this.ghosts=map.ghosts;
        this.pacman=map.pacman;
    }

    public void setOnStartAction(Runnable onStartAction) {
        this.onStartAction = onStartAction;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, boardWidth, boardHeight);

        drawMap(g2);

        if(titleImg!=null){
            g2.drawImage(titleImg, (boardWidth-300)/2,(int)(boardHeight*0.2), 300, 120, null);
        }

        if(startBtnImg!=null){
            g2.drawImage(startBtnImg, startRect.x, startRect.y, startRect.width, startRect.height, null);
            if(isPressing){
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(startRect.x, startRect.y, startRect.width, startRect.height, 12, 12);
            }
        }
    }

    private void drawMap(Graphics2D g){
        for(Block wall:walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        for(Ghost ghost:ghosts){
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        if(pacman!=null){
            g.drawImage(pacman.image, pacman.x, pacman.y,  pacman.width, pacman.height, null);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (startRect.contains(e.getPoint())) {
            isPressing = true;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isPressing && startRect.contains(e.getPoint())) {
            isPressing = false;
            onStartAction.run();
        }
        isPressing = false;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode=e.getKeyCode();

        if(keyCode==KeyEvent.VK_S || keyCode==KeyEvent.VK_ENTER){
            startGame();
        }
    }

    private void startGame() {
        if(onStartAction!=null){
            onStartAction.run();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
