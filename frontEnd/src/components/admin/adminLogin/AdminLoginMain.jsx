import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '/src/css/admin/common/AdminLoginMain.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleUser, faKey, faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { useAuth } from '/src/common/AuthContext';

const AdminLoginMain = () => {
    const nav = useNavigate();
    const { login, isAuthenticated, role } = useAuth();
    const [useremail, setUserEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [message, setMessage] = useState('');

    useEffect(() => {
        if (isAuthenticated && role === 'ROLE_ADMIN') {
            nav('/admin/dashboard', { replace: true });
        }
        if (isAuthenticated && role === 'ROLE_MEMBER') {
            nav('/', { replace: true })
        }
    }, [isAuthenticated])

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await login(useremail, password);
            if (response.status === 200) {
                nav('/admin/dashboard');
            }
        } catch (error) {
            setMessage('아이디 비밀번호를 확인해 주세요');
            setUserEmail('');
            setPassword('');
        }
    };

    const toggleShowPassword = () => {
        setShowPassword(!showPassword);
    };

    return (
        <div className='LoginMainWrap'>
            <div className="LoginMain">
                <div className="logo-container">
                    <img src="/assets/images/headerLogo.png" alt="어울림" onClick={() => { nav('/') }} />
                </div>
                <form className="login-form">
                    <div className="LoginMainInput-group">
                        <label htmlFor="username" style={{ width: "40px" }}>
                            <FontAwesomeIcon icon={faCircleUser} style={{ fontSize: "20px" }} />
                        </label>
                        <input type="text" id="username" onChange={(e) => setUserEmail(e.target.value)} placeholder="아이디" required />
                    </div>
                    <div className="LoginMainInput-group">
                        <label htmlFor="password" style={{ width: "40px" }}>
                            <FontAwesomeIcon icon={faKey} style={{ fontSize: "20px" }} />
                        </label>
                        <input type={showPassword ? "text" : "password"} id="password" value={password}
                            onChange={(e) => setPassword(e.target.value)} required placeholder="비밀번호" />
                        <span className="password-toggle" onClick={() => { toggleShowPassword() }}>
                            <FontAwesomeIcon icon={showPassword ? faEyeSlash : faEye} style={{ fontSize: "18px", cursor: "pointer" }} />
                        </span>
                    </div>
                    <div className='LoginMainCheckMessageArea'>
                        <span className='LoginMainCheckMessage'>{message}</span>
                    </div>
                    <button type="submit" className="login-button" onClick={handleSubmit}>로그인</button>
                </form>
            </div>
        </div>
    );
};

export default AdminLoginMain;
