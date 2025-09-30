# 💬 Advanced Java Chat Application

A real-time **multi-user chat application** built using **Java**, featuring a modern GUI with **emoji support**, **dark/light theme toggle**, and **file sharing**. The application uses **socket programming** and **multithreading** to enable seamless communication between clients and the server.

---

## 🚀 Features

- 💬 Real-time group chat between multiple users  
- 😀 Emoji support for expressive communication  
- 🎨 Dark/Light theme toggle for better user experience  
- 📁 File sharing capability  
- 👥 Online users list updates dynamically  
- ⌨️ Enter-to-send message functionality  
- 🔒 Secure logout and graceful exit handling  

---

## 🧠 Technologies Used

- **Java**  
- **Swing**  
- **Socket Programming**  
- **Multithreading**

---

## 🏗️ Project Structure

├── MyServer.java # Server class (accepts clients and broadcasts messages)
├── MyThread.java # Handles each client on the server side
├── MyClient.java # Main client GUI and user actions
└── ClientThread.java # Listens for messages from the server

## ⚙️ How to Run

### 🖥️ Step 1: Start the Server
```bash
javac ChatApp.java
java MyServer
💻 Step 2: Start the Client(s)
Open multiple terminals or systems and run:

bash
Copy code
java MyClient
🔑 Step 3: Login and Chat
Enter your nickname when prompted.

Start chatting, send emojis or files, and toggle between themes!

📸 Preview
✨ Chat window with emojis, dark theme, and user list
(Add a screenshot here if available)

🔮 Future Enhancements
💌 Private messaging (Direct Messages)

📎 File transfer using byte streams

🕒 Timestamped chat bubbles

🔐 User authentication system

🧾 Author
Sanjay Naddunuri
🎓 Computer Science & Engineering | SR University, Warangal
📧 naddunurisanjay@gmai.com
💻 GitHub: https://github.com/sanjaygithub

⭐ If you like this project, don't forget to give it a star on GitHub!


Would you like me to make a **GitHub-style banner (title image)** for the top of the README (with your project name and icons)? It will make your repo look more professional.
