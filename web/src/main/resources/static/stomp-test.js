let stompClient = null;
let isConnected = false;

function getStatus() {
  return document.getElementById("status");
}

function updateStatus(message, type) {
  const status = getStatus();
  status.textContent = message;
  status.className = `status ${type}`;
}

function addMessage(content, type) {
  const messagesDiv = document.getElementById("messages");
  const messageEl = document.createElement("div");
  messageEl.className = `message ${type}`;

  const timestamp = new Date().toLocaleTimeString();
  messageEl.innerHTML = `
        <div class="timestamp">${timestamp}</div>
        <div>${escapeHtml(content)}</div>
    `;

  messagesDiv.appendChild(messageEl);
  messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function escapeHtml(text) {
  const map = {
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#039;",
  };
  return text.replace(/[&<>"']/g, (m) => map[m]);
}

function connect() {
  const token = document.getElementById("token").value;
  const chatId = document.getElementById("chatId").value;

  if (!token) {
    addMessage("Error: JWT token is required", "error");
    return;
  }

  if (!chatId) {
    addMessage("Error: Chat ID is required", "error");
    return;
  }

  updateStatus("Connecting...", "connecting");

  const socket = new WebSocket("ws://localhost:8080/ws");

  stompClient = Stomp.over(socket);

  const headers = {
    Authorization: `Bearer ${token}`,
  };

  stompClient.connect(
    headers,
    (frame) => {
      isConnected = true;
      updateStatus("Connected", "connected");
      addMessage("Connected to WebSocket", "received");

      // Subscribe to topic
      stompClient.subscribe(`/topic/chat/${chatId}`, (message) => {
        const body = JSON.parse(message.body);
        addMessage(`Received: ${JSON.stringify(body, null, 2)}`, "received");
      });

      // Subscribe to error queues
      stompClient.subscribe("/queue/errors", (message) => {
        addMessage(`Error queue: ${message.body}`, "error");
      });

      stompClient.subscribe("/user/queue/errors", (message) => {
        addMessage(`User error queue: ${message.body}`, "error");
      });

      addMessage(`Subscribed to /topic/chat/${chatId}`, "received");
      addMessage("Subscribed to /queue/errors", "received");
      addMessage("Subscribed to /user/queue/errors", "received");
      document.getElementById("sendBtn").disabled = false;
    },
    (error) => {
      isConnected = false;
      updateStatus("Connection Error", "disconnected");
      addMessage(`Error: ${error}`, "error");
      document.getElementById("sendBtn").disabled = true;
    },
  );
}

function disconnect() {
  if (stompClient && isConnected) {
    stompClient.disconnect(() => {
      isConnected = false;
      updateStatus("Disconnected", "disconnected");
      addMessage("Disconnected from WebSocket", "received");
      document.getElementById("sendBtn").disabled = true;
    });
  }
}

function sendMessage() {
  if (!isConnected) {
    addMessage("Error: Not connected to WebSocket", "error");
    return;
  }

  const chatId = document.getElementById("chatId").value;
  const messageContent = document.getElementById("message").value;

  if (!messageContent) {
    addMessage("Error: Message cannot be empty", "error");
    return;
  }

  const payload = {
    text: messageContent,
  };

  try {
    stompClient.send(`/app/chat/${chatId}/send`, {}, JSON.stringify(payload));

    addMessage(`Sent: ${messageContent}`, "sent");
    document.getElementById("message").value = "";
  } catch (error) {
    addMessage(`Error sending message: ${error}`, "error");
  }
}

// Allow sending with Enter key
document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("message").addEventListener("keypress", (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  });
});
