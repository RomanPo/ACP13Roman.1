package ua.artcode.taxi.view.run;

import ua.artcode.taxi.remote.RemoteUserService;
import ua.artcode.taxi.view.UserLogin;

/**
 * Created by serhii on 05.06.16.
 */
public class RunLogin {
    public static void main(String[] args) {
        new UserLogin(new RemoteUserService());
    }


}
