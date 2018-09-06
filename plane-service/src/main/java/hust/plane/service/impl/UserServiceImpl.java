package hust.plane.service.impl;

import hust.plane.constant.WebConst;
import hust.plane.mapper.mapper.UserMapper;
import hust.plane.mapper.pojo.User;
import hust.plane.mapper.pojo.UserExample;
import hust.plane.service.interFace.UserService;
import hust.plane.utils.DateKit;
import hust.plane.utils.PlaneUtils;
import hust.plane.utils.page.TailPage;
import hust.plane.utils.pojo.TipException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserMapper userDao;

	/**
	 * 登陆
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public User login(String username, String password) {
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			throw new TipException("用户名和密码不能为空");
		}
		UserExample example = new UserExample();
		UserExample.Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(username);
		int count = userDao.countByExample(example);
		if (count < 1) {
			throw new TipException("没有该用户");
		}
		String pwd = PlaneUtils.MD5encode(username + password);
		criteria.andPasswordEqualTo(pwd);
		// criteria.andRoleEqualTo("0");
		List<User> userList = userDao.selectByExample(example);
		if (userList.size() != 1) {
			throw new TipException("用户名密码错误或您没有操作权限");
		}
		return userList.get(0);
	}

	/**
	 * 注册
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public int register(String username, String password) {
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			throw new TipException("用户名和密码不能为空");
		}
		int usernameCount = userDao.selectByUserName(username);
		if (usernameCount == 1) {
			throw new TipException("该用户名已经存在");
		}
		User user = new User();
		user.setName(username);
		user.setPassword(PlaneUtils.MD5encode(username + password));
		// 注册不能成为管理员
		// user.setRole("0");
		Date date = new Date();
		user.setCreatetime(date);
		user.setUpdatetime(date);
		user.setTasknum(0);

		int count = userDao.insertSelectiveWithIdInc(user);// 主键不是int类型的值，无法自增
		// int count = userDao.insertSelective(user);
		return count;
	}

	/**
	 * 根据用户id查询到用户
	 *
	 * @param uid
	 * @return
	 */
	@Override
	public User queryUserById(int uid) {
		User user = null;
		if (uid != 0) {
			user = userDao.selectByPrimaryKey(uid);
		}
		return user;
	}

	/**
	 * 修改用户名密码
	 * 
	 * @param request
	 * @param oldpassword
	 * @param password
	 */
	@Override
	public boolean modifyPwd(HttpServletRequest request, String oldpassword, String password) {
		if (StringUtils.isBlank(oldpassword) || StringUtils.isBlank(password)) {
			throw new TipException("旧密码和新密码不能为空");
		}
		User user = PlaneUtils.getLoginUser(request);
		oldpassword = PlaneUtils.MD5encode(user.getName() + oldpassword);
		if (!oldpassword.equals(user.getPassword())) {
			throw new TipException("输入的原密码不正确");
		}
		password = PlaneUtils.MD5encode(user.getName() + password);
		if (oldpassword.equals(password)) {
			throw new TipException("新密码不能和原密码相同");
		}
		user.setPassword(password);
		user.setUpdatetime(DateKit.getNow());

		if (userDao.updateByPrimaryKeySelective(user) == 1)
			return true;
		else
			return false;
	}

	// @Override
	// public List<User> findByUserRole(User userExmple) {
	//
	// UserExample example = new UserExample();
	// UserExample.Criteria criteria = example.createCriteria();
	// criteria.andRoleEqualTo(userExmple.getRole());
	//
	// return userDao.selectByExample(example);
	// }

	@Override
	public TailPage<User> getAllUserWithPage(TailPage<User> page) {
		int count = userDao.selectUserCount();
		if (count < 1) {
			LOGGER.error("用户表为空");
		}
		page.setItemsTotalCount(count);
		List<User> userList = userDao.selectAllUser(page);
		page.setItems(userList);
		return page;
	}

	@Override
	public int delUserById(Integer userid) {
		if (userid == null || userid <= 0) {
			throw new TipException("获取用户id错误");
		}
		// String Role = userDao.selectByPrimaryKey(userid).getRole();
		// if (StringUtils.isNotBlank(Role) && Role.equals("0")) {
		// throw new TipException("权限不足以删除管理员");
		// }
		int count = userDao.deleteByPrimaryKey(userid);
		if (count != 1) {
			throw new TipException("删除用户异常");
		}
		return count;
	}

	@Override
	public int modifyUserRoleAndDes(int userid, String description) {
		if (userid == 0 || StringUtils.isBlank(description)) {
			throw new TipException("角色和描述的填写不能为空！");
		}
		User user = userDao.selectByPrimaryKey(userid);
		user.setDescription(description);
		user.setUpdatetime(new Date());
		int count = userDao.updateByPrimaryKeySelective(user);
		if (count != 1) {
			throw new TipException("修改用户异常");
		}
		return count;
	}

	@Override
	public int addUserWithInfo(Integer addUserId, String addUsername, String addUserPaw, String addUserRole,
			String addUserDescripte) {
		if (addUserId == null || StringUtils.isBlank(addUsername) || StringUtils.isBlank(addUserPaw)
				|| StringUtils.isBlank(addUserRole) || StringUtils.isBlank(addUserDescripte)) {
			throw new TipException("填写的信息不完整,请填写完整");
		}
		User user = new User();
		user.setId(addUserId);
		user.setName(addUsername);
		user.setPassword(PlaneUtils.MD5encode(addUsername + addUserPaw));
		// user.setRole(addUserRole);
		user.setDescription(addUserDescripte);
		user.setCreatetime(new Date());
		int count = userDao.insertSelective(user);
		if (count != 1) {
			throw new TipException("新增用户操作异常");
		}
		return count;
	}

	// @Override
	// public TailPage<User> getUserByRoleOrIdWithPage(String searchUserStatus,
	// String searchUserId, TailPage<User> page) {
	// if (searchUserId.equals(WebConst.SEARCH_NO_USERID)) {
	// int count = userDao.selectCountWithRole(searchUserStatus);
	// page.setItemsTotalCount(count);
	// List<User> userList = userDao.selectUserByRole(page, searchUserStatus);
	// page.setItems(userList);
	// } else {
	// page.setItemsTotalCount(1);
	// List<User> userList = new ArrayList<>(1);
	// userList.add(userDao.selectByPrimaryKey(searchUserId));
	// page.setItems(userList);
	// }
	// return page;
	// }

	@Override
	public boolean updataTasknumByUser(User user) {
		// TODO Auto-generated method stub
		if (userDao.userAddTasknum(user) == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean reduceTasknumByUser(User user) {

		if (userDao.userReduceTasknum(user) == 1)
			return true;
		else
			return false;
	}

	@Override
	public User getUserById(Integer userid) {
		// TODO Auto-generated method stub
		return userDao.selectByPrimaryKey(userid);
	}

	@Override
	public List<User> fuzzySearchWithUser(String queryString) {
		if (StringUtils.isBlank(queryString)) {
			throw new TipException("用户输入的放飞员为空");
		}
		UserExample example = new UserExample();
		UserExample.Criteria criteria = example.createCriteria();
		criteria.andNameLike("%" + queryString + "%");
		List<User> bUserList = userDao.selectByExample(example);
		return bUserList;
	}
}
