<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">

    <title>聊天页面</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">

    <script type="text/javascript">
        //判断当前浏览器是否支持WebSocket
        if('WebSocket' in window){
            websocket = new WebSocket("ws://localhost:8080/chat/" + '${userId}');
            console.log("link success");
        }else{
            alert('Not support websocket');
        }

        //连接发生错误的回调方法
        websocket.onerror = function(){
            setMessageInnerHTML("error");
        };

        //连接成功建立的回调方法
        websocket.onopen = function(event){
            setMessageInnerHTML("open");
        };
        //接收到消息的回调方法
        websocket.onmessage = function(event){
            console.log(event.data);
            setMessageInnerHTML(event.data);
        };

        //连接关闭的回调方法
        websocket.onclose = function(){
            setMessageInnerHTML("close");
        };

        //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
        window.onbeforeunload = function(){
            websocket.close();
        };

        //将消息显示在网页上
        function setMessageInnerHTML(innerHTML){
            document.getElementById('returnMessage').innerHTML += innerHTML + '<br/>';
        }

        //关闭连接
        function closeWebSocket(){
            websocket.close();
            document.getElementById('send').disabled = true;
            document.getElementById('close').disabled = true;
            document.getElementById('connect').disabled = false;
        }

        //发送消息
        function send(){
            //接收者名称
            var toName = document.getElementById('toName').value;
            if('' == toName){
                alert("请填写接收者");
                return;
            }
            //发送的消息
            var message = document.getElementById('message').value;
            if('' == message){
                alert("请填写发送信息");
                return;
            }
            websocket.send(toName+"-f,t-"+message);
        }

        function connect() {
            //判断当前浏览器是否支持WebSocket
            if('WebSocket' in window){
                <%--websocket = new WebSocket("ws://localhost:8080/chat" + '${userId}');--%>
                console.log("link success");
                document.getElementById('send').disabled = false;
                document.getElementById('close').disabled = false;
                document.getElementById('connect').disabled = true;
            }else{
                alert('Not support websocket');
            }

            //连接发生错误的回调方法
            websocket.onerror = function(){
                setMessageInnerHTML("error");
            };

            //连接成功建立的回调方法
            websocket.onopen = function(event){
                setMessageInnerHTML("open");
            };
            //接收到消息的回调方法
            websocket.onmessage = function(event){
                console.log(event.data);
                setMessageInnerHTML(event.data);
            };

            //连接关闭的回调方法
            websocket.onclose = function(){
                setMessageInnerHTML("close");
            };

            //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
            window.onbeforeunload = function(){
                websocket.close();
            };
        }
    </script>
</head>

<body>
webSocket Demo---- ${userId} <br />
发送给谁：<input id="toName" type="text" /><br>
发送内容：<input id="message" type="text" /><br>
<button id="send" onclick="send()"> Send </button>
<button id="close" onclick="closeWebSocket()"> Close </button>
<button id="connect" onclick="connect();" disabled="disabled">Connect</button>
<div id="returnMessage"></div>
</body>
</html>
