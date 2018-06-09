package ch.ffhs.ftoop.bridge.dame.game.actor;

import ch.ffhs.ftoop.bridge.dame.game.board.PieceColor;

import java.util.Objects;

/**
 * Models a player of the game.
 */
public class Player {

    private final String name;
    private final PieceColor color;
    private int score;

    public Player(String name, PieceColor color) {
        this.name = name;
        this.color = color;
        this.score = 0;
    }

    /**
     * Increases the score of the player by 1.
     */
    public void increaseScore() {
        this.score++;
    }

    public String getName() {
        return this.name;
    }

    public PieceColor getColor() {
        return this.color;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name) &&
                color == player.color;
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, color);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", color=" + color +
                ", score=" + score +
                '}';
    }
}
