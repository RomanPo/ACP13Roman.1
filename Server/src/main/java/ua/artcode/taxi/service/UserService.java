package ua.artcode.taxi.service;

import ua.artcode.taxi.exception.*;
import ua.artcode.taxi.model.Address;
import ua.artcode.taxi.model.Order;
import ua.artcode.taxi.model.OrderStatus;
import ua.artcode.taxi.model.User;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Map;

/**
 * Created by serhii on 23.04.16.
 */
public interface UserService {

    //register
    User registerPassenger(User user) throws RegisterException;
    User registerDriver(User user) throws RegisterException;

    //login (return accessToken)
    String login(String phone, String pass) throws LoginException;

    //actions for passenger
    Order makeOrder(String accessToken, Address from, Address to, String message)
                        throws OrderMakeException, UserNotFoundException, InputDataWrongException;
    Order makeOrderAnonymous(String phone, String name, String from, String to, String message)
                        throws OrderMakeException, InputDataWrongException;
    Order calculateOrder(Address from, Address to) throws InputDataWrongException;
    Order getOrderInfo(long orderId) throws OrderNotFoundException;
    Order getLastOrderInfo(String accessToken) throws UserNotFoundException, OrderNotFoundException;
    Order cancelOrder(long orderId) throws OrderNotFoundException;
    Order closeOrder(String accessToken, long orderId) throws OrderNotFoundException,
                                    WrongStatusOrderException, DriverOrderActionException;

    //actions for driver
    Order takeOrder(String accessToken, long orderId)
            throws OrderNotFoundException, WrongStatusOrderException, DriverOrderActionException;
    List<Order> getAllOrdersByStatus(OrderStatus status);
    Map<Integer, Order> getMapDistancesToDriver(List<Order> ordersInProgress, int[] distances);
    int[] getArrayDistancesToDriver(List<Order> orders, Address addressDriver);

    //action for all
    User getUser(String accessToken);
    List<Order> getAllOrdersUser(String accessToken);
    User updateUser(User newUser, String accessToken) throws RegisterException;
    User deleteUser(String accessToken);
}
