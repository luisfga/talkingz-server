package br.com.luisfga.talkingz.server.wsocket;

import br.com.luisfga.talkingz.commons.orchestration.command.*;
import br.com.luisfga.talkingz.server.database.sql.dao.MessageDAO;
import br.com.luisfga.talkingz.server.database.sql.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.luisfga.talkingz.commons.constants.MessageStatus;
import br.com.luisfga.talkingz.commons.MessageWrapper;
import br.com.luisfga.talkingz.commons.UserWrapper;
import br.com.luisfga.talkingz.commons.orchestration.Orchestration;
import br.com.luisfga.talkingz.commons.orchestration.OrchestrationDecoder;
import br.com.luisfga.talkingz.commons.orchestration.OrchestrationEncoder;
import br.com.luisfga.talkingz.commons.orchestration.response.CommandFindContact;
import br.com.luisfga.talkingz.commons.orchestration.response.ResponseCommandFindContact;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.EOFException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author luisfga.com.br
 */
@ApplicationScoped
@ServerEndpoint(value="/MessagingWSEndpoint/{userId}", decoders = { OrchestrationDecoder.class }, encoders = { OrchestrationEncoder.class })
public class MessagingWSEndpoint {

    private final Logger logger = LoggerFactory.getLogger(MessagingWSEndpoint.class);
    
    //mapeado usando userId como chave, pois normalmente o que precisamos é a sessão, daí usamos o userId para pegar uma sessão
    private static final ConcurrentHashMap<String, Session> onlineUsers = new ConcurrentHashMap<>();
    
    private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    @Inject
    UserDAO userDAO;

