package br.com.luisfga.talkingz.server.database.sql.entity;


import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@NamedQueries({
        @NamedQuery(name = "AppUser.findById", query = "SELECT au FROM AppUser au WHERE au.id=:id"),
        @NamedQuery(name = "AppUser.findBySearchToken", query = "SELECT au FROM AppUser au WHERE au.searchToken=:searchToken")

})
public class AppUser {

    @Id
    @Column(columnDefinition = "uuid")
    public UUID id;
    @Column(name = "user_name")
    public String userName;
    @Lob
    public byte[] thumbnail;
    public String email;
    @Column(name = "join_time")
    public OffsetDateTime joinTime;
    public String password;
    @Column(name = "search_token")
    public String searchToken;

}
