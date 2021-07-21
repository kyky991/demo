package com.zing.user.rpc;

import com.zing.user.domain.dto.UserDTO;

import java.util.List;

public interface IUserRpc {

    UserDTO get(Long id);

    List<UserDTO> list(List<Long> ids);

}
