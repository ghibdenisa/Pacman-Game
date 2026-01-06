package core;

public class GameState {
    private int score;
    private int lives;
    private boolean gameOver;
    private boolean paused;

    private static final int INITIAL_LIVES = 3;

    public GameState() {
        reset();
    }

    public void reset() {
        score = 0;
        lives = INITIAL_LIVES;
        gameOver = false;
        paused = false;
    }

    public void addScore(int points) {
        score+=points;
    }

    public void loseLife(){
        lives--;
        if(lives<=0)
            gameOver = true;
    }

    public void togglePaused(){
        if(!gameOver)
            paused = !paused;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPaused() {
        return paused;
    }
}
