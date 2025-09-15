// Enhanced Java Chat Application with Emoji Support, Styled GUI, Theme Toggle, File Sharing, and Enter-to-Send
// Includes: MyServer, MyThread, MyClient, ClientThread

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

// ========== SERVER SIDE ==========
class MyServer {
    ArrayList<Socket> al = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();
    ServerSocket ss;
    Socket s;
    public static final int PORT = 12345;
    public static final String UPDATE_USERS = "updateuserslist:";
    public static final String LOGOUT_MESSAGE = "@@logoutme@@:";
    public static final String FILE_TRANSFER_PREFIX = "@@file@@:";

    public MyServer() {
        try {
            ss = new ServerSocket(PORT);
            System.out.println("Server Started on port " + PORT);
            while (true) {
                s = ss.accept();
                Runnable r = new MyThread(s, al, users);
                Thread t = new Thread(r);
                t.start();
            }
        } catch (Exception e) {
            System.err.println("Server Error: " + e);
        }
    }

    public static void main(String[] args) {
        new MyServer();
    }
}

class MyThread implements Runnable {
    Socket s;
    ArrayList<Socket> al;
    ArrayList<String> users;
    String username;

    MyThread(Socket s, ArrayList<Socket> al, ArrayList<String> users) {
        this.s = s;
        this.al = al;
        this.users = users;
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            username = dis.readUTF();
            al.add(s);
            users.add(username);
            tellEveryOne("****** " + username + " Logged in at " + new Date() + " ******");
            sendNewUserList();
        } catch (Exception e) {
            System.err.println("MyThread Constructor Error: " + e);
        }
    }

    public void run() {
        try (DataInputStream dis = new DataInputStream(s.getInputStream())) {
            String s1;
            while (true) {
                s1 = dis.readUTF();
                if (s1.equalsIgnoreCase(MyServer.LOGOUT_MESSAGE)) break;
                tellEveryOne(username + " said: " + s1);
            }
            users.remove(username);
            tellEveryOne("****** " + username + " Logged out at " + new Date() + " ******");
            sendNewUserList();
            al.remove(s);
            s.close();
        } catch (Exception e) {
            System.out.println("MyThread Run Error: " + e);
        }
    }

    public void sendNewUserList() {
        tellEveryOne(MyServer.UPDATE_USERS + users.toString());
    }

    public void tellEveryOne(String message) {
        for (Socket client : al) {
            try {
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                dos.writeUTF(message);
                dos.flush();
            } catch (Exception e) {
                System.err.println("Broadcast Error: " + e);
            }
        }
    }
}

// ========== CLIENT SIDE ==========
class MyClient implements ActionListener {
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    JButton sendButton, logoutButton, loginButton, exitButton, themeButton, fileButton;
    JFrame chatWindow;
    JTextArea txtBroadcast, txtMessage;
    JList<String> usersList;
    JPanel emojiPanel;
    boolean darkTheme = true;
    String currentUser = "";

