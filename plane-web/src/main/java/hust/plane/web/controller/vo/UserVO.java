package hust.plane.web.controller.vo;

import hust.plane.mapper.pojo.User;

public class UserVO {
	private int id;

	private String username;

	private String password;

	private String createtime;

	private String updatetime;

	private String descripte;

	private Integer tasknum;

	public UserVO() {
	}

	public UserVO(User user) {
		this.id = user.getId();
		if (user.getName() != null)
			this.username = user.getName();
		if (user.getDescription() != null)
			this.descripte = user.getDescription();
		if (user.getTasknum() != null)
			this.tasknum = user.getTasknum();
		if (user.getCreatetime() != null)
			this.createtime = user.getCreatetime().toString();
		if (user.getUpdatetime() != null)
			this.updatetime = user.getUpdatetime().toString();
	}

	public int getUserid() {
		return id;
	}

	public void setUserid(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getDescripte() {
		return descripte;
	}

	public void setDescripte(String descripte) {
		this.descripte = descripte;
	}

	public Integer getTasknum() {
		return tasknum;
	}

	public void setTasknum(Integer tasknum) {
		this.tasknum = tasknum;
	}
}
