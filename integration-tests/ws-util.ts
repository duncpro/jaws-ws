import WebSocket from 'ws';

export function listenForClientboundMessage<T>(ws: WebSocket, messageType: string): Promise<T> {
    return new Promise<T>((resolve, reject) => {
        ws.onmessage = (event) => {
            const messageBody = JSON.parse(event.data as string);

            if (messageBody.type === messageType) {
                resolve(messageBody as T);
            }
        }
        ws.onerror = (error) => reject(error)
    });
}
