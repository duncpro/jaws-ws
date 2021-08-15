import * as fs from 'fs';
import WebSocket from 'ws';
import {listenForClientboundMessage} from './ws-util';

const wsApiUrl = (() => {
    const cfnOutputsPath = process.env.PATH_TO_CFN_OUTPUTS!;
    const cfnOutputsContent = fs.readFileSync(cfnOutputsPath);
    const cfnOutputs = JSON.parse(cfnOutputsContent.toString());
    return cfnOutputs.JawsWsStack.MainWebSocketApiUrl;
})();

describe('ping/pong', () => {
    const ws = new WebSocket(wsApiUrl);

    beforeAll(() => new Promise(resolve => ws.onopen = resolve), 30 * 1000);
    afterAll(() => ws.close());

   test('server sends pong after client sends ping', async () => {
       const receivedPong = listenForClientboundMessage(ws, 'PONG');

       ws.send(JSON.stringify({
           type: 'PING'
       }));

       return receivedPong;
   }, 30 * 1000);
});
