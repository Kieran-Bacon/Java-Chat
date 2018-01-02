import java.net.*;
import java.io.*;

import java.util.Scanner;

public class TCPClient
{
    public static void main (String args[]) {

        if (args.length != 2) {
            System.out.println( "Incorrect arugment usage" );
        } 

        // Define the basic information.
        int portNum = 5665;
        String userMsg = args[0];
        String address = args[1];

        // Create socket variables.
        Socket socket = null;
        DataInputStream inStream;
        DataOutputStream outStream;
        try{

            // Connect to the socket and create the data streams.
            socket = new Socket(args[1], portNum);
            inStream = new DataInputStream( socket.getInputStream() );
            outStream = new DataOutputStream ( socket.getOutputStream() );

            Scanner commandLine = new Scanner(System.in);
            while( true ){
                // Send the message.
                outStream.writeUTF( userMsg );
                // Receive a response.
                String resp = inStream.readUTF();
                System.out.println("Returned: "+ resp );

                System.out.print( "Enter: ");
                userMsg = commandLine.nextLine();
            }
        }
        catch (UnknownHostException e) {
            System.out.println("Connection terminated" );
        }
        catch (EOFException e) {
            System.out.println("Connection Timed Out");
        }
        catch (IOException e) {
            System.out.println("IO Error:"+e.getMessage());
        }
        finally {
            if(socket != null) {
                try {
                    socket.close();
                }
                catch (IOException e) {
                    System.out.println("Error: close socket : "+e.getMessage());
                }
            }
        }
    }
}