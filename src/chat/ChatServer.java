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
   private Vector<ClientInfo> vc; // 연결된 클라이언트 소켓을 담는 컬렉션
   private FileWriter txt;


   public ChatServer() {
      try {
         vc = new Vector<>();
         serverSocket = new ServerSocket(10000);
         System.out.println(TAG + "클라이언트 연결 대기중...");
         // 메인쓰레드의 역할
         while (true) {
            Socket socket = serverSocket.accept();
            System.out.println(TAG + "연결 성공");
            ClientInfo clientInfo = new ClientInfo(socket);
            clientInfo.start();
            vc.add(clientInfo);
         }
      } catch (IOException e) {
         e.printStackTrace();
         System.out.println("실패");
      }
   }

   class ClientInfo extends Thread {

      String id;
      Socket socket;
      BufferedReader reader;
      PrintWriter writer; // BufferdWriter와 다른점은 내려쓰기 함수를 지원

      public ClientInfo(Socket socket) {
         this.socket = socket;
         try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
         } catch (Exception e) {
            System.out.println("서버 연결 실패:" + e.getMessage());
            e.printStackTrace();
         }
      }

      // 역할 : 클라이언트로 부터 받은 메시지를 모든 클라이언트 한테 재전송
      @Override
      public void run() {
         try {
               String input = null;
               writer.println("아이디를 입력하세요. 예)ID:아이디");
               writer.flush();

               while ((input = reader.readLine()) != null) {
                  String gubun[] = input.split(":");
                  if (id == null) {
                     if (gubun[0].equals(protocol.ID)) { // 입력양식 ID:ssar
                        // 변수에 ID 저장
                        id = gubun[1];
                        writer.println("당신의 아이디는 " + id + "입니다.");
                        writer.flush();
                     } else {
                        writer.println("아이디를 먼저 입력하세요!");
                        writer.flush();

                     }
                  }else if (id != null) {
                    // input 내용을 파일에 쓰면 되는데
                     writer.flush();
                     for (int i = 0; i < vc.size(); i++) {
                        if (vc.get(i) != this) { // 내가 아닌 다른 클라이언트 들에게
                           vc.get(i).writer.println("[" + id + "] " + input); // 메시지를 뿌림
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