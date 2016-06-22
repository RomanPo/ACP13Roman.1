package ua.artcode.taxi.remote;

import com.google.gson.Gson;
import ua.artcode.taxi.exception.*;
import ua.artcode.taxi.model.Address;
import ua.artcode.taxi.model.Order;
import ua.artcode.taxi.model.OrderStatus;
import ua.artcode.taxi.model.User;
import ua.artcode.taxi.service.UserService;
import ua.artcode.taxi.to.Message;
import ua.artcode.taxi.to.MessageBody;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Created by serhii on 05.06.16.
 */
public class RemoteUserService implements UserService {

    private Gson gson;
    private Socket connection;
    private BufferedReader bf;
    private PrintWriter pw;

    public RemoteUserService() {

        gson = new Gson();

        try {
            connection = new Socket("10.100.27.130", 9999);
            bf = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            pw = new PrintWriter(connection.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public User registerPassenger(User user) throws RegisterException {
        return null;
    }

    @Override
    public User registerDriver(User user) throws RegisterException {
        return null;
    }

    @Override
    public String login(String phone, String pass) throws LoginException {

        Message src = new Message();
        src.setMethodName("login");

        MessageBody messageBody = new MessageBody();
        messageBody.getMap().put("phone", phone);
        messageBody.getMap().put("pass", pass);

        src.setMessageBody(messageBody);
        String jsonMessage = gson.toJson(src);

        pw.println(jsonMessage);
        pw.flush();

        String jsonResponse =  null;
        try {
            jsonResponse = bf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(jsonResponse);

        Message response = gson.fromJson(jsonResponse, Message.class);
        // json - Object
        return response.getMessageBody().getMap().get("accessKey").toString();
    }

    @Override
    public Order makeOrder(String accessToken, Address from, Address to, String message) throws OrderMakeException, UserNotFoundException, InputDataWrongException {
        return null;
    }

    @Override
    public Order makeOrderAnonymous(String phone, String name, String from, String to, String message) throws OrderMakeException, InputDataWrongException {
        return null;
    }

    @Override
    public Order calculateOrder(Address from, Address to) throws InputDataWrongException {
        return null;
    }

    @Override
    public Order getOrderInfo(long orderId) throws OrderNotFoundException {
        return null;
    }

    @Override
    public Order getLastOrderInfo(String accessToken) throws UserNotFoundException, OrderNotFoundException {
        return null;
    }

    @Override
    public Order cancelOrder(long orderId) throws OrderNotFoundException {
        return null;
    }

    @Override
    public Order closeOrder(String accessToken, long orderId) throws OrderNotFoundException, WrongStatusOrderException, DriverOrderActionException {
        return null;
    }

    @Override
    public Order takeOrder(String accessToken, long orderId) throws OrderNotFoundException, WrongStatusOrderException, DriverOrderActionException {
        return null;
    }

    @Override
    public List<Order> getAllOrdersByStatus(OrderStatus status) {
        return null;
    }

    @Override
    public Map<Integer, Order> getMapDistancesToDriver(List<Order> ordersInProgress, int[] distances) {
        return null;
    }

    @Override
    public int[] getArrayDistancesToDriver(List<Order> orders, Address addressDriver) {
        return new int[0];
    }

    @Override
    public User getUser(String accessToken) {
        return null;
    }

    @Override
    public List<Order> getAllOrdersUser(String accessToken) {
        return null;
    }

    @Override
    public User updateUser(User newUser, String accessToken) throws RegisterException {
        return null;
    }

    @Override
    public User deleteUser(String accessToken) {
        return null;
    }
}
