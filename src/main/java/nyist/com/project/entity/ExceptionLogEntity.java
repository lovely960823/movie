package nyist.com.project.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Table(name="exceptionlog")
@Data
public class ExceptionLogEntity extends BaseEntity implements Serializable{

	@Id
	private Integer id;
	private String params;//参数
	private String name;//名称
	private String message;//信息
	private String userId;//用户id
	private String userName;//用户名称
	private String method;//方法
	private String ip;//IP
	private String createTime;
	@Transient
	private String beginDate;
    @Transient
    private String endDate;
}
