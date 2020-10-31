import com.thinking.machines.server.*;
public class Application implements RequestListener
{
private TMNetworkServer tmNetworkServer;
private RequestWrapper requestWrapper;
private String newId;
public Application()
{
tmNetworkServer = new TMnetworkServer(5000,5001,this);
System.out.println("Server instantiated");
}
public Object acceptRequest(Client client, String actionType, Object object)
{
System.out.println("Request arrived for action : "+actionType);
return "Cool";
}
public void setContents(String newId, RequestWrapper requestWrapper)
{
this.newId = newId;
this.requestWrapper = requestWrapper;
}
public void waitForApplicationToEnd()
{
this.tmNetworkServer.waitForServerToStop();
}
public void sendRequest()
{
this.tmNetworkServer.receiveRequest(newId, requestWrapper);
}

public static void main(String gg[])
{
Application application = new Application();
application.sendRequest();
application.waitForApplicationToEnd();
}
}