    @Inject
    MessageDAO messageDAO;
    
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //XXXXXXXXXXXXXX - WEBSOCKET api - XXXXXXXXXXXXXX
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        cachedThreadPool.execute(() -> login(session, UUID.fromString(userId)));
    }
    
    private void login(Session session, UUID userId) {
        
        logger.debug(" ");
        logger.debug("oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
        logger.debug("Efetuando login (sessão:"+session.getId()+ ")" + " para o usuário: " + userId);
        onlineUsers.remove(userId.toString());
        onlineUsers.put(userId.toString(), session);
        logger.debug("Usuários online: "+onlineUsers.size());
        logger.debug(onlineUsers.keySet().toString());

        FeedBackCommandLogin feedBackCommandLogin = new FeedBackCommandLogin();

        //sincronizar as mensagens enviadas A esse usuário que estejam pendentes
        List<MessageWrapper> pendingMessages = messageDAO.findPendingMessages(userId);

        boolean hasPendings = false;
        if (pendingMessages.size() > 0) {
            feedBackCommandLogin.getPendingMessages().addAll(pendingMessages);
            hasPendings = true;
        }

        //sincronizar as mensagens enviadas POR esse usuário que já foram recebidas pelo destinatário
        List<UUID> pendingConfirmationUUIDs = messageDAO.findPendingConfirmations(userId);
        if (pendingConfirmationUUIDs.size() > 0) {
            feedBackCommandLogin.getPendingConfirmationUUIDs().addAll(pendingConfirmationUUIDs);
            hasPendings = true;
        }

        if (hasPendings) {
            logger.debug("Enviando pacote de pendências"
                + " (Mensagens["+pendingMessages.size()+"] | Confirmações["+pendingConfirmationUUIDs.size()+"])"
                + "para o usuário:" + userId + " (" + new SimpleDateFormat("HH:mm:ss", new Locale("pt","BR")).format(new Date()) + ")");

            sendCommandOrFeedBack(feedBackCommandLogin, session);                
        } else {
            logger.debug("Usuário " + userId + " sem pendências!");
        }
        logger.debug("oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
        logger.debug(" ");
        
    }
    
    @OnClose
    public void close(Session session, @PathParam("userId") String userId, CloseReason reason) {
        logger.debug(" ");
        logger.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        logger.debug("Conexão fechada para o usuário " + userId);
        logger.debug(reason.toString());
        logger.debug(reason.getReasonPhrase());
        logger.debug("Total de conexões no momento:" + onlineUsers.size());
        logger.debug(onlineUsers.toString());
        logger.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        logger.debug(" ");
        
        onlineUsers.remove(userId);
    }

    @OnError
    public void onError(Throwable error, Session session, @PathParam("userId") String userId) {
        logger.debug("onError");
        if (!(error instanceof EOFException)) {
            logger.debug(" ");
            logger.debug("--------------------------------------------------------------");
            logger.debug("ERRO - para o usuário: "+userId);
            logger.debug(error.getMessage());
            logger.debug("-----------------------STACK TRACE ---------------------------");
            error.printStackTrace();
            logger.debug("--------------------------------------------------------------");
            logger.debug(" ");
        }
    }
 
    @OnMessage
    public void handleMessage(Orchestration orchestration, Session session) {

        cachedThreadPool.execute(() -> {
            if (orchestration instanceof CommandSend)
                processCommandSend((CommandSend)orchestration, session);

            else if (orchestration instanceof FeedBackCommandDeliver)
                processFeedBackMessageReceived((FeedBackCommandDeliver)orchestration);

            else if (orchestration instanceof FeedBackCommandConfirmDelivery)
                processFeedBackMessageConfirmed((FeedBackCommandConfirmDelivery)orchestration);

            else if (orchestration instanceof CommandSyncUser)
                processCommandSyncUser((CommandSyncUser)orchestration, session);

            else if (orchestration instanceof CommandFindContact)
                processCommandFindContact((CommandFindContact)orchestration, session);
        });
    }
    
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //XXXXXXXXXXXXXX - OrchestraApp  - XXXXXXXXXXXXXX
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    private void processCommandSend(CommandSend commandSend, Session session) {
        logger.debug(" ");
        logger.debug("CommandSend - ETAPA 1");
        logger.debug("Enviar de:" + commandSend.getMessageWrapper().getSenderId() + " para:" + commandSend.getMessageWrapper().getDestId());
        
        //setar hora da mensagem sincronizada (criada) pelo servidor
        commandSend.getMessageWrapper().setSentTimeInMilis(System.currentTimeMillis());

        //salvar mensagem (Status: ON_TRAFFIC)
        logger.debug("Salvando mensagem");
        messageDAO.saveMessage(commandSend);

        //enviar feedback informando que a mensagem foi salva e retorna UUID e data criada no servidor
        FeedBackCommandSend feedBackMessageOnTraffic = new FeedBackCommandSend();
        feedBackMessageOnTraffic.setId(commandSend.getMessageWrapper().getId());
        feedBackMessageOnTraffic.setSentTimeInMillis(commandSend.getMessageWrapper().getSentTimeInMilis());
        logger.debug("FeedBackMessageOnTraffic - ETAPA 2 (uuid:"+ feedBackMessageOnTraffic.getId()+")");
        sendCommandOrFeedBack(feedBackMessageOnTraffic, session);
        
        //verificar se usuário destino está online
        Session destinationSession = onlineUsers.get(commandSend.getMessageWrapper().getDestId());
        if (isConnected(destinationSession)) {
            logger.debug("O destinatário está online");
            CommandDeliver commandDeliver = new CommandDeliver();
            commandDeliver.setMessageWrapper(commandSend.getMessageWrapper());
            
            //effectively send message
            logger.debug("CommandDeliver - ETAPA 3 (uuid:"+ commandDeliver.getMessageWrapper().getId()+")");
            sendCommandOrFeedBack(commandDeliver, destinationSession);
            
        } else {
            logger.debug("O destinatário está offline, a mensagem será enviada quando ele conectar");
        }

        logger.debug(" ");
    }
    
    //feedback retornado por um destinatário, ao receber uma mensagem
    private void processFeedBackMessageReceived(FeedBackCommandDeliver feedBackCommandDeliver) {
        logger.debug(" ");
        logger.debug("FeedBackCommandDeliver - ETAPA 4 (uuid:" + feedBackCommandDeliver.getId()+ ")");
        
        //atulizar status da mensagem para 'entregue'
        messageDAO.updateMessageStatus(feedBackCommandDeliver.getId(), MessageStatus.MSG_STATUS_DELIVERED);
        
        //enviar confirmação para o remetente original
        CommandConfirmDelivery commandConfirmDelivery = new CommandConfirmDelivery();
        commandConfirmDelivery.setId(feedBackCommandDeliver.getId());
        commandConfirmDelivery.setSenderId(feedBackCommandDeliver.getSenderId());
        
        logger.debug("CommandConfirmDelivery - ETAPA 5 (uuid:" + commandConfirmDelivery.getId()+ ") alvo:" + commandConfirmDelivery.getSenderId());
        logger.debug("Id do alvo (sender) = " + commandConfirmDelivery.getSenderId());
        Session destinationSession = onlineUsers.get(commandConfirmDelivery.getSenderId().toString());
        sendCommandOrFeedBack(commandConfirmDelivery, destinationSession);
        logger.debug(" ");
    }
    
    //feedback retornado por um remetente, quando o processo de confirmação estiver concluído
    private void processFeedBackMessageConfirmed(FeedBackCommandConfirmDelivery feedBackMessageConfirmed) {
        logger.debug(" ");
        logger.debug("FeedBackMessageConfirmed - ETAPA 6 (uuid:" + feedBackMessageConfirmed.getId() + ")");
        
        //atulizar status da mensagem para 'entregue'
        messageDAO.updateMessageStatus(feedBackMessageConfirmed.getId(), MessageStatus.MSG_STATUS_CONFIRMED);
        logger.debug(" ");
    }
    
    private void processCommandSyncUser(CommandSyncUser commandSyncUser, Session session) {
        logger.debug(" ");
        logger.debug("Processando comando SyncUser: " + commandSyncUser.getUserWrapper().getId());

        userDAO.synchronizeMainUser(commandSyncUser.getUserWrapper());

        //feedback simples, vazio, apenas para sinalizar sucesso
        FeedBackCommandSyncUser feedBackSyncUser = new FeedBackCommandSyncUser();
        
        sendCommandOrFeedBack(feedBackSyncUser, session);
        logger.debug(" ");        
    }
    
    private void processCommandFindContact(CommandFindContact commandFindContact, Session session) {
        logger.debug(" ");
        logger.debug("Processando comando FindContact");
        UserWrapper userWrapper = userDAO.findUserByToken(commandFindContact.getSearchToken());

        ResponseCommandFindContact responseFindContact = new ResponseCommandFindContact();
        responseFindContact.setUserWrapper(userWrapper);
        
        sendCommandOrFeedBack(responseFindContact, session);
        logger.debug(" ");
    }
    
    private void sendCommandOrFeedBack(Orchestration wsmessage, Session destSession) {
        logger.debug(" ");
        logger.debug("Sessões : " + onlineUsers);
        logger.debug("Sessão destino : " + destSession);
        logger.debug("Sessão destino aberta? : " + (destSession!=null && destSession.isOpen()));

        if (destSession != null && destSession.isOpen()) {

            destSession.getAsyncRemote().sendObject(wsmessage, result ->
                    logger.debug("Comando enviado: "
                            + wsmessage.getType()
                            + " ("
                            + new SimpleDateFormat("HH:mm:ss", new Locale("pt","BR")).format(new Date())
                            +")"));

        } else {
            logger.debug("O destinatário pretendido está offline");
        }
        logger.debug(" ");
    }
    
    private boolean isConnected(Session someSession){
        return someSession != null && someSession.isOpen();
    }

}