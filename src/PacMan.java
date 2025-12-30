import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener, MouseListener {
    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;

        char direction='U';
        int velocityX=0;
        int velocityY=0;

        public Block(int x, int y, int width, int height, Image image){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction){
            char prevDirection=this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for(Block wall:walls){
                if(collision(this, wall))
                {
                    this.x-=this.velocityX;
                    this.y-=this.velocityY;
                    this.direction=prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity(){
            if(this.direction=='U'){
                this.velocityX=0;
                this.velocityY=-tileSize/4;
            }
            else if(this.direction=='D'){
                this.velocityX=0;
                this.velocityY=tileSize/4;
            }
            else if(this.direction=='L'){
                this.velocityX=-tileSize/4;
                this.velocityY=0;
            }
            else if(this.direction=='R'){
                this.velocityX=tileSize/4;
                this.velocityY=0;
            }
        }

        void reset()
        {
            this.x=this.startX;
            this.y=this.startY;
        }
    }

    private int rowCnt=22;
    private int colCnt=19;
    private int tileSize=32;
    private int boardWidth=colCnt*tileSize;
    private int boardHeight=rowCnt*tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;
    private Image pacmanClosedImage;

    private Image pauseImage;

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] direction={'U','D','L','R'};
    Random rand=new Random();
    int score=0;
    int lives=3;
    boolean gameOver=false;
    boolean isPaused=false;
    char pendingDirection=' ';
    int pendingFrames=0;
    final int MAX_PENDINGS_FRAMES=4;

    boolean isEating=false;
    int eatFrame=0;
    final int EAT_ANIMATION_SPEED=1;

    int ghostMoveCnt=0;
    final int GHOST_DIRECTION_CHANGE=15;

    float pauseScale = 1.0f;
    boolean scaleUp = true;

    boolean pauseButtonAnimating = false;
    int pauseAnimFrame = 0;
    final int PAUSE_ANIM_FRAMES = 4;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "OOOOOOOOOOOOOOOOOOO",
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };


    public PacMan(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);
        addMouseListener(this);

        wallImage=new ImageIcon(getClass().getResource("images/wall.png")).getImage();
        blueGhostImage=new ImageIcon(getClass().getResource("images/blueGhost.png")).getImage();
        orangeGhostImage=new ImageIcon(getClass().getResource("images/orangeGhost.png")).getImage();
        pinkGhostImage=new ImageIcon(getClass().getResource("images/pinkGhost.png")).getImage();
        redGhostImage=new ImageIcon(getClass().getResource("images/redGhost.png")).getImage();

        pacmanUpImage=new ImageIcon(getClass().getResource("images/pacmanUp.png")).getImage();
        pacmanDownImage=new ImageIcon(getClass().getResource("images/pacmanDown.png")).getImage();
        pacmanLeftImage=new ImageIcon(getClass().getResource("images/pacmanLeft.png")).getImage();
        pacmanRightImage=new ImageIcon(getClass().getResource("images/pacmanRight.png")).getImage();
        pacmanClosedImage=new ImageIcon(getClass().getResource("images/pacmanClosed.png")).getImage();

        pauseImage=new ImageIcon(getClass().getResource("images/pause.png")).getImage();

        loadMap();
        int dirIndex=0;
        for(Block ghost:ghosts){
            ghost.updateDirection(direction[dirIndex%4]);
            dirIndex++;
        }
        gameLoop=new Timer(50,this);
        gameLoop.start();
    }

    public void loadMap()
    {
        walls=new HashSet<Block>();
        foods=new HashSet<Block>();
        ghosts=new HashSet<Block>();

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
                    Block wall=new Block(x, y, tileSize, tileSize, wallImage);
                    walls.add(wall);
                }
                else if(tileMapChar=='b')
                {
                    Block ghost=new Block(x, y, tileSize, tileSize, blueGhostImage);
                    ghosts.add(ghost);
                }
                else if(tileMapChar=='o')
                {
                    Block ghost=new Block(x, y, tileSize, tileSize, orangeGhostImage);
                    ghosts.add(ghost);
                }
                else if(tileMapChar=='p')
                {
                    Block ghost=new Block(x, y, tileSize, tileSize, pinkGhostImage);
                    ghosts.add(ghost);
                }
                else if(tileMapChar=='r')
                {
                    Block ghost=new Block(x, y, tileSize, tileSize, redGhostImage);
                    ghosts.add(ghost);
                }
                else if(tileMapChar=='P')
                {
                    pacman=new Block(x, y, tileSize, tileSize, pacmanRightImage);
                }
                else if(tileMapChar==' ')
                {
                    Block food=new Block(x+14, y+14, 4, 4, null);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g)
    {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for(Block ghost:ghosts)
        {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for(Block wall:walls)
        {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.white);
        for(Block food:foods)
        {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        int lifeSize=28;
        int lifeSpacing=3;
        int lifeX=tileSize/2;
        int lifeY=tileSize/2-lifeSize+12;

        for(int i=0; i<lives; i++)
        {
            g.drawImage(pacmanRightImage, lifeX+i*lifeSize-10, lifeY, lifeSize, lifeSize, null);
        }
        if(gameOver)
        {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else
        {
            int scoreX=lifeX+lives*lifeSize;
            int scoreY=tileSize/2+6;
            g.drawString(" Score: " + String.valueOf(score), scoreX, scoreY);
        }

        if(isPaused)
        {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, boardWidth, boardHeight);

            int imgW = 200;
            int imgH = 70;
            int imgX = (boardWidth - imgW) / 2;
            int imgY = (boardHeight - imgH) / 2;

            g.drawImage(pauseImage, imgX, imgY, imgW, imgH, null);

            if(pauseButtonAnimating)
            {
                g.setColor(new Color(0, 0, 0, 80));
                g.fillRoundRect(imgX, imgY, imgW, imgH, 12, 12);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String resumeText = "Apasă pe imagine sau P pentru a continua";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(resumeText);

            g.drawString(resumeText, (boardWidth - textWidth) / 2, imgY + 70 + 30);
        }
    }

    public boolean canMove(char dir)
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

        Block testBlock = new Block(testX, testY, pacman.width, pacman.height, null);

        for(Block wall:walls)
        {
            if(collision(testBlock,wall))
                return false;
        }
        return true;
    }

    public void autoAlign(char newDirection)
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
                {
                    pacman.x=targetX;
                }
            }
            else
            {
                int targetY=pacman.y+tileSize/2;
                int diff=Math.abs(pacman.y-targetY);
                if(diff<=12)
                {
                    pacman.y=targetY;
                }
            }
        }
    }

    public void move()
    {
        if(isEating)
        {
            eatFrame++;
            if(eatFrame >= EAT_ANIMATION_SPEED)
            {
                isEating=false;
                eatFrame=0;
                updatePacmanImage();
            }
        }

        if(pendingDirection != ' ' && pendingDirection != pacman.direction)
        {
            pendingFrames++;

            if(canMove(pendingDirection))
            {
                autoAlign(pendingDirection);
                pacman.updateDirection(pendingDirection);
                if(!isEating)
                    updatePacmanImage();
                updatePacmanImage();
                pendingDirection= ' ';
                pendingFrames=0;
            }
            else if(pendingFrames >= MAX_PENDINGS_FRAMES)
            {
                pendingDirection= ' ';
                pendingFrames=0;
            }
        }

        pacman.x+=pacman.velocityX;
        pacman.y+=pacman.velocityY;

        if(pacman.x+pacman.width < 0)
        {
            pacman.x=boardWidth;
        }
        else if(pacman.x > boardWidth)
        {
            pacman.x=-pacman.width;
        }

        for(Block wall:walls)
        {
            if(collision(pacman, wall))
            {
                pacman.x-=pacman.velocityX;
                pacman.y-=pacman.velocityY;
                break;
            }
        }

        for(Block ghost:ghosts)
        {
            if(collision(pacman, ghost))
            {
                lives-=1;
                if(lives==0)
                {
                    gameOver=true;
                    return;
                }
                resetPositions();
            }
            if(ghost.y==tileSize*9 && ghost.direction!='U' && ghost.direction!='D')
            {
                ghost.updateDirection('U');
            }
            ghost.x+=ghost.velocityX;
            ghost.y+=ghost.velocityY;

            boolean needsNewDirection=false;

            for(Block wall:walls)
            {
                if(collision(ghost, wall) || ghost.x <= 0 || ghost.x+ghost.width >= boardWidth)
                {
                    ghost.x-=ghost.velocityX;
                    ghost.y-=ghost.velocityY;
                    needsNewDirection=true;
                    break;
                }
            }

            if(needsNewDirection || (ghostMoveCnt % GHOST_DIRECTION_CHANGE == 0))
            {
                char newDirection = getGhostDirection(ghost);
                ghost.updateDirection(newDirection);
            }
        }

        ghostMoveCnt++;

        Block foodEaten=null;
        for(Block food:foods)
        {
            if(collision(pacman, food))
            {
                foodEaten=food;
                score+=10;

                isEating=true;
                eatFrame=0;
                pacman.image=pacmanClosedImage;
            }
        }
        foods.remove(foodEaten);

        if(foods.isEmpty())
        {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b)
    {
        return a.x < b.x+b.width &&
                a.x+a.width > b.x &&
                a.y < b.y+b.height &&
                a.y+a.height > b.y;
    }

    public void resetPositions()
    {
        pacman.reset();
        pacman.velocityX=0;
        pacman.velocityY=0;
        for(Block ghost:ghosts)
        {
            ghost.reset();
            char newDirection=direction[rand.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    public char getGhostDirection(Block ghost)
    {
        java.util.ArrayList<Character> validDir=new java.util.ArrayList<>();
        for(char dir:direction) {
            if ((ghost.direction == 'U' && dir == 'D') ||
                    (ghost.direction == 'D' && dir == 'U') ||
                    (ghost.direction == 'L' && dir == 'R') ||
                    (ghost.direction == 'R' && dir == 'L')) {
                continue;
            }

            int testX = ghost.x;
            int testY = ghost.y;

            if (dir == 'U')
                testY -= tileSize / 4;
            else if (dir == 'D')
                testY += tileSize / 4;
            else if (dir == 'R')
                testX += tileSize / 4;
            else if (dir == 'L')
                testX -= tileSize / 4;

            Block testBlock = new Block(testX, testY, ghost.width, ghost.height, null);

            boolean canMove = true;
            for (Block wall : walls) {
                if (collision(testBlock, wall)) {
                    canMove = false;
                    break;
                }
            }

            if (canMove && testX > 0 && testX + ghost.width < boardWidth)
                validDir.add(dir);
        }

        if(validDir.isEmpty())
            {
                if(ghost.direction == 'U') return 'D';
                if(ghost.direction == 'D') return 'U';
                if(ghost.direction == 'L') return 'R';
                if(ghost.direction == 'R') return 'L';
            }

        if(rand.nextInt(100) < 30 && !validDir.isEmpty())
            {
                int dx = pacman.x - ghost.x;
                int dy = pacman.y - ghost.y;

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

    public void updatePacmanImage() {
            if(pacman.direction == 'U') {
                pacman.image = pacmanUpImage;
            } else if(pacman.direction == 'D') {
                pacman.image = pacmanDownImage;
            } else if(pacman.direction == 'L') {
                pacman.image = pacmanLeftImage;
            } else if(pacman.direction == 'R') {
                pacman.image = pacmanRightImage;
            }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!isPaused) {
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
        if(gameOver){
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
        {
            isPaused = !isPaused;
        }

        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);

        if(e.getKeyCode() == KeyEvent.VK_UP) pendingDirection='U';
        else if(e.getKeyCode() == KeyEvent.VK_DOWN) pendingDirection='D';
        else if(e.getKeyCode() == KeyEvent.VK_LEFT) pendingDirection='L';
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) pendingDirection='R';

        if(gameOver){
            loadMap();
            resetPositions();
            lives=3;
            score=0;
            gameOver=false;
            gameLoop.start();
        }
        if(e.getKeyCode()==KeyEvent.VK_UP)
        {
            pacman.updateDirection('U');
        }
        else if(e.getKeyCode()==KeyEvent.VK_DOWN)
        {
            pacman.updateDirection('D');
        }
        else if(e.getKeyCode()==KeyEvent.VK_LEFT)
        {
            pacman.updateDirection('L');
        }
        else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
        {
            pacman.updateDirection('R');
        }

        if(pacman.direction=='U')
        {
            pacman.image=pacmanUpImage;
        }
        else if(pacman.direction=='D')
        {
            pacman.image=pacmanDownImage;
        }
        else if(pacman.direction=='L')
        {
            pacman.image=pacmanLeftImage;
        }
        else if(pacman.direction=='R')
        {
            pacman.image=pacmanRightImage;
        }

        if(!isEating)
            updatePacmanImage();
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
