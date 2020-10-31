package com.thinking.machines.network.common;
import java.net.*;
import java.io.*;
public class ReceiverSocketWrapper
{
private Socket socket;
private InputStream is;
private OutputStream os;
private Thread thread;
private String clientId;
private Client client;
private RequestListener requestListener;
public ReceiverSocketWrapper(Socket socket, InputStream is, OutputStream os, String clientId, RequestListener requestListner)
{
this.socket = socket;
this.is = is;
this.os = os;
this.client = new Client(this.clientId);
this.requestListener = requestListener;
}
public void start()
{
thread = new Thread(new Runnable(){
public void run()
{
try
{
byte header[] = new byte[1024];
int headerSize;
int length;
byte b[] = new byte[10];
byte tmp[];
byte ack[] = new byte[1];
byte data[] = new byte[1024];
byte bytes[];
Object result;
ByteArrayOutputStream baos;
ByteArrayInputStream bais;
ObjectInputStream ois;
ObjectOutputStream oos;
ResponseWrapper responseWrapper = null;
RequestWrapper requestWrapper;
ack[0] = 65;
int nos,i,j,k,count;
while(true)
{
headerSize = is.read(header);
for(i=0;i<=9;i++)
{
b[i] = header[i];
}
length = Integer.parseInt((new String(b)).trim());
ReceiverSocketWrapper.this.os.write(ack);
ReceiverSocketWrapper.this.os.flush();
baos = new ByteArrayOutputStream();
while(length>0)
{
count = ReceiverSocketWrapper.this.is.read(data);
ReceiverSocketWrapper.this.os.write(ack);
ReceiverSocketWrapper.this.os.flush();
baos.write(data,0,count);
length-=count;
}
bytes = baos.toByteArray();
bais = new ByteArrayInputStream(bytes);
ois = new ObjectInputStream(bais);
try
{
requestWrapper = (RequestWrapper)ois.readObject();
try
{
result = requestListener.acceptRequest(client, requestWrapper.getActionType(), requestWrapper.getObject());
if(result instanceof Throwable)
{
responseWrapper = new ResponseWrapper((Throwable)result);
}
else
{
responseWrapper = new ResponseWrapper(result);
}
}catch(Throwable throwable)
{
responseWrapper = new ResponseWrapper(throwable);
}
// code to send response here
baos = new ByteArrayOutputStream();
oos = new ObjectOutputStream(baos);
oos.writeObject(responseWrapper);
bytes = baos.toByteArray();
length = bytes.length;
tmp = String.valueOf(length).getBytes();
nos = 10 - tmp.length;
for(i=0;i<nos;i++) header[i] = 32;
j=0;
while(i<=9)
{
header[i] = tmp[j];
i++;
j++;
}
ReceiverSocketWrapper.this.os.write(header);
ReceiverSocketWrapper.this.os.flush();
ReceiverSocketWrapper.this.is.read(ack);
count = 1024;
while(length>0)
{
if(length<1024) count = length;
ReceiverSocketWrapper.this.os.write(bytes,0,count);
ReceiverSocketWrapper.this.os.flush();
ReceiverSocketWrapper.this.is.read(ack);
length-=count;
}  // while ends
// code to send response ends over here
}catch(ClassNotFoundException classNotFoundException) 
{
System.out.println(classNotFoundException); // should not happen 
}
}
}catch(IOException ioException) 
{
ioException.printStackTrace();
// some needs to be done over here 
}
}
});
thread.start(); 
}
} // class ends
