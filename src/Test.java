public class Test {
    public static void main(String[] args){
        Grid test = new Grid(9);
        test.printMap();
        System.out.println("\n\n\n\n");
        Logic temp = new Logic(test, 3);
        temp.run();
    }
}
