package br.com.luisfga.talkingz.server.database.sql.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@NamedQueries({
        @NamedQuery(name = "Message.findPendingMessages", query = "SELECT m FROM Message m WHERE m.destId = :destId AND m.msgStatus = :statusOnTraffic  ORDER BY sentTime ASC"),
        @NamedQuery(name = "Message.findPendingConfirmations", query = "SELECT m.id FROM Message m WHERE m.senderId = :senderId AND m.msgStatus = :statusDelivered  ORDER BY sentTime ASC")
})
public class Message {
    @Id
    private UUID id;

    @Column(name = "sender_id")
    private UUID senderId;

    @Column(name = "dest_id")
    private UUID destId;

    @Column(name = "sent_time")
    private OffsetDateTime sentTime;

    @Column(columnDefinition = "varchar(1000)")
    private String content;

    @Column(name = "mime_type")
    private byte mimeType;

    @Lob
    @Column(name = "media_thumbnail")
    private byte[] mediaThumbnail;

    @Column(name = "media_download_token")
    private String mediaDownloadToken;

    @Column(name = "msg_status")
    private byte msgStatus;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getDestId() {
        return destId;
    }

    public void setDestId(UUID destId) {
        this.destId = destId;
    }

    public OffsetDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(OffsetDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte getMimeType() {
        return mimeType;
    }

    public void setMimeType(byte mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getMediaThumbnail() {
        return mediaThumbnail;
    }

    public void setMediaThumbnail(byte[] mediaThumbnail) {
        this.mediaThumbnail = mediaThumbnail;
    }

    public String getMediaDownloadToken() {
        return mediaDownloadToken;
    }

    public void setMediaDownloadToken(String mediaDownloadToken) {
        this.mediaDownloadToken = mediaDownloadToken;
    }

    public byte getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(byte msgStatus) {
        this.msgStatus = msgStatus;
    }
}
