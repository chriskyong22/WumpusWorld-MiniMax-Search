package sample.back;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.lang.Math;

public class Logic {

    private Grid map;
    private int depth;
    private Move bestMove;

    public Logic(Grid map, int depthSearch) {
        this.map = map;
        this.depth = depthSearch;
    }

    public double run(int heuristicSelected){
        /**
        if(checkWin() != -1){
            System.out.println("Game over!\n");
            return -1;
        }
         **/
        double value = alphabeta(this.map, this.depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, heuristicSelected);
        System.out.println("The value is: " + value);
        //map.printMap();
        //System.out.println("\n\n\n\n");
        move(map.getCell(bestMove.getOrigin().getRow(), bestMove.getOrigin().getCol()),  map.getCell(bestMove.getGoal().getRow(), bestMove.getGoal().getCol()));
        map.printMap();
        return value;
    }

    public void resetMap(Cell c1, Cell c2){
        map.setCell(c1);
        map.setCell(c2);
    }

    public double averageDistanceToPits(boolean AI) {
        double totalAverageDist = 0;

        ArrayList<Cell> piecesToUse = AI ? this.map.getAICells() : this.map.getPlayerCells();
        if (piecesToUse.size() == 0) {
            return 0;
        }

        for (Cell cell : piecesToUse) {
            double totalDist = 0;
            int x1 = cell.getRow();
            int y1 = cell.getCol();
            for (Cell pitCell : this.map.getPitLocations()) {
                int x2 = pitCell.getRow();
                int y2 = pitCell.getCol();
                double euclideanDist = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
                totalDist += euclideanDist;
            }
            totalAverageDist += (totalDist / this.map.getPitLocations().size());
        }

        return totalAverageDist / this.map.getMapSize();
    }

    public ArrayList<Cell> getKillableEnemyLocations(Cell cell) {
        Character piece = cell.getType();

        ArrayList<Cell> killableEnemyLocations = new ArrayList<>();
        HashSet<Character> killableEnemies = new HashSet<>();

        switch (piece) {
            case 'W':
                //killableEnemies.add('W');
                killableEnemies.add('M');
                break;
            case 'H':
                //killableEnemies.add('H');
                killableEnemies.add('W');
                break;
            case 'M':
                //killableEnemies.add('M');
                killableEnemies.add('H');
                break;
        }

        for (int i = 0; i < map.getMapSize(); i++) {
            for (int j = 0; j < map.getMapSize(); j++) {
                // check if the current piece is an opposing player
                if ((cell.belongToPlayer() == '1' && this.map.getCell(i,j).belongToPlayer() == '2') ||
                (cell.belongToPlayer() == '2' && this.map.getCell(i,j).belongToPlayer() == '1')) {
                    //check if this enemy is killable, given our piece
                    if (killableEnemies.contains(this.map.getCell(i,j).getType())) {
                        killableEnemyLocations.add(map.getCell(i,j));
                    }
                }
            }
        }

        return killableEnemyLocations;
    }

    public double getAvgClosestKillableEnemy(boolean AI) {
        double totalMinDist = 0;

        ArrayList<Cell> piecesToUse = AI ? this.map.getAICells() : this.map.getPlayerCells();
        if (piecesToUse.size() == 0) {
            return 0;
        }

        for (Cell cell : piecesToUse) {
            double curMinDist = Integer.MAX_VALUE;
            ArrayList<Cell> killableEnemyLocations = getKillableEnemyLocations(cell);
            if (killableEnemyLocations.size() == 0) {
                continue;
            }
            int x1 = cell.getRow();
            int y1 = cell.getCol();

            for (Cell killableEnemyLocation : killableEnemyLocations) {
                int x2 = killableEnemyLocation.getRow();
                int y2 = killableEnemyLocation.getCol();
                double euclideanDist = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
                curMinDist = Math.min(curMinDist, euclideanDist);
            }

            totalMinDist += curMinDist;
        }

        return -1 * totalMinDist;
    }

