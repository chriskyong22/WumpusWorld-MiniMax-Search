import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Logic {

    private Grid map;
    private int depth;
    private Move bestMove;

    public Logic(Grid map, int depthSearch){
        this.map = map;
        this.depth = depthSearch;
    }

    public void run(int heuristicSelected){
        double value = alphabeta(map, this.depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, heuristicSelected);
        System.out.println("The value is: " + value);
        map.printMap();
        System.out.println("\n\n\n\n");
        move(map.getCell(bestMove.getOrigin().getRow(), bestMove.getOrigin().getCol()),  map.getCell(bestMove.getGoal().getRow(), bestMove.getGoal().getCol()));
        map.printMap();
    }

    public void resetMap(Cell c1, Cell c2){
        map.setCell(c1);
        map.setCell(c2);
    }

    public double calculateHeuristic(int heuristic){ //Heuristics should be in the view of the AI
        switch(heuristic){
            case 0:
                if(map.getAICount() > map.getPlayerCount()){
                    return 100;
                }else if(map.getAICount() == map.getPlayerCount()){
                    return 0;
                }else{
                    return -100;
                }
            default:
                break;
        }
        return -1;
    }
    //Initial Call, a = -infinity | b = +infinity
    public double alphabeta(Grid map, int depth, double a, double b, boolean maximizingPlayer, int heuristicSelected){
        if(depth == 0 || checkWin() != -1){
            return calculateHeuristic(heuristicSelected);
        }
        if(maximizingPlayer){ //AI TURN
            double value = Integer.MIN_VALUE;
            Comparator<Move> newComp = new Comparator<Move>() {
                @Override
                public int compare(Move o1, Move o2) {
                   return Double.compare(o1.getHeuristicValue(), o2.getHeuristicValue());
                }
            };
            PriorityQueue<Move> queue = new PriorityQueue<Move>(11, newComp);
            queue.addAll(allPossibleMoves(1,heuristicSelected));
            while(!queue.isEmpty()){
                Move child = queue.poll();
                int playerCount =  map.getPlayerCount();
                int aiCount = map.getAICount();
                Cell origin = map.getCell(child.getOrigin().getRow(), child.getOrigin().getCol());
                Cell goal = map.getCell(child.getGoal().getRow(), child.getGoal().getCol());
                move(origin, goal);
                double temp = alphabeta(map, depth - 1, a, b, false, heuristicSelected);
                if(depth == this.depth && temp > value){ // Selects the best move for the AI (based on initial state of the board (depth == this.depth)
                    bestMove = child;
                }
                map.setPlayerPieces(playerCount);
                map.setAIPieces(aiCount);
                resetMap(child.getOrigin(), child.getGoal());

                value = Math.max(value, temp);
                a = Math.max(a, value);
                if(a >= b){
                    break;
                }
            }
            return value;
        }else{ //PLAYER TURN
            double value = Integer.MAX_VALUE;
            Comparator<Move> newComp = new Comparator<Move>() {
                @Override
                public int compare(Move o1, Move o2) {
                    if(o1.getHeuristicValue() < o2.getHeuristicValue()){
                        return 1;
                    }else if(o1.getHeuristicValue() == o2.getHeuristicValue()){
                        return 0;
                    }else{
                        return -1;
                    }
                }
            };

            PriorityQueue<Move> queue = new PriorityQueue<Move>(11, newComp);
            queue.addAll(allPossibleMoves(2,heuristicSelected));
            while(!queue.isEmpty()){
                Move child = queue.poll();
                int playerCount =  map.getPlayerCount();
                int aiCount = map.getAICount();
                Cell origin = map.getCell(child.getOrigin().getRow(), child.getOrigin().getCol());
                Cell goal = map.getCell(child.getGoal().getRow(), child.getGoal().getCol());
                move(origin, goal);

                value = Math.min(value, alphabeta(map, depth - 1, a, b, true, heuristicSelected));

                map.setPlayerPieces(playerCount);
                map.setAIPieces(aiCount);
                resetMap(child.getOrigin(), child.getGoal());

                b = Math.min(b, value);
                if(b <= a){
                    break;
                }
            }
            return value;
        }
    }
    /**
     * (Remember to check for invalid move beforehand)
     * start moves to goal
     * @param start Cell to move from
     * @param goal Cell to move to
     */
    public void move(Cell start, Cell goal){
        if(goal.isPit()){ //the piece gets destroyed so negative
            map.destroyCell(start);
        }else if(!goal.isEmpty()){
            int result = battle(start, goal);
            switch (result){
                case 0: //Both pieces should be destroyed (neutral)
                    map.destroyCell(start);
                    map.destroyCell(goal);
                    break;
                case 1: //your piece wins, so opponent gets destroyed
                    map.destroyCell(goal);
                    goal.setPlayerPiece(start.belongToPlayer());
                    goal.setType(start.getType());
                    start.reset();
                    break;
                case 2: //opponent piece wins, so your piece should be destroyed (Opponent +1 pieces over you)
                    map.destroyCell(start);
                    break;
            }
        }else{ //Moving into an empty space, nothing really happens no pieces get destroyed
            goal.setPlayerPiece(start.belongToPlayer());
            goal.setType(start.getType());
            start.reset();
        }
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
        }else if(playerPieces == 0){ //Note do not need aiPieces != 0 because the two case above fulfill it
            return 2;
        }else{
            return -1;
        }
    }

    /**
     * Gives you an ArrayList of Moves that contains the COPY of the cells
     * @param playerOrAI 1 for AI | 2 for Player
     * @param heuristicSelected the chosen heuristic
     * @return An ArrayList of Moves that contains a COPY of the cells
     */
    public ArrayList<Move> allPossibleMoves(int playerOrAI, int heuristicSelected){
        ArrayList<Move> possibleMoves = new ArrayList<Move>();

        switch(playerOrAI){
            case 1: //AI
                ArrayList<Cell> aiCells = map.getAICells();
                for(Cell aiCell : aiCells){
                    ArrayList<Cell> moves = possibleMoves(aiCell);
                    for(Cell move : moves){
                        Cell copyOrigin = aiCell.copy();
                        Cell copyGoal = move.copy();
                        int playerCount =  map.getPlayerCount();
                        int aiCount = map.getAICount();
                        move(aiCell, move);
                        double heuristic = calculateHeuristic(heuristicSelected);
                        resetMap(copyOrigin, copyGoal);
                        map.setPlayerPieces(playerCount);
                        map.setAIPieces(aiCount);
                        possibleMoves.add(new Move(copyOrigin, copyGoal, heuristic));
                    }
                }
                break;
            case 2: //Player
                ArrayList<Cell> playerCells = map.getPlayerCells();
                for(Cell playerCell : playerCells){
                    ArrayList<Cell> moves = possibleMoves(playerCell);
                    for(Cell move : moves){
                        Cell copyOrigin = playerCell.copy();
                        Cell copyGoal = move.copy();
                        int playerCount =  map.getPlayerCount();
                        int aiCount = map.getAICount();
                        move(playerCell, move);
                        double heuristic = calculateHeuristic(heuristicSelected);
                        resetMap(copyOrigin, copyGoal);
                        map.setPlayerPieces(playerCount);
                        map.setAIPieces(aiCount);
                        possibleMoves.add(new Move(copyOrigin, copyGoal, heuristic));
                    }
                }
                break;
            default:
                System.out.println("Error: Generating all possible moves for not player or AI");
                break;
        }
        return possibleMoves;
    }

    public ArrayList<Cell> possibleMoves(Cell c1){
        c1.printCell();
        ArrayList<Cell> moves = map.getNeighbors(c1.getRow(), c1.getCol());
        moves.removeIf(move -> !isValidMove(c1, move));
        return moves;
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
