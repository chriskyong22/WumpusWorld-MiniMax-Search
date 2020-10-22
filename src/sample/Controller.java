package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import sample.back.*;

public class Controller {

    //inject FXML objects
    @FXML
    private Label gameStatusLabel;
    @FXML
    private TextField dimField;
    @FXML
    private Pane gridPane;

    Grid g = new Grid();

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
                cells[x][y] = new Rectangle(size,size);
                cells[x][y].setX(offset + x*(size + gap));
                cells[x][y].setY(offset + y*(size + gap));

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
}
