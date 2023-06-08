package com.abc.luntan.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.abc.luntan.dto.LoginFormDto;
import com.abc.luntan.dto.UserDTO;
import com.abc.luntan.utils.*;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.abc.luntan.entity.User;
import com.abc.luntan.service.UserService;
import com.abc.luntan.mapper.UserMapper;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.cli.Digest;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.abc.luntan.utils.RedisConstants.*;
import static com.abc.luntan.utils.SystemConstants.*;
import static org.springframework.data.elasticsearch.annotations.FieldType.Date;

/**
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailCient;

    @Override
    public CommonResult isLogin() {
        return UserHolder.getUser()==null ? CommonResult.success(false):CommonResult.success(true);
    }

    @Override
    public CommonResult editUserInfo(UserDTO user)  {
        // 只能修改部分数据
        UserDTO userDTO = UserHolder.getUser();
        String nickname = user.getNickName();
        Integer sex = user.getSex();
        if (StrUtil.isBlank(nickname) && sex == null ){
            return CommonResult.fail("上传的信息为空");
        }


        if(sex==null||(sex!=1&&sex!=0)){
            return CommonResult.fail("性别不存在");
        }else{
            userDTO.setSex(sex);
        }

        if(StrUtil.isNotBlank(nickname)){
            userDTO.setNickName(nickname);
        }
        boolean update=false;
        synchronized (this){
            //先更新redis，再更新数据库：理论上先删除redis，这里没必要
            String token = request.getHeader("authorization");
            String key = LOGIN_USER_KEY + token;
            RMap<Object, Object> rMap = redissonClient.getMap(key);
            rMap.put("nickname", userDTO.getNickName());
            rMap.put("sex", userDTO.getSex());
            UpdateChainWrapper<User> updateChainWrapper = update().
                    eq("user_id",userDTO.getEmail()).
                    set("nickname", userDTO.getNickName()).
                    set("sex", userDTO.getSex());
            update = updateChainWrapper.update();
        }

        if(update){
            return CommonResult.success("修改成功");
        }else {
            return CommonResult.fail("服务器忙，请稍后再试");
        }

    }

    @Override
    public CommonResult register(LoginFormDto user) {
        if(!checkEmail(user.getEmail())){
            return CommonResult.fail("邮箱不合法");
        }

        if (!checkPassword(user.getPassword())) {
            return CommonResult.fail("密码不合规");
        }

        String key = CREATE_CODE_KEY + user.getEmail();
        String realCode = (String)redissonClient.getBucket(key).get();

        if(realCode==null||!realCode.equals(user.getCode())){
            return CommonResult.fail("验证码不正确");
        }


        //随机生成 加密salt
        String salt = RandomUtil.randomStringUpper(16);
        String passwordMD5 = getPasswordBySalt(user.getPassword(), salt);

        User userNew = new User(user.getEmail(), USER_NICK_NAME_PREFIX+user.getEmail().substring(0,6),
                passwordMD5,salt,
                IMAGE_UPLOAD_DIR+DEFAULT_AVATAR, DateUtil.date(),1);

        save(userNew);
        return CommonResult.success("注册成功");
    }

    @Override
    public CommonResult resetPassword(String oldPassword, String password) {
        UserDTO userDTO = UserHolder.getUser();
        String email = userDTO.getEmail();
        User user = getById(email);
        String passwordBySalt = getPasswordBySalt(oldPassword, user.getSalt());

        if(!user.getPassword().equals(passwordBySalt)){
            return CommonResult.fail("原密码不正确");
        }
        if(!checkPassword(password)){
            return CommonResult.fail("密码不合规");
        }

        String passwordMd5 = getPasswordBySalt(password,user.getSalt());
        user.setPassword(passwordMd5);

        if (!updateById(user)) {
           return CommonResult.fail("服务器忙，请稍后再试");
        }
        return CommonResult.success("重置密码成功");

    }

    @Override
    public CommonResult updateAvatar(MultipartFile file, HttpServletRequest request){
/*        if (!MyFileUtil.isImg(file)) {
            return CommonResult.fail("图片格式不正确");
        }

        if (!MyFileUtil.sizeCheck(file,1)) {
            return CommonResult.fail("图片大小不符合要求");
        }
        UserDTO userDTO = UserHolder.getUser();
        try {
            String format = DateUtil.format(DateUtil.date(), "yyyy/MM/");
            String type = FileTypeUtil.getType(file.getInputStream());
            String name= RandomUtil.randomString(10)+"."+ type;
            String key = "avatar/" + format + name;
            CommonResult result= qiNiuService.uploadFile(file.getInputStream(), key);
            if (result.getCode()==200) {
                //更新数据库
                String oldAvatar = userDTO.getAvatar();
                String url = IMAGE_UPLOAD_DIR +key;
                update().eq("user_id",userDTO.getEmail()).set("avatar",url).update();
                //更新redis
                String token = RedisConstants.LOGIN_USER_KEY + request.getHeader("authorization");
                redissonClient.getMap(token).put("avatar", url);

                //删除旧头像
                if (!oldAvatar.equals(IMAGE_UPLOAD_DIR + DEFAULT_AVATAR)) {
                    // 对象存储
                    String oldKey = oldAvatar.substring(IMAGE_UPLOAD_DIR.length());
                    qiNiuService.delete(oldKey);
                }
                return CommonResult.success("头像更新成功");
            }

        } catch (IOException e) {
            log.error(userDTO.getEmail()+" : 头像上传失败 ");
            return CommonResult.fail("头像上传失败");
        }*/
        return CommonResult.fail("头像上传失败");

    }

    @Override
    public CommonResult sendCode(String email) {
        // 检查邮箱是否合法
        if (!checkEmail(email)) {
            return CommonResult.fail("邮箱不合法");
        }

        // 检查是否注册
        if (this.getById(email)!=null) {
            return CommonResult.fail("该邮箱已被注册");
        }

        return sendEmail(email);
    }

    /**
     * 发送验证码
     * @param email
     * @return
     */

    private CommonResult sendEmail(String email) {
        String code = RandomUtil.randomNumbers(6);
        String key = CREATE_CODE_KEY + email;
        // 发送email上下文
        Context context = new Context();

        // 存入redis
        redissonClient.getBucket(key).set(code,CREATECODE_TTL,TimeUnit.MINUTES);
        context.setVariable("code",code);


        String content = templateEngine.process("mail/mail", context);

        try {
            // 发邮件
            mailCient.sendMail(email,"欢迎加入luntan",content);

        }catch (Exception e) {
            log.error("邮件发送失败"+e.getMessage());
            return CommonResult.fail("邮件发送失败");
        }

        return CommonResult.success("发送成功");

    }

    @Override
    public CommonResult queryUserInfo(String userId) {
        User user = query().eq("user_id", userId).one();

        if(user==null){
            return CommonResult.fail("User not found");
        }
        UserDTO userDTO = new UserDTO(user.getUserId(),user.getNickname(),user.getAvatar(),user.getSex());

        return CommonResult.success(userDTO);
    }

    @Override
    public CommonResult logout() {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return CommonResult.fail("请先登录");
        }

        //todo :单点登录
        String token = request.getHeader("authorization");
        String key = LOGIN_USER_KEY + token;
        redissonClient.getBucket(key).delete();
        return CommonResult.success("退出登录成功");
    }

    @Override
    public CommonResult login(LoginFormDto loginFormDto) {
        // 1.验证邮箱
        // 2.验证账号密码是否正确
        if(loginFormDto==null){
            throw new IllegalArgumentException("loginFormDto cannot be null");
        }

        String email = loginFormDto.getEmail();
        if (!checkEmail(email)){
            return CommonResult.validateFail("邮箱格式不正确");
        }

        User user = query().eq("user_id", email).one();

        if (user == null) {
            return CommonResult.validateFail("账号不存在");
        }
        //先拿盐进行加密
        String password = this.getPasswordBySalt(loginFormDto.getPassword(),user.getSalt());

        String passwordReal = user.getPassword();
        if (!passwordReal.equals(password)) {
            return CommonResult.validateFail("密码错误");
        }

        // 3.生成token,放入redis
        String token = UUID.randomUUID().toString(true);
        String key = LOGIN_USER_KEY + token;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("email", user.getUserId());
        map.put("nickname", user.getNickname());
        map.put("avatar", user.getAvatar());
        map.put("sex", user.getSex());

        RMap<Object, Object> clientMap = redissonClient.getMap(key);
        clientMap.putAll(map);

        clientMap.expire(LOGIN_USER_TTL, TimeUnit.HOURS);

        return CommonResult.success(token);
    }

    private boolean checkEmail(String email){
        return Validator.isEmail(email);
    }


    private boolean checkPassword(String password){
        if(password.length()<6){
            return  false;
        }
        for(char i :password.toCharArray()){
            if (!Character.isLetterOrDigit(i)) {
                return false;
            }
        }
        return true;

    }

    private String getPasswordBySalt(String password,String salt){

        // 加密策略： 随机生成的16位树 + 密码，进行md5加密
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        return md5.digestHex(salt+password);
    }

}




