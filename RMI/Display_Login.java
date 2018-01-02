import java.rmi.*;
import java.util.*;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

public class Display_Login{

    Client client; // Client information

    // Display variables
    JFrame window;
    JTextField ipAddress_input, username_input, password_input;
    JButton loginButton;

    /** Draw the login display */
    public Display_Login(){
        window=new JFrame("Kieran's instant messenger");

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(5,1));

        JPanel title = new JPanel();

        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new GridLayout(1,2));
        ipAddress_input = new JTextField();
        addressPanel.add(new JLabel("Server address:"));
        addressPanel.add(ipAddress_input);
        
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new GridLayout(1,2));
        username_input = new JTextField();
        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(username_input);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new GridLayout(1,2));
        password_input = new JTextField();
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(password_input);

        JPanel loginPanel = new JPanel();
        loginButton = new JButton("Login");
        loginPanel.add(loginButton);

        content.add(title);
        content.add(addressPanel);
        content.add(usernamePanel);
        content.add(passwordPanel);
        content.add(loginPanel);

        window.setContentPane(content);
        window.setSize(250,300);
        window.setVisible(true);

        loginButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){login();}  
        });
    }

    /** Log the user with the information provided */
    public void login(){
        System.out.println("Something for next time");
    }

    /** Start the process */
    public static void main(String [] args){
        System.out.println("Login Windown loading...");
        Display_Login loginScreen = new Display_Login();
    }
};