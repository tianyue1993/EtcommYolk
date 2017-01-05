package etcomm.com.etcommyolk.service.data;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import etcomm.com.etcommyolk.service.db.DatabaseHelper;


public class UserDao {
	private Context context;
	private Dao<User,Integer> userDaoOpe;
	private DatabaseHelper helper;
	@SuppressWarnings("unchecked")
	public UserDao(Context context)
	{
		this.context = context;
		try {
			helper = DatabaseHelper.getHelper(context);
			userDaoOpe = helper.getDao(User.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void add(User user){
		try {
			userDaoOpe.create(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public User getDefaultUser(String userid){
		List<User> list = null;
		try {
			list = userDaoOpe.queryBuilder().where().eq("user_id", userid).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list == null||list.size()<1){
			return null;
		}
		return list.get(0);
	}
	public List<User> getUserList(){
		List<User> list = null;
		try {
			list = userDaoOpe.queryBuilder().where().gt("id", -1).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
