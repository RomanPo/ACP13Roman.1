package ua.artcode.taxi.run;

import com.google.gson.Gson;
import ua.artcode.taxi.dao.AppDB;
import ua.artcode.taxi.dao.OrderDaoInnerDbImpl;
import ua.artcode.taxi.dao.UserDaoInnerDbImpl;
import ua.artcode.taxi.model.Address;
import ua.artcode.taxi.model.User;
import ua.artcode.taxi.model.UserIdentifier;
import ua.artcode.taxi.service.UserService;
import ua.artcode.taxi.service.UserServiceImpl;
import ua.artcode.taxi.service.ValidatorImpl;
import ua.artcode.taxi.to.Message;
import ua.artcode.taxi.to.MessageBody;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;


public class RunServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9999);

        Gson gson = new Gson();

        AppDB appDB = new AppDB();
        appDB.addUser(new User(
                UserIdentifier.P, "1234","1234","Oleg", new Address()));


        UserService userService = new UserServiceImpl(
                new UserDaoInnerDbImpl(appDB),
                new OrderDaoInnerDbImpl(appDB),
                new ValidatorImpl(appDB));

        while(true){
            // waiting for new client
            Socket clientSocket = serverSocket.accept();

            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
            BufferedReader bf = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            while(true){

                String requestBody = bf.readLine() + "\n";
                System.out.println(requestBody);

                Message message = gson.fromJson(requestBody, Message.class);

                if("login".equals(message.getMethodName())){
                    Map<String, Object> map = message.getMessageBody().getMap();
                    Object phone = map.get("phone");
                    Object pass = map.get("pass");
                    try {
                        String accessKey = userService.login(phone.toString(), pass.toString());

                        Message responseMessage = new Message();
                        MessageBody messageBody = new MessageBody();

                        messageBody.getMap().put("accessKey", accessKey);
                        responseMessage.setMessageBody(messageBody);

                        printWriter.println(gson.toJson(responseMessage));
                        printWriter.flush();
                    } catch (LoginException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String getMenu(){

        return "1. Add user \n" + "2. Exit\n";

    }


}
