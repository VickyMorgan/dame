package ch.ffhs.ftoop.bridge.dame.game.observer;

import ch.ffhs.ftoop.bridge.dame.game.actor.Player;

/**
 * Observer, for when the turn has been finished.
 */
@FunctionalInterface
public interface TurnFinishedObserver {

    /**
     * Called when the turn has been finished.
     *
     * @param currentPlayer The player, that has their turn now.
     */
    void onTurnFinished(Player currentPlayer);
}
