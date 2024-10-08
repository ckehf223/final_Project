import { useEffect, useState } from 'react';
import '/src/css/member/service/NoticeCustom.css'
import instance from "/src/auth/axios";
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBullhorn, faMagnifyingGlass, faFilePen, faBackwardStep, faForwardStep, faCaretLeft, faCaretRight } from '@fortawesome/free-solid-svg-icons';

const NoticeCustom = () => {
    const navi = useNavigate();
    const [notices, setNotices] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [noticesPerPage] = useState(10);
    const [serchTerm, setSerchTerm] = useState('');

    useEffect(() => {
        fetchNotices();
    }, []);

    const fetchNotices = (query = '') => {
        const url = query ? `/api/notices/search?query=${query}` : `/api/notices/list`;
        instance.get(url)
            .then(response => {
                setNotices(response.data);
            })
            .catch(error => {
                console.error('FETCH 에러 입니다.', error);
                setNotices([]);
            });
    };

    const paginate = (pageNumber) => setCurrentPage(pageNumber);
    const indexOfLastNotice = currentPage * noticesPerPage;
    const indexOfFirstNotice = indexOfLastNotice - noticesPerPage;
    const currentNotices = Array.isArray(notices) ? notices.slice(indexOfFirstNotice, indexOfLastNotice) : [];

    const pageNumbers = [];
    for (let i = 1; i <= Math.ceil(notices.length / noticesPerPage); i++) {
        pageNumbers.push(i);
    }

    const handleTitleClick = (noticeNo) => {
        instance.put(`/api/notices/increaseView/${noticeNo}`)
            .then(response => {
                navi(`/service/noticeRead/${noticeNo}`);
            })
            .catch(error => {
                console.error("조회수 증가 오류", error);
            });
    };

    const handelSearchChange = (e) => {
        setSerchTerm(e.target.value);
    };

    const handelSearchSubmit = () => {
        fetchNotices(serchTerm);
    };

    const handleFirstPage = () => {
        setCurrentPage(1);
    };

    const handleLastPage = () => {
        setCurrentPage(pageNumbers.length);
    };

    const handelPrevPage = () => {
        setCurrentPage(prev => Math.max(prev - 1, 1));
    }

    const handelNextPage = () => {
        setCurrentPage(prev => Math.min(prev + 1, pageNumbers.length));
    }


    return (
        <div className="NoticeCustom">
            <div className="NoticeCustom_Header">
                <h1><FontAwesomeIcon icon={faBullhorn} /> 공지사항</h1>
            </div>
            <div className='searchBox'>
                <div className='writeBtn' />
                <div className='SB'>
                    <input placeholder='검색' value={serchTerm} onChange={handelSearchChange} />
                    <FontAwesomeIcon icon={faMagnifyingGlass} onClick={handelSearchSubmit} style={{ marginRight: "15px", color: "gray", cursor: 'pointer' }} />
                </div>
            </div>
            <div className='NoticeCustomTable'>
                <table>
                    <thead>
                        <tr>
                            <th>번호</th>
                            <th>제목</th>
                            <th>조회수</th>
                            <th>등록일</th>
                        </tr>
                    </thead>
                    <tbody>
                        {currentNotices.map((notice, index) => (
                            <tr key={notice.noticeNo}>
                                <td className='NotNo'>{indexOfFirstNotice + index + 1}</td>
                                <td className='NotTitle' onClick={() => handleTitleClick(notice.noticeNo)}>
                                    <span className='noticeKeyword'>{`[ ${notice.keyword} ]`}</span>{notice.title}</td>
                                <td className='NotCnt'>{notice.count}</td>
                                <td className='NotRegDate'>{new Date(notice.regDate).toLocaleDateString()}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            {/* 페이지 번호 설정 */}
            <div className='pageNum'>
                <button className="FirstPageBtn" onClick={handleFirstPage} disabled={currentPage === 1}>
                    <FontAwesomeIcon icon={faBackwardStep} /></button>
                <button className="prevPageBtn" onClick={handelPrevPage} disabled={currentPage === 1}>
                    <FontAwesomeIcon icon={faCaretLeft} /></button>
                {pageNumbers.map(number => (
                    <span key={number} onClick={() => paginate(number)}>
                        <button className={currentPage === number ? 'currentBtn active' : 'currentBtn'}>{number}</button></span>
                ))}
                <button className="prevPageBtn" onClick={handelNextPage} disabled={currentPage === pageNumbers.length}>
                    <FontAwesomeIcon icon={faCaretRight} /></button>
                <button className="FirstPageBtn" onClick={handleLastPage} disabled={currentPage === pageNumbers.length}>
                    <FontAwesomeIcon icon={faForwardStep} /></button>
            </div>
        </div>
    );
};

export default NoticeCustom;