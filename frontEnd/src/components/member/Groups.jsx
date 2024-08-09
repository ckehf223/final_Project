import React, { useState, useEffect } from "react";
import ClubItem from "./ClubItem"; // ClubItem 컴포넌트 분리
import "/src/css/member/groups.css";

function Groups() {
  const [clubs, setClubs] = useState([]);
  const featuredCategories = ["스포츠", "맛집탐방", "독서"];
  const backgroundColor = "#f1f7ff"; // 설정할 배경색

  useEffect(() => {
    fetch("http://localhost:8080/api/club/")
      .then((response) => response.json())
      .then((data) => {
        setClubs(data);
      })
      .catch((error) => {
        console.error("Error fetching clubs:", error);
      });
  }, []);

  return (
    <div id="groupDiv">
      {/* 핫한 소셜 클럽! 오늘 뭐해? */}
      <div className="groupDiv">
        <span>핫한 소셜 클럽! 오늘 뭐해?</span>
      </div>
      <div className="groupType">
        {clubs
          .filter((club) => club.category === featuredCategories[0]) // 첫 번째 카테고리 필터링
          .slice(0, 4)
          .map((club) => (
            <ClubItem
              key={club.clubNo}
              club={club}
              backgroundColor={backgroundColor}
            />
          ))}
      </div>

      {/* 망설이면 마감! 지금 바로 신청하세요! */}
      <div className="groupDiv">
        <span>망설이면 마감! 지금 바로 신청하세요!</span>
      </div>
      <div className="groupType">
        {clubs
          .filter((club) => club.category === featuredCategories[1]) // 두 번째 카테고리 필터링
          .slice(0, 4)
          .map((club) => (
            <ClubItem
              key={club.clubNo}
              club={club}
              backgroundColor={backgroundColor}
            />
          ))}
      </div>

      {/* 우리 동네, 우리들의 이야기 */}
      <div className="groupDiv">
        <span>우리 동네, 우리들의 이야기</span>
      </div>
      <div className="groupType">
        {clubs
          .filter((club) => club.category === featuredCategories[2]) // 세 번째 카테고리 필터링
          .slice(0, 4)
          .map((club) => (
            <ClubItem
              key={club.clubNo}
              club={club}
              backgroundColor={backgroundColor}
            />
          ))}
      </div>

      {/* 모임 목록 (전체 클럽 데이터) */}
      <div className="groupDiv">
        <span>모임 목록</span>
      </div>
      <div className="groupType group-list">
        {clubs.map((club) => (
          <ClubItem
            key={club.clubNo}
            club={club}
            backgroundColor={backgroundColor}
          />
        ))}
      </div>
    </div>
  );
}

export default Groups;
