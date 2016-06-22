package ua.artcode.taxi.service;

import ua.artcode.taxi.dao.AppDB;
import ua.artcode.taxi.exception.InputDataWrongException;
import ua.artcode.taxi.exception.RegisterException;
import ua.artcode.taxi.model.Address;
import ua.artcode.taxi.model.ClientAccessToken;
import ua.artcode.taxi.model.User;
import ua.artcode.taxi.model.UserIdentifier;

import java.util.Collection;

public class ValidatorImpl implements Validator {

    private AppDB appDB;
    private ClientAccessToken accessToken;

    public ValidatorImpl(AppDB appDB) {
        this.appDB = appDB;
    }

    @Override
    public boolean validateLogin(String phone, String password) {
        boolean result = false;

        Collection<User> users = appDB.getUsers().keySet();

        for (User user : users) {
            if (user.getPhone().equals(phone) && user.getPass().equals(password)) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean validateRegistration(String phone) throws RegisterException {

        Collection<User> users = appDB.getUsers().keySet();

        for (User user : users) {
            if (user.getPhone().equals(phone)) {
                throw new RegisterException("This phone using already");
            }
        }

        return true;
    }

    @Override
    public boolean validateAddress(Address address) throws InputDataWrongException {

        if (address.getCountry().equals("")) {
            throw new InputDataWrongException("Wrong data: country");
            //result = false;
        } else if (address.getCity().equals("")) {
            throw new InputDataWrongException("Wrong data: city");
            //result = false;
        } else if (address.getStreet().equals("")) {
            throw new InputDataWrongException("Wrong data: street");
            //result = false;
        } else if (address.getHouseNum().equals("")) {
            throw new InputDataWrongException("Wrong data: houseNum");
            //result = false;
        }

        return true;
    }

    @Override
    public boolean validateChangeRegistration(UserIdentifier identifier, int id, String phone)
                                                                        throws RegisterException {

        Collection<User> users = appDB.getUsers().keySet();

        for (User user : users) {
            if (user.getPhone().equals(phone) && user.getId() != id &&
                        user.getIdentifier().equals(identifier)) {
                throw new RegisterException("This phone using already");
            }
        }

        return true;
    }
}
