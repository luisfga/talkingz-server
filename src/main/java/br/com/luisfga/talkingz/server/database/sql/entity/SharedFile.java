package br.com.luisfga.talkingz.server.database.sql.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "shared_file")
public class SharedFile {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "download_token")
    private String downloadToken;

    @Column(name = "file_bytes")
    private byte[] fileBytes;

    @Column(name = "mime_type")
    private short mimeType;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "date_time")
    private OffsetDateTime dateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDownloadToken() {
        return downloadToken;
    }

    public void setDownloadToken(String downloadToken) {
        this.downloadToken = downloadToken;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public short getMimeType() {
        return mimeType;
    }

    public void setMimeType(short mimeType) {
        this.mimeType = mimeType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
