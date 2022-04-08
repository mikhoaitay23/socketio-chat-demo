const express = require('express');
const bodyParser = require('body-parser');
const socketIo = require('socket.io')();
const fs = require('fs');
var app = express();

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var server = app.listen(3000);
var io = socketIo.listen(server);


io.on('connection',function(socket) {
    //The moment one of your client connected to socket.io server it will obtain socket id
    //Let's print this out.
    console.log(`Connection : SocketId = ${socket.id}`)
    //Since we are going to use userName through whole socket connection, Let's make it global.   
    var userName = '';
    var listUser = [];

    fs.readFile('./users.json', 'utf8', (err, data) => {
        if (err) {
            console.log(`Error reading file from disk: ${err}`);
        } else {
            listUser = JSON.parse(data)
        }
    });

    socket.on('signup', function(data){
        const userData = JSON.parse(data)
        if(listUser.filter(x => x.username === userData.username).length > 0){

        } else {
            listUser.push(userData)
            fs.writeFile('./users.json', JSON.stringify(listUser),
            {
              encoding: "utf8",
              flag: "w",
              mode: 0o666
            },
            (err) => {
              if (err){
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

    socket.on('login', function(data){
        const userData = JSON.parse(data)
        if(listUser.filter(user => user.username === userData.username).length > 0){
            io.emit('on_login', userData.username)
        } else {
            io.emit('on_login', "")
        }
    })

    socket.on('members', function(){
        io.emit('get_members', listUser)
    })
    
    socket.on('subscribe', function(data) {
        console.log('subscribe trigged')
        const room_data = JSON.parse(data)
        userName = room_data.userName;
        const roomName = room_data.roomName;
    
        socket.join(`${roomName}`)
        console.log(`Username : ${userName} joined Room Name : ${roomName}`)
        
        // Let the other user get notification that user got into the room;
        // It can be use to indicate that person has read the messages. (Like turns "unread" into "read")

        //TODO: need to chose
        //io.to : User who has joined can get a event;
        //socket.broadcast.to : all the users except the user who has joined will get the message
        //socket.broadcast.to(`${roomName}`).emit('newUserToChatRoom',userName);
        io.to(`${roomName}`).emit('newUserToChatRoom',userName);

    })

    socket.on('unsubscribe',function(data) {
        console.log('unsubscribe trigged')
        const room_data = JSON.parse(data)
        const userName = room_data.userName;
        const roomName = room_data.roomName;
    
        console.log(`Username : ${userName} leaved Room Name : ${roomName}`)
        socket.broadcast.to(`${roomName}`).emit('userLeftChatRoom',userName)
        socket.leave(`${roomName}`)
    })

    socket.on('newMessage',function(data) {
        console.log('newMessage triggered')

        const messageData = JSON.parse(data)
        const messageContent = messageData.messageContent
        const roomName = messageData.roomName

        console.log(`[Room Number ${roomName}] ${userName} : ${messageContent}`)
        
        // Just pass the data that has been passed from the writer socket
        const chatData = {
            userName : userName,
            messageContent : messageContent,
            roomName : roomName
        }
        socket.broadcast.to(`${roomName}`).emit('updateChat',JSON.stringify(chatData)) // Need to be parsed into Kotlin object in Kotlin
    })

    //If you want to add typing function you can make it like this.
    socket.on('typing',function(roomNumber){
        console.log('typing triggered')
        socket.broadcast.to(`${roomNumber}`).emit('typing')
    })

    socket.on('stopTyping',function(roomNumber){
        console.log('stopTyping triggered')
        socket.broadcast.to(`${roomNumber}`).emit('stopTyping')
    })

    socket.on('disconnect', function () {
        console.log("One of sockets disconnected from our server.")
    });
})