package com.example.demodeal.controller;

import com.example.demodeal.domain.User;
import com.example.demodeal.domain.Result;
import com.example.demodeal.enums.ResultEnum;
import com.example.demodeal.repository.UserRepository;
import com.example.demodeal.service.UserService;
import com.example.demodeal.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;



    //查询用户
    @GetMapping(value = "/user/id/{id}")
    public Result<User> userFindOne(@PathVariable("id") long id) {
        return  ResultUtil.success(ResultEnum.SUCCESS,userRepository.findOne(id));
    }

    /**
     * 查询所有用户列表
     * @return
     */
    @GetMapping(value = "/user")
    public Result<User> userList(){
        return ResultUtil.success(ResultEnum.SUCCESS,userRepository.findAll());
    }


    /**
     * 添加用户
     * @return
     */
    @PostMapping(value = "/user")
    public Result<User> userAdd(User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(201, bindingResult.getFieldError().getDefaultMessage());
        }

        user.setPassword(user.getPassword());
        user.setUsername(user.getUsername());

        return ResultUtil.success(ResultEnum.SUCCESS,userRepository.save(user));
    }

    //更新
    @PutMapping(value = "/user/{id}")
    public Result<User> userUpdate(@PathVariable("id") long id,
                                     @RequestParam("username") String username,
                                     @RequestParam("password") String password ) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);

        return  ResultUtil.success(ResultEnum.SUCCESS,userRepository.save(user));
    }

    //删除
    @DeleteMapping(value = "/user/{id}")
    public Result<User> userDelete(@PathVariable("id") long id) {
        userRepository.delete(id);
        return ResultUtil.success(ResultEnum.SUCCESS,null);
    }


}