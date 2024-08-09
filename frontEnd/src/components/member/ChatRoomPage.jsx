import React, { useState, useEffect, useRef } from "react";
// import { Client } from "@stomp/stompjs";
// import SockJS from "sockjs-client";
import "/src/css/member/chatroompage.css";

// const SOCKET_URL = "http://localhost:8080/ws";

let stompClient = null;

function ChatRoomPage({ room, onBack }) {
  // const [message, setMessage] = useState("");
  // const [messages, setMessages] = useState([]);
  // const [connected, setConnected] = useState(false);
  // const [userId, setUserId] = useState(1);
  // const messageListRef = useRef(null);
  // const [profileImages, setProfileImages] = useState({});
  // useEffect(() => {
  //   console.log("ChatRoomPage component mounted");

  //   connect();
  //   fetchMessages();

  //   return () => {
  //     console.log("ChatRoomPage component unmounted, disconnecting...");
  //     disconnect();
  //   };
  // }, []);

  // useEffect(() => {
  //   // 예시로 사용자 프로필 이미지 데이터를 가져오는 코드
  //   const fetchProfileImages = async () => {
  //     try {
  //       const response = await fetch("/api/profile-images");
  //       const data = await response.json();
  //       setProfileImages(data); // 데이터 형식이 {userId: imageUrl} 형태인지 확인
  //     } catch (error) {
  //       console.error("Error fetching profile images:", error);
  //     }
  //   };

  //   fetchProfileImages();
  // }, []);

  // useEffect(() => {
  //   const fetchProfileImages = async () => {
  //     try {
  //       const response = await fetch("/api/profile-images");
  //       const contentType = response.headers.get("content-type");

  //       if (contentType && contentType.includes("application/json")) {
  //         const data = await response.json();
  //         setProfileImages(data);
  //       } else {
  //         console.error("Unexpected response type:", contentType);
  //       }
  //     } catch (error) {
  //       console.error("Error fetching profile images:", error);
  //     }
  //   };
  //   fetchProfileImages();
  //   scrollToBottom();
  // }, [messages]);

  // const fetchMessages = async () => {
  //   console.log("Fetching previous messages...");
  //   try {
  //     const response = await fetch(
  //       `http://localhost:8080/api/chat/${room.clubNo}/messages`
  //     );
  //     const data = await response.json();

  //     if (Array.isArray(data)) {
  //       setMessages(data);
  //       console.log("Messages fetched successfully:", data);
  //     } else {
  //       console.error("Unexpected response format:", data);
  //     }
  //   } catch (error) {
  //     console.error("Error fetching messages:", error);
  //     setMessages([]); // 기본값으로 빈 배열 설정
  //   }
  // };

  // const connect = () => {
  //   console.log("Connecting to WebSocket...");

  //   const socket = new SockJS(SOCKET_URL);
  //   stompClient = new Client({
  //     webSocketFactory: () => socket,
  //     debug: (str) => {
  //       console.log("STOMP Debug:", str);
  //     },
  //     reconnectDelay: 5000,
  //     heartbeatIncoming: 4000, // 서버로부터 수신하는 핑 간격
  //     heartbeatOutgoing: 4000, // 서버로 전송하는 핑 간격
  //     onConnect: () => {
  //       console.log("Connected to WebSocket");

  //       stompClient.subscribe(`/topic/${room.clubNo}`, onMessageReceived);
  //       console.log(`Subscribed to /topic/${room.clubNo}`);

  //       setTimeout(() => {
  //         console.log("Sending join message...");
  //         stompClient.publish({
  //           destination: "/app/chat.addUser",
  //           body: JSON.stringify({
  //             userId: userId,
  //             message: "",
  //             type: "JOIN",
  //           }),
  //         });
  //         console.log("Join message sent");
  //       }, 1000);
  //     },
  //     onDisconnect: (frame) => {
  //       console.log("Disconnected from WebSocket:", frame);
  //     },
  //     onStompError: (frame) => {
  //       console.error("STOMP Error:", frame.headers["message"]);
  //       console.error("STOMP Error details:", frame.body);
  //     },
  //   });

  //   console.log("Activating STOMP client...");
  //   stompClient.activate();
  // };

  // const onMessageReceived = (msg) => {
  //   const message = JSON.parse(msg.body);
  //   setMessages((prevMessages) => [...prevMessages, message]);
  // };

  // const disconnect = () => {
  //   if (stompClient !== null) {
  //     stompClient.deactivate();
  //     setConnected(false);
  //   }
  // };

  // const handleSendMessage = () => {
  //   if (message.trim() !== "") {
  //     const chatMessage = {
  //       userId: userId,
  //       message: message,
  //       clubNo: room.clubNo,
  //     };

  //     setTimeout(() => {
  //       try {
  //         stompClient.publish({
  //           destination: `/app/chat.sendMessage`,
  //           body: JSON.stringify(chatMessage),
  //         });
  //         setMessage("");
  //         console.log("Message sent and input cleared");
  //       } catch (error) {
  //         console.error("Error sending message:", error);
  //       }
  //     }, 500);
  //   }
  // };

  // const scrollToBottom = () => {
  //   if (messageListRef.current) {
  //     messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
  //   }
  // };

  // return (
  //   <div className="chat-room-page">
  //     <div className="chat-header">
  //       <button onClick={onBack}>{"<"}</button>
  //       <button
  //         onClick={() => {
  //           /* 다른 동작 */
  //         }}
  //       >
  //         X
  //       </button>
  //     </div>

  //     <div className="chat-status">
  //       {connected ? <p>Connected to WebSocket</p> : <p>Not connected</p>}
  //     </div>

  //     <div className="chat-messages" ref={messageListRef}>
  //       <div className="message-list">
  //         {messages.map((msg, index) => (
  //           <div
  //             key={index}
  //             className={`message ${msg.userId === userId ? "my-message" : "other-message"
  //               }`}
  //           >
  //             {msg.userId !== userId && (
  //               <div className="profile-container">
  //                 {profileImages && profileImages[msg.userId] ? (
  //                   <img
  //                     src={profileImages[msg.userId]}
  //                     alt={`${msg.userId} 프로필`}
  //                     className="profile-image"
  //                   />
  //                 ) : (
  //                   <div className="profile-placeholder">
  //                     <img
  //                       src="/path/to/default/profile.png" // 기본 프로필 이미지 경로
  //                       alt="기본 프로필"
  //                       className="profile-image"
  //                     />
  //                   </div>
  //                 )}
  //                 <div className="message-content">
  //                   <span className="nickname">{msg.userId}</span>
  //                 </div>
  //               </div>
  //             )}
  //             <p
  //               className={`content ${msg.userId === userId ? "my-content" : ""
  //                 }`}
  //             >
  //               {msg.message}
  //             </p>
  //           </div>
  //         ))}
  //       </div>
  //     </div>

  //     <div className="chat-input">
  //       <input
  //         type="text"
  //         value={message}
  //         onChange={(e) => setMessage(e.target.value)}
  //         placeholder="메시지를 입력하세요"
  //         onKeyPress={(e) => {
  //           if (e.key === "Enter") {
  //             handleSendMessage();
  //           }
  //         }}
  //       />
  //       <button onClick={handleSendMessage}>전송</button>
  //     </div>
  //   </div>
  // );
}

export default ChatRoomPage;
