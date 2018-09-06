package hust.plane.service.interFace;


import java.io.File;


import hust.plane.mapper.pojo.Route;

public interface FileService {
	boolean insertRoute(File file, Route route);
}
