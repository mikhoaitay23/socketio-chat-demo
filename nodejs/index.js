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
    var listUser = [];
    var listMessage = [];

    fs.readFile('./users.json', 'utf8', (err, data) => {
        if (err) {
            console.log(`Error reading file from disk: ${err}`);
        } else {
            listUser = JSON.parse(data)
        }
    });

    fs.readFile('./histories.json', 'utf8', (err, data) => {
        if (err) {
            console.log(`Error reading file from disk: ${err}`);
        } else {
            listMessage = JSON.parse(data)
        }
    });

    socket.on('signup', function (data) {
        const userData = JSON.parse(data)
        if (listUser.filter(x => x.username === userData.username).length > 0) {

        } else {
            listUser.push(userData)
            fs.writeFile('./users.json', JSON.stringify(listUser),
                {
                    encoding: "utf8",
                    flag: "w",
                    mode: 0o666
                },
                (err) => {
                    if (err) {
                        console.log(err);
                        io.emit('on_sign_up', false);
                    }
                    else {
                        io.emit('on_sign_up', true);
                        console.log("File written successfully\n");
                        console.log("The written has the following contents:");
                    }
                });
        }
    })

    socket.on('login', function (data) {
        const userData = JSON.parse(data)
        if (listUser.filter(user => user.username === userData.username).length > 0) {
            io.emit('on_login', userData.username)
        } else {
            io.emit('on_login', "")
        }
    })

    socket.on('members', function () {
        io.emit('get_members', listUser)
    })

    socket.on('subscribe', function (data) {
        console.log('subscribe trigged')
        const room_data = JSON.parse(data)
        const sender = room_data.sender;
        const receiver = room_data.receiver;
        console.log(room_data)
        if (listMessage.filter(x => (x.sender === sender && x.receiver === receiver) || (x.sender === receiver && x.receiver === sender)).length > 0) {
            socket.join(`${receiver}`)
            console.log(`Username : ${sender} joined Room Name : ${receiver}`)
            // Let the other user get notification that user got into the room;
            // It can be use to indicate that person has read the messages. (Like turns "unread" into "read")

            //TODO: need to chose
            //io.to : User who has joined can get a event;
            //socket.broadcast.to : all the users except the user who has joined will get the message
            //socket.broadcast.to(`${roomName}`).emit('newUserToChatRoom',userName);
            io.emit('userWithChatHistory', listMessage);
        } else {
            io.to(`${receiver}`).emit('userWithChatHistory', '')
        }
    })

    socket.on('unsubscribe', function (data) {
        console.log('unsubscribe trigged')
        const room_data = JSON.parse(data)
        const userName = room_data.userName;
        const roomName = room_data.roomName;

        console.log(`Username : ${userName} leaved Room Name : ${roomName}`)
        socket.broadcast.to(`${roomName}`).emit('userLeftChatRoom', userName)
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
                    socket.broadcast.to(`${messageData.receiver}`).emit('updateChat', JSON.stringify(messageData)) // Need to be parsed into Kotlin object in Kotlin
                }
            });
    })

    //If you want to add typing function you can make it like this.
    socket.on('typing', function (roomNumber) {
        console.log('typing triggered')
        socket.broadcast.to(`${roomNumber}`).emit('typing')
    })

    socket.on('stopTyping', function (roomNumber) {
        console.log('stopTyping triggered')
        socket.broadcast.to(`${roomNumber}`).emit('stopTyping')
    })

    socket.on('disconnect', function () {
        console.log("One of sockets disconnected from our server.")
    });
})