import './App.css';
import Admin from '/src/pages/Admin';
import Member from '/src/pages/Member';
import { Routes, Route, Router } from 'react-router-dom';
import MemberIntro from '/src/pages/member/MemberIntro';
import RegisterMember from '/src/pages/member/RegisterMember';
import LoginMain from '/src/pages/member/login/LoginMain';
import FindbyId from '/src/pages/member/login/FindbyId';
import FindbyPw from '/src/pages/member/login/FindbyPw';
import { AuthProvider } from '/src/common/AuthContext';
import OAuth2RedirectHandler from './common/OAuth2RedirectHandler';
{/* 추가한 부분 */ }
function App() {
  return (
    <>
      <AuthProvider>
        <Routes>
          <Route path="/*" element={<Member />} />
          <Route path="/admin/*" element={<Admin />} />
          <Route path='/login' element={<LoginMain />} />
          <Route path='/signup' element={<MemberIntro />} />
          <Route path='/joinMember' element={<RegisterMember />} />
          {/* 추가한 부분 */}
          <Route path='/findbyid' element={<FindbyId />} />
          <Route path='/findbypw' element={<FindbyPw />} />
          <Route path='/oauth2/redirect' element={<OAuth2RedirectHandler />} />
        </Routes >
        {/* </Router> */}
      </AuthProvider>
    </>
  );
}

export default App;
