///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package br.com.luisfga.talkingz.server.database.cassandra;
//
//import com.datastax.oss.driver.api.core.CqlSession;
//import com.datastax.oss.driver.api.core.cql.BoundStatement;
//import com.datastax.oss.driver.api.core.cql.PreparedStatement;
//import br.com.luisfga.talkingz.commons.UserWrapper;
//
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//
///**
// *
// * @author Luis
// */
//public class UserDAO {
//
//    //TODO retornar um WSUserWrapper
//    public UserWrapper createNewUser(){
//        UserWrapper userWrapper = null;
////        try {
////            long now = System.currentTimeMillis();
////
////            CqlSession session = JDBCUtils.getCassandraSession();
////
////            com.datastax.oss.driver.api.core.cql.PreparedStatement cst = session.prepare("insert into user (id, join_time) values (blobAsUuid(timeuuidAsBlob(now())), now())");
////            session.execute(query)
////
////            PreparedStatement st = conn.prepareStatement("insert into user (join_time) values (?)", Statement.RETURN_GENERATED_KEYS);
////            st.setLong(1, now);
////            st.executeUpdate();
////
////            ResultSet rs = st.getGeneratedKeys();
////            long newUserID = 0;
////            if (rs.next()) {
////                newUserID = rs.getLong(1);
////                System.out.println("New generated user ID: " + newUserID);
////            }
////            conn.close();
////
////            userWrapper = new UserWrapper();
////            userWrapper.setId(newUserID);
////            userWrapper.setJoinDateInMilis(now);
////
////
////
////        } catch (SQLException ex) {
////            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        return userWrapper;
//    }
//
//    public UserWrapper findUserByToken(String token){
//        UserWrapper userWrapper = null;
////        try {
////            Connection conn = JDBCUtils.getMySQLConnection();
////            PreparedStatement st = conn.prepareStatement("select id,name,thumbnail,email,join_time,token from user where token = ?");
////            st.setString(1, token);
////
////            ResultSet rs = st.executeQuery();
////            if (rs.next()) {
////                userWrapper = new UserWrapper();
////                userWrapper.setId(rs.getLong(1));
////                userWrapper.setName(rs.getString(2));
////                userWrapper.setThumbnail(rs.getBytes(3));
////                userWrapper.setEmail(rs.getString(4));
////                userWrapper.setJoinDateInMilis(rs.getLong(5));
////                userWrapper.setToken(rs.getString(6));
////
////            }
////            conn.close();
////
////        } catch (SQLException ex) {
////            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        return userWrapper;
//    }
//
//    public void synchronizeMainUser(UserWrapper userWrapper){
//
//        CqlSession session = CqlSession.builder()
//                .addContactPoint(new InetSocketAddress("127.0.0.1", 9042))
//                .withLocalDatacenter("datacenter1")
//                .build();
//
//        PreparedStatement ps = session.prepare("UPDATE orchestra.user SET name=?,thumbnail=?,email=?,search_token=?,join_time=? WHERE id=?");
//        BoundStatement bs = ps.bind()
//                .setString("name", userWrapper.getName())
//                .setByteBuffer("thumbnail", userWrapper.getThumbnail()!=null?ByteBuffer.wrap(userWrapper.getThumbnail()):null)
//                .setString("email", userWrapper.getEmail())
//                .setString("search_token", userWrapper.getSearchToken())
//                .setLong("join_time", userWrapper.getJoinTime())
//                .setUuid("id", userWrapper.getId());
//
//        session.execute(bs);
//
//    }
//
//    public byte[] uuid2ByteArray(java.util.UUID uuid) {
//        long msb = uuid.getMostSignificantBits();
//        long lsb = uuid.getLeastSignificantBits();
//        byte[] buffer = new byte[16];
//
//        for (int i = 0; i < 8; i++) {
//            buffer[i] = (byte) (msb >>> 8 * (7 - i));
//        }
//        for (int i = 8; i < 16; i++) {
//            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
//        }
//
//        return buffer;
//    }
//}
