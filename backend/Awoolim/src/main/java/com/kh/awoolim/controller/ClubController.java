package com.kh.awoolim.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.awoolim.common.jwt.JWTUtil;
import com.kh.awoolim.domain.Club;
import com.kh.awoolim.service.ClubMemberService;
import com.kh.awoolim.service.ClubService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/club")
@Slf4j
public class ClubController {

	@Value("${upload.path}")
	private String uploadDir;

	private final JWTUtil jwtUtil;

	private ClubService clubService;

	private ClubMemberService clubMemberService;

	
	@GetMapping("/")
    public List<Club> getAllClubs(HttpServletResponse response) {
		log.info("getAllClubs");
		response.setStatus(HttpStatus.OK.value());
        return clubService.getAllClubs();
    }

    @GetMapping("/image/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) {
        byte[] imageData = clubService.getImage(imageName);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
    }

    @PostMapping("/search") // 검색 요청 처리
    public List<Club> searchClubs(@RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam Map<String, Object> filters) {
        return clubService.searchClubs(searchTerm, filters);
    }
	
	
	public ClubController(JWTUtil jwtUtil, ClubService clubService, ClubMemberService clubMemberService) {
		this.jwtUtil = jwtUtil;
		this.clubService = clubService;
		this.clubMemberService = clubMemberService;
	}

