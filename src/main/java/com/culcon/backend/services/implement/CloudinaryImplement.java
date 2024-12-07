package com.culcon.backend.services.implement;

import com.cloudinary.Cloudinary;
import com.culcon.backend.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryImplement implements CloudinaryService {

	private final Cloudinary cloudinary;

	@Override
	public Map uploadImage(
		MultipartFile file, Map info) throws IOException {

//		var info = Map.of(
//			"asset_folder", "user_pfp",
//			"display_name", fileName,
//			"public_id", fileName
//		);

		return cloudinary
			.uploader()
			.upload(file.getBytes(), info);
	}
}
