package ua.artcode.taxi.service;

import org.apache.log4j.Logger;
import org.junit.*;
import ua.artcode.taxi.dao.AppDB;
import ua.artcode.taxi.dao.OrderDaoInnerDbImpl;
import ua.artcode.taxi.dao.UserDaoInnerDbImpl;
import ua.artcode.taxi.model.Address;
import ua.artcode.taxi.model.User;
import ua.artcode.taxi.model.UserIdentifier;

import javax.security.auth.login.LoginException;

/**
 * Created by serhii on 04.06.16.
 */
public class TestUserService {

    private static final Logger LOG = Logger.getLogger(TestUserService.class);

    private static UserService userService;
    private static AppDB appDB;

    @BeforeClass
    public static void beforeAllTest() {
        appDB = new AppDB();
        userService = new UserServiceImpl(new UserDaoInnerDbImpl(appDB),
                new OrderDaoInnerDbImpl(appDB),
                new ValidatorImpl(appDB));
    }

    @Before
    public void beforeEachTest() {
        LOG.trace("before test");
        appDB.addUser(new User(UserIdentifier.P, "1234", "1234", "Oleg", new Address()));
    }

    @After
    public void afterEachTest() {
        // clear test data
        appDB = new AppDB();
    }


    @Test
    public void testLogIn() {
        LOG.trace("test login");
        try {
            String res = userService.login("1234", "1234");
            Assert.assertNotNull(res);
        } catch (LoginException e) {
            e.printStackTrace();
            new AssertionError("Logi in ");
        }
    }

    @Test(expected = LoginException.class)
    public void testLogInNegative() throws LoginException {
        String res = userService.login("1dfgdfg", "sdfgsdfg");
        //Assert.assertNotNull(res);
    }

    @Test // new instance of TestUserService
    public void testRegister() {

    }


}
