package hust.plane.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hust.plane.constant.WebConst;
import hust.plane.mapper.pojo.*;
import hust.plane.service.interFace.*;
import hust.plane.utils.DateKit;
import hust.plane.utils.JsonUtils;
import hust.plane.utils.pojo.TipException;
import hust.plane.web.controller.vo.RouteVO;
import hust.plane.web.controller.vo.TaskVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hust.plane.utils.PlaneUtils;
import hust.plane.utils.page.TailPage;
import hust.plane.utils.page.TaskPojo;
import hust.plane.utils.pojo.JsonView;
import hust.plane.web.controller.vo.AlarmVO;
import hust.plane.web.controller.webUtils.WordUtils;
import net.sf.ehcache.search.expression.And;

@Controller
public class TaskController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);
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
	@Autowired
	private AlarmService alarmService;
	@Autowired
	private RouteService routeServiceImpl;
	@Autowired
	private UserGroupService userGroupService;

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

	// 跳转任务创建
	@RequestMapping("/toTaskCreate")
	public String toTaskCrate(Model model, Task task, HttpServletRequest request) {
		// 操作者
		// 放飞者
		// 回收者
		// 无人机编号
		// 飞行路线
		User aUser = PlaneUtils.getLoginUser(request);
		Task task2 = new Task();
		TaskVO taskVO = new TaskVO();
		if (task.getId()!=null && task.getId()!=0) {       // 判断对象是否为空
			task2 = taskServiceImpl.getTaskByTask(task);
			if (task2.getPlanstarttime() == null) {
				task2.setPlanstarttime(DateKit.get2HoursLater());
			}
			if (task2.getUserA() != null) {
				taskVO.setUserAName(userServiceImpl.getNameByUserId(task2.getUserA()));
			}
			if (task2.getUserZ() != null) {
				taskVO.setUserZName(userServiceImpl.getNameByUserId(task2.getUserZ()));
			}
		}else {
			taskVO.setPlanstarttime(DateKit.get2HoursLater());
		}
		
		
		taskVO.setTaskVo(task2);

		List<Uav> uavs = uavServiceImpl.getAllPlane();
		List<FlyingPath> planePaths = flyingPathServiceImpl.findAllFlyingPath();

		model.addAttribute("usercreator", aUser);
		model.addAttribute("uavs", uavs);
		model.addAttribute("planePaths", planePaths);

		model.addAttribute("taskvo", taskVO);
		model.addAttribute("curNav", "createTask");

		return "createTask";
	}

	// 创建任务
	@RequestMapping(value = "taskCreate", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String createTask(Task task, HttpServletRequest request) {

		// 保存新建的任务
		if (taskServiceImpl.saveTask(task)) {
			User userA = new User();
			User userZ = new User();
			userA.setId(task.getUserA());
			userZ.setId(task.getUserZ());
			userServiceImpl.updataTasknumByUser(userA);
			userServiceImpl.updataTasknumByUser(userZ); // 并且把操作员的任务数量+1
			return JsonView.render(0, WebConst.SUCCESS_RESULT);
		}

		return JsonView.render(1, WebConst.SUCCESS_RESULT);
	}

	// 分页查询
	@RequestMapping("/taskPageList")
	public String queryPage(Task task, TailPage<TaskPojo> page, Model model, HttpServletRequest request) {

		User userCreator = PlaneUtils.getLoginUser(request);
		List<Integer> groupIdList = userGroupService.selectGroupIdWithUserId(userCreator.getId());
		if (groupIdList.contains(Integer.valueOf(1))) {
			task.setUsercreator(null);
		} else {
			task.setUsercreator(userCreator.getId());
		}
		page = taskServiceImpl.queryPage(task, page);

		model.addAttribute("selectStatus", task.getFinishstatus());
		model.addAttribute("page", page);
		model.addAttribute("curNav", "taskAllList");
		return "taskList";
	}

	// 时间逆序查询
	@RequestMapping(value = "timeReverseView", method = RequestMethod.GET)
	public String timeReverseView(Task task, TailPage<TaskPojo> page, Model model, HttpServletRequest request) {

		User userCreator = PlaneUtils.getLoginUser(request);
		List<Integer> groupIdList = userGroupService.selectGroupIdWithUserId(userCreator.getId());
		if (groupIdList.contains(Integer.valueOf(1))) {
			task.setUsercreator(null);
		} else {
			task.setUsercreator(userCreator.getId());
		}
		page = taskServiceImpl.queryPageWithTime(task, page);
		model.addAttribute("selectStatus", task.getFinishstatus());
		model.addAttribute("page", page);
		model.addAttribute("curNav", "taskAllList");
		return "taskList";
	}

	@RequestMapping(value = "onsureFly", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String onsureFly(Task task) {

		if (taskServiceImpl.setStatusTaskByTask(task, 8) == true) {
			return JsonView.render(1, "已确认，可以放飞");
		} else {
			return JsonView.render(1, "确认失败！");
		}

	}

	// 拒绝放飞
	@RequestMapping(value = "rejectFly", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String rejectFly(Task task) {

		if (taskServiceImpl.setStatusTaskByTask(task, 4) == true) {
			return JsonView.render(1, "已驳回，不可放飞");
		} else {
			return JsonView.render(1, "驳回失败！");
		}
	}

	// 任务分派
	@RequestMapping(value = "assignTask", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String assignTask(Task task) {

		Task task2 = taskServiceImpl.getTaskByTask(task);

		User userA = userServiceImpl.getUserById(task2.getUserA());
		User userZ = userServiceImpl.getUserById(task2.getUserZ());
		if (taskServiceImpl.setStatusTaskByTask(task2, 2) == true) {// 设置任务分派
			userServiceImpl.updataTasknumByUser(userA); // 增加az任务数目
			userServiceImpl.updataTasknumByUser(userZ);
			return JsonView.render(1, "任务分派成功!");
		} else {
			return JsonView.render(1, "任务分派失败!");
		}

	}

	@RequestMapping(value = "onsureTaskOver", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String onsureTaskOver(Task task, HttpServletRequest request) {

		Task task2 = taskServiceImpl.getTaskByTask(task);
		int status = task2.getStatus();

		User userA = userServiceImpl.getUserById(task2.getUserA());
		User userZ = userServiceImpl.getUserById(task2.getUserZ());

		if (status == 9) {
			taskServiceImpl.setStatusTaskByTask(task, 10); // 设置任务完成
			taskServiceImpl.setFinishStatusTaskByTask(task, 1);

			userServiceImpl.reduceTasknumByUser(userA); // 减少az任务数目
			userServiceImpl.reduceTasknumByUser(userZ);

			return JsonView.render(1, "巡视任务确认完成!");
		} else {

			return JsonView.render(1, "巡视任务确认失败!");
		}
	}

	// 删除处于创建状态的任务
	@RequestMapping(value = "deleteTask", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String deleteTask(Task task) {

		if (task.getId() != null) {
			if (taskServiceImpl.deleteByTask(task) == true) {
				return new JsonView(0, "SUCCESS", "删除成功").toString();
			}
			return new JsonView(0, "SUCCESS", "删除失败").toString();
		}

		return new JsonView(0, "SUCCESS", "未传入飞行路径编号,删除失败").toString();
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
				// alarmVo.setImgBaseCode();
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
			for (User user : bUserList) {
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

	// 获取任务的告警信息
	@RequestMapping(value = "alarmWithId", method = RequestMethod.GET)
	public String getAlarmWithId(@RequestParam(value = "id") int id, Model model) {
		List<Alarm> alarmWitIdList = alarmService.getAlarmsByTaskId(Integer.valueOf(id));
		List<AlarmVO> alarmList = new ArrayList<AlarmVO>();
		Iterator<Alarm> iterator = alarmWitIdList.iterator();
		while (iterator.hasNext()) {
			Alarm alarm = iterator.next();
			AlarmVO alarmVo = new AlarmVO(alarm);
			alarmList.add(alarmVo);
		}
		// 告警点路由显示
		List<Route> allRoute = routeServiceImpl.getAllRoute();
		List<RouteVO> routeList = new ArrayList<RouteVO>();
		for (int i = 0; i < allRoute.size(); i++) {
			RouteVO routeVo = new RouteVO(allRoute.get(i));
			routeList.add(routeVo);
		}
		model.addAttribute("routeList", JsonUtils.objectToJson(routeList));
		model.addAttribute("alarmList", JsonUtils.objectToJson(alarmList));
		model.addAttribute("curNav", "taskAllList");
		return "alarmListWithTaskId";
	}

}
