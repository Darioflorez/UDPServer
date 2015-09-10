# UDPServer
This is an UDP server
The client contacts the server at port 6478
In this game the client have to guess a randon rumber at the server side.
This server just can attend one client at time. All other client that try to connect are goin to get "busy". 

Protocol:
1. In order to connect the client have to send "hello" to the server
2. Server respond "ok" if the client sends "hello" otherwise "no hello"
3. In order to start the game the client have to send "start"
4. Server responds "ready" and the game starts if client sends "start" otherwise "no start"
5. The client just can send numbers to the server
6. If the server gets other than numbers it responds with "no number"
7. every time the server gets a number it responds with up if number is less than its number or down if the number is higher than its number.
8. The server respond with "correct" if the number is equals to its number and the game start over
9. in order to start a new game the client have to send "start" again.
10. The client can stop the connection with the server anytime it wants with the command "bye".
11. If the server gets "bye" it close the connection with the client and start listen for a new client
