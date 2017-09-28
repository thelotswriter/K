package structures;

public enum MoveType2D
{

    FORWARD, BACKWARD, BOTH, NEITHER;

    public static MoveType2D stringToMoveType(String move)
    {
        if(move.equalsIgnoreCase("forward"))
        {
            return FORWARD;
        } else if(move.equalsIgnoreCase("backward"))
        {
            return BACKWARD;
        } else if(move.equalsIgnoreCase("both"))
        {
            return BOTH;
        } else
        {
            return NEITHER;
        }
    }

}
