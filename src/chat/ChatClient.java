package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame {

   private final static String TAG = "ChatClient:";
   private ChatClient chatClient = this;

   private static final int PORT = 10000;

   private JButton btnConnect, btnSend;
   private JTextField tfHost, tfChat;
   private JTextArea taChatList;
   private ScrollPane scrollPane;

   private JPanel topPanel, bottomPanel;

   private Socket socket;
   private PrintWriter writer;
   private BufferedReader reader;
   
   private FileWriter fw;
   private List<String> fileString;


   public ChatClient() {
      init();
      setting();
      batch();
      listener();
      setVisible(true);

   }

   private void init() {
      btnConnect = new JButton("connect");
      btnSend = new JButton("send");
      tfHost = new JTextField("127.0.0.1", 20);
      tfChat = new JTextField(20);
      taChatList = new JTextArea(10, 30);// row,column
      scrollPane = new ScrollPane();
      topPanel = new JPanel();
      bottomPanel = new JPanel();
      fileString = new ArrayList<>();
      try {
		fw = new FileWriter("D:/TEST.txt");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }

   private void setting() {
      setTitle("채팅 다대다 클라이언트");
      setSize(350, 500);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);
      taChatList.setBackground(Color.ORANGE);
      taChatList.setForeground(Color.BLUE);
   }

   private void batch() {
      topPanel.add(tfHost);
      topPanel.add(btnConnect);
      bottomPanel.add(tfChat);
      bottomPanel.add(btnSend);
      scrollPane.add(taChatList);

      add(topPanel, BorderLayout.NORTH);
      add(scrollPane, BorderLayout.CENTER);
      add(bottomPanel, BorderLayout.SOUTH);
   }

   private void listener() {
      btnConnect.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            connect();
         }
      });

      btnSend.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            send();
         }
      });
      tfChat.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					send();
				}
			}
		});
      this.addWindowListener(new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent e) {
            System.out.println("종료버튼실행");
           save();
         }
      });
      
      
   }

   private void send() {
      String chat = tfChat.getText();
      // 1번 taChatList 뿌리기
      taChatList.append("[내메시지] " + chat + "\n");
      writer.write(chat + "\n");
      fileString.add("[내메시지] " +chat);
      writer.flush();

      // chat 비우기
      tfChat.setText("");
   }

   private void connect() {
      String host = tfHost.getText();
      try {
         socket = new Socket(host, PORT);
         reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         writer = new PrintWriter(socket.getOutputStream());
         ReaderThread rt = new ReaderThread();
         rt.start();
      } catch (Exception e1) {
         System.out.println(TAG + "서버 연결 에러" + e1.getMessage());
      }
   }

   private void save(){
      try{
         System.out.println("세이브실행");
         //TODO : JTextArea 저장방법
        
    
         for (int i = 0; i < fileString.size(); i++) {
			fw.write(fileString.get(i)+"\n");
			 fw.flush();
		}
       
        
         fw.close();
         System.out.println("성공");
      }catch(Exception e){
         e.printStackTrace();
      }

   }

   class ReaderThread extends Thread {
      // while을 돌면서 서버로부터 메시지를 받아서 taChatList에 뿌리기
      @Override
      public void run() {
         try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input = null;
            while ((input = reader.readLine()) != null) {
               taChatList.append(input + "\n");
               fileString.add(input);
            }

         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public static void main(String[] args) {
      new ChatClient();
   }
}
