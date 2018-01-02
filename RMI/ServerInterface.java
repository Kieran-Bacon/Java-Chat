import java.rmi.*;
import java.util.*;
 
public interface ServerInterface extends Remote{	
	public boolean login ( ClientInterface user, String passwd, String topic )throws RemoteException;
	public void logout( String name, String topic ) throws RemoteException;
	public void publish( String user, String topic, String content )throws RemoteException;
	public Vector getActiveUsers( String topic ) throws RemoteException;
}