package com.thinking.machines.network.common;
public interface RequestListener
{
public Object acceptRequest(Client client, String actionType, Object object);
}
