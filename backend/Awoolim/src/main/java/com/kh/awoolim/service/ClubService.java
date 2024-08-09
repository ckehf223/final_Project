package com.kh.awoolim.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.kh.awoolim.domain.Club;
import com.kh.awoolim.domain.Member;
import com.kh.awoolim.mapper.ClubMapper;
import com.kh.awoolim.mapper.ClubMemberMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ClubService {

	@Autowired
	private ClubMapper clubMapper;

	@Autowired
	private ClubMemberMapper clubMemberMapper;

	public int register(Club club) {
		int count = clubMapper.clubCount(club.getUserId());
		if(count >= 3) {
			return 0;
		}
		clubMapper.create(club);
		return 1;
	}
	
    public List<Club> getAllClubs() {
    	List<Club> list = clubMapper.getAllClubs();
        return list;
    }

    public byte[] getImage(String imageName) {
        try (InputStream is = getClass().getResourceAsStream("/static/images/" + imageName)) {
            if (is == null) {
                throw new IOException("Image not found: " + imageName);
            }
            return StreamUtils.copyToByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + imageName, e);
        }
    }

    public List<Club> searchClubs(String searchTerm, Map<String, Object> filters) {
        return clubMapper.searchClubs(searchTerm, filters);
    }

	
	public Club readByClub(int clubNo) {
		return clubMapper.readClub(clubNo);
	}
	
	public Club readByClubNo(int clubNo) {
		Club club = clubMapper.readClub(clubNo);
		 if (club.getClubImage() != null && !club.getClubImage().isEmpty()) {
             try {
                 String clubImage = (String)encodeImageToBase64("/static/images/" + club.getClubImage().trim());
                 club.setClubImage(clubImage);
             } catch (IOException e) {
                 System.err.println("Error encoding image for club " + club.getClubNo() + ": " + e.getMessage());
             }
         }
		 return club;
	}
	
	public Map<String, Object> readClub(int clubNo) throws IOException {
		Map<String, Object> clubData = new HashMap<>();
		Club club = null;
		List<Club> clubList = null;
		List<Member> memberList = null;
		club = clubMapper.readClub(clubNo);
		
		clubList = clubMapper.readPopularTop4();
		memberList = clubMemberMapper.readClubMember(clubNo);
		Map<String, String> imageList = new HashMap<>();
		System.out.println(club);
		if (club != null) {
			clubData.put("club", club);
			String clubImage = (String)encodeImageToBase64("/static/images/" + club.getClubImage().trim());
			imageList.put("clubImage0", clubImage);
		}
		
		if (clubList.size() > 0) {
			clubData.put("clubList", clubList);
			for (Club data : clubList) {
				String clubImage =(String) encodeImageToBase64("/static/images/" + data.getClubImage().trim());
				imageList.put("clubImage" + data.getClubNo(), clubImage);
			}

		}
		if (memberList.size() > 0) {
			clubData.put("memberList", memberList);
			for (Member data : memberList) {
				if(data.getUserBackImage() != null && data.getUserBackImage() != "") {
					String backImage = (String)encodeImageToBase64("/static/images/" + data.getUserBackImage().trim());
					imageList.put("backImage" + data.getUserId(), backImage);
				}
				String userImage = (String)encodeImageToBase64("/static/images/" + data.getUserImage().trim());
				imageList.put("userImage" + data.getUserId(), userImage);
				
			}
		}
		clubData.put("imageData", imageList);
		if (club == null && clubList == null && memberList == null) {
			return null;
		}
		return clubData;
	}
	
	public Map<String,Object> readMyClubList(int userId) throws IOException{
		Map<String,Object> clubMap = new HashMap<>();
		List<Club> apprList = clubMapper.readMyClubDisapprovalList(userId);
		List<Club> disaList = clubMapper.readMyApprovalClubList(userId);
		if(apprList.size() > 0 && apprList != null) {
			for(Club data : apprList) {
				if(data.getClubImage() != null && data.getClubImage() != "") {
					String clubImage = (String)encodeImageToBase64("/static/images/" + data.getClubImage().trim());
					data.setClubImage(clubImage);
				}
			}
		}
		if(disaList.size() > 0 && disaList != null) {
			for(Club data : disaList) {
				if(data.getClubImage() != null && data.getClubImage() != "") {
					String clubImage = (String)encodeImageToBase64("/static/images/" + data.getClubImage().trim());
					data.setClubImage(clubImage);
				}
			}
		}
		clubMap.put("apprList", apprList);
		clubMap.put("disaList", disaList);
		
		return clubMap;
	}
	public Map<String, Object> readMyMadeClubList(int userId) throws IOException {
	    Map<String, Object> clubMap = new HashMap<>();
	    Map<String, String> countMap = new HashMap<>();
	    List<Club> clubList = clubMapper.readMyMadeClubList(userId);
	    
	    if (clubList != null && clubList.size() > 0) {
	        for (Club data : clubList) {
	            int count = clubMemberMapper.myClubSignupCount(data.getClubNo());
	            countMap.put("count" + data.getClubNo(), String.valueOf(count));
	            if (data.getClubImage() != null && !data.getClubImage().isEmpty()) {
	                try {
	                    String clubImage = (String)encodeImageToBase64("/static/images/" + data.getClubImage().trim());
	                    data.setClubImage(clubImage);
	                } catch (IOException e) {
	                    System.err.println("Error encoding image for club " + data.getClubNo() + ": " + e.getMessage());
	                }
	            }
	        }
	        clubMap.put("clubList", clubList);
	        clubMap.put("countMap", countMap);
	    }
	    
	    return clubMap;
	}

	public void modifyClub(Club club) {
		clubMapper.modifyClub(club);
	}
	
	public void addClubCount(int clubNo) {
		clubMapper.addClubCount(clubNo);
	}
	
	public void minusClubCount(int clubNo) {
		clubMapper.minusClubCount(clubNo);
	}
	public String encodeImageToBase64(String imagePath) throws IOException {
		ClassPathResource imgFile = new ClassPathResource(imagePath);
		byte[] imageBytes = Files.readAllBytes(imgFile.getFile().toPath());
		return Base64.getEncoder().encodeToString(imageBytes);
	}

}