    public double calculatePiecesDifference(boolean AI) {
        // base cases
        if (AI && map.getAICount() == 0)
            return -1000;
        if (!AI && map.getPlayerCount() == 0)
            return -1000;


        double WEIGHT = 10;

        // get the base calculation for the difference in pieces
        double base;
        if (AI)
            base = (map.getAICount() - map.getPlayerCount()) * WEIGHT;
        else
            base =  (map.getPlayerCount() - map.getAICount()) * WEIGHT;

        //now, we will either add or subtract points from the base calculation depending on the number of our
        //wumpus/hero/mage pieces to the opponent's pieces. We want to prioritize maxing our pieces and minimizing the other players
        // i.e. if we have 2 wumpus and they have 0 hero's, this is an extremely good situation for us since hero kills wumpus.
        int AIwumpus, AIhero, AImage;
        int Pwumpus, Phero, Pmage;
        AIwumpus = AIhero = AImage = Pwumpus = Phero = Pmage = 0;
        for (int i = 0; i < map.getMapSize(); i++) {
            for (int j = 0; j < map.getMapSize(); j++) {
                Cell cell = this.map.getCell(i,j);
                if (cell.belongToPlayer() == '2') {
                    switch(cell.getType()) {
                        case 'W':
                            AIwumpus += 1;
                            break;
                        case 'H':
                            AIhero += 1;
                            break;
                        case 'M':
                            AImage += 1;
                            break;
                    }
                } else if (cell.belongToPlayer() == '1') {
                    switch(cell.getType()) {
                        case 'W':
                            Pwumpus += 1;
                            break;
                        case 'H':
                            Phero += 1;
                            break;
                        case 'M':
                            Pmage += 1;
                            break;
                    }
                }
            }
        }

        int wumpusAdvantage, heroAdvantage, mageAdvantage;
        wumpusAdvantage = heroAdvantage = mageAdvantage = 0;
        if (AI) {
             wumpusAdvantage = AIwumpus - Phero;
             heroAdvantage = AIhero - Pmage;
             mageAdvantage = AImage - Pwumpus;
        } else {
             wumpusAdvantage = Pwumpus - AIhero;
             heroAdvantage = Phero - AImage;
             mageAdvantage = Pmage - AIwumpus;
        }
        base = base + wumpusAdvantage*WEIGHT;
        base = base + heroAdvantage*WEIGHT;
        base = base + mageAdvantage*WEIGHT;

        return base;
    }

    public double calculateTotalPieces(boolean AI) {
        double WEIGHT = 10;

        if (AI) { //AI
            return map.getAICount() * WEIGHT;
        }

        // PLAYER
        return map.getPlayerCount() * WEIGHT;
    }


    private ArrayList<Cell> getThreatLocations(Cell cell) {
        Character piece = cell.getType();

        ArrayList<Cell> threatLocations = new ArrayList<>();
        HashSet<Character> threats = new HashSet<>();

        switch (piece){
            case 'W':
                threats.add('H');
                threats.add('W');
                break;
            case 'H':
                threats.add('M');
                threats.add('H');
                break;
            case 'M':
                threats.add('W');
                threats.add('M');
                break;
        }

        for (int i=0; i<map.getMapSize(); i++){
            for (int j=0; j<map.getMapSize(); j++){
                if ((cell.belongToPlayer() == '1' && this.map.getCell(i,j).belongToPlayer() == '2') ||
                        (cell.belongToPlayer() == '2' && this.map.getCell(i,j).belongToPlayer() == '1')) {
                    if(threats.contains(this.map.getCell(i,j).getType())) {
                        threatLocations.add(map.getCell(i,j));
                    }
                }
            }
        }
        return threatLocations;
    }

    public double getAvgFurthestThreat(boolean AI){
        double totalMaxDist = 0;

        ArrayList<Cell> piecesToUse = AI ? this.map.getAICells() : this.map.getPlayerCells();
        if(piecesToUse.size() == 0){
            return 0;
        }

        for (Cell cell : piecesToUse) {
            double maxDistToEnemy = 0;
            ArrayList<Cell> threatLocations = getThreatLocations(cell);
            if(threatLocations.size() == 0){
                continue;
            }
            int x1 = cell.getRow();
            int y1 = cell.getCol();

            for (Cell threatLocation : threatLocations){
                int x2 = threatLocation.getRow();
                int y2 = threatLocation.getCol();
                double euclideanDist = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
                maxDistToEnemy += euclideanDist;
            }
            totalMaxDist += maxDistToEnemy / threatLocations.size();
        }

        return totalMaxDist / piecesToUse.size();
    }

