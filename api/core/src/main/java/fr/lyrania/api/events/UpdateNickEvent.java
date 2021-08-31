package fr.lyrania.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class UpdateNickEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String nickname;
    
    public UpdateNickEvent(Player player, String nickname) {
        super(player);

        this.nickname = nickname;
    }

    public boolean hasNickname() {
        return this.nickname != null;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public static class Pre extends UpdateNickEvent {

        public Pre(Player player, String nickname) {
            super(player, nickname);
        }
    }

    public static class Post extends UpdateNickEvent {

        public Post(Player player, String nickname) {
            super(player, nickname);
        }
    }
}
