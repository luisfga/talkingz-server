///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package br.com.luisfga.talkingz.server.database.cassandra;
//
//import br.com.luisfga.talkingz.commons.MessageWrapper;
//import br.com.luisfga.talkingz.commons.orchestration.command.CommandSend;
//
//import javax.enterprise.context.ApplicationScoped;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
///**
// *
// * @author Luis
// */
//public class MessageDAO {
//
//    public void saveMessage(CommandSend cmdSend){
////        try {
////            Connection conn = JDBCUtils.getMySQLConnection();
////            PreparedStatement st = conn.prepareStatement("insert into message "
////                    + "(uuid, sender_id, dest_id, sent_time, content, mime_type, media_thumbnail, media_download_token, status_msg) "
////                    + "values (?,?,?,?,?,?,?,?,?)");
////
////            st.setLong(1, cmdSend.getMessageWrapper().getUUID());
////            st.setLong(2, cmdSend.getMessageWrapper().getSenderId());
////            st.setLong(3, cmdSend.getMessageWrapper().getDestId());
////            st.setLong(4, cmdSend.getMessageWrapper().getSentTimeInMilis());
////            st.setString(5, cmdSend.getMessageWrapper().getContent());
////            st.setByte(6, cmdSend.getMessageWrapper().getMimetype());
////            st.setBytes(7, cmdSend.getMessageWrapper().getMediaThumbnail());
////            st.setString(8, cmdSend.getMessageWrapper().getDownloadToken());
////            st.setInt(9, MessageStatus.MSG_STATUS_ON_TRAFFIC);
////
////            st.execute();
////
////            System.out.println("Mensagem salva (#"+cmdSend.getMessageWrapper().getUUID()+")");
////
////            conn.close();
////
////        /* caso a conexão de um usuário caia após enviar uma mensagem e antes de receber o feedback confirmando que esta foi salva,
////           seu app cliente, ao reconectar, tentará enviar a mensagem novamente. E se ela já tiver sido salva realmente, dará erro de chave duplicada.
////           Nesse caso basta ignorar e prosseguir com o processo e confirmar o status ON_TRAFFIC*/
////        } catch (SQLIntegrityConstraintViolationException ex) {
////            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Erro ao tentar salvar mensagem. PROVAVELMENTE uma tentativa de salvar uma mensagem repetida.", ex);
////
////        } catch (SQLException ex) {
////            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Erro ao tentar salvar mensagem", ex);
////        }
//
//    }
//
//    public List<MessageWrapper> findPendingMessages(UUID destId){
//        List<MessageWrapper> pendingMessages = new ArrayList<>();
////        try {
////            Connection conn = JDBCUtils.getMySQLConnection();
////
////            //get messages being sent
////            PreparedStatement selectStatement = conn.prepareStatement("select "
////                    + "uuid, sender_id, dest_id, sent_time, mime_type, content, media_thumbnail, media_download_token, status_msg "
////                    + "from message where dest_id = ? and status_msg = ? order by sent_time asc");
////
////            selectStatement.setLong(1, destId);
////            selectStatement.setInt(2, MessageStatus.MSG_STATUS_ON_TRAFFIC);
////            ResultSet rs = selectStatement.executeQuery();
////            while (rs.next()) {
////                MessageWrapper message = new MessageWrapper();
////                message.setUUID(rs.getLong(1));
////                message.setSenderId(rs.getLong(2));
////                message.setDestId(rs.getLong(3));
////                message.setSentTimeInMilis(rs.getLong(4));
////                message.setMimetype(rs.getByte(5));
////                message.setContent(rs.getString(6));
////                message.setMediaThumbnail(rs.getBytes(7));
////                message.setDownloadToken(rs.getString(8));
////                message.setStatus(rs.getInt(9));
////                System.out.println("Mensagem pendente encontrada (uuid:"+message.getUUID()+")");
////                pendingMessages.add(message);
////            }
////
////            conn.close();
////
////        } catch (SQLException ex) {
////            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        return pendingMessages;
//    }
//
//    public List<UUID> findPendingConfirmations(UUID senderId){
//
//        List<UUID> pendingConfirmationUUIDs = new ArrayList<>();
//
////        try {
////            Connection conn = JDBCUtils.getMySQLConnection();
////
////            //get messages being sent
////            PreparedStatement selectStatement = conn.prepareStatement("select uuid from message "
////                    + "where sender_id = ? and status_msg = ? order by sent_time asc");
////
////            selectStatement.setLong(1, senderId);
////            selectStatement.setInt(2, MessageStatus.MSG_STATUS_DELIVERED);
////
////            ResultSet rs = selectStatement.executeQuery();
////            while (rs.next()) {
////                pendingConfirmationUUIDs.add(rs.getLong(1));
////            }
////
////            conn.close();
////
////        } catch (SQLException ex) {
////            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        return pendingConfirmationUUIDs;
//    }
//
//    public void updateMessageStatus(UUID uuid, int statusMsg){
////        try {
////            Connection conn = JDBCUtils.getMySQLConnection();
////            PreparedStatement st = conn.prepareStatement("update message set status_msg = ? where uuid = ?");
////
////            st.setInt(1, statusMsg);
////            st.setLong(2, uuid);
////
////            st.executeUpdate();
////
////            System.out.println("Mensagem(#"+uuid+") alterada - novo status: "+statusMsg);
////
////            conn.close();
////        } catch (SQLException ex) {
////            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
//    }
//
//}
