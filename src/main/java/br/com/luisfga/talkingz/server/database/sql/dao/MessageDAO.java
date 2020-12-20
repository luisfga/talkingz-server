package br.com.luisfga.talkingz.server.database.sql.dao;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import br.com.luisfga.talkingz.server.database.sql.entity.Message;
import io.agroal.api.AgroalDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.luisfga.talkingz.commons.constants.MessageStatus;
import br.com.luisfga.talkingz.commons.MessageWrapper;
import br.com.luisfga.talkingz.commons.orchestration.command.CommandSend;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.transaction.Transactional;

/**
 *
 * @author luisfga.com.br
 */
@ApplicationScoped
public class MessageDAO {

    private final Logger logger = LoggerFactory.getLogger(MessageDAO.class);

    @Inject
    EntityManager em;

    @Inject
    AgroalDataSource dataSource;

    @Transactional
    public void saveMessage(CommandSend cmdSend){
        //salvar mensagem (Status: ON_TRAFFIC)
        logger.debug("Salvando mensagem");
        try {
            Message message = new Message();

            message.setId(cmdSend.getMessageWrapper().getId());
            message.setSenderId(UUID.fromString(cmdSend.getMessageWrapper().getSenderId()));
            message.setDestId(UUID.fromString(cmdSend.getMessageWrapper().getDestId()));

            message.setSentTime(OffsetDateTime.ofInstant(new Date(cmdSend.getMessageWrapper().getSentTimeInMilis()).toInstant(), ZoneId.systemDefault()));

            message.setContent(cmdSend.getMessageWrapper().getContent());
            message.setMimeType(cmdSend.getMessageWrapper().getMimetype());
            message.setMediaThumbnail(cmdSend.getMessageWrapper().getMediaThumbnail());
            message.setMediaDownloadToken(cmdSend.getMessageWrapper().getDownloadToken());
            message.setMsgStatus(MessageStatus.MSG_STATUS_ON_TRAFFIC);

            em.persist(message);

        /* caso a conexão de um usuário caia após enviar uma mensagem e antes de receber o feedback confirmando que esta foi salva,
           seu app cliente, ao reconectar, tentará enviar a mensagem novamente. E se ela já tiver sido salva realmente, dará erro de chave duplicada.
           Nesse caso basta ignorar e prosseguir com o processo e confirmar o status ON_TRAFFIC*/
        } catch (PersistenceException eee){
            logger.error("Erro ao tentar salvar mensagem. PROVAVELMENTE uma tentativa de salvar uma mensagem repetida.", eee.getMessage());
        }
    }

    @Transactional
    public List<MessageWrapper> findPendingMessages(UUID destId){
        List<MessageWrapper> pendingMessages = new ArrayList<>();

        Query findPendingMessages = em.createNamedQuery("Message.findPendingMessages");
        findPendingMessages.setParameter("destId", destId);
        findPendingMessages.setParameter("statusOnTraffic", MessageStatus.MSG_STATUS_ON_TRAFFIC);

        List<Message> msgs = findPendingMessages.getResultList();
        logger.debug("Usuário com " + msgs.size()  + " mensagen(s) pendente(s) ");

        msgs.forEach(msg -> {
                MessageWrapper message = new MessageWrapper();
                message.setId(msg.getId());
                message.setSenderId(msg.getSenderId().toString());
                message.setDestId(msg.getDestId().toString());
                message.setSentTimeInMilis(msg.getSentTime().toInstant().toEpochMilli());
                message.setMimetype((byte)msg.getMimeType());
                message.setContent(msg.getContent());
                message.setMediaThumbnail(msg.getMediaThumbnail());
                message.setDownloadToken(msg.getMediaDownloadToken());
                message.setStatus(msg.getMsgStatus());
                logger.debug("UUID da mensagem pendente:"+message.getId()+")");
                pendingMessages.add(message);
        });
        return pendingMessages;
    }

    @Transactional
    public List<UUID> findPendingConfirmations(UUID senderId){
        Query findPendingConfirmations = em.createNamedQuery("Message.findPendingConfirmations");
        findPendingConfirmations.setParameter("senderId", senderId);
        findPendingConfirmations.setParameter("statusDelivered",MessageStatus.MSG_STATUS_DELIVERED);
        List<UUID> pendingConfirmations = findPendingConfirmations.getResultList();
        logger.debug("Usuário com " + pendingConfirmations.size()  + " confirmação(ões) pendente(s) ");
        return pendingConfirmations;
    }

    @Transactional
    public void updateMessageStatus(UUID uuid, byte statusMsg){
        logger.debug("Atualizando a mensagem " + uuid + " para o status " + statusMsg);
        Message message = em.find(Message.class, uuid);
        logger.debug("Mensagem encontrada? " + (message != null));
        message.setMsgStatus(statusMsg);
        em.persist(message);
    }
}
