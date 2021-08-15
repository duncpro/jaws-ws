package com.duncpro.jaws.ws;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.example.ServerboundMessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

public class ServerboundMessageRouter {
    @Inject
    private Map<ServerboundMessageType, ServerboundMessageHandler> messageHandlers;

    @Inject
    private WebSocketLifecycleHandler lifecycleHandler;

    public void route(APIGatewayV2ProxyRequestEvent event) {

        switch (event.getRequestContext().getRouteKey()) {
            case "$connect":
                lifecycleHandler.connecting(event);
                break;
            case "$disconnect":
                lifecycleHandler.disconnected(event);
                break;
            case "$default":
                final ServerboundMessageType messageType;
                try {
                    messageType = getMessageType(event);
                } catch (JsonProcessingException e) {
                    logger.error("Dropping serverbound message because the message-type could not be determined due to" +
                            " a JSON processing error. Did the client send malformed JSON?", e);
                    logger.error("Unable to deserialize JSON message: " + event.getBody());
                    return;
                }
                messageHandlers.get(messageType).handle(event);
                break;
            default:
                throw new AssertionError();
        }
    }

    private ServerboundMessageType getMessageType(APIGatewayV2ProxyRequestEvent messageEvent) throws JsonProcessingException {
        final var json = new ObjectMapper();
        json.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final var deserializedMessage = json.readValue(messageEvent.getBody(), Map.class);
        final var messageTypeName = ((String) deserializedMessage.get("type"));
        return ServerboundMessageType.valueOf(messageTypeName);
    }

    private static final Logger logger = LoggerFactory.getLogger(ServerboundMessageRouter.class);
}
