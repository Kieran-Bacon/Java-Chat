import java.rmi.*;
import java.util.*;
 
public interface ClientInterface extends Remote{	
	public void tell (String content)throws RemoteException;
	public String getUsername()throws RemoteException;
	public void updateUsers( Vector names ) throws RemoteException;
}