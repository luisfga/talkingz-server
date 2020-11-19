package br.com.luisfga.talkingz.server.database.sql.entity;

import br.com.luisfga.talkingz.commons.MessageStatus;

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
    public UUID id;

    @Column(name = "sender_id")
    public UUID senderId;

    @Column(name = "dest_id")
    public UUID destId;

    @Column(name = "sent_time")
    public OffsetDateTime sentTime;

    public String content;

    @Column(name = "mime_type")
    public short mimeType;

    @Lob
    @Column(name = "media_thumbnail")
    public byte[] mediaThumbnail;

    @Column(name = "media_download_token")
    public String mediaDownloadToken;

    @Column(name = "msg_status")
    public short msgStatus;

}
