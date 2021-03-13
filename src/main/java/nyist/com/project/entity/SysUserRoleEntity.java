package nyist.com.project.entity;

import java.io.Serializable;

import javax.persistence.Table;

import lombok.Data;

/**
 * 用户角色表
 * @author ljw
 *
 */
@Table(name="sys_user_role")
@Data
public class SysUserRoleEntity implements Serializable{

	
	private String roleId; //角色id
	
	private String userId; //用户id
}
