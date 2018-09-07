package hust.plane.web.controller;

import hust.plane.mapper.pojo.Alarm;
import hust.plane.service.interFace.AlarmService;
import hust.plane.utils.page.AlarmPojo;
import hust.plane.utils.page.TailPage;
import hust.plane.utils.pojo.JsonView;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


@Controller
public class AlarmHistoryController {
    @Resource
    private AlarmService alarmService;

    @RequestMapping(value = "alarmHistory")
    public String alarmHistoryQueryPage(Alarm alarm, TailPage<AlarmPojo> page, Model model) {
        if (alarm.getStatus() != null && alarm.getStatus() == -1) {
            alarm.setStatus(null);
        }
        if (alarm.getId() != null && alarm.getId() == 0) {
            alarm.setId(null);
        }
        page = alarmService.queryAlarmWithPage(alarm, page);
        model.addAttribute("selectStatus", alarm.getStatus());
        model.addAttribute("page", page);
        model.addAttribute("curNav", "alarmhistory");
        return "alarmHistory";
    }

    @RequestMapping(value = "dealWithAlarm")
    @ResponseBody
    public String dealWithAlarm(int alarmid) {
        alarmService.updateAlarmStatus(alarmid);
        return new JsonView(0).toString();
    }
}
