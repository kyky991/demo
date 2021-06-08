package com.zing.netty.easychat.mapper;

import com.zing.netty.easychat.pojo.vo.FriendRequestVO;
import com.zing.netty.easychat.pojo.vo.MyFriendVO;

import java.util.List;

public interface UserMapperCustom {

    List<FriendRequestVO> queryFriendRequests(String acceptUserId);

    List<MyFriendVO> queryMyFriends(String userId);

    void updateMessageSignedBatch(List<String> msgIds);

}
