package nyist.com.project.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
/**
 * 所有日志
 * @author ljw
 *
 */
@Table(name="sys_global_log")
@Data
public class SysGlobalLogEntity extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	
	private String uri;//请求url
	
	private String type;//请求类型
	
	private String method;//请求方法
	
	private String params;//请求参数
	
	private String ip;//ip
	
	private String results;//返回结果
	
	private Integer userId;//操作人id
	
	private String userName;//操作人名称
	
	private String createTime;//操作时间
	
	@Transient
	private String beginDate;
    @Transient
    private String endDate;
}
