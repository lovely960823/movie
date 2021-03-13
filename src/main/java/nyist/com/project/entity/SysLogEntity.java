package nyist.com.project.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Table(name="sys_log")
@Data
public class SysLogEntity extends BaseEntity implements Serializable{

	 	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		@Id
		private Integer id;

	    private String username; //用户名

	    private String operDesc; //操作描述
	    
	    private String operModule; //操作模块
	    
	    private String operType; //操作类型

	    private String method; //方法名

	    private String params; //参数

	    private String ip; //ip地址

	    @Column(name="createDate")
	    private String createDate; //操作时间
	    
	    @Transient
	    private String beginDate;
	    
	    @Transient
	    private String endDate;
}
