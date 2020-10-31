//import com.thinking.machines.server.*;
//import com.thinking.machines.network.common.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Application implements RequestListener
{
private TMNetworkServer tmNetworkServer;
public Application()
{
tmNetworkServer=new TMNetworkServer(5000,5001,this);
System.out.println("Server instantiated");
}

public Object onData(Client client,String actionType,Object object)
{
System.out.println("Request arrived for action : "+actionType);
System.out.println("Data arrived : "+object);
String data=(String)object;
if(data.equals("hell"))
{
return new RuntimeException("Invalid request");
}
if(data.equals("bad"))
{
throw new RuntimeException("Very bad");
}
return data+","+data;
}

public void onOpen(Client client)
{
JFrame frame = new JFrame();
Container container = frame.getContentPane();
JLabel label=new JLabel("                                            ");
JTextField textField = new JTextField(20);
JButton button = new JButton("Send");
container.setLayout(new FlowLayout());
container.add(textField);
container.add(button);
container.add(label);
button.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
String g=textField.getText();
System.out.println("Sending request");
tmNetworkServer.sendRequest(client,"add",g,new ResponseListener(){
public void onError(String error)
{
label.setText("Error : "+error);
System.out.println("Error : "+error);
}
public void onException(Throwable throwable)
{
label.setText("Exception : "+throwable.getMessage());
System.out.println("Exception : "+throwable.getMessage());
}
public void onResponse(Object object)
{
String rr=(String)object;
System.out.println("Result "+rr);
label.setText("Result : "+rr);
}
});
System.out.println("Request sent");
}
});
frame.setLocation(10,10);
frame.setSize(500,400);
frame.setVisible(true);
System.out.println("Client instantiated");

}

public void onClose(Client client)
{

}

public void onError(Client client)
{

}

public void waitForApplicationToEnd()
{
this.tmNetworkServer.waitForServerToStop();
}

public static void main(String gg[])
{
Application application=new Application();
application.waitForApplicationToEnd();
}
}
