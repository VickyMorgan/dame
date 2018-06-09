package ch.ffhs.ftoop.bridge.dame.game.move;

import ch.ffhs.ftoop.bridge.dame.game.actor.Player;
import ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition;
import ch.ffhs.ftoop.bridge.dame.game.board.Piece;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A move encapsulates the information related to a players turn. It includes which player played which piece from where to where.
 */
public class Move {

    private final Player player;
    private final Piece piece;
    private final BoardPosition from;
    private final BoardPosition to;
    private final MoveType type;

    private Move(Player player, Piece piece, BoardPosition from, BoardPosition to) {
        this.player = player;
        this.piece = piece;
        this.from = from;
        this.to = to;
        this.type = MoveType.determineType(from, to);
    }

    public static Move from(Player player, Piece piece, BoardPosition from, BoardPosition to) {
        checkNotNull(player);
        checkNotNull(from);
        checkNotNull(to);

        return new Move(player, piece, from, to);
    }

    public Player getPlayer() {
        return player;
    }

    public Piece getPiece() {
        return piece;
    }

    public BoardPosition getFrom() {
        return from;
    }

    public BoardPosition getTo() {
        return to;
    }

    public MoveType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Move{" +
                "player=" + player +
                ", piece=" + piece +
                ", from=" + from +
                ", to=" + to +
                ", type=" + type +
                '}';
    }
}
