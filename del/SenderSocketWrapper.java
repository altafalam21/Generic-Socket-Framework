//package com.thinking.machines.network.common;
import java.net.*;
import java.io.*;
import java.util.*;
public class SenderSocketWrapper
{
private Socket socket;
private InputStream is;
private OutputStream os;
private Thread thread;
private Queue<Pair<RequestWrapper,ResponseListener>> queue;
public SenderSocketWrapper(Socket socket,InputStream is,OutputStream os)
{
this.socket=socket;
this.is=is;
this.os=os;
}
public void start()
{
queue=new LinkedList<>();
thread=new Thread(){
public void run()
{
RequestWrapper requestWrapper;
ResponseListener responseListener;
Pair<RequestWrapper,ResponseListener> pair;
try
{
while(true)
{
if(queue.size()==0)
{
System.out.println(queue.size()+", hence suspending");
suspend();
}
System.out.println(queue.size()+", got out of suspension mode");
pair=queue.remove();
requestWrapper=pair.getFirst();
responseListener=pair.getSecond();
// code to send request and get response starts here
byte header[]=new byte[1024];
int headerSize;
int length;
//System.out.println("Kalu 1");
byte b[]=new byte[10];
byte tmp[];
byte ack[]=new byte[1];
byte data[]=new byte[1024];
byte bytes[];
Object result;
ByteArrayOutputStream baos;
ByteArrayInputStream bais;
ObjectInputStream ois;
ObjectOutputStream oos;
ResponseWrapper responseWrapper=null;
ack[0]=65;
int nos,i,j,k,count;
//System.out.println("Kalu 2");

baos=new ByteArrayOutputStream();
oos=new ObjectOutputStream(baos);
oos.writeObject(requestWrapper);
bytes=baos.toByteArray();
length=bytes.length;
tmp=String.valueOf(length).getBytes();
nos=10-tmp.length;
for(i=0;i<nos;i++) header[i]=32;
j=0;
while(i<=9)
{
header[i]=tmp[j];
i++;
j++;
}
//System.out.println("Kalu 3");
//System.out.println("("+new String(header)+")");
SenderSocketWrapper.this.os.write(header);
SenderSocketWrapper.this.os.flush();
//System.out.println("Kalu 3.2");
int ggg;
while(true)
{
ggg=SenderSocketWrapper.this.is.read(ack);
System.out.println(ggg);
if(ggg!=-1) break;
}
//System.out.println("Kalu 3.5");
count=1024;
while(length>0)
{
if(length<1024) count=length;
SenderSocketWrapper.this.os.write(bytes,0,count);
SenderSocketWrapper.this.os.flush();
while(true)
{
if(SenderSocketWrapper.this.is.read(ack)!=-1) break;
}
length-=count;
}
//System.out.println("Kalu 4");

while(true)
{
headerSize=is.read(header);
if(headerSize!=-1) break;
}
for(i=0;i<=9;i++)
{
b[i]=header[i];
}
length=Integer.parseInt((new String(b)).trim());
SenderSocketWrapper.this.os.write(ack);
SenderSocketWrapper.this.os.flush();
baos=new ByteArrayOutputStream();
//System.out.println("Kalu 5");

while(length>0)
{
count=SenderSocketWrapper.this.is.read(data);
if(count==-1) continue;
SenderSocketWrapper.this.os.write(ack);
SenderSocketWrapper.this.os.flush();
baos.write(data,0,count);
length-=count;
}
bytes=baos.toByteArray();
bais=new ByteArrayInputStream(bytes);
ois=new ObjectInputStream(bais);
try
{
responseWrapper=(ResponseWrapper)ois.readObject();
}catch(ClassNotFoundException classNotFoundException)
{
classNotFoundException.printStackTrace(); // should not happen
}
//System.out.println("Kalu 6");

if(responseWrapper.hasException())
{
responseListener.onException(responseWrapper.getException());
}
else
{
responseListener.onResponse(responseWrapper.getResult());
}
System.out.println("Response arrived & processed");
// code to send request and get response ends here

}
}catch(IOException ioException)
{
ioException.printStackTrace();
}
}
};
thread.start();
System.out.println("Actual sender thread started");
}
public void addRequest(RequestWrapper requestWrapper,ResponseListener responseListener)
{
System.out.println(queue.size()+", now adding request");
Pair<RequestWrapper,ResponseListener> pair;
pair=new Pair<>();
pair.setFirst(requestWrapper);
pair.setSecond(responseListener);
queue.add(pair);
System.out.println(queue.size()+", added request");
thread.resume();
System.out.println(queue.size()+", thread resumed");
}

}