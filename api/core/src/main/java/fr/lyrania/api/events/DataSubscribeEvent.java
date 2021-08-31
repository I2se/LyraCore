package fr.lyrania.api.events;

import fr.lyrania.common.database.DataHolder;
import fr.lyrania.common.database.DataRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.Set;

public class DataSubscribeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DataRegistry registry;
    private final Set<Pair<String, Class<? extends DataHolder<?>>>> holdersClasses;

    public DataSubscribeEvent(DataRegistry registry) {
        this.registry = registry;
        this.holdersClasses = new HashSet<>();
    }

    public void subscribe(String key, Class<? extends DataHolder<?>> holderClass) {
        this.holdersClasses.add(new ImmutablePair<>(key, holderClass));
    }

    public DataRegistry getRegistry() {
        return registry;
    }

    public Set<Pair<String, Class<? extends DataHolder<?>>>> getHoldersClasses() {
        return holdersClasses;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
