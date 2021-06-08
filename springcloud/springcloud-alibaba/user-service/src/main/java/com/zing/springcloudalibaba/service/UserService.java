package com.zing.springcloudalibaba.service;

import com.zing.springcloudalibaba.domain.PointsLog;
import com.zing.springcloudalibaba.domain.UserInfo;
import com.zing.springcloudalibaba.domain.messaging.BlogPointsDTO;
import com.zing.springcloudalibaba.mapper.PointsLogMapper;
import com.zing.springcloudalibaba.mapper.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Zing
 * @date 2020-07-13
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private PointsLogMapper pointsLogMapper;

    @Transactional(rollbackFor = Exception.class)
    public void collectPoints(BlogPointsDTO dto) {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(dto.getUserId());
        userInfo.setPoints(userInfo.getPoints() + dto.getPoints());
        userInfoMapper.updateByPrimaryKeySelective(userInfo);

        pointsLogMapper.insert(
                PointsLog.builder().userId(userInfo.getId())
                        .points(dto.getPoints())
                        .event("POST BLOG")
                        .createTime(new Date()).build()
        );

        log.info("积分添加完毕");
    }
}
