import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


/*
채팅 클라이언트
스레드 ( 세부적 실행단위 )
1. ReceiverWorker implements Runnable : 친구들의 메세지를 입력받는 역할
2. ChatClient의 main thread : ReceiverWorker Thread 생성 start
	자신은 친구들에게 메세지를 출력하는 역할
 */
public class ChatClient {
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private Scanner sc;

    public void go() throws UnknownHostException, IOException {
        try {
            socket = new Socket("43.201.209.183", 5432);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(),true);
            sc = new Scanner(System.in);

            ReceiverWorker rw = new ReceiverWorker();
            Thread thread = new Thread(rw);
            thread.setDaemon(true);

            thread.start();

            System.out.println("**ChatClient가 서버에 접속**");

            while (true) {

                //System.out.print("서버에 보낼 메세지:");
                String message = sc.nextLine();
                pw.println(message);

                if (message.trim().equals("종료")) {
                    System.out.println("**ChatClient 종료합니다**");
                    break;
                }
            }
        }finally {
            closeAll();
        }
    }

    class ReceiverWorker implements Runnable {

        public void run() {
            try {

                receiveMessage();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void receiveMessage() throws IOException {
            while (true) {
                String message = br.readLine();

                if (message == null) {
                    break;
                }

                System.out.println(message);
                System.out.print("서버에 보낼 메세지:");
            }
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        try {
            client.go();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void closeAll() throws IOException {
        if (pw != null)
            pw.close();
        if (sc != null)
            sc.close();
        if (br != null)
            br.close();
        if (socket != null)
            socket.close();
    }

}