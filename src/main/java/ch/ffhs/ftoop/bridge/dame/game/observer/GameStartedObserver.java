package ch.ffhs.ftoop.bridge.dame.game.observer;

/**
 * Observer, for when the game has been started.
 */
@FunctionalInterface
public interface GameStartedObserver {

    /**
     * Called when the game has been started.
     */
    void onGameStarted();
}
