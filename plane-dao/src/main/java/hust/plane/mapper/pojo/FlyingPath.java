package hust.plane.mapper.pojo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class FlyingPath {

	private Integer id;

	private Integer airportZ;

	private Integer airportA;

	private String name;

	private String pathdata;

	private String description;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createtime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updatetime;

	private String heightdata;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAirportZ() {
		return airportZ;
	}

	public void setAirportZ(Integer airportZ) {
		this.airportZ = airportZ;
	}

	public Integer getAirportA() {
		return airportA;
	}

	public void setAirportA(Integer airportA) {
		this.airportA = airportA;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getPathdata() {
		return pathdata;
	}

	public void setPathdata(String pathdata) {
		this.pathdata = pathdata;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getHeightdata() {
		return heightdata;
	}

	public void setHeightdata(String heightdata) {
		this.heightdata = heightdata == null ? null : heightdata.trim();
	}
}