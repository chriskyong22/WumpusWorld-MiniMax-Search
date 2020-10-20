public class Grid {
    private int gridSize;
    private Cell[][] map;

    public Grid(){
        this.gridSize = 3;
        initializeMap();
    }
    public Grid(int gridSize){
        this.gridSize = gridSize;
        initializeMap();
    }

    public void initializeMap(){
        map = new Cell[gridSize][gridSize];
        for(int row = 1; row < gridSize - 1; row++){
            for(int column = 0; column < gridSize; column++){
                map[row][column] = new Cell();
            }
            int numOfPits = (gridSize/3) - 1;
            while(numOfPits > 0){
                int pitCol = (int) (Math.random() * gridSize);
                if(map[row][pitCol].getType() == 'E'){
                    map[row][pitCol].setType('P');
                    numOfPits--;
                }
            }
        }
        for(int col = 0; col < gridSize; col++){
            char type = 'W';
            switch ((col%3)){
                case 0:
                    type = 'W';
                    break;
                case 1:
                    type = 'H';
                    break;
                case 2:
                    type = 'M';
                    break;
                default:
                    System.out.println("Error: the initialization of the hero/mage/wumpus is wrong");
                    break;
            }
            map[0][col] = new Cell(type);
            map[gridSize-1][col] = new Cell(type);
        }
    }

    public void printMap(){
        for(int row = 0; row < gridSize; row++){
            for(int col = 0; col < gridSize; col++){
                System.out.print(map[row][col].getType());
            }
            System.out.println();
        }
    }

}
