package com.tanishk.project.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
	
	@Override
	public String uploadImage(String path, MultipartFile file) throws IOException {
		//File names of current/original file
		String originalFile = file.getOriginalFilename();
		//Generate a unique file name
		String randomId = UUID.randomUUID().toString();
		String fileName = randomId.concat(originalFile).substring(originalFile.lastIndexOf('.'));
		String filePath = path + File.separator + fileName;
		//Check if path exists
		File folder = new File(path);
		if(!folder.exists()) {
			folder.mkdir();
		}
		//Upload to server
		Files.copy(file.getInputStream(), Paths.get(filePath));
		return fileName;
	}

}
