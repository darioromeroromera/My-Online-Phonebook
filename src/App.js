import logo from './logo.svg';
import './App.css';
import RegistrationForm from './components/RegistrationForm';
import { Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import PrivateRoute from './components/PrivateRoute';
import LoginForm from './components/LoginForm';
import NotFound from './components/NotFound';

function App() {
  return (
    <>
      <Routes>
        <Route exact path='/' element={<PrivateRoute><Home/></PrivateRoute>}/>
        <Route path='/register' element={<RegistrationForm/>}/>
        <Route path='/login' element={<LoginForm/>}/>
        <Route path='*' element={<NotFound/>}/>
      </Routes>
    </>
  );
}

export default App;
