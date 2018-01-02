import java.rmi.*;
import java.util.*;
import java.rmi.server.UnicastRemoteObject;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.InetAddress;

public class Server extends UnicastRemoteObject implements ServerInterface{


	private Vector topicsLookup = new Vector();
	private Vector topicHistory = new Vector();
	private Vector<Vector<ClientInterface>> userGroups = new Vector<Vector<ClientInterface>>();
	private Vector knownUsers = new Vector();

	public Server() throws RemoteException {};

	public static void main(String[] args) {
		try {
			System.setSecurityManager (new RMISecurityManager());

			java.rmi.registry.LocateRegistry.createRegistry(5665);

			String address = InetAddress.getLocalHost().getHostAddress();
			System.out.print( "Address: " + address );
			//System.setProperty( "java.rmi.server.hostname", "pestilence.ex.ac.uk" );
			
			Server object = new Server();
			Naming.rebind("rmi://"+address+"/SuperChat", object );

			System.out.println("[System] Chat Server is ready.");
		}
		catch (Exception e) {
				System.out.println("Chat Server failed: " + e);
		}
	}

	public boolean login( ClientInterface user, String passwd, String topic ) throws RemoteException
	{
		String name = user.getUsername();
		System.out.println( user.getUsername() + " is attempting to connect...");

		for( int i = 0; i < knownUsers.size(); i++ ){
			String[] usrpwd = (String[]) knownUsers.elementAt(i);
			if( usrpwd[0].equals( name ) ){
				if( usrpwd[1].equals( passwd ) ){
					addUserToTopic( user, topic );
					user.tell( "You have successfully logged in to topic: " + topic );
					return true;
				}
				else {
					user.tell( "Incorrect Username password combination" );
					return false;
				}
			}
		}

		// User not known to Server

		// Store username and password combination.
		String[] urspwd = { name, passwd };
		knownUsers.add( urspwd );

		publish( "SYSTEM", topic, name + " has connected.");

		user.tell( "**SYSTEM** You have successfully logged in to " + topic );
		// Add new user to the topic.
		addUserToTopic( user, topic );

		return true;
	}

	public void logout( String name, String topic ) throws RemoteException {

		for( int i = 0; i < knownUsers.size(); i++ ){
			String[] usrpwd = (String[]) knownUsers.elementAt( i );

			if( usrpwd[0].equals( name ) ){
				
				int index = findTopic( topic );
				if( index == -1 ) return;

				Vector clients = (Vector) userGroups.elementAt( index );
				for( int j = 0; j < clients.size(); j++ ){
					ClientInterface client = (ClientInterface) clients.elementAt(j);
					try{
						if( client.getUsername().equals( name ) ){
							clients.remove( j );
							break;
						}
					} catch( Exception e ){
						clients.remove( j );
					}
				}
				userGroups.set( index, clients );
				updateTopicUsers( topic );

				publish( "SYSTEM", topic, name+" has disconnected." );

				break;
			} 
		}


	}

	private void addUserToTopic( ClientInterface user, String topic )
	{
		String name;
		try{
			name = user.getUsername();
		} catch( RemoteException e ){
			try{
				logout( "", topic );
			} catch( Exception j ){}
			return;
		}

		// Find or create topic
		int index = findTopic( topic );
		if( index == -1 ){
			// Topic does not exist, create a new vector to store users.
			Vector clientGroup = new Vector();

			// Record the topic and Record the client.
			topicsLookup.add( topic );
			topicHistory.add( "--Topic History--\n" );
			clientGroup.add( user );
			userGroups.add( clientGroup );

		} else {

			Vector clientGroup = (Vector) userGroups.elementAt( index );
			// Add the new user to the group;
			clientGroup.add( user );
			try{
				user.tell( (String) topicHistory.elementAt( index ) );
			} catch (Exception e ) {
				System.out.println( "ERROR: Could not send history" );
			}
			

			// Insert the group back into the global vector.
			userGroups.set( index, clientGroup );

			updateTopicUsers( topic );
		}
	}

	public void publish( String user, String topic, String content) throws RemoteException{

		int index = findTopic( topic );
		if( index == -1 )
		{
			System.out.println( "ERROR: "+user+" attempted to publish to a topic that does not exist");
			return;
		}

		Vector clients = (Vector) userGroups.elementAt( index );

		DateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date time = new Date();
        String tag = "[" + format.format( time ) + "-" + user + "] ";

		for( int i = 0; i < clients.size(); i++ ){
			ClientInterface client = (ClientInterface) clients.elementAt( i );
			try{
				client.tell( tag + content );
			} catch (Exception e ) {
				try{logout( user, topic );} catch( Exception j ) {}
			}
		}

		String history = (String) topicHistory.elementAt( index );
		history = history + tag + content + "\n";
		topicHistory.set( index, history );
	}

	public Vector getActiveUsers( String topic ) throws RemoteException 
	{
		int index = findTopic( topic );
		if( index == -1 ){
			System.out.println( "ERROR: attempt to get users from a topic that does not exist");
			return new Vector();
		}

		Vector names = new Vector();
		Vector clients = (Vector) userGroups.elementAt( index );

		for( int i = 0; i < clients.size(); i++ ){
			ClientInterface client = (ClientInterface) clients.elementAt(i);
			names.add(client.getUsername());
		}

		return names;
	}

	private Vector getNames( String topic ){
		Vector names = new Vector();
		int index = findTopic( topic );
		if( index != -1 ){
			Vector clientGroup = (Vector) userGroups.elementAt(index);
			for( int i = 0; i < clientGroup.size(); i++ ){
				ClientInterface client = (ClientInterface) clientGroup.elementAt(i);
				try{
					names.add( client.getUsername() );
				}
				catch ( Exception e) {
					try{logout("",topic);} catch( Exception j ) {}
				}
			}
		}
		return names;
	}

	private int findTopic( String topic ){
		int index = -1;
		for( int i = 0; i < topicsLookup.size(); i++ ){
			String topicTitle = (String) topicsLookup.elementAt(i);
			if( topicTitle.equals( topic ) ){
				// Foind the topic.
				return i;
			}
		}
		return index;
	}

	private void updateTopicUsers( String topic ){

		int index = findTopic( topic );
		if( index == -1 ){
			System.out.println( "ERROR: Tried to find non existent topic" );
			return;
		}
		// Topic exists, extract the topic users and their names.
		Vector clientGroupNames = getNames( topic );
		Vector clientGroup = (Vector) userGroups.elementAt( index );

		for( int i = 0; i < clientGroup.size() - 1; i++ ){
			ClientInterface client = (ClientInterface) clientGroup.elementAt(i);
			try{
				client.updateUsers( clientGroupNames );
			} catch( Exception e ){
				try{
					client.tell( "**SYSTEM** Error user display has not updated" );
				} catch( Exception j ){
					try{ logout( "", topic );} catch( Exception z ) {}
				}
			}
		}
	}
}