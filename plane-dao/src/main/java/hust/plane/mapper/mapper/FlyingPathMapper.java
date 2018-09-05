package hust.plane.mapper.mapper;


import java.util.List;

import hust.plane.mapper.pojo.FlyingPath;
import hust.plane.utils.page.TailPage;

public interface FlyingPathMapper {
	
	 FlyingPath selectByFlyingPathVO(FlyingPath flyingPath);

	 //void insertFlyingPath();

	 void insertFlyingPath(FlyingPath flyingPath);

	 FlyingPath selectByFlyingPathId(FlyingPath flyingPath);

	int flyingPathCount(FlyingPath flyingPath);

	List<FlyingPath> queryFlyingPathPage(FlyingPath flyingPath, TailPage<FlyingPath> page);

	List<FlyingPath> findAllFlyingPath();

	void deleteFlyingPath(FlyingPath flyingPath); 
}
