package hust.plane.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hust.plane.mapper.mapper.TaskMapper;
import hust.plane.mapper.mapper.UserMapper;
import hust.plane.mapper.pojo.Task;
import hust.plane.mapper.pojo.TaskExample;
import hust.plane.mapper.pojo.TaskExample.Criteria;
import hust.plane.mapper.pojo.User;
import hust.plane.service.interFace.TaskService;
import hust.plane.utils.page.TailPage;
import hust.plane.utils.page.TaskPojo;
@Service
public class TaskServiceImpl implements TaskService {
	
	@Autowired
	private TaskMapper taskMapper;
	@Autowired
	private UserMapper userMapper;

	@Override
	public List<TaskPojo> getALLTask() {
		TaskExample example=new TaskExample();
		//得到所有的任务
		List<Task> taskList = taskMapper.selectByExample(example);
		List<TaskPojo> list=null;
		//得到每个人的名称
		if(taskList!=null)
		{
			list=new ArrayList<TaskPojo>();
			for(Task task:taskList)
			{
				TaskPojo taskPojo=new TaskPojo();
				//查询姓名
				User user1 = userMapper.selectByPrimaryKey(task.getUsercreator());
				User user2 = userMapper.selectByPrimaryKey(task.getUserA());
				User user3 = userMapper.selectByPrimaryKey(task.getUserZ());
				taskPojo.setTask(task);
				taskPojo.setUserAName(user1.getName());
				taskPojo.setUserBName(user2.getName());
				taskPojo.setUserCName(user3.getName());
				list.add(taskPojo);
			}
		}
		return list;
	}

	//分页查询
	@Override
	public TailPage<TaskPojo> queryPage(Task task, TailPage<TaskPojo> page) {
		TaskExample example =new TaskExample();
		Criteria createCriteria = example.createCriteria();
		if(task.getFinishstatus() == -1)
		{
			task.setFinishstatus(null);
		}
		else if(task.getFinishstatus()!=null){
			createCriteria.andStatusEqualTo(task.getFinishstatus());
		}
		if(task.getId()!=null)
		{
			createCriteria.andIdEqualTo(task.getId());
		}
		int itemsTotalCount = taskMapper.countByExample(example);
		List<Task> taskList = taskMapper.queryPage(task, page);
		//包装数据
		List<TaskPojo> items=null;
		if(taskList!=null)
		{
			items=new ArrayList<TaskPojo>();
			for(Task task1:taskList)
			{
				TaskPojo taskPojo=new TaskPojo();
				//查询姓名
				User user1 = userMapper.selectByPrimaryKey(task.getUsercreator());
				User user2 = userMapper.selectByPrimaryKey(task.getUserA());
				User user3 = userMapper.selectByPrimaryKey(task.getUserZ());
				taskPojo.setTask(task1);
				taskPojo.setUserAName(user1.getName());
				taskPojo.setUserBName(user2.getName());
				taskPojo.setUserCName(user3.getName());
				items.add(taskPojo);
			}
		}
		page.setItemsTotalCount(itemsTotalCount);
		page.setItems(items);
		return page;
	}

	@Override
	public void saveTask(Task task) {
		Date date = new Date();
		task.setCeatetime(date);
		task.setFinishstatus(0);
		//设置状态未完成
		
		taskMapper.insert(task);
		
	}

	@Override
	public void setStatusTaskByTask(Task task, int status) {
		// TODO Auto-generated method stub
		Task task2 = taskMapper.selectByPrimaryKey(task.getId());
		task2.setStatus(status);
		
		taskMapper.updateByPrimaryKey(task2);
		
	}

	@Override
	public String getStatusByTask(Task task) {
		
		return taskMapper.getStatusByTask(task);
	}

	@Override
	public void setFinishStatusTaskByTask(Task task, int finishstatus) {
		// TODO Auto-generated method stub
		Task task2 = taskMapper.selectByPrimaryKey(task.getId());
		task2.setFinishstatus(finishstatus);
		
		taskMapper.updateByPrimaryKey(task2);
	}

	@Override
	public Task getTaskByTask(Task task) {
		// TODO Auto-generated method stub
		return taskMapper.selectByPrimaryKey(task.getId());
	}

	@Override
	public List<Task> getTasklistByAuser(User aUser) {
		
		return taskMapper.getTasklistByUserCreator(aUser.getId());
	}
	
	

}
