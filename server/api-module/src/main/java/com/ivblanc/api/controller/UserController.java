package com.ivblanc.api.controller;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ivblanc.api.config.security.JwtTokenProvider;
import com.ivblanc.api.dto.req.CheckEmailReqDTO;
import com.ivblanc.api.dto.req.SignOutReqDTO;
import com.ivblanc.api.dto.req.UpdatePersonalReqDTO;
import com.ivblanc.api.dto.req.UpdatePwReqDTO;
import com.ivblanc.api.service.UserService;
import com.ivblanc.api.service.common.CommonResult;
import com.ivblanc.api.service.common.ResponseService;
import com.ivblanc.core.entity.User;
import com.ivblanc.core.exception.ApiMessageException;
import com.ivblanc.core.repository.UserRepository;
// import com.ivblanc.core.utils.PasswordValidate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = {"USER"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/user")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ResponseService responseService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    // private final PasswordValidate pv;

    /**
     * 회원정보 변경 : put /update
     * 회원탈퇴 : delete /signOut
     * 이메일 중복체크 : get /checkEmail
     */

    // 회원 정보 변경 - 비밀번호
    @ApiOperation(value = "비밀번호 변경", notes = "비밀번호 변경")
    @PutMapping(value = "/update/pw", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CommonResult userUpdatePw(@Valid UpdatePwReqDTO req) throws Exception{
        // 존재하는 회원인지 확인
        User user = userService.findByEmail(req.getEmail());
        if(user == null)
            throw new ApiMessageException("등록되지 않은 아이디입니다.");

        if(!passwordEncoder.matches(req.getPw(), user.getPassword())){
            throw new ApiMessageException("비밀번호가 일치하지 않습니다.");
        }

        // 빈 어쩌구 에러나는데 비밀번호 검사는 프론트에서 해서 보내줄거라 그냥 빼버림
        // if(!pv.checkPwForm(req.getPassword_new()) ){
        //     throw new ApiMessageException("비밀번호는 영문,숫자,특수문자 중 2가지 이상을 포함하며 8자리 이상, 14자리 이하입니다");
        // }
        //
        // if(!pv.checkPwMatch(req.getPassword_new(), req.getPassword_check())){
        //     throw new ApiMessageException("비밀번호를 확인해주세요.");
        // }

        user.updatePassword(passwordEncoder.encode(req.getPw_new()));
        userRepository.save(user);

        return responseService.getSuccessResult("비밀번호 변경 성공.");
    }


    // 회원 정보 변경 - 개인정보
    @ApiOperation(value = "개인정보 변경", notes = "개인정보 변경")
    @PutMapping(value = "/update/personal", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CommonResult userUpdatePersonal(@Valid UpdatePersonalReqDTO req) throws Exception{
        // 존재하는 회원인지 확인
        User user = userService.findByEmail(req.getEmail());
        if(user == null)
            throw new ApiMessageException("등록되지 않은 회원입니다.");

        if(!passwordEncoder.matches(req.getPw(), user.getPassword())){
            throw new ApiMessageException("비밀번호가 일치하지 않습니다.");
        }

        user.updateAge(req.getAge());
        user.updateGender(req.getGender());
        user.updatePhone(req.getPhone());
        userRepository.save(user);

        return responseService.getSuccessResult("개인정보 변경 성공.");
    }


    // 회원 탈퇴
    @ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴")
    @DeleteMapping(value = "/signOut", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CommonResult userSignOut(@Valid SignOutReqDTO req) throws Exception{
        // 존재하는 회원인지 확인
        User user = userService.findByEmail(req.getEmail());
        if(user == null)
            throw new ApiMessageException("등록되지 않은 회원입니다.");

        if(!passwordEncoder.matches(req.getPw(), user.getPassword())){
            throw new ApiMessageException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.deleteById(user.getUserId());

        return responseService.getSuccessResult("탈퇴 성공");
    }

    // 이메일 중복체크
    @ApiOperation(value = "이메일 중복체크", notes = "이메일 중복체크")
    @GetMapping(value = "/checkEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CommonResult checkEmail(@Valid CheckEmailReqDTO req) throws Exception{
        // 존재하는 회원인지 확인
        User user = userService.findByEmail(req.getEmail());
        if(user != null)
            throw new ApiMessageException("이미 등록된 회원입니다.");

        return responseService.getSuccessResult("사용 가능한 이메일입니다.");
    }

}
