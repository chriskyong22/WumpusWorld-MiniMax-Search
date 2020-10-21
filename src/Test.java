import java.util.ArrayList;

public class Test {
    public static void main(String[] args){
        Grid test = new Grid(6);
        test.printMap();
        System.out.println("\n\n\n\n");
        Logic temp = new Logic(test, 3);
        test.printMap();
        ArrayList<Move> moves = temp.allPossibleMoves(1, 0);
        System.out.println(moves.size());
        temp.move(test.getCell(0,2), test.getCell(4,1));
        test.printMap();
        temp.run(0);
    }
}
