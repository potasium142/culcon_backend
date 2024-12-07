package com.culcon.backend.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
	Map uploadImage(
		MultipartFile file,
		Map info
	) throws IOException;
}
