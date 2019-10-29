package com.pipichao.web;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/chat/{userId}")
public class ChatWebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。但为了实现服务端与单一客户端通信，用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<Map<String, ChatWebSocket>> mapSocket = new CopyOnWriteArraySet<Map<String, ChatWebSocket>>();
    private static CopyOnWriteArraySet<Map<String, Session>> mapSession = new CopyOnWriteArraySet<Map<String, Session>>();

    //当前用户
    private String userId;

    /**
     * 连接时执行
     * @param userId 当前用户名称
     * @param session 当前用户session
     * @throws Exception
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) throws Exception {
        //存放当前用户的MyWebSocket对象
        Map<String, ChatWebSocket> wsSocket = new HashMap<String, ChatWebSocket>();
        //用map建立用户和当前用户的MyWebSocket对象关系
        wsSocket.put(userId, this);
        mapSocket.add(wsSocket);
        //存放当前用户的session对象
        Map<String, Session> wsSession = new HashMap<String, Session>();
        //用map建立用户和当前用户的session对象关系
        wsSession.put(userId, session);
        mapSession.add(wsSession);
        //在线数加1
        addOnlineCount();
        this.userId = userId;
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 关闭时执行
     */
    @OnClose
    public void onClose(){
        //删除当前用户的MyWebSocket对象和session对象,并且人数减1
        removeCurrentUser();
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到消息时执行
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("来自客户端的消息:" + message);
        String[] messages = message.split("-f,t-");
        //接收者名称
        String toName = messages[0].trim();
        //发送给接收者的信息
        String toMessage = 1 >= messages.length ? "" : messages[1];
        //用户判断接收者是否存在
        boolean flag = false;
        //发消息
        for(Map<String, ChatWebSocket> item: mapSocket){
            try {
                for (String key : item.keySet()) {
                    if(toName.equals(key)){
                        flag = true;
                        ChatWebSocket myWebSocket = item.get(key);
                        myWebSocket.sendMessage(key, toMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
        if(!flag){
            session.getBasicRemote().sendText(toName + "用户不在线！"); //回复用户
        }
//        //这里注释掉的内容是群发消息
//        for(Map<String, MyWebSocket> item: mapSocket){
//            try {
//              for (String key : item.keySet()) {
//                  MyWebSocket myWebSocket = item.get(key);
//                  myWebSocket.sendMessage(key, toMessage);
//              }
//            } catch (IOException e) {
//                e.printStackTrace();
//                continue;
//            }
//        }
    }

    /**
     * 发送消息。
//     * @param message
     * @throws IOException
     */
    public void sendMessage(String toName, String toMessage) throws IOException{
        for(Map<String, Session> item: mapSession){
            try {
                for (String string : item.keySet()) {
                    if(toName.equals(string)){
                        Session toSession = item.get(string);
                        toSession.getBasicRemote().sendText(toMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 删除当前用户的MyWebSocket对象和session对象,并且人数减1
     */
    public void removeCurrentUser(){
        //删除当前用户的WebSocket
        for(Map<String, ChatWebSocket> item: mapSocket){
            for (String string : item.keySet()) {
                if(userId.equals(string)){
                    mapSocket.remove(item);
                }
            }
        }
        //删除当前用户的session
        for(Map<String, Session> item: mapSession){
            for (String string : item.keySet()) {
                if(userId.equals(string)){
                    mapSession.remove(item);
                }
            }
        }
        //在线数量减1
        subOnlineCount();
    }

    /**
     * 连接错误时执行
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("用户id为：{}的连接发送错误" + this.userId);
        error.printStackTrace();
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        ChatWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        ChatWebSocket.onlineCount--;
    }
}
