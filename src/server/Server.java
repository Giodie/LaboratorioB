package server;
import java.io.*;
import java.net.*;
public class Server{
    private ServerSocket serverSocket;
    public final static int PORT = 8080;
    public Server(){
        try{
            serverSocket = new ServerSocket(PORT);
        }catch(IOException e){}
    }
    public void esegui(){
        while(true){
            try{
                System.out.println("Attendo una connessione");
                Socket socket = serverSocket.accept();
                System.out.println("Connessione effettuata");
                new ServerMulti(socket);
            }catch(IOException e){}
        }
    }
    public static void main(String[] args){
        Server server = new Server();
        server.esegui();
    }
}
