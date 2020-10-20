public class Cell {
    private char cellType;

    public Cell(){
        this.cellType = 'E';
    }

    public Cell(char type){
        this.cellType = type;
    }

    public char getType(){
        return this.cellType;
    }
    public void setType(char type){
        this.cellType = type;
    }

}
