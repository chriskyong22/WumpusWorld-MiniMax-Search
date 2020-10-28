package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML
    private TextField depthField;
    @FXML
    private Label valueLabel;
    @FXML
    private ListView<String> heuristicList;
    @FXML
    private Label currLabel;
    @FXML
    private Label legendLabel;

    Grid g = new Grid();
    Logic l = new Logic(g,3);
    String[] heuristics = new String[]{"1. Distance To Pits","2. Closest Killable Enemy","3. Difference in Pieces","4. Total Pieces", "5. Maximum Distance From Threat", "6. Weighted Heuristics 1-5"};
    Point start = null;
    Point goal = null;


    @FXML
    public void initialize(){
        heuristicList.getItems().addAll(heuristics);
        legendLabel.setText("Legend: Blue - Player Pieces, Green - AI Pieces.\nDefault Depth: 3.\nDefault Heuristic: Heuristic 0.");
    }


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
     * Sets the search depth per user selection. Default is 3.
     */
    @FXML
    public void selectDepth(){
        l = new Logic(g,(int) Double.parseDouble(depthField.getText()));
    }

    /**
     * Builds a grid in the output pane
     * Color Coding:
     * player piece - blue
     * ai piece - green
     * @param g - grid to be built
     */
    public void buildGrid(Grid g){
        //clear pane
        gridPane.getChildren().clear();

        int dim = g.getMapSize();
        int size = 75;
        int offset = 200;
        int gap = 5;
        ImageView[][] cells = new ImageView[dim][dim];
        Image phero = new Image("/sample/phero.png");
        Image aihero = new Image("/sample/aihero.png");
        Image pit = new Image("/sample/pit.png");
        Image aimage = new Image("/sample/aimage.jpg");
        Image pmage = new Image("/sample/pmage.jpg");
        Image pwumpus = new Image("/sample/pwumpus.png");
        Image aiwumpus = new Image("/sample/aiwumpus.png");
        Image empty = new Image("/sample/empty.png");

        for (int y = 0; y < dim; y++){
            for (int x = 0; x < dim; x++){
                final int finalX = x;
                final int finalY = y;
                cells[x][y] = new ImageView();
                cells[x][y].setFitHeight(size);
                cells[x][y].setFitWidth(size);
                cells[x][y].setX(offset + x*(size + gap));
                cells[x][y].setY(offset + y*(size + gap));
                cells[x][y].setOnMouseClicked(e -> select(finalX,finalY));
                Cell currCell = g.map[y][x];
                Image hero = null;
                Image wumpus = null;
                Image mage = null;


                if(currCell.belongToPlayer() == '1'){
                    hero = phero;
                    wumpus = pwumpus;
                    mage = pmage;
                }
                else if (currCell.belongToPlayer() == '2'){
                    hero = aihero;
                    wumpus = aiwumpus;
                    mage = aimage;
                }


                switch(currCell.getType()){
                    case 'E':
                        cells[x][y].setImage(empty);
                        break;
                    case 'H':
                        cells[x][y].setImage(hero);
                        break;
                    case 'M':
                        cells[x][y].setImage(mage);
                        break;
                    case 'W':
                        cells[x][y].setImage(wumpus);
                        break;
                    case 'P':
                        cells[x][y].setImage(pit);
                        break;
                }

                gridPane.getChildren().add(cells[x][y]);

            }
        }
    }

    /**
     * Selects a start/goal pair based on user input.
     * @param x - cell x coordinate
     * @param y - cell y coordinate
     */
    private void select(int x, int y){
        if (start == null){
            start = new Point(x,y);
            currLabel.setText("Current Selection: [" + x + "," + y + "]");
            return;
        }
        else{
            goal = new Point(x,y);
        }
        Cell startCell = g.getCell((int) start.getY(),(int) start.getX());
        Cell goalCell = g.getCell((int) goal.getY(),(int) goal.getX());
        if(l.validPlayerMove(startCell,goalCell)) {
            l.move(startCell, goalCell);
            buildGrid(g);
            currLabel.setText("Moving to: [" + x + "," + y + "]");
        }
        start = null;
        goal = null;
    }

    /**
     * Runs the AI's move and checks if the game is over.
     */
    @FXML
    private void nextTurn(){
        double val;
        int heuristicSelected = heuristicList.getSelectionModel().getSelectedIndex();
        if(heuristicSelected == -1){
            val = l.run(0);
        }
        else{val = l.run(heuristicSelected);}

        valueLabel.setText("Move Value: \n" + val);
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
