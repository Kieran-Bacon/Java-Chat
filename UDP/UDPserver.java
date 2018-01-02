import java.io.*;
import java.net.*;

class UDPServer
{
    public static void main(String args[]) throws Exception
    {
        // Define port number.
        int portNum = 5665;
        if( args.length == 1 ){
            portNum = Integer.parseInt( args[0] );
        }

        // Setting up message buffers.
        int bufferSize = 1024;
        byte[] receiveByteMsg = new byte[ bufferSize ];
        byte[] sendByteMsg = new byte[ bufferSize ];

        // Define socket variable.
        DatagramSocket socket = null;

        int msgCounter = 0;
        // Begin server operation.
        while(true)
        {
            try{
                // Bind server to socket.
                if( socket == null ) socket = new DatagramSocket( portNum );

                // Wait to receive message.
                DatagramPacket receivePacket = new DatagramPacket(receiveByteMsg, receiveByteMsg.length);
                socket.receive(receivePacket);
                msgCounter++;

                // Extract message and remove trailing null characters.
                String msg = (new String( receivePacket.getData())).substring( 0, receivePacket.getLength() );
                System.out.println("LOGGER msgNum - " + msgCounter + ": " + msg);

                // Extract Origin information.
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                // Edit initial Message
                String respMsg = "[**"+ msg.toUpperCase() +"**]";
                sendByteMsg = respMsg.getBytes();

                // Construct the return packet and send.
                DatagramPacket sendPacket = new DatagramPacket( sendByteMsg, sendByteMsg.length, IPAddress, port);
                socket.send(sendPacket);
            }
            catch( SocketException e ) {
                System.out.println( "Socket Error: " + e.getMessage() );
            }
            catch( IOException e ) {
                System.out.println( "IO Error: " + e.getMessage() );
            }
            finally {
                if( socket != null ){ socket.close(); socket = null; }
            }
        }
    }
}