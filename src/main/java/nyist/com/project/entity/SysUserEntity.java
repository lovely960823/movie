package nyist.com.project.entity;


import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
/**
 * 测试登录用户表
 * @author Administrator
 *
 */
@Table(name="sys_user")
@Data
public class SysUserEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	
	private String account;//账号
	
	private String password;//密码
	
	private String realName;//真实名称
	
	private String email;//邮箱
	
	private String phone;//手机号
	
	private String status;//状态
	
	private String openid;//第三方凭证
	
	private String createTime;//创建时间
	
	private String updateTime;//修改时间
	
	private String profile;//头像
	
	private String lastLoginTime;//最近登录时间
	
}
