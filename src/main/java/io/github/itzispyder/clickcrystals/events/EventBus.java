package io.github.itzispyder.clickcrystals.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

/**
 * Represents an event bus for calling and passing events
 */
public class EventBus {

    private final Map<Class<? extends Listener>, Listener> subscribedListeners;

    /**
     * Constructs a new event bus for handling events
     */
    public EventBus() {
        this.subscribedListeners = new HashMap<>();
    }

    /**
     * Subscribe a listener to the event bus
     * @param listener listener
     */
    public void subscribe(Listener listener) {
        if (listener == null) return;
        subscribedListeners.remove(listener.getClass());
        subscribedListeners.put(listener.getClass(),listener);
    }

    /**
     * Unsubscribes a listener from the event bus
     * @param listener listener
     */
    public void unsubscribe(Listener listener) {
        if (listener == null) return;
        subscribedListeners.remove(listener.getClass());
    }

    /**
     * Passes an event
     * @param event event
     * @return true if cancelled, false if not
     * @param <E> event type
     */
    public <E extends Event> boolean pass(E event) {
        listeners().values().forEach(listener -> {
            List<Method> methods = Arrays
                    .stream(listener.getClass().getDeclaredMethods())
                    .filter(Objects::nonNull)
                    .filter(method -> method.isAnnotationPresent(EventHandler.class))
                    .sorted(Comparator.comparing(method -> ((Method) method).getAnnotation(EventHandler.class).priority()).reversed())
                    .toList();

            methods.forEach(method -> {
                this.tryInvoke(method, listener, event);
            });
        });

        return event instanceof Cancellable c && c.isCancelled();
    }

    public <C extends CallbackInfo, E extends Event> void passWithCallbackInfo(C info, E event, Consumer<CallbackInfo> action) {
        pass(event);
        if (event instanceof Cancellable c && c.isCancelled()) {
            action.accept(info);
        }
    }

    public <C extends CallbackInfo, E extends Event> void passWithCallbackInfo(C info, E event) {
        passWithCallbackInfo(info, event, CallbackInfo::cancel);
    }

    private <E extends Event> void tryInvoke(Method method, Listener listener, E event) {
        try {
            if (!isValid(method, event)) return;
            method.setAccessible(true);
            method.invoke(listener, event);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private <E extends Event> boolean isValid(Method method, E event) {
        if (method == null || event == null) return false;
        if (!method.isAnnotationPresent(EventHandler.class)) return false;
        if (method.getReturnType() != void.class) return false;
        if (method.getParameterCount() != 1) return false;
        return method.getParameters()[0].getType() == event.getClass();
    }

    /**
     * Returns a map of registered events by the event bus
     * @return map
     */
    public HashMap<Class<? extends Listener>, Listener> listeners() {
        return new HashMap<>(subscribedListeners);
    }
}
