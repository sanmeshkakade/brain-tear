const WebSocket = require("ws")
const server = new WebSocket.Server({port:3001})

const errorSend = (msg) =>{
    socket.send(msg);
}

var username = "nousername";

(() => {
    let roomNo = 1;
    let room = null;
    server.on("connection",(socket,req) => {
        
        console.log("PLayer has Connected")
        

        if (room === null){
            room = new Room(roomNo);
            room.playerX = new Player(room,socket,"X")
        }
        else{
            room.playerO = new Player(room,socket,"O")
            room = null;
            roomNo++;
            console.log("A Game has started")
        }
    });

})();

class Room {
    constructor(roomNo){
        this.board = Array(9).fill(null);
        this.roomID = roomNo;
    }

    isWinner(){
        const wins = [[0,1,2],[3,4,5],[6,7,8],[0,3,6],[1,4,7],[2,5,8],[0,4,8],[2,4,6]];
        const checkWin = wins.some(([x,y,z]) => this.board[x] !== null && this.board[x] === this.board[y] && this.board[y] === this.board[z]);
        return checkWin

    }

    isBoardFilled(){
        return this.board.every((pos) => pos !== null )
    }

    
    move(pos,player){

        if(player !== this.currPlayer){
            throw new Error("Not Your Turn")
        }
        if(this.board[pos] !== null ){
            throw new Error("Tile Already Filled")
        }
        if(!player.opponent){
            throw new Error("Can't Play with Your Self")
        }
        
        this.board[pos] = this.currPlayer.mark
        this.currPlayer = this.currPlayer.opponent
            

    }

}

class Player{

    constructor(room,socket,mark){
        Object.assign(this,{room,socket,mark});  // Combining all methods and values of room, socket and mark in "this"

        if(mark === "X"){
            room.currPlayer = this;   
            socket.send("Waiting for Your Oppenent ")
        }
        else{
            
            this.opponent = room.playerX
            this.opponent.opponent = this      // Sets opponent of the X player 
            socket.send("X will move first")   // Send message to PLayerO
            this.opponent.socket.send("Your Move")    // Send message to PLayerX
          
        }
        
        socket.on("message",(msg) =>{

            if(msg.startsWith("User")){
                this.username = msg.substring(4);
                if(this.opponent){
                    this.opponent.socket.send("Oppuser"+this.username)
                    this.socket.send("Oppuser"+this.opponent.username)
                }
            }
            
            if(msg === "Q"){            // Make button (id Quit)
            try{
             this.opponent.socket.send("OTHER_PLAYER_LEFT");
             console.log("Player Left")
                socket.close()
            }
            catch{}

            }

           if(String(msg).startsWith("M")){
                this.opponent.socket.send(msg);
            }

            

            if(!isNaN(msg)){
                const pos = Number(msg);    // Pos is the position which the player has selected

                try{
                    room.move(pos,this);           // Try Moving                    

                    this.socket.send(`VALID${pos}`)  // Validating Move

                    this.opponent.socket.send(`Opponent${pos}`)  // Sending Opponent the move you made

                    if(this.room.isWinner()){       // Check Win
                        socket.send("Winner")
                        this.opponent.socket.send("Defeat")
                    }
                    else if(this.room.isBoardFilled()){ // Check Tie
                        socket.send("Tie")
                        this.opponent.socket.send("Tie")
                    }
                }
                catch(e){                           // Give Error if any
                    socket.send(`Invalid Move : ${e.message}`);
                }
            }

            

        });
        
        socket.send(`Welcome ${mark}`)           // Socket.send
        
    }
       
    

}

