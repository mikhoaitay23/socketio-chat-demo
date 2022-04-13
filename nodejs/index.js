const express = require('express');
const bodyParser = require('body-parser');
const socketIo = require('socket.io')();
const fs = require('fs');
var app = express();

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var server = app.listen(3000);
var io = socketIo.listen(server);


io.on('connection', function (socket) {
    //The moment one of your client connected to socket.io server it will obtain socket id
    //Let's print this out.
    console.log(`Connection : SocketId = ${socket.id}`)
    //Since we are going to use userName through whole socket connection, Let's make it global.   
    var listMessage = [];
    var count = 0;

    fs.readFile('./histories.json', 'utf8', (err, data) => {
        if (err) {
            console.log(`Error reading file from disk: ${err}`);
        } else {
            listMessage = JSON.parse(data)
        }
    });

    socket.on('subscribe', function (data) {
        console.log('subscribe trigged')
        const room_data = JSON.parse(data)
        const roomName = room_data.roomName;
        console.log(room_data)
        socket.join(`${roomName}`)
        io.to(`${roomName}`).emit('userWithChatHistory', listMessage);
    })

    socket.on('unsubscribe', function (data) {
        console.log('unsubscribe trigged')
        const room_data = JSON.parse(data)
        const userName = room_data.userName;
        const roomName = room_data.roomName;

        console.log(`Username : ${userName} leaved Room Name : ${roomName}`)
        socket.to(`${roomName}`).emit('userLeftChatRoom', userName)
        socket.leave(`${roomName}`)
    })

    socket.on('newMessage', function (data) {
        const messageData = JSON.parse(data)
        listMessage.push(messageData)
        fs.writeFile('./histories.json', JSON.stringify(listMessage),
            {
                encoding: "utf8",
                flag: "w",
                mode: 0o666
            },
            (err) => {
                if (err) {
                    console.log(err);
                }
                else {
                    const message = {
                        userName: messageData.userName,
                        messageContent: messageData.messageContent,
                        roomName: messageData.roomName,
                        type: messageData.type
                    }
                    io.to(`${messageData.roomName}`).emit('updateChat', JSON.stringify(message))
                }
            });
    })

    //If you want to add typing function you can make it like this.
    socket.on('typing', function (roomNumber) {
        if (count === 0) {
            count++
            socket.to(`${roomNumber}`).emit('typing')
        }
    })

    socket.on('stopTyping', function (roomNumber) {
        count = 0
        socket.to(`${roomNumber}`).emit('stopTyping')
    })

    socket.on("image", function (image) {
        console.log(" image réçu : " + image)
        image = image.replace(/^data:image\/png;base64,/, "");

        fs.writeFile("out.png", image, 'base64', function (err) {
            console.log(err);
        });

    });

    socket.on('disconnect', function () {
        console.log("One of sockets disconnected from our server.")
    });
})