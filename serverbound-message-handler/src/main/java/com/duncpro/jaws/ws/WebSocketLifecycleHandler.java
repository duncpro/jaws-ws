package com.duncpro.jaws.ws;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;

public interface WebSocketLifecycleHandler {
    void connecting(APIGatewayV2ProxyRequestEvent event);

    void disconnected(APIGatewayV2ProxyRequestEvent event);
}
