package org.cloud.provider.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 系统用户
 *
 * @author 李庆海
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "SOS_USER")
public class User implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /***/
    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 50)
    private String id;

    /**
     * 登录账号
     */
    @Column(name = "LOGIN_NAME", nullable = false, length = 50)
    @Size(max = 50)
    private String loginName;

    /**
     * 用户名称
     */
    @Column(name = "USER_NAME", nullable = true, length = 50)
    private String userName;

    /**
     * 登录密码
     */
    @Column(name = "PASSWORD", nullable = false, length = 32)
    @JsonIgnore
    private String password;

    /**
     * 注册日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE", nullable = false)
    @CreatedDate
    private Date registDate;

    /**
     * 用户状态
     */
    @Column(name = "ENABLED", nullable = false, length = 1)
    private Integer enabled;


}