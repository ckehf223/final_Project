import React, { useEffect, useState } from 'react';
import '/src/css/member/RegisterMember.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';


const RegisterMember = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);

    const [formData, setFormData] = useState({
        useremail: '',
        password: '',
        passwordCheck: '',
        username: '',
        usergender: '',
        userbirth: '',
        userphone: '',
        nickname: '',
        phoneCheckCode: '',
        snsType: '',
    });

    const [userEmailError, setUserEmailError] = useState('');
    const [passwordVisible, setPasswordVisible] = useState(false);
    const [passwordCheckVisible, setPasswordCheckVisible] = useState(false);
    const [passwordError, setPasswordError] = useState(null);
    const [passwordCheckError, setPasswordCheckError] = useState(null);
    const [userNameError, setUserNameError] = useState(null);
    const [userPhoneError, setUserPhoneError] = useState(null);
    const [timeLeft, setTimeLeft] = useState(180);
    const [isActive, setIsActive] = useState(false);
    const [phoneCheckSuccess, setPhoneCheckSuccess] = useState(false);
    const [phoneCheckMessage, setPhoneCheckMessage] = useState('');
    const [userEmailSuccess, setUserEmailSuccess] = useState('');

    const nav = useNavigate();

    useEffect(() => {
        // 초기 상태 설정
        setFormData(prevFormData => ({
            ...prevFormData,
            useremail: queryParams.get('email') || '',
            username: queryParams.get('name') || '',
            snsType: queryParams.get('type') || '',
        }));
    }, [location.search]);

    useEffect(() => {
        let interval;
        if (isActive && timeLeft > 0) {
            interval = setInterval(() => {
                setTimeLeft((prevTimeLeft) => prevTimeLeft - 1);
            }, 1000);
        } else if (timeLeft === 0) {
            setIsActive(false);
        }

        return () => clearInterval(interval);
    }, [isActive, timeLeft]);

    const handleButtonClick = async () => {
        try {
            const response = await axios.post('http://localhost:8080/send-sms', {
                phoneNumber: formData.userphone,
                message: "Hello!"
            }, {
                headers: { 'Content-Type': 'application/json' },
                withCredentials: true
            });

            if (response.status === 200) {
                setTimeLeft(180);
                setIsActive(true);
                setUserPhoneError('인증번호가 전송되었습니다.');
            }
        } catch (error) {
            setUserPhoneError('인증번호 전송에 실패했습니다.');
            console.log("인증번호 전송 오류: " + error);
        }
    };
    const handleCodeCheck = async () => {
        try {
            const response = await axios.post('http://localhost:8080/check-code',
                {
                    code: formData.phoneCheckCode,
                    phoneNumber: formData.userphone
                },
                {
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: true,
                })

            if (response.data === 1) {
                setPhoneCheckMessage('인증되었습니다.');
                setUserPhoneError('')
                setIsActive(false);
                phoneCheckSuccess(true);
            } else if (response.data === 0) {
                alert('이미 가입한 전화번호 입니다.');
                nav('/login', { replace: true })
            } else {
                setUserPhoneError('인증 번호를 확인해주세요')
                setFormData({ ...formData, phoneCheckCode: '' });
            }

        } catch (error) {
            setPhoneCheckMessage('인증번호 확인에 실패했습니다.');
        }
    };

    const formatTime = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (userEmailError === '' && userNameError === '' && passwordError === '' && phoneCheckSuccess) {
            // 회원가입 로직
            try {
                await axios.post('http://localhost:8080/member/registerMember',
                    {
                        userEmail: formData.useremail,
                        password: formData.password,
                        userName: formData.username,
                        userBirth: formData.userbirth,
                        userGender: formData.usergender,
                        userPhone: formData.userphone,
                        snsType: formData.snsType
                    },
                    {
                        headers: { 'Content-Type': 'application/json' },
                        withCredentials: true,
                    }
                ).then(response => {
                    if (response.data === 1) {
                        alert('회원가입이 완료되었습니다.');
                        nav('/login', { replace: true });
                    }
                })
            } catch (error) {
                console.log("회원가입시 에러 발생" + error)
            }

        } else {
            alert('입력된 정보를 확인해보세요');
        }
    };

    const validateEmail = async () => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(formData.useremail)) {
            setUserEmailError('유효하지 않은 이메일 주소입니다.');
            setUserEmailSuccess('')
        } else {
            try {
                const response = await axios.post('http://localhost:8080/member/checkEmail', { userEmail: formData.useremail },
                    {
                        headers: { 'Content-Type': 'application/json' },
                        withCredentials: true,
                    }
                );

                if (response.status === 200) {
                    if (response.data) {
                        setUserEmailError('중복된 아이디입니다.');
                        setUserEmailSuccess('')
                        setFormData({ ...formData, useremail: '' });
                    } else {
                        setUserEmailSuccess('사용가능한 아이디입니다.');
                        setUserEmailError('')
                    }
                }
            } catch (error) {
                console.log("이메일 중복체크 에러: " + error);
            }
        }
    };

    const validatePasswordPatten = () => {
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,12}$/;
        if (!passwordRegex.test(formData.password)) {
            setPasswordError('비밀번호는 8~12자 영문, 숫자, 특수문자 혼합이어야 합니다.');
        } else {
            setPasswordError('');
        }
    }

    const validatePasswordMatch = () => {
        if (formData.password !== formData.passwordCheck) {
            setPasswordCheckError('비밀번호가 일치하지 않습니다.');
        } else {
            setPasswordCheckError('');
        }
    };

    const validateUserNamePatten = () => {
        const userNamedRegex = /^[가-힣]{2,7}$/;
        if (!userNamedRegex.test(formData.username)) {
            setUserNameError('이름은 한글 2~7자 입니다.');
        } else {
            setUserNameError('');
        }
    }

    const togglePasswordVisibility = () => {
        setPasswordVisible(!passwordVisible);
    };

    const togglePasswordCheckVisibility = () => {
        setPasswordCheckVisible(!passwordCheckVisible);
    };

    return (
        <div className='RegisterMemberWrap'>
            <div className="RegisterMemberLogoArea">
                <img src="src/assets/images/headerLogo.png" alt="어울림" />
            </div>
            <div className="RegisterMember">
                <h2>회원 가입</h2>
                <form className="register-form" onSubmit={handleSubmit}>
                    <div className="RegisterMemberInputBox">
                        <div className='RegisterMemberInputArea'>
                            <label htmlFor="useremail"><span className='InputStarSpan'>*</span> 이메일</label>
                            <input type="email" id="useremail" name="useremail" value={formData.useremail} placeholder='이메일 형식에 맞게 작성하세요.'
                                onChange={handleChange} onBlur={validateEmail} required readOnly={queryParams == null} />
                        </div>
                        <div className='RegisterMemberErrorArea'>
                            {userEmailError && <span className="error">{userEmailError}</span>}
                            {userEmailSuccess && <span className="success">{userEmailSuccess}</span>}
                        </div>
                    </div>
                    <div className="RegisterMemberInputBox">
                        {queryParams.size == 0 && (<div className='RegisterMemberInputArea'>
                            <label htmlFor="password"><span className='InputStarSpan'>*</span> 비밀번호</label>
                            <input type={passwordVisible ? "text" : "password"} name="password" value={formData.password}
                                onChange={handleChange} onBlur={validatePasswordPatten} required placeholder='8~12자 영문,숫자,특수문자 혼합입니다.' />
                            <span className="password-toggle" onClick={togglePasswordVisibility}>
                                <FontAwesomeIcon className='EyeSlashIcon' icon={passwordVisible ? faEyeSlash : faEye} />
                            </span>
                        </div>)}
                        <div className='RegisterMemberErrorArea'>
                            {passwordError && <span className='error'>{passwordError}</span>}
                        </div>
                    </div>
                    <div className="RegisterMemberInputBox">
                        {queryParams.size == 0 && <div className='RegisterMemberInputArea'>
                            <label htmlFor="passwordCheck"><span className='InputStarSpan'>*</span> 비밀번호 확인</label>
                            <input type={passwordCheckVisible ? "text" : "password"} name="passwordCheck" value={formData.passwordCheck}
                                onChange={handleChange} onBlur={validatePasswordMatch} required />
                            <span className="password-toggle" onClick={togglePasswordCheckVisibility}>
                                <FontAwesomeIcon className='EyeSlashIcon' icon={passwordCheckVisible ? faEyeSlash : faEye} />
                            </span>
                        </div>}

                        <div className='RegisterMemberErrorArea'>
                            {passwordCheckError && <span className='error' style={{ width: "24%", maginRight: "5px" }}>{passwordCheckError}</span>}
                        </div>
                    </div>
                    <div className="RegisterMemberInputBox">
                        <div className='RegisterMemberInputArea'>
                            <label htmlFor="username"><span className='InputStarSpan'>*</span> 이름</label>
                            <input type="text" name="username" value={formData.username} onChange={handleChange} onBlur={validateUserNamePatten} required placeholder='2~7자 한글로 작성하세요.'
                                readOnly={queryParams == null} />
                        </div>
                        <div className='RegisterMemberErrorArea'>
                            {userNameError && <span className='error' style={{ width: "23%", marginRight: "5px" }}>{userNameError}</span>}
                        </div>
                    </div>
                    <div className="RegisterMemberInputBox">
                        <div className='RegisterMemberInputArea'>
                            <label htmlFor="usergender"><span className='InputStarSpan'>*</span> 성별</label>
                            <select name="usergender" value={formData.usergender} onChange={handleChange} required >
                                <option value="A">선택하세요 *</option>
                                <option value="M">Male</option>
                                <option value="F">Female</option>
                            </select>
                        </div>
                    </div>
                    <div className="RegisterMemberInputBox">
                        <div className='RegisterMemberInputArea'>
                            <label htmlFor="userbirthInput"><span className='InputStarSpan'>*</span> 생년월일</label>
                            <input type="date" id="userbirthInput" name="userbirth" value={formData.userbirth}
                                onChange={handleChange} required />
                        </div>
                    </div>
                    <div className="RegisterMemberInputBox">
                        <div className='RegisterMemberInputArea'>
                            <label htmlFor="userphoneInput"><span className='InputStarSpan'>*</span> 전화번호</label>
                            <input type="text" id="userphoneInput" name="userphone" value={formData.userphone} onChange={handleChange} required />
                            <button type="button" className='RegisterMemberPhoneCheckButton' onClick={handleButtonClick}>인증번호 전송</button>
                        </div>
                        <div className='RegisterMemberErrorArea'>
                            {userPhoneError && <span className="error">{userPhoneError}</span>}
                        </div>
                    </div>
                    <div className="RegisterMemberInputBox">
                        {isActive && <div className='RegisterMemberInputArea'>
                            <label htmlFor="phoneCheck"><span className='InputStarSpan'>*</span> 인증번호</label>
                            <input type="text" id="phoneCheck" name="phoneCheckCode" value={formData.phoneCheckCode} onChange={handleChange} />
                            <span className='phoneCheckTimer'>{formatTime(timeLeft)}</span>
                            <button type="button" className='RegisterMemberPhoneCheckButton' onClick={handleCodeCheck}>인증번호 확인</button>
                        </div>
                        }
                        <div className='RegisterMemberErrorArea'>
                            {phoneCheckSuccess && <span className="error" id='phoneCheckMessage'>{phoneCheckMessage}</span>}
                        </div>
                    </div>
                    <button type="submit" className="register-button" onClick={handleSubmit}>회원 가입</button>
                </form>
            </div>
        </div >
    );
};

export default RegisterMember;