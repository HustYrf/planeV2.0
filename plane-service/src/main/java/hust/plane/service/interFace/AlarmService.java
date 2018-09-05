package hust.plane.service.interFace;

import hust.plane.mapper.pojo.Alarm;
import hust.plane.utils.page.AlarmPojo;
import hust.plane.utils.page.TailPage;

import java.util.List;


public interface AlarmService {

    List<Alarm> getAllAlarm();

    TailPage<AlarmPojo> queryAlarmWithPage(Alarm alarm, TailPage<AlarmPojo> page);

    Alarm selectAlarmById(int id);

    void updateAlarmStatus(int alarmid);

    int insertAlarmById(String planeId);

    int updateAlarmDesc(int alarmid, String description);

	List<Alarm> getAlarmsByTaskId(int taskid);
	
	int insertAlarmByAlarms(Alarm alarm);
}
