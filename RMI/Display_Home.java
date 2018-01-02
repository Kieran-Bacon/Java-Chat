import java.rmi.*;
import java.util.*;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

public class Home{
    /** Draw the topic and friends box */
    public Home(){}

    /** Connect to chat topic */
    public void connect(){}

    /** Recieve topics */
    public void topics(vector topics){}

    /** Recieve topic membership update */
    public void topicMembers( string topic_title, vector members){}

    /** Log out of the current session */
    public void logout(){}
};