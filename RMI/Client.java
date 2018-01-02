import java.rmi.*;
import java.util.*;
import java.rmi.server.UnicastRemoteObject;

/**
Client respresentation. 
 */
public class Client extends UnicastRemoteObject implements ClientInterface{

	private String username; // Display name of the user
	private ClientUI GUI;    // Graphic window displaying messages

	public Client( String usr ) throws RemoteException {
		username = usr;
	}

	public void tell( String content ) throws RemoteException {
		System.out.println( content );
		GUI.writeMsg( content );
	}

	public String getUsername() throws RemoteException {
		return username;
	}

	public void updateUsers( Vector names ){
		GUI.updateUsers( null );
		GUI.updateUsers( names );
	}

	public void setGUI( ClientUI gui ){
		GUI = gui;
	}
}