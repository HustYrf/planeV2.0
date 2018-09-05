package hust.plane.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hust.plane.constant.WebConst;
import hust.plane.utils.DateKit;
import hust.plane.utils.pojo.TipException;
import hust.plane.web.controller.admin.UserController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hust.plane.mapper.pojo.Alarm;
import hust.plane.mapper.pojo.FlyingPath;
import hust.plane.mapper.pojo.Task;
import hust.plane.mapper.pojo.Uav;
import hust.plane.mapper.pojo.User;
import hust.plane.service.interFace.AlarmService;
import hust.plane.service.interFace.FlyingPathService;
import hust.plane.service.interFace.UavService;
import hust.plane.service.interFace.TaskService;
import hust.plane.service.interFace.UserService;
import hust.plane.utils.PlaneUtils;
import hust.plane.utils.page.TailPage;
import hust.plane.utils.page.TaskPojo;
import hust.plane.utils.pojo.JsonView;
import hust.plane.web.controller.vo.AlarmVO;
import hust.plane.web.controller.webUtils.WordUtils;

@Controller
public class taskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(taskController.class);
    @Autowired
    private TaskService taskServiceImpl;
    @Autowired
    private FlyingPathService flyingPathServiceImpl;
    @Autowired
    private UserService userServiceImpl;
    @Autowired
    private UavService uavServiceImpl;
    @Autowired
    private AlarmService alarmserviceImpl;


    @RequestMapping("/task")
    public String gettestTask() {
        return "taskList";
    }


    // 得到所有的任务
    @RequestMapping("/taskList")
    public String getALLTask(Model model) {
        List<TaskPojo> allTask = taskServiceImpl.getALLTask();
        model.addAttribute("taskList", allTask);
        model.addAttribute("curNav", "taskAllList");
        return "taskList";
    }

    // 跳转新建任务
    @RequestMapping("/toTaskCreate")
    public String toTaskCrate(Model model, Task task, HttpServletRequest request) {
        // 操作者
        // 放飞者
        // 回收者
        // 无人机编号
        // 飞行路线
        User aUser = PlaneUtils.getLoginUser(request);
        /*User userExmple = new User();
        userExmple.setRole("1");
        List<User> bUsers = userServiceImpl.findByUserRole(userExmple);
        userExmple.setRole("2");
        List<User> cUsers = userServiceImpl.findByUserRole(userExmple);*/
        Uav uav = new Uav();
        uav.setStatus(1);
        List<Uav> uavs = uavServiceImpl.findByPlaneStatus(uav);

        List<FlyingPath> planePaths = flyingPathServiceImpl.findAllFlyingPath();

        model.addAttribute("aUser", aUser);
       /* model.addAttribute("bUsers", bUsers);
        model.addAttribute("cUsers", cUsers);*/
        model.addAttribute("planes", uavs);
        model.addAttribute("planePaths", planePaths);

        task.setPlanstarttime(DateKit.get2HoursLater());
        // 在这传输数据
        model.addAttribute("task", task);
        model.addAttribute("curNav", "createTask");
        return "createTask";
    }

    // 创建任务
    @RequestMapping("/taskCreate")
    public String createTask(Task task, HttpServletRequest request) {
        // 初始状态为1归档
        task.setStatus(1);
        User aUser = PlaneUtils.getLoginUser(request);

        task.setUsercreator(aUser.getId());
        taskServiceImpl.saveTask(task); // 保存新建的任务

        User userA = new User();
        User userZ = new User();
        userA.setId(task.getUserA());
        userZ.setId(task.getUserZ());
        userServiceImpl.updataTasknumByUser(userA);
        userServiceImpl.updataTasknumByUser(userZ); // 并且把操作员的任务数量+1

        return "redirect:/taskPageList";
    }

    // 分页查询
    @RequestMapping("/taskPageList")
    public String queryPage(Task task, TailPage<TaskPojo> page, Model model, HttpServletRequest request) {

        User userCreator = PlaneUtils.getLoginUser(request);
        task.setUsercreator(userCreator.getId());
        
        /* if (task.getId()==null) {
            task.setTaskid(task.getId());
        } else {
            task.setTaskid(null);
        }*/

        if (task.getFinishstatus() == -1) {
            // 查询全部
            task.setFinishstatus(null);
        }
        page = taskServiceImpl.queryPage(task, page);
        
        model.addAttribute("selectStatus", task.getFinishstatus());
        model.addAttribute("page", page);
        model.addAttribute("curNav", "taskAllList");
        return "taskList";
    }

    @RequestMapping(value = "onsureFly", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String onsureFly(Task task) {

        taskServiceImpl.setStatusTaskByTask(task, 7);

        return JsonView.render(1, "已确认，可以放飞");
    }

    @RequestMapping(value = "onsureTaskOver", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String onsureTaskOver(Task task, HttpServletRequest request) {

        Task task2 = taskServiceImpl.getTaskByTask(task);
        int status = task2.getStatus();

        User userA = userServiceImpl.getUserById(task2.getUserA());
        User userZ = userServiceImpl.getUserById(task2.getUserZ());

        if (status==9) {
            taskServiceImpl.setStatusTaskByTask(task, 10); // 设置任务完成
            taskServiceImpl.setFinishStatusTaskByTask(task, 1);

            userServiceImpl.reduceTasknumByUser(userA); // 减少bc任务数目
            userServiceImpl.reduceTasknumByUser(userZ);

            return JsonView.render(1, "巡视任务确认完成!");
        } else {

            return JsonView.render(1, "巡视任务确认失败!");
        }
    }

    // 打印任务报告
    @RequestMapping("taskReport")
    public void taskReport(Task task, HttpServletRequest request, HttpServletResponse response) {

        Task task2 = taskServiceImpl.getTaskByTask(task);

        User userCreator = PlaneUtils.getLoginUser(request);
        User userA = userServiceImpl.getUserById(task2.getUserA());
        User userZ = userServiceImpl.getUserById(task2.getUserZ());

        Uav uav = new Uav(); // 任务执行的无人机
        uav.setId(task2.getUavId());
        uav = uavServiceImpl.getPlaneByPlane(uav);

        List<Alarm> alarms = alarmserviceImpl.getAlarmsByTaskId(task2.getId());
        List<AlarmVO> alarmVos = new ArrayList<AlarmVO>();

        String webappRoot = WordUtils.getRootPath();
        if (alarms.size() > 0) {
            for (int i = 0; i < alarms.size(); ++i) {
                AlarmVO alarmVo = new AlarmVO(alarms.get(i));
                alarmVo.setImgBaseCode(webappRoot);
                //alarmVo.setImgBaseCode();
                alarmVos.add(alarmVo);
            }
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("alarms", alarmVos);
        dataMap.put("plane", uav);
        dataMap.put("task", task2);
        dataMap.put("userA", userCreator);
        dataMap.put("userB", userA);
        dataMap.put("userC", userZ);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String filename = task2.getId() + "-" + sdf.format(date) + ".doc";

        try {
            WordUtils.exportMillCertificateWord(request, response, dataMap, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 人员动态搜索提示
     */
    @RequestMapping(value = "searchFlyer", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String searchFlyerTips(@RequestParam(value = "queryString") String queryString) {
        List<String> userNameList = new ArrayList<>();
        try {
            List<User> bUserList = userServiceImpl.fuzzySearchWithUser(queryString);
            for(User user:bUserList){
                userNameList.add(user.getName());
            }
        } catch (Exception e) {
            String msg = "用户模糊搜素失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return JsonView.render(1, msg);
        }
        return JsonView.render(0, WebConst.SUCCESS_RESULT, userNameList);
    }

}
