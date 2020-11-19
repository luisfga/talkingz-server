package br.com.luisfga.talkingzapp.server.database.sql.dao;

import io.agroal.api.AgroalDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.luisfga.talkingzapp.common.UserWrapper;
import br.com.luisfga.talkingzapp.server.database.sql.entity.AppUser;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 *
 * @author luisfga.com.br
 */
@ApplicationScoped
public class UserDAO {

    private Logger logger = LoggerFactory.getLogger(UserDAO.class);

    @Inject
    AgroalDataSource dataSource;

    @Inject
    EntityManager em;

    @Transactional
    public UserWrapper findUserByToken(String token){

        try {
            Query findByToken = em.createNamedQuery("AppUser.findBySearchToken");
            findByToken.setParameter("searchToken", token);
            AppUser user = (AppUser) findByToken.getSingleResult();

            UserWrapper userWrapper = new UserWrapper();
            userWrapper.setId(user.id);
            userWrapper.setName(user.userName);
            userWrapper.setThumbnail(user.thumbnail);
            userWrapper.setEmail(user.email);
            userWrapper.setJoinTime(user.joinTime.toInstant().toEpochMilli());
            userWrapper.setSearchToken(user.searchToken);

            return userWrapper;

        } catch (NoResultException nre) {
            return null;
        }

    }

    @Transactional
    public void synchronizeMainUser(UserWrapper userWrapper){

        Query findById = em.createNamedQuery("AppUser.findById");
        findById.setParameter("id", userWrapper.getId());

        AppUser user = null;
        try {
            user = (AppUser) findById.getSingleResult();
            user.userName = userWrapper.getName();
            user.thumbnail = userWrapper.getThumbnail();
            user.email = userWrapper.getEmail();
            user.searchToken = userWrapper.getSearchToken();

        } catch (NoResultException nre){
            user = new AppUser();

            user.id = userWrapper.getId();
            user.userName = userWrapper.getName();
            user.thumbnail = userWrapper.getThumbnail();
            user.email = userWrapper.getEmail();
            user.searchToken = userWrapper.getSearchToken();
            user.joinTime = OffsetDateTime.ofInstant(new java.util.Date(userWrapper.getJoinTime()).toInstant(), ZoneId.systemDefault());
        } finally {
            em.persist(user);
        }

    }

    //Postgresql UPSERT
//    public UserWrapper synchronizeMainUser(UserWrapper userWrapper){
//
//        try (Connection conn = SQLUtils.getPostgresConnection()) {
//
//            String upsertString = ""
//                    + "insert into "
//                        + "app_user (id, user_name, thumbnail, email, search_token) "
//                        + "values (?,?,?,?,?) "
//                        + "on conflict (id) do "
//                            + "update set "
//                                + "user_name=EXCLUDED.user_name,"
//                                + "thumbnail=EXCLUDED.thumbnail,"
//                                + "email=EXCLUDED.email,"
//                                + "search_token=EXCLUDED.search_token "
//                                + "where app_user.id = EXCLUDED.id";
//
//            PreparedStatement st = conn.prepareStatement(upsertString);
//            st.setObject(1, userWrapper.getId());
//            st.setString(2, userWrapper.getName());
//            st.setBytes(3, userWrapper.getThumbnail());
//            st.setString(4, userWrapper.getEmail());
//            st.setString(5, userWrapper.getSearchToken());
//
//            st.executeUpdate();
//
//        } catch (SQLException ex) {
//            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

}