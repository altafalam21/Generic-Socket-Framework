package com.thinking.machines.server;
import com.thinking.machines.network.common.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class TMNetworkServer
{
private RequestListener requestListener;
private ServerSocket socketForReceiving;
private ServerSocket socketForSending;
private int portForReceiving;
private int portForSending;
private Thread senderThreadListener;
private Thread receiverThreadListener;
private HashMap<String,Pair<ReceiverSocketWrapper,SenderSocketWrapper>> connectionsMap;
public TMNetworkServer(int portForReceiving, int portForSending, RequestListener requestListener)
{
this.requestListener = requestListener;
this.portForReceiving = portForReceiving;
this.portForSending = portForSending;
this.connectionsMap = new HashMap<>();
try
{
this.socketForReceiving = new ServerSocket(this.portForReceiving);
this.socketForSending = new ServerSocket(this.portForSending);
}catch(Exception exception)
{
exception.printStackTrace();
System.exit(0);
}
startReceiverThreadListener();
startSenderThreadListener();
} // constructor ends

public void startReceiverThreadListener()
{
receiverThreadListener = new Thread(new Runnable(){
public void run()
{
try
{
System.out.println("Port for receiving : "+portForReceiving);
while(true)
{
Socket socket = socketForReceiving.accept();
Thread thread = new Thread(new Runnable(){
public void run()
{
try
{
InputStream is = socket.getInputStream();
OutputStream os = socket.getOutputStream();
byte r[];
//byte ack[] = new byte[1];
//ack[0] = 101;
r = new byte[1024];
is.read(r);
//os.write(ack);
//os.flush();
// some security aspect to be done over here
String uuid = java.util.UUID.randomUUID().toString();
byte s[] = uuid.getBytes();
os.write(s);
os.flush();
byte ack[] = new byte[1];
is.read(ack);
ReceiverSocketWrapper receiverSocketWrapper;
receiverSocketWrapper = new ReceiverSocketWrapper(socket,is,os,uuid,requestListener);
Pair<ReceiverSocketWrapper, SenderSocketWrapper> pair = new Pair<>();
pair.setFirst(receiverSocketWrapper);
connectionsMap.put(uuid,pair);
}catch(Exception e)
{
e.printStackTrace();
}
} // run ends
});  // andar wala anonymous class ends 
thread.start();
} // while ends
}catch(Exception exception)
{
exception.printStackTrace();
}
} // bahar wale anonymous class ka run ends
});  // bahar wala anonymous class ends
receiverThreadListener.start();
} // startReceiverThreadListener ends

private void startSenderThreadListener()
{
senderThreadListener = new Thread(new Runnable(){
public void run()
{
try
{
int uuidLength;
int i;
System.out.println("Port for sending : "+portForSending);
while(true)
{
Socket socket = socketForSending.accept();
InputStream is = socket.getInputStream();
byte r[];
r=new byte[1024];
uuidLength= is.read(r);
for(i=uuidLength;i<1024;i++) r[i]=32;
String uuid = new String(r).trim();
Pair<ReceiverSocketWrapper,SenderSocketWrapper> pair = connectionsMap.get(uuid);
byte ack[] = new byte[1];
if(pair!=null) ack[0] = 100;
else ack[0] = 101;
OutputStream os = socket.getOutputStream();
os.write(ack);
os.flush();
if(pair == null)
{
socket.close();
return;
}
SenderSocketWrapper senderSocketWrapper = new SenderSocketWrapper(socket,is,os);
pair.setSecond(senderSocketWrapper);
ReceiverSocketWrapper receiverSocketWrapper = pair.getFirst();
senderSocketWrapper.start();
receiverSocketWrapper.start();
} // while ends 
}catch(Exception e)
{
e.printStackTrace();
}
} // bahar wala run
}); // bahar wala anonymous class ends
senderThreadListener.start();
}  // startSenderThreadListener ends

public void sendRequest(Client client, String actionType, Object object, ResponseListener responseListener)
{
Pair<ReceiverSocketWrapper, SenderSocketWrapper> pair;
pair = connectionsMap.get(client.getClient());
if(pair==null)
{
responseListener.onError("Client disconnected");
return;
}
SenderSocketWrapper senderSocketWrapper;
senderSocketWrapper = pair.getSecond();
RequestWrapper requestWrapper = new RequestWrapper();
requestWrapper.setObject(object);
requestWrapper.setActionType(actionType);
senderSocketWrapper.addRequest(requestWrapper, responseListener);
}

public void waitForServerToStop()
{
try
{
Thread.sleep(6000);
}catch(Exception e)
{
e.printStackTrace();
}
try
{
receiverThreadListener.join();
}catch(Exception e)
{
e.printStackTrace();
}
try
{
senderThreadListener.join();
}catch(Exception e)
{
e.printStackTrace();
}

} // waitForServerToStop ends


} // class ends


