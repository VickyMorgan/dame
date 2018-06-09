package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Defines all the rules of the game.
 */
public enum MoveRule {
    /**
     * A player can only play pieces that match their color.
     */
    PLAYER_MUST_PLAY_OWN_PIECES,

    /**
     * A player must play a piece every turn, they cannot "sit one out".
     */
    PLAYER_MUST_PLAY_PIECE,

    /**
     * A piece must be moved to a different tile than the current one.
     */
    PIECE_MUST_MOVE,

    /**
     * Only pieces still on the board (not captured ones) can be played.
     */
    PIECE_MUST_STILL_BE_ON_BOARD,

    /**
     * Pieces must always be placed on dark tiles.
     */
    PIECE_MUST_BE_PLACED_ON_DARK_TILE,

    /**
     * Pieces can only be places on free tiles.
     */
    PIECE_MUST_BE_PLACED_ON_FREE_TILE,

    /**
     * Pieces must always be moved forward in direction to the opponents kings row, expect when the piece is a king.
     */
    PIECE_MUST_MOVE_FORWARD,

    /**
     * Pieces must always move diagonally to other tiles, no moving vertically or horizontally.
     */
    PIECE_MUST_MOVE_DIAGONALLY,

    /**
     * Pieces must always move a set distance. If the piece is not jumping over another piece, this distance is one.
     * If it is jumping over a piece, then the distance must be even (one step to the jumped-over piece, another to the tile behind).
     */
    PIECE_MUST_MOVE_CORRECT_DISTANCE,

    /**
     * Pieces can only jump over opponents pieces.
     */
    PIECE_MUST_ONLY_JUMP_OVER_OPPONENT_PIECES,

    /**
     * Advanced rule: When a piece has direct neighbours that could be jumped over, it is compulsory to jump over them.
     * This disallows the player do "dodge" or move out of the way of other pieces.
     */
    PIECE_MUST_COMPULSORY_JUMP_IF_OPPONENT_PIECE_NEARBY
}
