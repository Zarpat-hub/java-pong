package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;

public class GameEngine {
    private final int HEIGHT = 600;
    private final int WIDTH = 1200;
    private final int LINE_WIDTH = 5;
    private final int LINE_HEIGHT = 15;
    private final int playerWidth = 30;
    private final int playerHeight = 100;
    private final int playerOffsetOnMove = 5;
    private int ballXspeed = 5;
    private int ballYspeed = 5;
    private final int D = 8;
    private int scoreP1 = 0;
    private int scoreP2 = 0;
    private boolean start = false;

    Player player1 = new Player(0,HEIGHT/2 - playerHeight/2);
    Player player2 = new Player(WIDTH-playerWidth,HEIGHT/2 - playerHeight/2);
    Ball ball = new Ball(HEIGHT/2 - D/2,WIDTH/2-D/2,D);

    public void Init(Stage stage){
        Group root = new Group();
        Scene s = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        final Canvas canvas = new Canvas(s.getWidth(),s.getHeight());
        canvas.setFocusTraversable(true);
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        final DoubleProperty x  = new SimpleDoubleProperty();
        final DoubleProperty y  = new SimpleDoubleProperty();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(x, 0),
                        new KeyValue(y, 0)
                ),
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(x, WIDTH ),
                        new KeyValue(y, HEIGHT)
                )
        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Timeline.INDEFINITE);

        AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                if(start==true) {
                    DrawPlayer(canvas, Color.DARKBLUE, player2);
                    DrawPlayer(canvas, Color.DARKBLUE, player1);
                    DrawBall(canvas, Color.YELLOW, ball);
                    DrawScore(canvas);
                    gc.clearRect(ball.getPosX() - ballXspeed, ball.getPosY() - ballYspeed, D, D);
                    try {
                        AnalyzeBall(ball);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    AnalyzeScore();
                    ball.setPosX(ball.getPosX() + ballXspeed);
                    ball.setPosY(ball.getPosY() + ballYspeed);
                }
                else{
                    ball.setPosX(WIDTH/2-D/2);
                    ball.setPosY(HEIGHT/2-D/2);
                    ballXspeed = 5;
                    ballYspeed = 5;
                    player1.setPlayerY(HEIGHT/2-playerHeight/2);
                    player1.setPlayerX(0);
                    player2.setPlayerY(HEIGHT/2-playerHeight/2);
                    player2.setPlayerX(WIDTH-playerWidth);
                    gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
                    DrawScore(canvas);
                    DrawPlayer(canvas,Color.DARKBLUE,player1);
                    DrawPlayer(canvas,Color.DARKBLUE,player2);
                }
            }
        };

        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()){
                    case UP:
                        player2.setPlayerY(player2.getPlayerY()-playerOffsetOnMove);
                        gc.clearRect(player2.getPlayerX(), player2.getPlayerY()+playerOffsetOnMove, playerWidth, playerHeight);
                        break;
                    case DOWN:
                        player2.setPlayerY(player2.getPlayerY()+5);
                        gc.clearRect(player2.getPlayerX(), player2.getPlayerY()-playerOffsetOnMove, playerWidth, playerHeight);
                        break;
                    case W:
                        player1.setPlayerY(player1.getPlayerY()-playerOffsetOnMove);
                        gc.clearRect(player1.getPlayerX(), player1.getPlayerY()+playerOffsetOnMove, playerWidth, playerHeight);
                        break;
                    case S:
                        player1.setPlayerY(player1.getPlayerY()+5);
                        gc.clearRect(player1.getPlayerX(), player1.getPlayerY()-playerOffsetOnMove, playerWidth, playerHeight);
                        break;
                }
            }
        });
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(start==false) start=true;
            }
        });

        root.getChildren().add(canvas);

        stage.setScene(s);
        stage.show();

        timer.start();
        timeline.play();
    }

    private void AnalyzeBall(Ball ball) throws InterruptedException {

        if(ball.getPosX() < 0){
            scoreP2++;
            start=false;
        }
        if(ball.getPosX() > WIDTH){
            scoreP1++;
            start=false;
        }

        if(ball.getPosY()+D > HEIGHT && (ballXspeed > 0 || ballXspeed <0) )
            ballYspeed *= -1;
        if(ball.getPosX()+D > player2.getPlayerX() && ball.getPosY() >= player2.getPlayerY() && ball.getPosY() <= player2.getPlayerY()+playerHeight)
            ballXspeed *= -1;
        if(ball.getPosY() - D < 0 && ballYspeed < 0)
            ballYspeed *= -1;
        if(ball.getPosX()  < player1.getPlayerX() + playerWidth && ball.getPosY() >= player1.getPlayerY() && ball.getPosY() <= player1.getPlayerY()+playerHeight)
            ballXspeed *= -1;
    }

    private void AnalyzeScore(){
        if(scoreP1==5 || scoreP2 ==5){
            try {
                FileWriter writer = new FileWriter("scores.txt",true);
                writer.append(scoreP1==5 ? "Player 1 wins "+scoreP1+"-"+scoreP2 : "Player 2 wins "+scoreP2+"-"+scoreP1);
                writer.append("\n------------------------------------\n");
                scoreP1=0;
                scoreP2=0;
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void DrawPlayer(Canvas canvas, Color color, Player player){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(player.getPlayerX(),player.getPlayerY(),playerWidth,playerHeight);
    }

    private void DrawBall(Canvas canvas, Color color, Ball ball) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(ball.getPosX(), ball.getPosY(), D, D);
    }

    private void DrawScore(Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(24));
        gc.fillText(Integer.toString(scoreP1),120,100);
        gc.fillText(Integer.toString(scoreP2),1080,100);
    }
}
