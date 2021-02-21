package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Pong_MiguelAngel_Torres extends Application {

    public static Circle cercle;
    public static Pane canvas;
    public static Text GameOver;
    public static Text teclaespacio;
    int punto1 = 0;
    int punto2 = 0;
    int toquepelota = 0;

    //Creación paletas

    class Paleta {
        class Posicio {
            int posX;
            int posY;

            public Posicio(int X, int Y) {
                this.posX = X;
                this.posY = Y;

            }
        }
        Rectangle paleta;
        Posicio posicio;
        int velocitat = 20;

        //Constructor
        public Paleta(int X, int Y) {
            this.posicio = new Posicio(X, Y);
        }

        //Mueve bola arriba
        public void mouAmunt() {
            posicio.posY = posicio.posY - this.velocitat;
            this.repinta();
        }

        //Mueve bola abajo
        public void mouAbaix() {
            posicio.posY = posicio.posY + this.velocitat;
            this.repinta();
        }

        private void repinta() {
            this.paleta.setLayoutY(posicio.posY);
        }
    }

    Paleta paleta1 = new Paleta(10,250);
    Paleta paleta2 = new Paleta(10,250);



    @Override
    public void start(final Stage primaryStage) {

        //Tamaño y color de la pantalla
        canvas = new Pane();
        final Scene escena = new Scene(canvas, 800, 600);
        canvas.setStyle("-fx-background-color: black");

        //Texto puntuacion
        Text puntuacion = new Text(punto1 + "  -  " + punto2);
        puntuacion.setFill(Color.WHITE);
        puntuacion.setFont(new Font(40));
        puntuacion.relocate(350, 10);
        primaryStage.setScene(escena);
        primaryStage.show();
        canvas.getChildren().add(puntuacion);


        //Pelota
        int radi=10;
        cercle = new Circle(radi, Color.WHITE);
        cercle.relocate(400-radi, 300-radi);
        canvas.getChildren().addAll(cercle);

        //Se crea paleta1
        paleta1.paleta = new Rectangle(10,70,Color.WHITE);
        paleta1.paleta.relocate(10,250);
        canvas.getChildren().addAll(paleta1.paleta);

        //Se crea paleta2
        paleta2.paleta = new Rectangle(10,70,Color.WHITE);
        paleta2.paleta.relocate(780,250);
        canvas.getChildren().addAll(paleta2.paleta);

        //Texto pulsa para jugar
        teclaespacio=new Text("Pitja la tecla espai per començar");
        teclaespacio.setFont(new Font("Roboto",25));
        teclaespacio.relocate(270, 450);
        teclaespacio.setFill(Color.WHITE);
        canvas.getChildren().addAll(teclaespacio);

        final Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {

            // Formula en graus
            double angle_en_radians =Math.toRadians(30);
            int velocitat = 2;
            double deltaX = velocitat*Math.cos(angle_en_radians);
            double deltaY = velocitat*Math.sin(angle_en_radians);

            // Simulació gravitatòria
            double temps= 0;
            final Bounds limits = canvas.getBoundsInLocal();

            @Override
            public void handle(final ActionEvent t) {


                //if para que se termine el juego a los 15 puntos
                if (punto1 < 15 && punto2 < 15) {

                    cercle.setLayoutX(cercle.getLayoutX() + deltaX);
                    cercle.setLayoutY(cercle.getLayoutY() + deltaY);

                    final boolean alLimitDret = cercle.getLayoutX() >= (limits.getMaxX() - cercle.getRadius());
                    final boolean alLimitEsquerra = cercle.getLayoutX() <= (limits.getMinX() + cercle.getRadius());
                    final boolean alLimitInferior = cercle.getLayoutY() >= (limits.getMaxY() - cercle.getRadius());
                    final boolean alLimitSuperior = cercle.getLayoutY() <= (limits.getMinY() + cercle.getRadius());

                    final boolean rectangle1infiz = paleta1.paleta.getLayoutY() > limits.getMaxY() - 75;
                    final boolean rectangle2infde = paleta2.paleta.getLayoutY() > limits.getMaxY() - 75;
                    final boolean rectangle1supiz = paleta1.paleta.getLayoutY() <= limits.getMinY();
                    final boolean rectangle2supde = paleta2.paleta.getLayoutY() <= limits.getMinY();

                    if (cercle.getBoundsInParent().intersects(paleta1.paleta.getBoundsInParent()) || cercle.getBoundsInParent().intersects(paleta2.paleta.getBoundsInParent()) ) {
                        //Augmento de velocidad a los 5 toques
                        if (toquepelota == 4 ) {
                            deltaX += 1 * Math.signum(deltaX);
                            toquepelota = 0;
                        } else {
                            toquepelota++;
                        }
                        deltaX *= -1;
                    }

                    //Recoloca la pelota y cuando se marca un gol la pelota pasa a su estado inicial
                    if (alLimitDret || alLimitEsquerra) {
                        //Para que ponga la pelota en el centro
                        cercle.relocate(400 - radi, 300 - radi);
                        deltaY *= -1;
                    }

                    // Pone la puntuación en la izquierda y cuando marca la pelota vuelve a su velocidad del principio
                    if (alLimitEsquerra) {
                        punto1++;
                        puntuacion.setText(punto1 + "  -  " + punto2);
                        deltaX = -velocitat*Math.cos(angle_en_radians);
                        toquepelota = 0;

                    }
                    // Pone la puntuación en la derecha y cuando marca la pelota vuelve a su velocidad del principio
                    if (alLimitDret) {
                        punto2++;
                        puntuacion.setText(punto1 + "  -  " + punto2);
                        deltaX = velocitat*Math.cos(angle_en_radians);
                        toquepelota = 0;
                    }

                    if (alLimitInferior || alLimitSuperior) {
                        deltaY *= -1;
                    }

                    //Limitador de las paletas para que no salgan de la pantalla
                    if (rectangle1infiz) {
                        paleta1.mouAmunt();
                    }
                    if (rectangle2infde) {
                        paleta2.mouAmunt();
                    }
                    if (rectangle1supiz) {
                        paleta1.mouAbaix();
                    }
                    if (rectangle2supde) {
                        paleta2.mouAbaix();
                    }

                }else {
                    //Fin de la partida (texto game over)
                    GameOver = new Text("Game Over");
                    GameOver.setFont(new Font(50));
                    GameOver.relocate(270, 250);
                    GameOver.setFill(Color.WHITE);
                    canvas.getChildren().add(GameOver);

                    paleta1.paleta.setFill(Color.BLACK);
                    paleta2.paleta.setFill(Color.BLACK);
                    puntuacion.setFill(Color.BLACK);
                    cercle.setFill(Color.BLACK);
                }
            }
        }));

        // Hace que le tengas que dar al espacio para poder jugar
        loop.setCycleCount(Timeline.INDEFINITE);
        canvas.requestFocus();
        canvas.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.SPACE)){
                moverpalas();
                loop.play();
                canvas.getChildren().remove(teclaespacio);
            }
        });
    }

    public void moverpalas(){
        canvas.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP : paleta2.mouAmunt(); break;
                case DOWN : paleta2.mouAbaix(); break;
                case W : paleta1.mouAmunt(); break;
                case S : paleta1.mouAbaix(); break;
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }

}