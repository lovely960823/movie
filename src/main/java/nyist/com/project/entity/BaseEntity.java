package nyist.com.project.entity;

import java.io.Serializable;

import javax.persistence.Transient;

import lombok.Data;

@Data
public class BaseEntity implements Serializable{

	@Transient
	public Integer pageSize;
	
	@Transient
	public Integer pageNum;
}
