import React, { useState, useEffect } from "react";
import ClubItem from "/src/components/member/club/ClubItem";
import "/src/css/member/common/groups.css";

function Groups() {
  const [clubs, setClubs] = useState([]);
  const [featuredCategories, setFeaturedCategories] = useState([]);
  const [visibleClubsCount, setVisibleClubsCount] = useState(12); // 초기 표시할 모임 수
  const backgroundColor = "#f1f7ff"; // 설정할 배경색

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch("http://ec2-3-35-137-210.ap-northeast-2.compute.amazonaws.com:8080/api/club/");
        const data = await response.json();
        setClubs(data);
        setFeaturedCategories(["스포츠", "맛집탐방", "독서"]);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    // 더보기 버튼 클릭 시 추가된 항목들에만 애니메이션 적용
    const newItems = document.querySelectorAll(
      `.club-item-wrapper:nth-child(n+${visibleClubsCount - 11})`
    );
    newItems.forEach((element) => {
      element.classList.add("show");
    });
  }, [visibleClubsCount]);

  const handleLoadMore = () => {
    setVisibleClubsCount((prevCount) => prevCount + 12); // 12개씩 추가로 표시
  };

  const renderClubItems = (category) =>
    clubs
      .filter((club) => club.category === category)
      .slice(0, 4)
      .map((club) => (
        <ClubItem
          key={club.clubNo}
          club={club}
          backgroundColor={backgroundColor}
        />
      ));

  return (
    <div id="groupDiv">
      {featuredCategories.length > 0 && (
        <>
          {[
            "핫한 소셜 클럽! 오늘 뭐해?",
            "여름하면 이 모임! 지금 바로 신청하세요!",
            "우리 동네, 우리들의 이야기",
          ].map((title, index) => (
            <div key={title} className="groupSection">
              <div className="groupDiv">
                <span>{title}</span>
              </div>
              <div className="groupType">
                {renderClubItems(featuredCategories[index])}
              </div>
            </div>
          ))}
        </>
      )}

      <div className="groupDiv">
        <span>모임 목록</span>
      </div>
      <div className="groupType group-list">
        {clubs.slice(0, visibleClubsCount).map((club, index) => (
          <div
            key={club.clubNo}
            className={`club-item-wrapper ${index < 12 ? "show" : ""}`}
          >
            <ClubItem club={club} backgroundColor={backgroundColor} />
          </div>
        ))}
      </div>

      {visibleClubsCount < clubs.length && (
        <button className="load-more-button" onClick={handleLoadMore}>
          더보기
        </button>
      )}
    </div>
  );
}

export default Groups;