	@PostMapping("/register")
	public ResponseEntity<Integer> registerClub(@RequestParam("clubTitle") String clubTitle,
			@RequestParam("clubGender") String clubGender, @RequestParam("category") String category,
			@RequestParam("city") String city, @RequestParam("district") String district,
			@RequestParam("regularType") int regularType, @RequestParam("maxMember") int maxMember,
			@RequestParam("dDay") String dDay,
			@RequestParam(value = "clubImage", required = false) MultipartFile clubImage,
			@RequestParam("detailInfo") String detailInfo, @RequestParam("ageLimit") String ageLimit,
			HttpServletRequest request) {

		log.info("club register POST Enter");
		try {

			String accessToken = request.getHeader("Authorization").substring(7);
			int userId = jwtUtil.getUserId(accessToken);

			Club club = new Club();
			club.setUserId(userId);
			club.setClubTitle(clubTitle);
			club.setClubGender(clubGender);
			club.setCategory(category);
			club.setCity(city);
			club.setDistrict(district);
			club.setRegularType(regularType);
			club.setMaxMember(maxMember);
			club.setDDay(dDay);
			club.setDetailInfo(detailInfo);
			club.setRecruitment(1);
			club.setAgeLimit(ageLimit);
			club.setClubImage("305d04e5-e53d-4419-8beb-555330a6a3d4.png");
			club.setMemberCount(0);

			// 디렉토리가 존재하지 않으면 생성
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			if (clubImage != null && !clubImage.isEmpty() && clubImage.getSize() > 0) {
				// UUID 생성
				String uuid = UUID.randomUUID().toString();
				// 파일 확장자 추출
				String originalFilename = clubImage.getOriginalFilename();
				String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
				// 새로운 파일 이름 생성
				String newFileName = uuid + fileExtension;
				log.info(newFileName);
				club.setClubImage(newFileName);
				byte[] bytes = clubImage.getBytes();
				Path path = Paths.get(uploadDir + File.separator + newFileName);
				Files.write(path, bytes);
			}

			int success = clubService.register(club);
			return ResponseEntity.status(HttpStatus.OK).body(success);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/read/{clubNo}")
	public ResponseEntity<Map<String, Object>> readClub(@PathVariable("clubNo") int clubNo) {
		log.info("read Club Enter");
		try {

			Map<String, Object> clubData = clubService.readClub(clubNo);
			if (clubData != null) {
				return ResponseEntity.status(HttpStatus.OK).body(clubData);
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

	}

	@PostMapping("/clubmember/signUp/{clubNo}")
	public ResponseEntity<Integer> clubMemberSignUp(@PathVariable("clubNo") int clubNo, HttpServletRequest request) {

		try {
			log.info("clubmemberSingup POST ENTER");
			String accessToken = request.getHeader("Authorization").substring(7);
			int userId = jwtUtil.getUserId(accessToken);
			System.out.println(userId);

			int check = clubMemberService.signUp(userId, clubNo);
			return ResponseEntity.status(HttpStatus.OK).body(check);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

	}

	@GetMapping("/readMyClub")
	public ResponseEntity<Map<String, Object>> readMyClub(HttpServletRequest request) {
		log.info("readMyClub GET ENTER");
		try {
			String accessToken = request.getHeader("Authorization").substring(7);
			int userId = jwtUtil.getUserId(accessToken);
			Map<String, Object> clubMap = clubService.readMyClubList(userId);
			return ResponseEntity.status(HttpStatus.OK).body(clubMap);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping("/exitClub/{clubNo}")
	public void exitClub(@PathVariable("clubNo") int clubNo, HttpServletRequest request, HttpServletResponse response) {

		log.info("exitClub POST ENTER");
		try {
			String accessToken = request.getHeader("Authorization").substring(7);
			int userId = jwtUtil.getUserId(accessToken);
			System.out.println(clubNo);
			System.out.println(userId);
			clubMemberService.deleteClubMember(userId, clubNo);
			clubService.minusClubCount(clubNo);
			response.setStatus(HttpStatus.OK.value());
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@GetMapping("/read/madeClubList")
	public ResponseEntity<Map<String, Object>> readMadeClubList(HttpServletRequest request) {
		log.info("madeClubList GET ENTER");
		try {
			String accessToken = request.getHeader("Authorization").substring(7);
			int userId = jwtUtil.getUserId(accessToken);
			Map<String, Object> clubMap = clubService.readMyMadeClubList(userId);
			return ResponseEntity.status(HttpStatus.OK).body(clubMap);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/modify/{clubNo}")
	public ResponseEntity<Club> getModifyClub(@PathVariable("clubNo") int clubNo) {
		log.info("modify GET ENTER");
		try {
			Club club = clubService.readByClubNo(clubNo);
			return ResponseEntity.status(HttpStatus.OK).body(club);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping("/modify")
	public void updateClub(@RequestParam("clubNo") int clubNo,@RequestParam("clubTitle") String clubTitle, @RequestParam("clubGender") String clubGender,
			@RequestParam("category") String category, @RequestParam("city") String city,
			@RequestParam("district") String district, @RequestParam("regularType") int regularType,
			@RequestParam("maxMember") int maxMember, @RequestParam("dDay") String dDay,
			@RequestParam("checkImage") String checkImage,
			@RequestParam(value = "clubImage", required = false) MultipartFile clubImage,
			@RequestParam("detailInfo") String detailInfo, @RequestParam("ageLimit") String ageLimit,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("modify POST Enter");

		try {
			Club club = clubService.readByClub(clubNo);
			club.setClubTitle(clubTitle);
			club.setCategory(category);
			club.setClubGender(clubGender);
			club.setDDay(dDay);
			club.setRegularType(regularType);
			club.setMaxMember(maxMember);
			club.setCity(city);
			club.setDistrict(district);
			club.setDetailInfo(detailInfo);
			club.setAgeLimit(ageLimit);
			
			// 디렉토리가 존재하지 않으면 생성
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			if(checkImage.equals("1")) {
				// UUID 생성
				String uuid = UUID.randomUUID().toString();
				// 파일 확장자 추출
				String originalFilename = clubImage.getOriginalFilename();
				String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
				// 새로운 파일 이름 생성
				String newFileName = uuid + fileExtension;
				log.info(newFileName);
				club.setClubImage(newFileName);
				byte[] bytes = clubImage.getBytes();
				Path path = Paths.get(uploadDir + File.separator + newFileName);
				Files.write(path, bytes);
			}
			
			clubService.modifyClub(club);
		} catch (Exception e) {

		}

	}
}
