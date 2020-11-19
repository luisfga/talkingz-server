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
        try {
            Message message = new Message();

            message.id = cmdSend.getMessageWrapper().getId();
            message.senderId = UUID.fromString(cmdSend.getMessageWrapper().getSenderId());
            message.destId = UUID.fromString(cmdSend.getMessageWrapper().getDestId());

            message.sentTime = OffsetDateTime.ofInstant(new Date(cmdSend.getMessageWrapper().getSentTimeInMilis()).toInstant(), ZoneId.systemDefault());

            message.content = cmdSend.getMessageWrapper().getContent();
            message.mimeType = cmdSend.getMessageWrapper().getMimetype();
            message.mediaThumbnail = cmdSend.getMessageWrapper().getMediaThumbnail();
            message.mediaDownloadToken = cmdSend.getMessageWrapper().getDownloadToken();
            message.msgStatus = MessageStatus.MSG_STATUS_ON_TRAFFIC;

            em.persist(message);

        /* caso a conexão de um usuário caia após enviar uma mensagem e antes de receber o feedback confirmando que esta foi salva,
           seu app cliente, ao reconectar, tentará enviar a mensagem novamente. E se ela já tiver sido salva realmente, dará erro de chave duplicada.
           Nesse caso basta ignorar e prosseguir com o processo e confirmar o status ON_TRAFFIC*/
        } catch (EntityExistsException eee){
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
        logger.debug("Usuário com mensagens " + msgs.size()  + " pendentes ");

        msgs.forEach(msg -> {
                MessageWrapper message = new MessageWrapper();
                message.setId(msg.id);
                message.setSenderId(msg.senderId.toString());
                message.setDestId(msg.destId.toString());
                message.setSentTimeInMilis(msg.sentTime.toInstant().toEpochMilli());
                message.setMimetype((byte)msg.mimeType);
                message.setContent(msg.content);
                message.setMediaThumbnail(msg.mediaThumbnail);
                message.setDownloadToken(msg.mediaDownloadToken);
                message.setStatus(msg.msgStatus);
                logger.debug("Mensagem pendente encontrada (uuid:"+message.getId()+")");
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
        logger.debug("Usuário com mensagens " + pendingConfirmations.size()  + " pendentes ");
        return pendingConfirmations;
    }

    @Transactional
    public void updateMessageStatus(UUID uuid, byte statusMsg){
        logger.debug("Atualizando a mensagem " + uuid + " para o status " + statusMsg);
        Message message = em.find(Message.class, uuid);
        logger.debug("Mensagem encontrada? " + (message != null));
        message.msgStatus = statusMsg;
        em.persist(message);
    }
}
