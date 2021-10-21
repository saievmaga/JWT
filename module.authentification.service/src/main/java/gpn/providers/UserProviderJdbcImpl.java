package gpn.providers;

import gpn.contract.Claim;
import gpn.contract.SystemUser;
import gpn.interfaces.providers.DataSourceBean;
import gpn.interfaces.providers.IUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class UserProviderJdbcImpl implements IUserProvider {

    private Sql2o sql2o;
    private ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

    public UserProviderJdbcImpl(@Autowired DataSourceBean dataSource) {
        sql2o = new Sql2o(dataSource.getDataSource());
    }

    @PostConstruct
    public void init() {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("domain_name", "domainName");
        mappings.put("user_name", "userName");
        mappings.put("display_name", "displayName");
        mappings.put("last_updated", "lastUpdated");
        sql2o.setDefaultColumnMappings(mappings);
    }

    @Override
    public SystemUser getUser(String userName) {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(findUserTemplate, false)
                    .addParameter("user_name", userName)
                    .executeAndFetchFirst(SystemUser.class);
        }
    }

    @Override
    public boolean isUserExists(String userName) {
        try (Connection connection = sql2o.open()) {
            Integer userCount = connection
                    .createQuery("select count(*) from main.ad_user where user_name = :userName", false)
                    .addParameter("userName", userName)
                    .executeScalar(Integer.class);
            return userCount != null && userCount > 0;
        }
    }

    @Override
    public void update(SystemUser user) {
        try (Connection connection = sql2o.open()) {
            user.setId(connection.createQuery(MergeUserTemplate)
                    .addParameter("domain_name", user.getDomainName())
                    .addParameter("user_name", user.getUserName())
                    .addParameter("display_name", user.getDisplayName())
                    .addParameter("email", user.getEmail())
                    .addParameter("guid", user.getGuid())
                    .addParameter("last_updated", Date.from(utc.toInstant()))
                    .executeUpdate()
                    .getKey(Long.class));
        }
    }

    @Override
    public List<Claim> getClaims(Long userId) {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(getClaimsTemplate, false)
                    .addParameter("uId", userId)
                    .executeAndFetch(Claim.class);
        }
    }

    private String MergeUserTemplate = "insert into main.ad_user (domain_name, user_name, display_name, email, guid, last_updated)\n" +
            "values(:domain_name, :user_name, :display_name, :email, :guid, :last_updated)\n" +
            "on conflict (domain_name, user_name)\n" +
            "do update set display_name = :display_name, email = :email, guid = :guid, last_updated = :last_updated";

    private final String findUserTemplate = "select id, domain_name as domainName\n" +
            ",user_name as userName\n" +
            ",display_name as displayName\n" +
            ",email\n" +
            ",guid\n" +
            ",last_updated as lastUpdated\n" +
            "from main.ad_user where user_name = :user_name;";

    private String getClaimsTemplate = "select c.type as type, c.code as value from  main.ad_user_claim auc\n" +
            "join main.claim c on auc.claim_id = c.id\n" +
            "where auc.ad_user_id = :uId";
}

