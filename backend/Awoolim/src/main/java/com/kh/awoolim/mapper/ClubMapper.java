package com.kh.awoolim.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.kh.awoolim.domain.Club;

public interface ClubMapper {

	public void create(Club club);

	public Club readClub(int clubNo);

	public List<Club> readPopularTop4();

	public int clubCount(int userId);

	public List<Club> readMyApprovalClubList(int userId);

	public List<Club> readMyClubDisapprovalList(int userId);

	public void addClubCount(int clubNo);

	public void minusClubCount(int clubNo);

	public List<Club> readMyMadeClubList(int clubNo);

	public void modifyClub(Club club);

	public List<Club> getAllClubs();

	byte[] getImage(String imageName);

	public List<Club> searchClubs(Map<String, Object> filters);
	
	public void delete(int clubNo);
	
	
}