package com.duncpro.jaws.ws;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;

public class NoopWebSocketLifecycleHandler implements WebSocketLifecycleHandler {
    @Override
    public void connecting(APIGatewayV2ProxyRequestEvent event) {}

    @Override
    public void disconnected(APIGatewayV2ProxyRequestEvent event) {}
}
