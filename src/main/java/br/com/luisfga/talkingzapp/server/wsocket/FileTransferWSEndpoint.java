package br.com.luisfga.talkingzapp.server.wsocket;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import br.com.luisfga.talkingzapp.server.database.sql.dao.SharedFileDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.luisfga.talkingzapp.common.orchestration.Orchestration;
import br.com.luisfga.talkingzapp.common.orchestration.OrchestrationEncoder;
import br.com.luisfga.talkingzapp.common.orchestration.OrchestrationDecoder;
import br.com.luisfga.talkingzapp.common.orchestration.response.CommandGetFile;
import br.com.luisfga.talkingzapp.common.orchestration.response.ResponseCommandGetFile;
import br.com.luisfga.talkingzapp.common.utils.SharedFileDTO;
import br.com.luisfga.talkingzapp.common.utils.TransferUtility;

/**
 *
 * @author Luis
 */
@ApplicationScoped
@ServerEndpoint(value="/FileTransferWSEndpoint",
  decoders = { OrchestrationDecoder.class },
  encoders = { OrchestrationEncoder.class })
public class FileTransferWSEndpoint {

    Logger logger = LoggerFactory.getLogger(FileTransferWSEndpoint.class);
    
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    //mapeado usando userId como chave, pois normalmente o que precisamos á a sessão como valor

    SharedFileDAO sharedFileDAO = new SharedFileDAO();

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //XXXXXXXXXXXXXX - WEBSOCKET api - XXXXXXXXXXXXXX
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        logger.debug(" ");
        logger.debug("XXXXXXXXXXXXXXXXX TRANSFER�NCIA XXXXXXXXXXXXXXXX");
        logger.debug("Abrindo canal de tranferência (sessão:"+session.getId()+ "). Aguardando login...");
        logger.debug("Total de canais no momento:" + sessions.size());
        logger.debug("################################################");
        logger.debug(" ");

    }

    @OnClose
    public void close(Session session, CloseReason reason) {

//        long userId = getUserIdBySession(session);
//        if (userId !=0)
//            onlineUsers.remove(userId);

        logger.debug(" ");
        logger.debug("XXXXXXXXXXXXXXXXXXX TRANSFER�NCIA XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        logger.debug("Canal de transferência - sessão: " + session.getId());
        logger.debug("Razão:" + reason.getReasonPhrase());
        sessions.remove(session);
        logger.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        logger.debug(" ");
    }

    @OnError
    public void onError(Throwable error, Session session) {
        if (!(error instanceof EOFException)) {
            logger.debug(" ");
            logger.debug("XXXXXXXXXXXXXXXXXXX TRANSFERÊNCIA XXXXXXXXXXXXXXXXXXXXXXXX");
            logger.debug("ERROR - on session: "+session.getId());
            logger.debug(error.getMessage());
            sessions.remove(session);
            logger.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            logger.debug(" ");
        }
    }

    @OnMessage
    public void handleMessage(ByteBuffer byteBuffer, Session session) {
        logger.debug(" ");
        logger.debug("Dados Binários Recebidos ("+byteBuffer.limit()+" bytes) ----------------------");

        SharedFileDTO sharedFile = TransferUtility.getSharedFileDTOFromByteBuffer(byteBuffer);

        logger.debug("File name: " + sharedFile.getDownloadToken());
        logger.debug("File content length: " + sharedFile.getBytes().length);
        //logger.debug("File content: " + new String(sharedFile.getBytes()));

        //salvar no banco
        logger.debug("Salvar no banco");
        sharedFileDAO.saveFile(sharedFile);
        logger.debug("Dados do arquivo salvo no banco");
        logger.debug(" ");
    }

    @OnMessage
    public void handleMessage(Orchestration orchestration, Session session) {

        if (orchestration instanceof CommandGetFile)
            processCommandGetFile((CommandGetFile)orchestration, session);

        else
            logger.debug("Comando inadequado para o endpoint de transferência de arquivos");

    }

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //XXXXXXXXXXXXXX - OrchestraApp  - XXXXXXXXXXXXXX
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
//    private void verificaSessoesObsoletas() {
//        //iniciar outra thread checando sessões mortas e removendo-as
//        cachedThreadPool.execute(() -> {
//            logger.debug(" ");
//            logger.debug("Total de conexões no momento:" + sessions.size());
//            logger.debug("Verificando sessões mortas");
//            Set<Session> sessionsToRemove = Collections.synchronizedSet(new HashSet<>());
//            for (Session session1 : sessions) {
//                if (!onlineUsers.contains(session1)) {
//                    logger.debug("Sessões obsoleta encontrada: " + session1);
//                    sessionsToRemove.add(session1);
//                }
//            }
//
//            logger.debug("Sessões a serem removidas: " + sessionsToRemove);
//            for (Session session : sessionsToRemove) {
//                if (session.isOpen()) {
//                    try {
//                        logger.debug("Fechando sessão: " + session.getId());
//                        session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Esta conexão estava sobrando"));
//                    } catch (IOException ex) {
//                        Logger.getLogger(FileTransferWSEndpoint.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
//
//            logger.debug("Total de conexões após verificação: " + sessions.size());
//            logger.debug(" ");
//            sessionsToRemove.clear();
//        });
//    }

//    private long getUserIdBySession(Session session){
//        for (Map.Entry<Long, Session> entry : onlineUsers.entrySet()) {
//            Long userId = entry.getKey();
//            Session sessionValue = (Session)entry.getValue();
//            if (session.equals(sessionValue))
//                return userId;
//        }
//        return 0;
//    }

    private void processCommandGetFile(CommandGetFile commandGetFile, Session session) {
        logger.debug(" ");
        SharedFileDTO sharedFileDTO = sharedFileDAO.findFileByDownloadToken(commandGetFile.getDownloadToken());

        if (sharedFileDTO != null) {
            ByteBuffer byteBuffer = TransferUtility.getByteBufferFromDTO(sharedFileDTO);

            logger.debug("Arquivo encontrado");
            logger.debug("Enviando arquivo solicitado: " + sharedFileDTO.getDownloadToken());

            ResponseCommandGetFile responseCommandGetFile = new ResponseCommandGetFile();
            responseCommandGetFile.setMessage("Iniciando download");
            sendCommandOrFeedBack(responseCommandGetFile, session);

            session.getAsyncRemote().sendBinary(byteBuffer, result ->
                    logger.debug("Comando de envio de arquivo: completado"));

        } else {
            ResponseCommandGetFile responseCommandGetFile = new ResponseCommandGetFile();
            responseCommandGetFile.setMessage("Arquivo não encontrado no servidor");
            sendCommandOrFeedBack(commandGetFile, session);
        }
        logger.debug(" ");
    }

    private void sendCommandOrFeedBack(Orchestration wsmessage, Session destSession) {
        logger.debug(" ");
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

}