    private ArrayList<Cell> getSavableAllyLocations(Cell cell) {
        Character piece  = cell.getType();

        ArrayList<Cell> savableAllyLocations = new ArrayList<>();
        HashSet<Character> savableAllies = new HashSet<>();

        switch (piece){
            case 'W':
                savableAllies.add('H');
                break;
            case 'H':
                savableAllies.add('M');
                break;
            case 'M':
                savableAllies.add('W');
                break;
        }

        for (int i=0; i<map.getMapSize(); i++) {
            for (int j = 0; j < map.getMapSize(); j++) {
                if ((cell.belongToPlayer() == '1' && this.map.getCell(i,j).belongToPlayer() == '1') ||
                        (cell.belongToPlayer() == '2' && this.map.getCell(i,j).belongToPlayer() == '2')) {
                    if(savableAllies.contains(this.map.getCell(i, j).getType())){
                        savableAllyLocations.add(map.getCell(i, j));
                    }
                }
            }
        }
        return savableAllyLocations;
    }

    public double getAvgClosestSavableAlly(boolean AI){
        double totalMinDist = 0;

        ArrayList<Cell> piecesToUse = AI ? this.map.getAICells() : this.map.getPlayerCells();
        if (piecesToUse.size() == 0) {
            return 0;
        }

        for (Cell cell : piecesToUse){
            double minDistToAlly = Integer.MAX_VALUE;
            ArrayList<Cell> savableAllyLocations = getSavableAllyLocations(cell);
            if(savableAllyLocations.size() == 0){
                continue;
            }
            int x1 = cell.getRow();
            int y1 = cell.getCol();

            for (Cell savableAllyLocation : savableAllyLocations){
                int x2 = savableAllyLocation.getRow();
                int y2 = savableAllyLocation.getCol();
                double euclideanDist = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
                minDistToAlly = Math.min(minDistToAlly, euclideanDist);
            }
            totalMinDist += minDistToAlly;
        }
        return totalMinDist / piecesToUse.size();
    }



