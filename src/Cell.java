public class Cell {
    private char cellType;
    private char playerPiece;
    public Cell(){
        this.cellType = 'E';
        this.playerPiece = '0';
    }

    public Cell(char type){
        this.cellType = type;
        this.playerPiece = '0';
    }

    public Cell(char type, char playerPiece){
        this.cellType = type;
        this.playerPiece = playerPiece;
    }

    public boolean isEmpty(){
        return this.cellType == 'E';
    }

    public boolean isPit(){
        return this.cellType == 'P';
    }

    public void destroy(){
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

}
