package gpn.service;

import gpn.contract.Claim;
import gpn.contract.SystemUser;
import gpn.exception.ApplicationException;
import gpn.exception.UserNotFoundException;
import gpn.interfaces.service.IAuthenticationService;
import gpn.interfaces.service.IClaimsService;
import gpn.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.util.List;
import java.util.ArrayList;

@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
//    @Autowired
//    private ILdapService ldapService;
    @Autowired
    private UserService userService;
    @Autowired
    private IClaimsService claimsService;

    @Override
    public String getAuthToken(String userName, String lastName, String phone) throws UserNotFoundException, ApplicationException {
//        SearchResult searchResult;
//        try {
//            searchResult = ldapService.findAccountByAccountName(userName);
//        } catch (NamingException e) {
//            throw new ApplicationException(e);
//        }
//        if (searchResult == null) {
//            throw new UserNotFoundException();
//        }

        SystemUser sUser = new SystemUser();
        sUser.setId(1L);
        sUser.setUserName(userName);
        sUser.setLastName(lastName);
        sUser.setPhoneNumber(phone);
        sUser.setDomainName("-");
        sUser.setDisplayName("-");
        sUser.setEmail("-");
        sUser.setGuid("-");
//        try {
//            fillAttributes(sUser, searchResult);
//        } catch (NamingException e) {
//            throw new ApplicationException(e);
//        }

        SystemUser cUser = sUser;
        boolean isNewUser = true;
        if (isNewUser) {
            List<Claim> defaultClaims = new ArrayList<>();
            Claim testClaim = new Claim("testType", "testValue");
            testClaim.setId(1L);
            defaultClaims.add(testClaim);
            sUser.setClaims(defaultClaims);
        }

        if (cUser != null) {
            sUser.setId(cUser.getId());
            sUser.setClaims(sUser.getClaims());
        }

//        SystemUser sUser = new SystemUser();

//        sUser.setId(1L);
//        sUser.setDomainName("user1");
//        sUser.setUserName("user2");
//        sUser.setDisplayName("user3");
//        sUser.setEmail("mail");
//        sUser.setGuid("uuid");

        final UserDetails userDetails = new User(userName, "$2a$10$ixlPY3AAd4ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWNhyFrrT3YUi", new ArrayList<>());
        return jwtTokenUtil.generateToken(userDetails, sUser);
    }

    /**
     * запонение контракта из ldap
     * @param ldapUser контракт
     * @param searchResult результат поиска из Ldap
     */
    private void fillAttributes(SystemUser ldapUser, SearchResult searchResult) throws NamingException {
        Attributes attributes = searchResult.getAttributes();
        ldapUser.setUserName(attributes.get("mailnickname").get(0).toString());
        ldapUser.setDomainName(attributes.get("mailnickname").get(0).toString());
        ldapUser.setDisplayName(attributes.get("displayname").get(0).toString());
        ldapUser.setEmail(attributes.get("mail").get(0).toString());
        ldapUser.setGuid(attributes.get("objectguid").get(0).toString());
    }
}
