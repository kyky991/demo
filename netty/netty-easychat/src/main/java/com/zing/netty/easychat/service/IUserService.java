package com.zing.netty.easychat.service;

import com.zing.netty.easychat.netty.ChatMessage;
import com.zing.netty.easychat.pojo.ChatMsg;
import com.zing.netty.easychat.pojo.User;
import com.zing.netty.easychat.pojo.vo.FriendRequestVO;
import com.zing.netty.easychat.pojo.vo.MyFriendVO;

import java.util.List;

public interface IUserService {

    /**
     * 判断用户名是否存在
     *
     * @param username
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 查询用户是否存在
     *
     * @param username
     * @param password
     * @return
     */
    User queryUserForLogin(String username, String password);

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    User saveUser(User user);

    /**
     * 修改用户记录
     *
     * @param user
     * @return
     */
    User updateUser(User user);

    /**
     * 搜索朋友的前置条件
     *
     * @param userId
     * @param friendName
     * @return
     */
    Integer preSearchFriends(String userId, String friendName);

    /**
     * 根据用户名查询用户对象
     *
     * @param username
     * @return
     */
    User queryUserByUsername(String username);

    /**
     * 添加好友请求记录，保存到数据库
     *
     * @param userId
     * @param friendName
     */
    void sendFriendRequest(String userId, String friendName);

    /**
     * 查询好友请求
     *
     * @param userId
     * @return
     */
    List<FriendRequestVO> queryFriendRequests(String userId);

    /**
     * 删除好友请求记录
     *
     * @param requesterId 请求方
     * @param acceptorId 接受方
     */
    void deleteFriendRequest(String requesterId, String acceptorId);

    /**
     * 通过好友请求：1. 保存好友
     * 2. 逆向保存好友
     * 3. 删除好友请求记录
     *
     * @param requesterId 请求方
     * @param acceptorId 接受方
     */
    void acceptFriendRequest(String requesterId, String acceptorId);

    /**
     * 查询好友列表
     *
     * @param userId
     * @return
     */
    List<MyFriendVO> queryMyFriends(String userId);

    /**
     * 保存聊天消息到数据库
     *
     * @param chatMessage
     * @return
     */
    String saveMessage(ChatMessage chatMessage);

    /**
     * 批量签收消息
     *
     * @param msgIds
     */
    void updateMessageSigned(List<String> msgIds);

    /**
     * 获取未签收消息列表
     *
     * @param userId
     * @return
     */
    List<ChatMsg> getUnreadMessages(String userId);
}
