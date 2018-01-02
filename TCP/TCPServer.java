import java.io.*;
import java.net.*;

public class TCPServer
{
    public static void main (String args[]) {

        // Define port number and Socket object.
        int portNum = 5665;
        ServerSocket server = null;

        int connNum = 0;

        while(true) {
            try{
                // Start the server and start accepting messages.
                if( server == null ) server = new ServerSocket( portNum );
                Socket request = server.accept();
                connNum++;

                // Handle connections via the threads connections class.
                Connection conn = new Connection( connNum, request );
            } 
            catch(IOException e) {
                // IO error occured.
                if( server != null ){

                    // Serve object is live.
                    try{
                        // Close the server for restart
                        server.close();
                    } catch( IOException i ){
                        // Error on attempt to close, exit
                        System.out.println( "Socket could not be closed, exiting" );
                        System.exit(0);
                    }
                    // Set the socket to null
                    server = null; 
                }

                // Print outcome
                System.out.println("Server IO Error :" + e.getMessage());
            }
        }
    }
}

class Connection extends Thread
{
    // Define class variables.
    int id;
    Socket socket;
    DataInputStream inStream;
    DataOutputStream outStream;


    // Constructor
    public Connection ( int cId, Socket reqtSocket ) {
        try {
            // Get data streams from socket.
            id = cId;
            socket = reqtSocket;
            inStream = new DataInputStream( socket.getInputStream());
            outStream = new DataOutputStream( socket.getOutputStream());

            // Run computation.
            this.start();
        } catch(IOException e){
            System.out.println("Connection IO Error:" + e.getMessage());
        }
    }

    public void run(){
        try {
            while(true){
                // Extract message from stream.
                String msg = inStream.readUTF();
                socket.setSoTimeout( 5000 );
                // Edit message.
                String respMsg = "[**"+ msg.toUpperCase() +"**]";
                
                System.out.println("Conn " + id + ": " + msg );
                // Return message.
                outStream.writeUTF( respMsg );
            }
        } 
        catch(EOFException e){
            // Connection terminated.
            System.out.println("Connection " + id + " has ended.");
        } 
        catch(IOException e) {
            System.out.println("Connection " + id + " timed out.");
        }
        finally{ 
            try {
                socket.close();
            }
            catch (IOException e){
                System.out.println( "Run IO Error" );
            }
        }
    }
}