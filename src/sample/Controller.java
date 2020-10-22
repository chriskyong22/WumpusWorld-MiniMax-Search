package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import sample.back.*;

import java.awt.*;

public class Controller {

    //inject FXML objects
    @FXML
    private Label gameStatusLabel;
    @FXML
    private TextField dimField;
    @FXML
    private Pane gridPane;

    Grid g = new Grid();
    Logic l = new Logic(g,3);
    Point start = null;
    Point goal = null;

    /**
    @FXML
    public void initialize(){

    }
**/

    /**
     * Starts a game with the user's chosen dimensions.
     */
    @FXML
    public void startGame(){

        int dimension = Integer.parseInt(dimField.getText());
        g = new Grid(dimension);
        l = new Logic(g,3);
        buildGrid(g);
    }

    /**
     * Builds a grid in the output pane
     * Color Coding:
     * Empty Cell - White
     * Hero Cell - Silver
     * Mage Cell - Blue
     * Wumpus Cell - Brown
     * Pit Cell - Black
     * @param g - grid to be built
     */
    public void buildGrid(Grid g){
        //clear pane
        gridPane.getChildren().clear();

        int dim = g.getMapSize();
        int size = 75;
        int offset = 200;
        int gap = 5;
        Rectangle[][] cells = new Rectangle[dim][dim];


        for (int y = 0; y < dim; y++){
            for (int x = 0; x < dim; x++){
                final int finalX = x;
                final int finalY = y;
                cells[x][y] = new Rectangle(size,size);
                cells[x][y].setX(offset + x*(size + gap));
                cells[x][y].setY(offset + y*(size + gap));
                cells[x][y].setOnMouseClicked(e -> select(finalX,finalY));

                switch(g.map[y][x].getType()){
                    case 'E':
                        cells[x][y].setFill(Color.WHITE);
                        break;
                    case 'H':
                        cells[x][y].setFill(Color.SILVER);
                        break;
                    case 'M':
                        cells[x][y].setFill(Color.BLUE);
                        break;
                    case 'W':
                        cells[x][y].setFill(Color.BROWN);
                        break;
                    case 'P':
                        cells[x][y].setFill(Color.BLACK);
                        break;
                }
                cells[x][y].setStroke(Color.BLACK);
                gridPane.getChildren().add(cells[x][y]);

            }
        }
    }

    private void select(int x, int y){
        if (start == null){
            start = new Point(x,y);
            return;
        }
        else{
            goal = new Point(x,y);
        }
        Cell startCell = g.getCell((int) start.getY(),(int) start.getX());
        Cell goalCell = g.getCell((int) goal.getY(),(int) goal.getX());
        if(l.isValidMove(startCell,goalCell)) {
            l.move(startCell, goalCell);
            buildGrid(g);
        }
        start = null;
        goal = null;
    }

    @FXML
    private void nextTurn(){
        l.run(0);
        buildGrid(g);
        int gameCon = l.checkWin();
        switch (gameCon){
            case 0:
                gameStatusLabel.setText("Game Status: \n It's a Draw");
                break;
            case 1:
                gameStatusLabel.setText("Game Status: \n Player Wins");
                break;
            case 2:
                gameStatusLabel.setText("Game Status: \n AI Wins");
                break;
            default:
                gameStatusLabel.setText("Game Status: \n In Progress");
                break;
        }
    }
}
