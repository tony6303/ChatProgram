package chat;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {

   private ServerSocket serverSocket;
   private static final String TAG = "ChatServer :";
   private Vector<ClientInfo> vc; // ����� Ŭ���̾�Ʈ ������ ��� �÷���
   private FileWriter txt;
   
   
   public ChatServer() {
      // TODO Auto-generated constructor stub
      try {
         vc = new Vector<>();
         serverSocket = new ServerSocket(10000);
         System.out.println(TAG + "Ŭ���̾�Ʈ ���� �����...");
         // ���ξ������� ����
         while (true) {
            Socket socket = serverSocket.accept();
            ClientInfo clientInfo = new ClientInfo(socket);
            clientInfo.start();
            vc.add(clientInfo);
         }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         System.out.println("����");
      }
   }

   class ClientInfo extends Thread {
      
      String id;
      Socket socket;
      BufferedReader reader;
      PrintWriter writer; // BufferdWriter�� �ٸ����� �������� �Լ��� ����

      public ClientInfo(Socket socket) {
         // TODO Auto-generated constructor stub
         this.socket = socket;
         try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
         } catch (Exception e) {
            // TODO: handle exception
            System.out.println("���� ���� ����:" + e.getMessage());
            e.printStackTrace();
         }
      }

      // ���� : Ŭ���̾�Ʈ�� ���� ���� �޽����� ��� Ŭ���̾�Ʈ ���� ������
      @Override
      public void run() {
         // TODO Auto-generated method stub
         try {
               String input = null;
               writer.println("���̵� �Է��ϼ���. ��)ID:���̵�");
               writer.flush();

               while ((input = reader.readLine()) != null) {
                  String gubun[] = input.split(":");
                  if (id == null) {
                     if (gubun[0].equals(protocol.ID)) { // �Է¾�� ID:ssar
                        // ������ ID ����
                        id = gubun[1];
                        writer.println("����� ���̵�� " + id + "�Դϴ�.");
                        writer.flush();
                     } else {
                        writer.println("���̵� ���� �Է��ϼ���!");
                        writer.flush();

                     }
                  }else if (id != null) {
                    // input ������ ���Ͽ� ���� �Ǵµ�
                     writer.flush();
                     for (int i = 0; i < vc.size(); i++) {
                        if (vc.get(i) != this) { // ���� �ƴ� �ٸ� Ŭ���̾�Ʈ �鿡��
                           vc.get(i).writer.println("[" + id + "] " + input); // �޽����� �Ѹ�
                           vc.get(i).writer.flush();

                        }
                     }
                  }
               }
            } catch (Exception e) {
               e.printStackTrace();
            }

      }
      
      
      
   }
   public static void main(String[] args) {
         new ChatServer();
      }
}