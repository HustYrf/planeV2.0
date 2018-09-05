package hust.plane.utils.page;

import hust.plane.mapper.pojo.Alarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlarmPojo  {
    private Integer alarmid;
    private String image;
    private String planeid;
    private String descripte;
    private String createTime;
    private String updateTime;
    private String alongda;
    private List<Double> positionList;
    private Integer status;

    public AlarmPojo(Alarm alarm) {

        this.alarmid = alarm.getId();
        if (alarm.getImageurl() != null) {
            this.image = alarm.getImageurl();
        }
        if (alarm.getDescription() != null) {
            this.descripte = alarm.getDescription();
        }
        if (alarm.getCreatetime() != null) {
            this.createTime =dateFormat(alarm.getCreatetime(),"yyyy/MM/dd HH:mm:ss");
        }
        if (alarm.getUpdatetime() != null) {
            this.updateTime = dateFormat(alarm.getUpdatetime(),"yyyy/MM/dd HH:mm:ss");

        }
        if(alarm.getPosition()!=null) {
            this.positionList = StringPointToList(alarm.getPosition());
            this.alongda = pointToString(StringPointToList(alarm.getPosition()));
        }
        if(alarm.getStatus()!=null)
        {
        	this.status=alarm.getStatus();
        }
    }

    public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getAlarmid() {
        return alarmid;
    }
    public void setAlarmid(Integer alarmid) {
        this.alarmid = alarmid;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getPlaneid() {
        return planeid;
    }
    public void setPlaneid(String planeid) {
        this.planeid = planeid;
    }
    public String getDescripte() {
        return descripte;
    }
    public void setDescripte(String descripte) {
        this.descripte = descripte;
    }
    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    public String getAlongda() {
        return alongda;
    }
    public void setAlongda(String alongda) {
        this.alongda = alongda;
    }
    public List<Double> getPositionList() {
        return positionList;
    }
    public void setPositionList(List<Double> positionList) {
        this.positionList = positionList;
    }
    public static List<Double> StringPointToList(String s)
    {
        List<Double> list =new ArrayList<Double>();
        String sub=s.substring(6, s.length()-1);
        double x=Double.parseDouble(sub.split(" ")[0]);
        double y=Double.parseDouble(sub.split(" ")[1]);
        list.add(x);
        list.add(y);
        return list;
    }
    /**
     * @author rfYang
     * @date 2018/7/5 16:46
     * @param [pointList]
     * @return java.lang.String
     */
    public static String pointToString(List<Double> pointList){
        return String.valueOf(pointList.get(0))+","+String.valueOf(pointList.get(1));
    }
    /**
     * 时间转换
     * @author rfYang
     * @date 2018/7/6 13:51
     * @param [date, dateFormat]
     * @return java.lang.String
     */
    public static String dateFormat(Date date, String dateFormat) {
        if(date != null) {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            if(date != null) {
                return format.format(date);
            }
        }

        return "";
    }
}
