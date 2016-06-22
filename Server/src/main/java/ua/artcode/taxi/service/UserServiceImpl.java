package ua.artcode.taxi.service;

import org.apache.log4j.Logger;
import ua.artcode.taxi.dao.OrderDao;
import ua.artcode.taxi.dao.UserDao;
import ua.artcode.taxi.exception.*;
import ua.artcode.taxi.model.*;
import ua.artcode.taxi.utils.geolocation.GoogleMapsAPI;
import ua.artcode.taxi.utils.geolocation.GoogleMapsAPIImpl;
import ua.artcode.taxi.utils.geolocation.Location;

import javax.security.auth.login.LoginException;
import java.util.*;

public class UserServiceImpl implements UserService {


    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);

    private UserDao userDao;
    private OrderDao orderDao;
    private ValidatorImpl validator;
    private double pricePerKilometer = 5;
    private GoogleMapsAPI googleMapsAPI = new GoogleMapsAPIImpl();
    private Map<String, User> accessKeys = new HashMap<>();

    public UserServiceImpl(UserDao userDao, OrderDao orderDao, ValidatorImpl validator) {
        LOG.trace("in constructor");
        this.userDao = userDao;
        this.orderDao = orderDao;
        this.validator = validator;
    }

    @Override
    public User registerPassenger(User user) throws RegisterException {
        LOG.info("registration of user " + user.toString());

        if (!validator.validateRegistration(user.getPhone())) {
            RegisterException registerException = new RegisterException("can not create exception");
            LOG.error("validation error", registerException);
            throw registerException;
        }

        return userDao.createUser(user);
    }

    @Override
    public User registerDriver(User user) throws RegisterException {
        LOG.info("registration of driver " + user.toString());
        if (!validator.validateRegistration(user.getPhone())) {
            RegisterException registerException = new RegisterException("can not create exception");
            LOG.error("validation error", registerException);
            throw registerException;
        }

        return userDao.createUser(user);
    }

    @Override
    public String login(String phone, String pass) throws LoginException {
        LOG.info("User with " + phone + " try log in");
        User found = null;

        boolean valid = validator.validateLogin(phone, pass);

        if (valid) {
            Collection<User> users = userDao.getAllUsers();
            for (User user : users) {
                found = user.getPhone().equals(phone) ? user : found ;
            }

        } else {
            LoginException loginException = new LoginException("User not found or incorrect password");
            LOG.error("login error", loginException);
            throw loginException;
        }

        String accessKey = UUID.randomUUID().toString();
        accessKeys.put(accessKey, found);

        LOG.info("End of log in");
        return accessKey;
    }

    @Override
    public Order makeOrder(String accessToken, Address from, Address to, String message)
                            throws OrderMakeException, UserNotFoundException, InputDataWrongException {

        Order newOrder = null;

        if (!validator.validateAddress(from) && !validator.validateAddress(to)) {
            throw new InputDataWrongException("Wrong input data addresses. Can not make order");
        }

        if (accessKeys.get(accessToken) != null) {

            Location location = googleMapsAPI.findLocation(from.getCountry(), from.getCity(),
                    from.getStreet(), from.getHouseNum());
            Location location1 = googleMapsAPI.findLocation(to.getCountry(), to.getCity(),
                    to.getStreet(), to.getHouseNum());
            int distance = (int) (googleMapsAPI.getDistance(location, location1) / 1000);
            int price = (int) pricePerKilometer * distance + 30;
            message = message.equals("") ? "" : accessKeys.get(accessToken).getName() + ": " + message;

            newOrder = new Order(from, to, accessKeys.get(accessToken), distance, price, message);

            orderDao.create(accessKeys.get(accessToken), newOrder);
            accessKeys.get(accessToken).getOrderIds().add(newOrder.getId());
        }

        return newOrder;
    }

    @Override
    public Order makeOrderAnonymous(String phone, String name, String fromStr, String toStr, String message)
                            throws OrderMakeException, InputDataWrongException {

        Address from = new Address(fromStr);
        Address to = new Address(toStr);

        if (!validator.validateAddress(from) && !validator.validateAddress(to)) {
            throw new InputDataWrongException("Wrong input data. Can not make order");
        }

        Location location = googleMapsAPI.findLocation(from.getCountry(), from.getCity(),
                from.getStreet(), from.getHouseNum());
        Location location1 = googleMapsAPI.findLocation(to.getCountry(), to.getCity(),
                to.getStreet(), to.getHouseNum());
        int distance = (int) (googleMapsAPI.getDistance(location, location1) / 1000);
        int price = (int) pricePerKilometer * distance + 30;


        User anonymousUser = userDao.createUser(new User(UserIdentifier.A, phone, name));
        Order newOrder = new Order(from, to, anonymousUser, distance, price, message);
        orderDao.create(anonymousUser, newOrder);

        return newOrder;
    }

    @Override
    public Order calculateOrder(Address from, Address to) throws InputDataWrongException {

        if (!validator.validateAddress(from) && !validator.validateAddress(to)) {
            throw new InputDataWrongException("Wrong input data. Can not make order");
        }

        Order testOrder = new Order();
        Location location = googleMapsAPI.findLocation(from.getCountry(), from.getCity(),
                        from.getStreet(), from.getHouseNum());
        Location location1 = googleMapsAPI.findLocation(to.getCountry(), to.getCity(),
                        to.getStreet(), to.getHouseNum());
        int distance = ((int) googleMapsAPI.getDistance(location, location1) / 1000);
        int price = (int) pricePerKilometer * distance + 30;

        testOrder.setDistance(distance);
        testOrder.setPrice(price);

        return testOrder;
    }

    @Override
    public Order getOrderInfo(long orderId) throws OrderNotFoundException {

        Order found = orderDao.find(orderId);

        if (found == null) {
            throw new OrderNotFoundException("Order not found in data base");
        }

        return found;
    }

    @Override
    public Order getLastOrderInfo(String accessToken) throws UserNotFoundException, OrderNotFoundException {

        if (accessToken != null) {
            List<Order> allUserOrders = getAllOrdersUser(accessToken);

            if (allUserOrders.size() <= 0) {
                throw new OrderNotFoundException("User don't have any orders");

            }

            return allUserOrders.get(allUserOrders.size() - 1);

        } else {
            throw new UserNotFoundException("wrong data user");
        }
    }

    @Override
    public Order cancelOrder(long orderId) throws OrderNotFoundException {

        Order cancelled = orderDao.find(orderId);

        if (cancelled != null) {
            cancelled.setOrderStatus(OrderStatus.CANCELLED);

        } else {
            throw new OrderNotFoundException("Order not found in data base");
        }

        return cancelled;
    }

    @Override
    public Order closeOrder(String accessToken, long orderId) throws OrderNotFoundException,
                                            WrongStatusOrderException, DriverOrderActionException {

        User user = accessKeys.get(accessToken);
        Order closed = orderDao.find(orderId);
        List<Order> ordersUser = userDao.getOrdersOfUser(user);
        Order result = null;

        for (Order order : ordersUser) {
            if (order.getId() == closed.getId()) {
                result = order;
            }
        }

        if (closed == null) {
            throw new OrderNotFoundException("Order not found in data base");

        } else if (result == null) {
            throw new DriverOrderActionException("Order not found in driver orders list");

        } else if (!result.getOrderStatus().equals(OrderStatus.IN_PROGRESS)) {
            throw new WrongStatusOrderException("This order has wrong status (not IN_PROGRESS)");
        } else {
            closed.setOrderStatus(OrderStatus.DONE);
        }

        return closed;
    }

    @Override
    public Order takeOrder(String accessToken, long orderId) throws OrderNotFoundException,
                                        WrongStatusOrderException, DriverOrderActionException {

        User user = accessKeys.get(accessToken);
        Order inProgress = orderDao.find(orderId);
        List<Order> ordersUser = userDao.getOrdersOfUser(user);

        for (Order order : ordersUser) {
            if (order.getOrderStatus().equals(OrderStatus.IN_PROGRESS)) {
                throw new DriverOrderActionException("Driver has orders IN_PROGRESS already");
            }
        }

        if (inProgress == null) {
            throw new OrderNotFoundException("Order not found in data base");

        } else if (!inProgress.getOrderStatus().equals(OrderStatus.NEW)) {
            throw new WrongStatusOrderException("This order has wrong status (not NEW)");
        }

        inProgress.setDriver(user);
        inProgress.setOrderStatus(OrderStatus.IN_PROGRESS);
        orderDao.addToDriver(user, inProgress);
        user.getOrderIds().add(inProgress.getId());

        return inProgress;
    }

    @Override
    public User getUser(String accessToken) {

        return accessKeys.get(accessToken);
    }

    @Override
    public List<Order> getAllOrdersUser(String accessToken) {

        return userDao.getOrdersOfUser(accessKeys.get(accessToken));
    }

    @Override
    public Map<Integer, Order> getMapDistancesToDriver(List<Order> ordersInProgress, int[] distances) {

        Map<Integer, Order> mapDistances = new HashMap<>(ordersInProgress.size());

        for (int i = 0; i < distances.length; i++) {
            mapDistances.put(distances[i], ordersInProgress.get(i));
        }

        Map<Integer, Order> sortingMapDistances = new HashMap<>(ordersInProgress.size());
        Arrays.sort(distances);
        for (int i = 0; i < distances.length; i++) {
            sortingMapDistances.put(distances[i], mapDistances.get(distances[i]));
        }

        return sortingMapDistances;
    }

    @Override
    public List<Order> getAllOrdersByStatus(OrderStatus status) {

        return orderDao.getOrdersByStatus(status);
    }

    @Override
    public User updateUser(User newUser, String accessToken) throws RegisterException {

        if (!validator.validateChangeRegistration(newUser.getIdentifier(), newUser.getId(), newUser.getPhone())) {
            throw new RegisterException("This phone is already in use by another user");

        } else {
            User updatedUser = userDao.updateUser(newUser);
            accessKeys.put(accessToken, updatedUser);

            return updatedUser;
        }
    }

    @Override
    public User deleteUser(String accessToken) {

        return userDao.deleteUser(accessKeys.get(accessToken).getId());
    }

    @Override
    public int[] getArrayDistancesToDriver(List<Order> orders, Address addressDriver){

        Location locationDriver = googleMapsAPI.findLocation
                (addressDriver.getCountry(), addressDriver.getCity(),
                        addressDriver.getStreet(), addressDriver.getHouseNum());

        int[] distances = new int[orders.size()];

        for (int i = 0; i < orders.size(); i++) {
            Location locationPassenger = googleMapsAPI.findLocation(orders.get(i).getFrom().getCountry(),
                    orders.get(i).getFrom().getCity(), orders.get(i).getFrom().getStreet(),
                    orders.get(i).getFrom().getHouseNum());

            distances[i] = new Distance(locationDriver, locationPassenger).calculateDistance();
        }

        return distances;
    }
}
