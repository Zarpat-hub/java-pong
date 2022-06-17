package sample;

public class Ball {
    private int radius;
    private int posY;
    private int posX;

    public int getPosY(){
        return  posY;
    }
    public int getPosX(){
        return posX;
    }
    public int getRadius(){
        return radius;
    }

    public void setPosY(int posY){
        this.posY = posY;
    }
    public void setPosX(int posX){
        this.posX = posX;
    }

    public Ball(int posY, int posX, int radius){
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
    }
}
