package com.thinking.machines.network.common;
public class ResponseWrapper implements java.io.Serializable
{
private Object result;
private Throwable throwable;
public ResponseWrapper()
{
}
public ResponseWrapper(Object result)
{
this.result = result;
}
public ResponseWrapper(Throwable throwable)
{
this.throwable = throwable;
}
public boolean hasException()
{
return this.throwable!=null;
}
public boolean hasResult()
{
return this.result!=null;
}
public Throwable getException()
{
return this.throwable;
}
public Object getResult()
{
return this.result;
}
}
