package sample;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {

    private int playerY;
    private int playerX;

    public void setPlayerY(int playerY){
        this.playerY = playerY;
    }
    public int getPlayerY(){
        return playerY;
    }
    public void setPlayerX(int playerX){
        this.playerX = playerX;
    }
    public int getPlayerX(){
        return playerX;
    }

    public Player(int posX, int posY){
       this.playerX = posX;
       this.playerY = posY;
    }
}
