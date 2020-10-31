package com.thinking.machines.network.common;
public interface ResponseListener
{
public void onError(String string);
public void onException(Throwable throwable);
public void onResponse(Object object);
}