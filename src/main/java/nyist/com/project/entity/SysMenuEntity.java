package nyist.com.project.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
/**
 * 菜单表
 * @author ljw 2020年11月25日15:17:03
 *
 */
@Data
@Table(name="sys_menu")
public class SysMenuEntity implements Serializable{

	@Id
	private Integer id;
	
	private String name;//名字
	
	private String pid;//父亲id
	
	private String url;//请求地址
	
	private String icon;//图标
	
	private String perm; //对应操作权限
	
	private String level; // 1 目录 2、子菜单 3、操作按钮
	
	private Integer sorts; //排序
	
	private String createDate;//创建时间
	
	private String updateDate;//修改时间
	
	@Transient
	private List<SysMenuEntity> childs = new ArrayList<SysMenuEntity>();//加载树形表格使用
}
