import java.rmi.*;
import java.util.*;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

public class ClientUI{
    private Client client;
    private ServerInterface server;

    public void doConnect(){
        if (connect.getText().equals("Connect")){
            if (name.getText().length()<2){JOptionPane.showMessageDialog(frame, "You need to type a name."); return;}
            if (ip.getText().length()<2){JOptionPane.showMessageDialog(frame, "You need to type an IP."); return;}	    	
            try{
                client = new Client( name.getText() );
                client.setGUI(this);

                String URL = "//" + ip.getText() +"/SuperChat";

                System.out.println( URL );
                server = (ServerInterface) Naming.lookup( URL );
                boolean isLoggedin = server.login(client, passwd.getText(), topicF.getText() );
                if( !isLoggedin ){ 
                    return; 
                }
                updateUsers(server.getActiveUsers( topicF.getText() ));
                passwd.setText( "" );
                connect.setText("Disconnect");			    
            }catch(Exception e){e.printStackTrace();JOptionPane.showMessageDialog(frame, "ERROR, we wouldn't connect....");}		  
        }else{
            try{
                updateUsers(null);
                connect.setText("Connect");
                server.logout( client.getUsername(), topicF.getText() );
            } catch( Exception e ) {
                writeMsg( "Oops... Failed to disconnect properly" );
            }
        }
    }  

    public void sendText(){
        if (connect.getText().equals("Connect")){
            JOptionPane.showMessageDialog(frame, "You need to connect first."); return;	
        }
        String st=tf.getText();
        st=st;
        tf.setText("");
        //Remove if you are going to implement for remote invocation
        try{
            server.publish( client.getUsername(), topicF.getText(), st);
        }catch(Exception e){e.printStackTrace();}
    }

    public void writeMsg(String st){  tx.setText(tx.getText()+"\n"+st);  }

    public void updateUsers(Vector users){
        DefaultListModel listModel = new DefaultListModel();

        if(users != null){
            for ( int i = 0; i<users.size(); i++ ){
                try{  
                    listModel.addElement( (String) users.elementAt(i) );
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        lst.setModel(listModel);
    }

    public static void main(String [] args){
        System.setSecurityManager (new RMISecurityManager());
        ClientUI c=new ClientUI();
    }  

    //User Interface code.
    public ClientUI(){
        frame=new JFrame("Group Chat");
        JPanel main =new JPanel();
        JPanel top =new JPanel();
        JPanel cn =new JPanel();
        JPanel bottom =new JPanel();
        ip=new JTextField();
        tf=new JTextField();
        name=new JTextField();
        passwd = new JTextField();
        topicF = new JTextField();
        tx=new JTextArea();
        connect=new JButton("Connect");
        JButton bt=new JButton("Send");
        lst=new JList();        
        main.setLayout(new BorderLayout(5,5));         
        top.setLayout(new GridLayout(1,0,2,9));   
        cn.setLayout(new BorderLayout(5,5));
        bottom.setLayout(new BorderLayout(5,5));
        top.add(new JLabel("Address: "));top.add(ip);
        top.add(new JLabel("Username: "));top.add(name);
        top.add(new JLabel("Password: "));top.add(passwd);
        top.add(new JLabel("Topic: "));top.add(topicF);    
        
        top.add(connect);
        cn.add(new JScrollPane(tx), BorderLayout.CENTER);        
        cn.add(lst, BorderLayout.EAST);    
        bottom.add(tf, BorderLayout.CENTER);    
        bottom.add(bt, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);
        main.add(cn, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);
        main.setBorder(new EmptyBorder(10, 10, 10, 10) );
        //Events
        connect.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){ doConnect();   }  });
        bt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){ sendText();   }  });
        tf.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){ sendText();   }  });

        frame.setContentPane(main);
        frame.setSize(900,600);
        frame.setVisible(true);  
    }
    JTextArea tx;
    JTextField tf,ip, name, passwd, topicF;
    JButton connect;
    JList lst;
    JFrame frame;
}