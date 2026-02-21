package enums;

/**
 * @author Dimitris Mouliakos
 */


//==========================================================================
// ENUM
//==========================================================================
public enum MoveDirection {
    LEFT (-1),
    RIGHT(1);
    
    
    private final int delta;
    
    
    // Assigns value into Enum (For internal Use only)
    MoveDirection(int delta){
        this.delta = delta;
    }
    
    
    // Getter
    public int delta() {
        return delta;
    }
}