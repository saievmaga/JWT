package gpn.interfaces.service;

import gpn.exception.ApplicationException;
import gpn.exception.UserNotFoundException;

import javax.naming.NamingException;

/**Сервис аутентификации пользователей*/
public interface IAuthenticationService {

    /**Получить аутентификационный токен для пользователя*/
    String getAuthToken(String firstName, String lastName, String phone) throws UserNotFoundException, NamingException, ApplicationException;
}
