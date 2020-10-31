import com.thinking.machines.server.*;
import com.thinking.machines.network.common.*;
public class ServerApplication implements RequestListener
{
private TMNetworkServer tmNetworkServer;
public ServerApplication()
{
tmNetworkServer=new TMNetworkServer(5000,5001,this);
System.out.println("Server instantiated");
}
public Object acceptRequest(Client client,String actionType,Object object)
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
public void waitForApplicationToEnd() 
{
this.tmNetworkServer.waitForServerToStop(); 
}
public static void main(String gg[]) 
{
ServerApplication serverApplication=new ServerApplication();
serverApplication.waitForApplicationToEnd(); 
}
}