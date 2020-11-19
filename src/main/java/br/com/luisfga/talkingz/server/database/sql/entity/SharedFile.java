package br.com.luisfga.talkingz.server.database.sql.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "shared_file")
public class SharedFile {

    @Id
    @GeneratedValue
    public long id;

    @Column(name = "download_token")
    public String downloadToken;

    @Column(name = "file_bytes")
    public byte[] fileBytes;

    @Column(name = "mime_type")
    public short mimeType;

    @Column(name = "file_size")
    public long fileSize;

    @Column(name = "date_time")
    public OffsetDateTime dateTime;

}
