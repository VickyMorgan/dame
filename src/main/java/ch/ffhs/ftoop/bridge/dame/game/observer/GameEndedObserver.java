package ch.ffhs.ftoop.bridge.dame.game.observer;

import ch.ffhs.ftoop.bridge.dame.game.actor.Player;

/**
 * Observer, for when the game has ended.
 */
@FunctionalInterface
public interface GameEndedObserver {

    /**
     * Called, when the game has ended.
     *
     * @param winner The winner of the game.
     * @param loser  The loser of the game.
     */
    void onGameEnded(Player winner, Player loser);
}
