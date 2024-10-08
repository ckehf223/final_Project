import React, { useState, useEffect, useRef } from "react";
import "/src/css/member/common/chatroompage.css";
import { useAuth } from "/src/common/AuthContext";
import instance from "/src/auth/axios";

const SOCKET_URL = "ws://localhost:8080/ws/chat";

function ChatRoomPage({ room }) {
  const [message, setMessage] = useState("");
  const [messages, setMessages] = useState([]);
  const { loginId } = useAuth();
  const messageListRef = useRef(null);
  const socketRef = useRef(null);

  useEffect(() => {
    socketRef.current = new WebSocket(SOCKET_URL);

    socketRef.current.onopen = () => {
      console.log("Connected to WebSocket");
    };

    socketRef.current.onmessage = (event) => {
      const newMessage = JSON.parse(event.data);
      setMessages((prevMessages) => [...prevMessages, newMessage]);
    };

    socketRef.current.onerror = (error) => {
      console.error("WebSocket error:", error);
    };

    socketRef.current.onclose = (event) => {
      console.log(
        `WebSocket connection closed: ${event.code}, reason: ${event.reason}`
      );
    };

    fetchMessages();

    return () => {
      if (
        socketRef.current &&
        socketRef.current.readyState === WebSocket.OPEN
      ) {
        socketRef.current.close();
      }
    };
  }, [room.clubNo]);

  const fetchMessages = async () => {
    try {
      const response = await instance.get(`/api/chat/${room.clubNo}/messages`, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (response.data) {
        setMessages(response.data);
      }
    } catch (error) {
      console.error("Error fetching messages:", error);
    }
  };
  const handleSendMessage = () => {
    if (message.trim() !== "" && loginId !== null) {
      const chatMessage = {
        USERID: loginId,
        MESSAGE: message,
        CLUBNO: room.clubNo,
      };
      socketRef.current.send(JSON.stringify(chatMessage));
      setMessage("");
    }
  };

  const scrollToBottom = () => {
    if (messageListRef.current) {
      messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
    }
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);
  return (
    <div className="chat-room-page">
      <div className="chat-messages" ref={messageListRef}>
        <div className="message-list">
          <span>{room.chatRoomName} 채팅을 시작하세요!</span>
          {messages.length > 0 &&
            messages.map((msg, index) => (
              <div
                key={index}
                className={`message ${msg.USERID === loginId || msg.userId === loginId
                    ? "my-message"
                    : "other-message"
                  }`}
              >
                {msg.USERID !== loginId && (
                  <div className="message-info">
                    <img
                      className="profile-image"
                      src={`data:image/jpeg;base64,${msg.USERIMAGE}`}
                      alt="프로필"
                    />
                    <span className="nickname">
                      {msg.NICKNAME !== null ? msg.NICKNAME : msg.USERNAME}
                    </span>
                  </div>
                )}
                <span className="chatmessagespan">{msg.MESSAGE}</span>
              </div>
            ))}
        </div>
      </div>

      <div className="chat-input">
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="메시지를 입력하세요"
          onKeyPress={(e) => {
            if (e.key === "Enter") {
              handleSendMessage();
            }
          }}
        />
        <button onClick={handleSendMessage}>전송</button>
      </div>
    </div>
  );
}

export default ChatRoomPage;
