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
    private UUID id;
    @Column(name = "user_name")
    private String userName;
    @Lob
    private byte[] thumbnail;
    private String email;
    @Column(name = "join_time")
    private OffsetDateTime joinTime;
    private String password;
    @Column(name = "search_token")
    private String searchToken;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OffsetDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(OffsetDateTime joinTime) {
        this.joinTime = joinTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSearchToken() {
        return searchToken;
    }

    public void setSearchToken(String searchToken) {
        this.searchToken = searchToken;
    }
}
