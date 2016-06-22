package ua.artcode.taxi.run;

import ua.artcode.taxi.dao.AppDB;
import ua.artcode.taxi.dao.OrderDaoInnerDbImpl;
import ua.artcode.taxi.dao.UserDaoInnerDbImpl;
import ua.artcode.taxi.service.UserService;
import ua.artcode.taxi.service.UserServiceImpl;
import ua.artcode.taxi.service.ValidatorImpl;
import ua.artcode.taxi.view.UserLogin;

public class StartApp {


    public static void main(String[] args) {
        UserService userService = new UserServiceImpl(new UserDaoInnerDbImpl(new AppDB()),
                                            new OrderDaoInnerDbImpl(new AppDB()),
                                                    new ValidatorImpl(new AppDB()));
        new UserLogin(userService);

    }
}
