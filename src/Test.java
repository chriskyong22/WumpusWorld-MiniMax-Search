import java.util.ArrayList;
import java.util.Scanner;

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
        String stop = "";
        while(!stop.equals("stop")){
            Scanner input = new Scanner(System.in);
            stop = input.nextLine();
            System.out.println("Your input is: " + stop);
            if(stop.equals("stop")){
                break;
            }
            String originX = stop.substring(0, stop.indexOf(','));
            String originY = stop.substring(stop.indexOf(',') + 1, stop.indexOf(' '));
            String goalX = stop.substring(stop.indexOf(' ') + 1, stop.indexOf(',', stop.indexOf(' ') + 1));
            String goalY = stop.substring(stop.indexOf(',', stop.indexOf(' ') + 1) + 1, stop.length());
            System.out.println("Parsed:\n[" + originX + "," + originY + "]\n[" + goalX + "," + goalY + "]");
            Cell origin = test.getCell(Integer.valueOf(originX), Integer.valueOf(originY));
            Cell goal = test.getCell(Integer.valueOf(goalX), Integer.valueOf(goalY));
            temp.move(origin, goal);
            System.out.println("Your Move:");
            test.printMap();
            System.out.println("\n\n\n");
            temp.run(0);
        }

    }
}
