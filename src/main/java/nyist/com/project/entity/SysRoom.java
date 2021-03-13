package nyist.com.project.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="sys_room")
public class SysRoom extends BaseEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	
	private String name;//房间号
	
	private String type;//房间型号
	
	private String price;//价格
	
	private String profile;//房间图
	
	private String status;//房间状态 1空闲 2被使用中
	
	private String descri;//简单描述
	
	private String beginTime;//状态开始日期
	
	private String endTime;//状态结束日期
	
	@Transient
	private String searchName;//全文检索字段
	
}
