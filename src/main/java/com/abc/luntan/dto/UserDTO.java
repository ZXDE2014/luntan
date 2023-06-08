package com.abc.luntan.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class UserDTO {

    //指定生效
    @NotNull(message = "邮箱不能为空",groups = UpdateGroup.class)
    private String email;
    @NotNull(message = "昵称不能为空")
    private String nickName;
    private String avatar;
    @NotNull(message = "性别不能为空")
    @Range(max = 1, min =0,message = "性别不能为0或1的其他")
    private Integer sex;

    public UserDTO() {
    }

    public UserDTO(String email, String nickName, String avatar, Integer sex) {
        this.email = email;
        this.nickName = nickName;
        this.avatar = avatar;
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }
}
