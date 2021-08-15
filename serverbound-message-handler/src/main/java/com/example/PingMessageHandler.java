package com.example;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.duncpro.jaws.ws.ServerboundMessageHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiAsyncClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class PingMessageHandler implements ServerboundMessageHandler {
    @Inject
    private ObjectMapper json;

    @Inject
    private ApiGatewayManagementApiAsyncClient apiGateway;

    @Override
    public void handle(APIGatewayV2ProxyRequestEvent messageEvent) {
        final var outboundMessage = new HashMap<String, Object>();
        outboundMessage.put("type", ClientboundMessageType.PONG.name());

        final String serializedOutboundMessage;
        try {
            serializedOutboundMessage = json.writeValueAsString(outboundMessage);
        } catch (JsonProcessingException e) {
            throw new AssertionError(e);
        }

        apiGateway.postToConnection(PostToConnectionRequest.builder()
                .data(SdkBytes.fromString(serializedOutboundMessage, StandardCharsets.UTF_8))
                .connectionId(messageEvent.getRequestContext().getConnectionId())
                .build())
                .join();
    }
}
