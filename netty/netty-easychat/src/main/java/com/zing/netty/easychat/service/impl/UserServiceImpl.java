package com.zing.netty.easychat.service.impl;

import com.zing.netty.easychat.enums.MsgSignFlagEnum;
import com.zing.netty.easychat.enums.SearchFriendStatusEnum;
import com.zing.netty.easychat.mapper.*;
import com.zing.netty.easychat.netty.ChatMessage;
import com.zing.netty.easychat.pojo.ChatMsg;
import com.zing.netty.easychat.pojo.FriendRequest;
import com.zing.netty.easychat.pojo.MyFriend;
import com.zing.netty.easychat.pojo.User;
import com.zing.netty.easychat.pojo.vo.FriendRequestVO;
import com.zing.netty.easychat.pojo.vo.MyFriendVO;
import com.zing.netty.easychat.service.IUserService;
import com.zing.netty.easychat.utils.FastDFSClient;
import com.zing.netty.easychat.utils.FileUtils;
import com.zing.netty.easychat.utils.QRCodeUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MyFriendMapper myFriendMapper;

    @Autowired
    private FriendRequestMapper friendRequestMapper;

    @Autowired
    private UserMapperCustom userMapperCustom;

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        User user = new User();
        user.setUsername(username);

        User result = userMapper.selectOne(user);
        return result != null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public User queryUserForLogin(String username, String password) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);

        return userMapper.selectOneByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public User saveUser(User user) {

        String userId = sid.nextShort();
        user.setId(userId);

        // 为每个用户生成一个唯一的二维码
        String qrCodePath = "D:\\tmp\\" + userId + "qrcode.png";
        qrCodeUtils.createQRCode(qrCodePath, "easychat_qrcode:" + user.getUsername());
        MultipartFile file = FileUtils.fileToMultipart(qrCodePath);

        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setQrcode(qrCodeUrl);

        userMapper.insert(user);

        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    protected User queryUserById(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public User updateUser(User user) {
        userMapper.updateByPrimaryKeySelective(user);
        return queryUserById(user.getId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preSearchFriends(String userId, String friendName) {

        User user = queryUserByUsername(friendName);

        // 1. 搜索的用户如果不存在，返回[无此用户]
        if (user == null) {
            return SearchFriendStatusEnum.USER_NOT_EXIST.status;
        }

        // 2. 搜索账号是你自己，返回[不能添加自己]
        if (user.getId().equals(userId)) {
            return SearchFriendStatusEnum.NOT_YOURSELF.status;
        }

        // 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Example example = new Example(MyFriend.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("myUserId", userId);
        criteria.andEqualTo("myFriendUserId", user.getId());
        MyFriend friend = myFriendMapper.selectOneByExample(example);
        if (friend != null) {
            return SearchFriendStatusEnum.ALREADY_FRIENDS.status;
        }

        return SearchFriendStatusEnum.SUCCESS.status;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public User queryUserByUsername(String username) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        return userMapper.selectOneByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String userId, String friendName) {

        // 根据用户名把朋友信息查询出来
        User friend = queryUserByUsername(friendName);

        // 1. 查询发送好友请求记录表
        Example example = new Example(FriendRequest.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId", userId);
        criteria.andEqualTo("acceptUserId", friend.getId());
        FriendRequest friendRequest = friendRequestMapper.selectOneByExample(example);
        if (friendRequest == null) {
            // 2. 如果不是你的好友，并且好友记录没有添加，则新增好友请求记录
            String requestId = sid.nextShort();

            FriendRequest request = new FriendRequest();
            request.setId(requestId);
            request.setSendUserId(userId);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());
            friendRequestMapper.insert(request);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVO> queryFriendRequests(String userId) {
        return userMapperCustom.queryFriendRequests(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendRequest(String requesterId, String acceptorId) {
        Example example = new Example(FriendRequest.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId", acceptorId);
        criteria.andEqualTo("acceptUserId", acceptorId);
        friendRequestMapper.deleteByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void saveFriend(String requesterId, String acceptorId) {
        MyFriend myFriend = new MyFriend();
        String id = sid.nextShort();
        myFriend.setId(id);
        myFriend.setMyUserId(requesterId);
        myFriend.setMyFriendUserId(acceptorId);
        myFriendMapper.insert(myFriend);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void acceptFriendRequest(String requesterId, String acceptorId) {
        saveFriend(requesterId, acceptorId);
        saveFriend(acceptorId, requesterId);
        deleteFriendRequest(requesterId, acceptorId);


    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendVO> queryMyFriends(String userId) {
        return userMapperCustom.queryMyFriends(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveMessage(ChatMessage chatMessage) {
        ChatMsg msg = new ChatMsg();
        String id = sid.nextShort();
        msg.setId(id);
        msg.setSendUserId(chatMessage.getSenderId());
        msg.setAcceptUserId(chatMessage.getReceiverId());
        msg.setCreateTime(new Date());
        msg.setSignFlag(MsgSignFlagEnum.UNSIGN.type);
        msg.setMsg(chatMessage.getMessage());

        chatMsgMapper.insert(msg);

        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateMessageSigned(List<String> msgIds) {
        userMapperCustom.updateMessageSignedBatch(msgIds);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ChatMsg> getUnreadMessages(String userId) {
        Example example = new Example(ChatMsg.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("signFlag", 0);
        criteria.andEqualTo("acceptUserId", userId);

        List<ChatMsg> result = chatMsgMapper.selectByExample(example);

        return result;
    }
}
