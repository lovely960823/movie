package nyist.com.project.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="sys_order")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order extends BaseEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private Integer id;
	
	private String rid;//房间id
	
	private String oid;//订单id
	
	private String createTime;//下单时间
	
	private String price;//价格
	
	private String type;
	
	private String username;//消费者名称
	
	private String phone;//用户联系方式
	
	private String idnumber;//用户证件号
	
	private String paytype;//用户付款方式
	

}
