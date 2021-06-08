package com.zing.netty.easychat.controller;

import com.zing.netty.easychat.enums.OperatorFriendRequestTypeEnum;
import com.zing.netty.easychat.enums.SearchFriendStatusEnum;
import com.zing.netty.easychat.pojo.ChatMsg;
import com.zing.netty.easychat.pojo.User;
import com.zing.netty.easychat.pojo.bo.UserBO;
import com.zing.netty.easychat.pojo.vo.MyFriendVO;
import com.zing.netty.easychat.pojo.vo.UserVO;
import com.zing.netty.easychat.service.IUserService;
import com.zing.netty.easychat.utils.FastDFSClient;
import com.zing.netty.easychat.utils.FileUtils;
import com.zing.netty.easychat.utils.JSONResult;
import com.zing.netty.easychat.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/u")
public class UserController {

    @Value("${fdfs.thumb-image.width}")
    private Integer THUMB_WIDTH;

    @Value("${fdfs.thumb-image.height}")
    private Integer THUMB_HEIGHT;

    @Autowired
    private IUserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    /**
     * 用户注册/登录
     *
     * @param user
     * @return
     */
    @PostMapping("/registerOrLogin")
    public JSONResult registerOrLogin(@RequestBody User user) throws Exception {

        // 0. 判断用户名和密码不能为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return JSONResult.errorMsg("用户名或密码不能为空...");
        }

        // 1. 判断用户名是否存在，如果存在就登录，如果不存在则注册
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        User result = null;
        if (usernameIsExist) {
            // 1.1 登录
            result = userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if (result == null) {
                return JSONResult.errorMsg("用户名或密码不正确...");
            }
        } else {
            // 1.2 注册
            user.setNickname(user.getUsername());
            user.setFaceImage("");
            user.setFaceImageBig("");
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            result = userService.saveUser(user);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(result, userVO);

        return JSONResult.ok(userVO);
    }

    /**
     * 上传用户头像
     *
     * @param userBO
     * @return
     */
    @PostMapping("/uploadFaceBase64")
    public JSONResult uploadFaceBase64(@RequestBody UserBO userBO) {

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        String base64 = userBO.getFaceData();
        String userFacePath = "D:\\tmp\\" + userBO.getUserId() + "userface64.png";

        try {
            FileUtils.base64ToFile(userFacePath, base64);

            // 上传文件到fastdfs
            MultipartFile file = FileUtils.fileToMultipart(userFacePath);
            String url = fastDFSClient.uploadBase64(file);
            System.out.println(url);

//		    "xxxxxxxxxxxxxxxxxx.png"
//		    "xxxxxxxxxxxxxxxxxx_80x80.png"

            // 获取缩略图的url
            String thumbSuffix = String.format("_%dx%d.", THUMB_WIDTH, THUMB_HEIGHT);
            String arr[] = url.split("\\.");
            String thumbUrl = arr[0] + thumbSuffix + arr[1];

            // 更细用户头像
            User user = new User();
            user.setId(userBO.getUserId());
            user.setFaceImage(thumbUrl);
            user.setFaceImageBig(url);

            User result = userService.updateUser(user);

            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(result, userVO);

            return JSONResult.ok(userVO);
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传头像出错，请稍后重试");
        }
    }

    /**
     * 设置用户昵称
     *
     * @param userBO
     * @return
     */
    @PostMapping("/updateNickname")
    public JSONResult updateNickname(@RequestBody UserBO userBO) {
        User user = new User();
        user.setId(userBO.getUserId());
        user.setNickname(userBO.getNickname());

        User result = userService.updateUser(user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(result, userVO);

        return JSONResult.ok(userVO);
    }

    /**
     * 搜索好友接口, 根据账号做匹配查询而不是模糊查询
     *
     * @param userId
     * @param friendName
     * @return
     */
    @PostMapping("/search")
    public JSONResult search(String userId, String friendName) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(friendName)) {
            return JSONResult.errorMsg("");
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preSearchFriends(userId, friendName);
        if (status.equals(SearchFriendStatusEnum.SUCCESS.status)) {
            User user = userService.queryUserByUsername(friendName);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return JSONResult.ok(userVO);
        } else {
            String errorMsg = SearchFriendStatusEnum.valueOf(status);
            return JSONResult.errorMsg(errorMsg);
        }
    }

    /**
     * 发送添加好友的请求
     *
     * @param userId
     * @param friendName
     * @return
     */
    @PostMapping("/addFriendRequest")
    public JSONResult addFriendRequest(String userId, String friendName) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(friendName)) {
            return JSONResult.errorMsg("");
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preSearchFriends(userId, friendName);
        if (status.equals(SearchFriendStatusEnum.SUCCESS.status)) {
            userService.sendFriendRequest(userId, friendName);
        } else {
            String errorMsg = SearchFriendStatusEnum.valueOf(status);
            return JSONResult.errorMsg(errorMsg);
        }

        return JSONResult.ok();
    }

    /**
     * 查询好友请求
     *
     * @param userId
     * @return
     */
    @PostMapping("/queryFriendRequests")
    public JSONResult queryFriendRequests(String userId) {

        // 0. 判断不能为空
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }

        // 1. 查询用户接受到的朋友申请
        return JSONResult.ok(userService.queryFriendRequests(userId));
    }

    /**
     * 接受方 通过或者忽略朋友请求
     *
     * @param requesterId
     * @param acceptorId
     * @param operType
     * @return
     */
    @PostMapping("/handleFriendRequest")
    public JSONResult handleFriendRequest(String requesterId, String acceptorId, Integer operType) {

        // 0. requesterId acceptorId 判断不能为空 operType判断不能为null
        if (StringUtils.isBlank(requesterId) || StringUtils.isBlank(acceptorId) || operType == null) {
            return JSONResult.errorMsg("");
        }

        if (operType == OperatorFriendRequestTypeEnum.IGNORE.type) {
            // 2. 判断如果忽略好友请求，则直接删除好友请求的数据库表记录
            userService.deleteFriendRequest(requesterId, acceptorId);
        } else if (operType == OperatorFriendRequestTypeEnum.ACCEPT.type) {
            // 3. 判断如果是通过好友请求，则互相增加好友记录到数据库对应的表
            //	   然后删除好友请求的数据库表记录
            userService.acceptFriendRequest(requesterId, acceptorId);
        }

        // 查询列表
        List<MyFriendVO> myFriends = userService.queryMyFriends(acceptorId);
        return JSONResult.ok(myFriends);
    }

    /**
     * 查询我的好友列表
     *
     * @param userId
     * @return
     */
    @PostMapping("/queryMyFriends")
    public JSONResult queryMyFriends(String userId) {
        // 0. userId 判断不能为空
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }

        // 查询列表
        List<MyFriendVO> myFriends = userService.queryMyFriends(userId);
        return JSONResult.ok(myFriends);
    }

    @PostMapping("/getUnreadMessages")
    public JSONResult getUnreadMessages(String userId) {
        // 0. userId 判断不能为空
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }

        // 查询列表
        List<ChatMsg> messages = userService.getUnreadMessages(userId);
        return JSONResult.ok(messages);
    }
}
