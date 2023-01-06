package cn.edu.bit.BITKill.util;

import cn.edu.bit.BITKill.model.CommonResp;
import cn.edu.bit.BITKill.model.GlobalData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

public class SendHelper {

    // 将response发给users中的所有用户
    // 若所有用户都发送成功，则返回true；否则返回false
    public static boolean sendMessageByList(List<String> users,CommonResp response){
        // 先确定所有用户都在可以发送的状态
        for (int i=0;i<users.size();i++){
            if(GlobalData.getSessionByUsername(users.get(i)) == null){
                return false;
            }
        }
        // 运行到这里说明所有要发送的用户都在线
        for (int i=0;i<users.size();i++){
            sendMessageBySession(GlobalData.getSessionByUsername(users.get(i)),response);
        }
        return true;
    }

    public static boolean sendMessageByUsername(String username,CommonResp response){
        WebSocketSession session = GlobalData.getSessionByUsername(username);
        if(session == null){
            return false;
        }else{
            return sendMessageBySession(session,response);
        }
    }

    // 通过一个session发送response
    public static boolean sendMessageBySession(WebSocketSession session,CommonResp response){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] responseJson = objectMapper.writeValueAsBytes(response);
            session.sendMessage(new TextMessage(responseJson));
            return true;
        }catch (IOException e){
            return false;
        }
    }
}