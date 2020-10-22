package sample.back;

import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args){
        Grid test = new Grid(6);
        //test.printMap();
        //System.out.println("\n\n\n\n");
        Logic temp = new Logic(test, 6);
        //test.printMap();
        //ArrayList<Move> moves = temp.allPossibleMoves(1, 0);
        //System.out.println(moves.size());
        //temp.move(test.getCell(0,2), test.getCell(4,1));
        //test.printMap();
        //temp.run(0);
        String stop = "";
        System.out.println("Initial Map Visual:");
        test.printMap();
        System.out.println("Note: Player pieces start at row 0 (topmost row) and please enter only VALID moves");
        System.out.println("To stop the game: type \"stop\"");
        while(!stop.equals("stop")){
            System.out.println("Please enter your VALID move in the format: \"row1,col1 row2,col2\"");
            // example: 0,0 0,1 will move the piece at 0,0 to 0,1
            Scanner input = new Scanner(System.in);
            stop = input.nextLine();
            System.out.println("Your input is: " + stop);
            if(stop.toLowerCase().equals("stop")){
                break;
            }
            String originX = stop.substring(0, stop.indexOf(','));
            String originY = stop.substring(stop.indexOf(',') + 1, stop.indexOf(' '));
            String goalX = stop.substring(stop.indexOf(' ') + 1, stop.indexOf(',', stop.indexOf(' ') + 1));
            String goalY = stop.substring(stop.indexOf(',', stop.indexOf(' ') + 1) + 1, stop.length());
            //System.out.println("Parsed:\n[" + originX + "," + originY + "]\n[" + goalX + "," + goalY + "]");
            Cell origin = test.getCell(Integer.valueOf(originX), Integer.valueOf(originY));
            Cell goal = test.getCell(Integer.valueOf(goalX), Integer.valueOf(goalY));
            temp.move(origin, goal);
            System.out.println("Map after your move");
            test.printMap();
            System.out.println("\n");
            System.out.println("Map after AI move");
            temp.run(0);
            System.out.println("\n\n");
        }

    }
}
