import java.io.*;
import java.net.*;
import java.util.UUID;

class UDPClient
{
    public static void main(String args[]) throws Exception
    {
        String address = "localhost";
        int portNum = 5665;

        // Determine user message.
        BufferedReader userMsg;
        if( args.length >= 2 ){
            // Unpackage message from arguments.
            userMsg = new BufferedReader(new StringReader(args[0]));
            address = args[1];

            if( args.length == 3 ){
                portNum = 5665;
            } else if( args.length > 3) {
                System.out.println( "Error: Unrecognised number of arguments" );
                System.exit(0);
            }
        } else {
            // Read input from the command line,
            userMsg = new BufferedReader(new InputStreamReader(System.in));
        }

        String msg = userMsg.readLine();

        for( int i = 1; i <= 500; i++ ){

            // Generate a new message content that is different in size
            // and unqiue.
            if(i > 1){
                msg = UUID.randomUUID().toString();
                for( int j = 0; j < i; j++ ){
                    msg =  msg + "a";
                }
            }

            // Create a and bind to a socket and set timeout.
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout( 500 );
            // Address information and protocols for the server.
            InetAddress IPAddress = InetAddress.getByName( address );

            // Construct a kilobyte buffers for message information.
            int bufferSize = 1024;
            byte[] sendByteMsg = new byte[ bufferSize ];
            byte[] receiveByteMsg = new byte[ bufferSize ];
            sendByteMsg = msg.getBytes();

            // Send the message data.
            DatagramPacket sendPacket = new DatagramPacket(sendByteMsg, sendByteMsg.length, IPAddress, portNum );
            socket.send(sendPacket);

            // Receive the server response.
            DatagramPacket receivePacket = new DatagramPacket( receiveByteMsg, receiveByteMsg.length );
            try{
                socket.receive(receivePacket);
            // Extract message and print to screen.
                String modifiedSentence = new String(receivePacket.getData());
                System.out.println("Returned "+ i +":" + modifiedSentence);
            } catch( SocketTimeoutException e ){
                System.out.println( "Timeout Error: " + i + " dropped" );
            }
            socket.close();
        }
    }
}