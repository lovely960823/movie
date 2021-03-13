package nyist.com.project.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
/**
 * 角色表
 * @author ljw 2020年11月25日15:16:49
 *
 */
@Data
@Table(name="sys_role")
public class SysRoleEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	
	private String name; //角色名称
}
