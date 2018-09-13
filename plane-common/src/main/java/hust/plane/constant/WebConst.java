package hust.plane.constant;

import org.springframework.stereotype.Component;
/**
 * 定义一些常用的字符串
 * @author rfYang
 * @date 2018/7/3 18:22
 * @param
 * @return
 */
@Component
public class WebConst {
    public static final String SUPER_ADMINISTRATOR_VIEW = "super_administrator";
    public static final String SEARCH_NO_USERNAME ="S_N_NAME" ;

    public static String LOGIN_SESSION_KEY = "login_user";

    public static final String USER_IN_COOKIE = "S_L_ID";
    /**
     * 成功返回
     */
    public static String SUCCESS_RESULT = "SUCCESS";
    /**
     * aes加密加盐
     */
    public static String AES_SALT = "0123456789abcdef";
    /**
     * 搜索不用用户序号
     */
    public static String SEARCH_NO_USERID = "S_N_ID";
    /**
     * 图片的路径
     */
    public static String ALARM_PIC_PATH = "D:/100MEDIA";
}
