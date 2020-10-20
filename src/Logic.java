public class Logic {

    private Grid map;
    private int depth;

    public Logic(Grid map, int depthSearch){
        this.map = map;
        this.depth = depthSearch;
    }

    public void run(){

    }

    /**
     * (Remember to check for invalid move beforehand)
     * start moves to goal
     * @param start
     * @param goal
     * @return value of this move
     */
    public int move(Cell start, Cell goal){
        int value = 0;
        if(goal.isPit()){ //the piece gets destroyed so negative
            map.destroyCell(start);
            value = -1000;
        }else if(!goal.isEmpty()){
            int result = battle(start, goal);
            switch (result){
                case 0: //Both pieces should be destroyed (neutral)
                    map.destroyCell(start);
                    map.destroyCell(goal);
                    value = 0;
                    break;
                case 1: //your piece wins, so opponent piece gets destroyed
                    goal.setPlayerPiece(start.belongToPlayer());
                    goal.setType(start.getType());
                    map.destroyCell(start);
                    value = 1000;
                    break;
                case 2: //opponent piece wins, so your piece should be destroyed (Opponent +1 pieces over you)
                    map.destroyCell(start);
                    value = -1100;
                    break;
            }
        }else{ //Moving into an empty space, nothing really happens no pieces get destroyed
            goal.setPlayerPiece(start.belongToPlayer());
            goal.setType(start.getType());
            map.destroyCell(start);
            value = 0;
        }
        return value;
    }

    /**
     * Check if a win condition has been met
     * @return -1 if no win condition has been met, 0 if draw, 1 if Player wins, 2 if AI wins
     */
    public int checkWin(){
        int playerPieces = map.getPlayerCount();
        int aiPieces = map.getAICount();
        if(playerPieces == 0 && aiPieces == 0){
            return 0;
        }else if(playerPieces != 0 && aiPieces == 0){
            return 1;
        }else if(playerPieces == 0 && aiPieces != 0){
            return 2;
        }else{
            return -1;
        }
    }

    public boolean isValidMove(Cell cell1, Cell cell2){
        return !(cell1.belongToPlayer() == '1' && cell2.belongToPlayer() == '1' ||
                cell1.belongToPlayer() == '2' && cell2.belongToPlayer() == '2');
    }

    /**
     *
     * @param cell1
     * @param cell2
     * @return 1 if cell1 wins, 0 if draw, 2 if Cell1 loses
     */
    public int battle(Cell cell1, Cell cell2){
        if(cell1.getType() == cell2.getType()){
            return 0;
        }
        if(cell1.getType() == 'H' && cell2.getType() == 'W' ||
            cell1.getType() == 'M' && cell2.getType() == 'H' ||
            cell1.getType() == 'W' && cell2.getType() == 'M'){
            return 1;
        }
        return 2;
    }

}
