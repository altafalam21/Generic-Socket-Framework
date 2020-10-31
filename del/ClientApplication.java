//import com.thinking.machines.client.*;
//import com.thinking.machines.network.common.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class ClientApplication extends JFrame implements RequestListener
{
private TMNetworkClient tmNetworkClient;
private Container container;
private JLabel label;
private JTextField textField;
private JButton button;
public ClientApplication()
{
tmNetworkClient=new TMNetworkClient("localhost",5000,5001,this);
label=new JLabel("                                            ");
textField=new JTextField(20);
button=new JButton("Send");
container=getContentPane();
container.setLayout(new FlowLayout());
container.add(textField);
container.add(button);
container.add(label);
button.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ev)
{
String g=textField.getText();
System.out.println("Sending request");
tmNetworkClient.sendRequest("add",g,new ResponseListener(){
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

setLocation(10,10);
setSize(500,400);
setVisible(true);
System.out.println("Client instantiated");

}
public Object onData(Client client,String actionType,Object object)
{
System.out.println("Request arrived for action : "+actionType);
return "Fools";
}
public void onClose(Client client)
{
}
public void onError(Client client)
{
}
public void onOpen(Client client)
{

}
public static void main(String gg[])
{
ClientApplication application=new ClientApplication();
}
}