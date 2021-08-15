package com.example;

import com.duncpro.jaws.ws.NoopWebSocketLifecycleHandler;
import com.duncpro.jaws.ws.ServerboundMessageHandler;
import com.duncpro.jaws.ws.WebSocketLifecycleHandler;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

public class MainModule extends AbstractModule {
    @Override
    public void configure() {
        bind(WebSocketLifecycleHandler.class).to(NoopWebSocketLifecycleHandler.class);

        final var messageHandlers = MapBinder
                .newMapBinder(binder(), ServerboundMessageType.class, ServerboundMessageHandler.class);

        messageHandlers.addBinding(ServerboundMessageType.PING).to(PingMessageHandler.class);
    }
}
