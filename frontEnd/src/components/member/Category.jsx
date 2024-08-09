import React, { useState, useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import ClubItem from "./ClubItem";
import "/src/css/member/category.css";

function Category() {
  const [currentCategory, setCurrentCategory] = useState("전체");
  const [currentSlide, setCurrentSlide] = useState(0);
  const sliderContainerRef = useRef(null);
  const [clubs, setClubs] = useState([]);
  const sportsLinkRef = useRef(null);

  const categoryColors = {
    스포츠: "#e7f4ff",
    맛집탐방: "#f4f4ff",
    독서: "#fff4f4",
    친목: "#fff4ff",
    전시: "#f4ffff",
    취미활동: "rgb(241, 250, 241)",
    스터디: "#fff4e6",
  };

  useEffect(() => {
    setCurrentCategory("전체");

    if (sportsLinkRef.current) {
      sportsLinkRef.current.focus();
    }

    fetch("http://localhost:8080/api/club/")
      .then((response) => response.json())
      .then((data) => setClubs(data))
      .catch((error) => {
        console.error("Error fetching clubs:", error);
      });
  }, []);

  const handleCategoryClick = (category) => {
    setCurrentCategory(category);
    setCurrentSlide(0);
  };

  const handlePrevClick = () => {
    setCurrentSlide((prev) => Math.max(prev - 1, 0));
  };

  const filteredClubs =
    currentCategory === "전체"
      ? clubs
      : clubs.filter((club) => club.category === currentCategory);

  const handleNextClick = () => {
    setCurrentSlide((prev) => Math.min(prev + 1, filteredClubs.length - 4));
  };

  return (
    <div id="categoryDiv">
      <div id="categorySpan">
        <span>카테고리</span>
      </div>
      <div id="categoryA">
        {/* 전체 카테고리 추가 */}
        <Link
          key="전체"
          to="#"
          onClick={() => handleCategoryClick("전체")}
          ref={currentCategory === "전체" ? sportsLinkRef : null}
        >
          전체
        </Link>
        {/* 나머지 카테고리 링크 */}
        {Array.from(new Set(clubs.map((club) => club.category))).map(
          (category) => (
            <Link
              key={clubs.clubNo}
              to="#"
              onClick={() => handleCategoryClick(category)}
            >
              {category}
            </Link>
          )
        )}
      </div>

      {filteredClubs.length > 0 ? (
        <>
          <div className="categoryDiv" ref={sliderContainerRef}>
            <div
              className="slide-container"
              style={{ transform: `translateX(-${currentSlide * 26}%)` }}
            >
              {filteredClubs.map((club) => (
                <ClubItem
                  key={club.clubNo}
                  club={club}
                  backgroundColor={categoryColors[club.category] || "#ffffff"} // 배경색 전달
                />
              ))}
            </div>
          </div>

          <div className="categoryButtonContainer">
            {currentSlide > 0 && (
              <img
                src="/src/assets/images/left-arrow.png"
                alt="이전 버튼"
                className="categoryButtonL"
                onClick={handlePrevClick}
              />
            )}
            {currentSlide < filteredClubs.length - 4 && (
              <img
                src="/src/assets/images/right-arrow.png"
                alt="다음 버튼"
                className="categoryButtonR"
                onClick={handleNextClick}
              />
            )}
          </div>
        </>
      ) : (
        <div>로딩 중...</div>
      )}
    </div>
  );
}

export default Category;
