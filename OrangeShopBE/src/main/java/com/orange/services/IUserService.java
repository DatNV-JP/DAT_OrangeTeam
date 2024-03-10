package com.orange.services;

import com.orange.common.payload.Page;
import com.orange.domain.dto.UserDTO;
import com.orange.domain.model.User;
import com.orange.payload.response.UserInfoRespone;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService extends BaseService<UserDTO, Long>{

    UserInfoRespone getUserInfo(String username);

    User findByUsername(String username);

    User changePass(String userMame, String newPass);

    void forGotPassWord(String userMame);

    boolean checkOTP(String userMame, String OTP);

    UserDTO disableOrActiveAccount(Long id, Boolean status);

    Page<?> fillAllByStt(Boolean status, Pageable pageable);

    List<UserDTO> findByUsernameOrName(String keyWord);

    Page<?>  search(String keyWord, Pageable pageable);

    void indexData() throws InterruptedException;
}
