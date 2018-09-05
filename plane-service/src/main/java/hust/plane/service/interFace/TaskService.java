package hust.plane.service.interFace;

import java.util.List;

import hust.plane.mapper.pojo.Task;
import hust.plane.mapper.pojo.User;
import hust.plane.utils.page.TailPage;
import hust.plane.utils.page.TaskPojo;


public interface TaskService {

    List<TaskPojo> getALLTask();

    TailPage<TaskPojo> queryPage(Task task, TailPage<TaskPojo> page);

    void saveTask(Task task);

	void setStatusTaskByTask(Task task, int status);

	String getStatusByTask(Task task);

	void setFinishStatusTaskByTask(Task task, int finishstatus);

	Task getTaskByTask(Task task);

	List<Task> getTasklistByAuser(User aUser);

}
