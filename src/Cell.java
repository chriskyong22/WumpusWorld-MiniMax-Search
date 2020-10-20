public class Cell {
    private char cellType;
    private char playerPiece;
    private int row;
    private int col;
    public Cell(int row, int col){
        this.cellType = 'E';
        this.playerPiece = '0';
        this.row = row;
        this.col = col;
    }

    public Cell(char type, char playerPiece, int row, int col){
        this.cellType = type;
        this.playerPiece = playerPiece;
        this.row = row;
        this.col = col;
    }

    public int getRow(){
        return this.row;
    }
    public void setRow(int row){
        this.row = row;
    }

    public void setCol(int col){
        this.col = col;
    }

    public int getCol(){
        return this.col;
    }

    public boolean isEmpty(){
        return this.cellType == 'E';
    }

    public boolean isPit(){
        return this.cellType == 'P';
    }

    public void reset(){
        this.cellType = 'E';
        this.playerPiece = '0';
    }

    /**
     * 0 belongs to no one
     * 1 belongs to player
     * 2 belongs to AI
     * @return 0 | no one, 1 | player, 2 | AI
     */
    public char belongToPlayer(){
        return this.playerPiece;
    }

    public void setPlayerPiece(char playerPiece){
        this.playerPiece = playerPiece;
    }

    public char getType(){
        return this.cellType;
    }

    public void setType(char type){
        this.cellType = type;
    }

    @Override
    public boolean equals(Object other){
        if(other == null || !(other instanceof Cell)){
            return false;
        }
        return (this.row == ((Cell) other).getRow() && this.col == ((Cell) other).getCol());
    }

}
