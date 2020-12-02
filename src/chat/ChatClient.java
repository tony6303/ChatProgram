package chat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

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
   }

   private void setting() {
      setTitle("ä�� �ٴ�� Ŭ���̾�Ʈ");
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
            System.out.println("�����ư����");
            ChatClient client = new ChatClient();
            client.save();
         }
      });
   }

   private void send() {
      String chat = tfChat.getText();
      // 1�� taChatList �Ѹ���
      taChatList.append("[���޽���] " + chat + "\n");
      writer.write(chat + "\n");
      writer.flush();

      // chat ����
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
         System.out.println(TAG + "���� ���� ����" + e1.getMessage());
      }
   }

   private void save(){
      try{
         System.out.println("���̺����");
         //TODO : JTextArea ������
         FileWriter fw = new FileWriter("TEST.txt");
         String str = "test test";
         fw.write(str);
         fw.flush();
//         fw.close();
         System.out.println("����");
      }catch(Exception e){
         e.printStackTrace();
      }

   }

   class ReaderThread extends Thread {
      // while�� ���鼭 �����κ��� �޽����� �޾Ƽ� taChatList�� �Ѹ���
      @Override
      public void run() {
         try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input = null;
            while ((input = reader.readLine()) != null) {
               taChatList.append(input + "\n");
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
