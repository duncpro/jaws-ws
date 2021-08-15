package com.duncpro.jaws.ws;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;

public interface ServerboundMessageHandler {
    void handle(APIGatewayV2ProxyRequestEvent messageEvent);
}
