package cn.edu.bit.BITKill.handler;


import cn.edu.bit.BITKill.model.params.CommonParam;
import cn.edu.bit.BITKill.model.params.CommonResp;
import cn.edu.bit.BITKill.model.GlobalData;
import cn.edu.bit.BITKill.model.params.RoomUserParam;
import cn.edu.bit.BITKill.service.GameService;
import cn.edu.bit.BITKill.service.LoginService;
import cn.edu.bit.BITKill.service.RegisterService;
import cn.edu.bit.BITKill.service.RoomService;
import cn.edu.bit.BITKill.util.SendHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class GameHandler extends TextWebSocketHandler {

    private final RegisterService registerService;

    private final LoginService loginService;

    private final RoomService roomService;

    private final GameService gameService;

    public GameHandler(RegisterService registerService, LoginService loginService, RoomService roomService, GameService gameService) {
        this.registerService = registerService;
        this.loginService = loginService;
        this.roomService = roomService;
        this.gameService = gameService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("session(id : "+session.getId()+") has connected!\n");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 进行具体的消息处理, 包括json解析等
        String paramJson = message.getPayload();

        // 进行json解析，得到param
        ObjectMapper objectMapper = new ObjectMapper();
        CommonParam param = objectMapper.readValue(paramJson,CommonParam.class);

        // 获取消息类型并进行处理
        String type = param.getType();
        try{
            switch (type){
                case "register":
                    registerService.register(session,paramJson);
                    break;
                case "login salt":
                    loginService.sendSalt(session,paramJson);
                    break;
                case "login":
                    loginService.login(session,paramJson);
                    break;
                case "create room":
                    roomService.createRoom(session, paramJson);
                    break;
                case  "get rooms":
                    roomService.getRooms(session, paramJson);
                    break;
                case "get a room":
                    roomService.getARoom(session, paramJson);
                    break;
                case "join room":
                    roomService.joinRoom(session, paramJson);
                    break;
                case "leave room":
                    roomService.leaveRoom(session, paramJson);
                    break;
                case "get a random room":
                    roomService.getARandomRoom(session, paramJson);
                    break;
                case "game start":
                    gameService.startGame(session,paramJson);
                    break;
                case "kill":
                    gameService.kill(session,paramJson);
                    break;
                case "witch":
                    gameService.witch(session,paramJson);
                    break;
                case "prophet":
                    gameService.prophet(session,paramJson);
                    break;
                case "elect":
                    gameService.elect(session,paramJson);
                    break;
                case "vote":
                    gameService.vote(session,paramJson);
                    break;
                case "send message":
                    gameService.sendMessage(session,paramJson);
                    break;
                case "last words":
                    gameService.sendMessage(session,paramJson);
                    break;
                default:
                    SendHelper.sendMessageBySession(session,new CommonResp<>("unkonwn request", true,"unkonwn request",null));
            }
        }catch (Exception e) {
            SendHelper.sendMessageBySession(session,new CommonResp());
            log.warn("Something wrong happens!");
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("There is an exception in session: "+session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("session(id : "+session.getId()+") has closed!");

        //因一些原因断开连接时，需要查询被断开的连接是否时某个用户对应的，若是，则将该用户登出
        String user = GlobalData.getUsernameBySession(session);
        if(user != null) {
            GlobalData.userLogout(user, session);
            System.out.println("user " + user + " is logged out due to closing session.");

            // 通知同房间的人该用户离线
            Long roomID = GlobalData.getUserRoomMap().get(user);
            if (roomID != null){
                SendHelper.sendMessageByList(GlobalData.getRoomByID(roomID).getPlayers(),new CommonResp<RoomUserParam>("offline",true,"player offline",new RoomUserParam(roomID,user)));
            }
        }


    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