    public void displayGUI() {
        chatWindow = new JFrame("Login for Chat");
        txtBroadcast = new JTextArea(10, 30);
        txtBroadcast.setEditable(false);
        txtMessage = new JTextArea(2, 20);
        usersList = new JList<>();

        sendButton = new JButton("Send");
        logoutButton = new JButton("Log out");
        loginButton = new JButton("Log in");
        exitButton = new JButton("Exit");
        themeButton = new JButton("Toggle Theme");
        fileButton = new JButton("Send File");

        setTheme();

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Broadcast messages from all online users", JLabel.CENTER), BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(txtBroadcast), BorderLayout.CENTER);

        JPanel southMessage = new JPanel(new FlowLayout());
        JScrollPane msgScroll = new JScrollPane(txtMessage);
        msgScroll.setPreferredSize(new Dimension(250, 50));
        southMessage.add(msgScroll);
        southMessage.add(sendButton);

        emojiPanel = new JPanel(new FlowLayout());
        String[] emojis = {"😀", "😂", "😍", "👍", "❤️", "🎉"};
        for (String emoji : emojis) {
            JButton emojiButton = new JButton(emoji);
            emojiButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            emojiButton.addActionListener(e -> txtMessage.append(emoji));
            emojiPanel.add(emojiButton);
        }

        JPanel southControl = new JPanel(new FlowLayout());
        southControl.add(loginButton);
        southControl.add(logoutButton);
        southControl.add(exitButton);
        southControl.add(themeButton);
        southControl.add(fileButton);

        JPanel southPanel = new JPanel(new GridLayout(3, 1));
        southPanel.add(southMessage);
        southPanel.add(emojiPanel);
        southPanel.add(southControl);

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(new JLabel("Online Users", JLabel.CENTER), BorderLayout.NORTH);
        eastPanel.add(new JScrollPane(usersList), BorderLayout.CENTER);

        chatWindow.add(centerPanel, BorderLayout.CENTER);
        chatWindow.add(eastPanel, BorderLayout.EAST);
        chatWindow.add(southPanel, BorderLayout.SOUTH);

        chatWindow.setSize(700, 550);
        chatWindow.setLocationRelativeTo(null);
        chatWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        chatWindow.setVisible(true);

        sendButton.addActionListener(this);
        logoutButton.addActionListener(this);
        loginButton.addActionListener(this);
        exitButton.addActionListener(this);
        themeButton.addActionListener(this);
        fileButton.addActionListener(this);

        logoutButton.setEnabled(false);
        loginButton.setEnabled(true);

        txtMessage.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    sendMessage();
                }
            }
        });

        chatWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                if (s != null) logoutSession();
                System.exit(0);
            }
        });
    }

    private void setTheme() {
        Color bg = darkTheme ? Color.BLACK : Color.WHITE;
        Color fg = darkTheme ? Color.GREEN : Color.BLACK;
        txtBroadcast.setBackground(bg);
        txtBroadcast.setForeground(fg);
        txtMessage.setBackground(bg);
        txtMessage.setForeground(fg);
        txtBroadcast.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtMessage.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == sendButton) sendMessage();
        else if (src == loginButton) {
            String uname = JOptionPane.showInputDialog(chatWindow, "Enter your nickname:");
            if (uname != null && !uname.trim().isEmpty()) clientChat(uname.trim());
        } else if (src == logoutButton) logoutSession();
        else if (src == exitButton) {
            logoutSession();
            System.exit(0);
        } else if (src == themeButton) {
            darkTheme = !darkTheme;
            setTheme();
        } else if (src == fileButton) selectAndSendFile();
    }

    private void sendMessage() {
        if (s == null || txtMessage.getText().trim().isEmpty()) return;
        try {
            dos.writeUTF(txtMessage.getText().trim());
            txtMessage.setText("");
        } catch (Exception e) {
            txtBroadcast.append("\nSend Error: " + e);
        }
    }

    private void selectAndSendFile() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(chatWindow);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            txtBroadcast.append("\n📁 Sending file: " + file.getName());
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                dos.writeUTF("[File] " + file.getName() + ":");
                while ((line = reader.readLine()) != null) {
                    dos.writeUTF(line);
                }
                dos.writeUTF("[End of " + file.getName() + "]");
            } catch (IOException ex) {
                txtBroadcast.append("\nFile Send Error: " + ex);
            }
        }
    }

    public void logoutSession() {
        if (s == null) return;
        try {
            dos.writeUTF(MyServer.LOGOUT_MESSAGE);
            Thread.sleep(300);
            s = null;
        } catch (Exception e) {
            txtBroadcast.append("\nLogout Error: " + e);
        }
        logoutButton.setEnabled(false);
        loginButton.setEnabled(true);
        chatWindow.setTitle("Login for Chat");
    }

    public void clientChat(String uname) {
        try {
            s = new Socket(InetAddress.getLocalHost(), MyServer.PORT);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            new Thread(new ClientThread(dis, this)).start();
            dos.writeUTF(uname);
            chatWindow.setTitle(uname + " - Chat Window");
            logoutButton.setEnabled(true);
            loginButton.setEnabled(false);
        } catch (Exception e) {
            txtBroadcast.append("\nConnection Error: " + e);
        }
    }

    public MyClient() {
        displayGUI();
    }

    public static void main(String[] args) {
        new MyClient();
    }
}

class ClientThread implements Runnable {
    DataInputStream dis;
    MyClient client;

    ClientThread(DataInputStream dis, MyClient client) {
        this.dis = dis;
        this.client = client;
    }

    public void run() {
        String s2 = "";
        do {
            try {
                s2 = dis.readUTF();
                if (s2.startsWith(MyServer.UPDATE_USERS)) updateUsersList(s2);
                else if (s2.equals(MyServer.LOGOUT_MESSAGE)) break;
                else {
                    client.txtBroadcast.append("\n" + s2);
                    int lineOffset = client.txtBroadcast.getLineStartOffset(client.txtBroadcast.getLineCount() - 1);
                    client.txtBroadcast.setCaretPosition(lineOffset);
                }
            } catch (Exception e) {
                client.txtBroadcast.append("\nClientThread run Error: " + e);
            }
        } while (true);
    }

    public void updateUsersList(String ul) {
        Vector<String> ulist = new Vector<>();
        ul = ul.replace("[", "").replace("]", "").replace(MyServer.UPDATE_USERS, "");
        StringTokenizer st = new StringTokenizer(ul, ",");
        while (st.hasMoreTokens()) {
            ulist.add(st.nextToken().trim());
        }
        client.usersList.setListData(ulist);
    }
}