    public double calculateHeuristic(int heuristic, boolean AI) { //Heuristics should be in the view of the AI
        double heuristicVal = 0;

        switch(heuristic){
            case 0:
                heuristicVal = averageDistanceToPits(AI);
                return AI ? heuristicVal : -1 * heuristicVal;

            case 1:
                heuristicVal = getAvgClosestKillableEnemy(AI);
                return AI ? heuristicVal : -1 * heuristicVal;

            case 2:
                heuristicVal = calculatePiecesDifference(AI);
                return AI ? heuristicVal : -1 * heuristicVal;

            case 3:
                heuristicVal = calculateTotalPieces(AI);
                return AI ? heuristicVal : -1 * heuristicVal;

            case 4:
                heuristicVal =
                        0.1*averageDistanceToPits(AI)
                        + (0.2*getAvgClosestKillableEnemy(AI))
                        + 0.5*calculatePiecesDifference(AI)
                        + 0.2*calculateTotalPieces(AI);
                return AI ? heuristicVal : -1*heuristicVal;

            case 5:
                heuristicVal = getAvgFurthestThreat(AI);
                return AI ? heuristicVal : -1 * heuristicVal;

            default:
                return 0;
        }
    }
    //Initial Call, a = -infinity | b = +infinity
    public double alphabeta(Grid map, int depth, double a, double b, boolean maximizingPlayer, int heuristicSelected) {
        if(depth == 0 || checkWin() != -1){
            return calculateHeuristic(heuristicSelected, maximizingPlayer);
        }
        if(maximizingPlayer) { //AI TURN
            double maxEvaluation = Integer.MIN_VALUE;

            // lambda expression ensures highest heuristic value will be at top.
            PriorityQueue<Move> queue = new PriorityQueue<Move>(11, (Move m1, Move m2) -> Double.compare(m2.getHeuristicValue(), m1.getHeuristicValue()));
            queue.addAll(allPossibleMoves(1, heuristicSelected));

            while(!queue.isEmpty()) {
                Move child = queue.poll();
                int playerCount =  map.getPlayerCount();
                int aiCount = map.getAICount();
                Cell origin = map.getCell(child.getOrigin().getRow(), child.getOrigin().getCol());
                Cell goal = map.getCell(child.getGoal().getRow(), child.getGoal().getCol());
                move(origin, goal);

                double curEvaluation = alphabeta(map, depth - 1, a, b, false, heuristicSelected);
                if(depth == this.depth && curEvaluation > maxEvaluation) { // Selects the best move for the AI (based on initial state of the board (depth == this.depth)
                    bestMove = child;
                }

                // undo move to test next move
                map.setPlayerPieces(playerCount);
                map.setAIPieces(aiCount);
                resetMap(child.getOrigin(), child.getGoal());

                maxEvaluation = Math.max(maxEvaluation, curEvaluation);
                a = Math.max(a, maxEvaluation);
                if(a >= b) {
                    break;
                }
            }
            return maxEvaluation;
        } else { //PLAYER TURN

            double minEvaluation = Integer.MAX_VALUE;

            // lambda expression ensures lowest heuristic value will be at top.
            PriorityQueue<Move> queue = new PriorityQueue<Move>(11, (Move m1, Move m2) -> Double.compare(m1.getHeuristicValue(), m2.getHeuristicValue()));
            queue.addAll(allPossibleMoves(2,heuristicSelected));

            while(!queue.isEmpty()){
                Move child = queue.poll();
                int playerCount =  map.getPlayerCount();
                int aiCount = map.getAICount();
                Cell origin = map.getCell(child.getOrigin().getRow(), child.getOrigin().getCol());
                Cell goal = map.getCell(child.getGoal().getRow(), child.getGoal().getCol());
                move(origin, goal);

                double curEvaluation = alphabeta(map, depth - 1, a, b, true, heuristicSelected);
                minEvaluation = Math.min(minEvaluation, curEvaluation);

                // undo move to test next move
                map.setPlayerPieces(playerCount);
                map.setAIPieces(aiCount);
                resetMap(child.getOrigin(), child.getGoal());

                b = Math.min(b, minEvaluation);
                if(b <= a){
                    break;
                }
            }
            return minEvaluation;
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
                        double heuristic = calculateHeuristic(heuristicSelected, true);
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
                        double heuristic = calculateHeuristic(heuristicSelected, false);
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
        //c1.printCell();
        ArrayList<Cell> moves = map.getNeighbors(c1.getRow(), c1.getCol());
        moves.removeIf(move -> !isValidMove(c1, move));
        return moves;
    }


    public boolean isValidMove(Cell cell1, Cell cell2){
        return !(cell1.belongToPlayer() == '1' && cell2.belongToPlayer() == '1' ||
                cell1.belongToPlayer() == '2' && cell2.belongToPlayer() == '2');
    }

    /**
     * Checks if a player move is valid
     * @param cell1 Origin Cell
     * @param cell2 Destination Cell
     * @return true if can move Origin to Destination, false otherwise
     */
    public boolean validPlayerMove(Cell cell1, Cell cell2){
        return isValidMove(cell1, cell2) && cell1.belongToPlayer() == '1' && isNeighbor(cell1, cell2);
    }

    /**
     * Checks if two cells are neighbors
     * @param c1 Cell 1
     * @param c2 Cell 2
     * @return true if neighbors, false if not neighbors
     */
    public boolean isNeighbor(Cell c1, Cell c2){
        return (c2.getRow() - 1 == c1.getRow() || c2.getRow() + 1 == c1.getRow() || c2.getRow() == c1.getRow()) &&
                (c2.getCol() - 1 == c1.getCol() || c2.getCol() + 1 == c1.getCol() || c2.getCol() == c1.getCol());
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
