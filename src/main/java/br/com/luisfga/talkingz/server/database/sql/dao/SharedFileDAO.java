package br.com.luisfga.talkingz.server.database.sql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.agroal.api.AgroalDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.luisfga.talkingz.commons.utils.SharedFileDTO;

import javax.inject.Inject;

/**
 *
 * @author luisfga.com.br
 */
public class SharedFileDAO {

    private final Logger logger = LoggerFactory.getLogger(SharedFileDAO.class);

    @Inject
    AgroalDataSource dataSource;

    public void saveFile(SharedFileDTO sharedFileDTO){

        try(Connection conn = dataSource.getConnection()){

            PreparedStatement st = conn.prepareStatement("insert into shared_file "
                    + "(download_token, file_bytes, mime_type, size, date_time) "
                    + "values (?,?,?,?,?)");

            st.setString(1, sharedFileDTO.getDownloadToken());
            st.setBytes(2, sharedFileDTO.getBytes());
            st.setInt(3, sharedFileDTO.getMimeType());
            st.setLong(4, sharedFileDTO.getBytes().length);
            st.setLong(5, System.currentTimeMillis());

            st.execute();

            logger.debug("Arquivo salvo (\""+sharedFileDTO.getDownloadToken()+"\" - "+sharedFileDTO.getBytes().length+" bytes)");

        } catch (SQLException ex) {
            logger.error("Erro ao salvar arquivo", ex);
        }
    }

    public SharedFileDTO findFileByDownloadToken(String downloadToken){
        SharedFileDTO sharedFileDTO = null;

        try(Connection conn = dataSource.getConnection()){

            PreparedStatement st = conn.prepareStatement("select file_name, file_bytes, mime_type from shared_file where download_token = ?");
            st.setString(1, downloadToken);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                sharedFileDTO = new SharedFileDTO();
                sharedFileDTO.setDownloadToken(rs.getString(1));
                sharedFileDTO.setBytes(rs.getBytes(2));
                sharedFileDTO.setMimeType(rs.getByte(3));
            }

        } catch (SQLException ex) {
            logger.error("Erro ao buscar arquivo pelo download token", ex);
        }
        return sharedFileDTO;
    }
}