import logo from './logo.svg';
import './App.css';
import RegistrationForm from './components/RegistrationForm';
import { Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import PrivateRoute from './components/PrivateRoute';
import LoginForm from './components/LoginForm';
import NotFound from './components/NotFound';
import AddOrUpdateContact from './components/AddOrUpdateContact';
import Profile from './components/Profile';

function App() {
  return (
    <>
      <Routes>
        <Route exact path='/' element={<PrivateRoute><Home/></PrivateRoute>}/>
        <Route path='/register' element={<RegistrationForm/>}/>
        <Route path='/login' element={<LoginForm/>}/>
        <Route path='/add' element={<AddOrUpdateContact isEdit={false}/>}/>
        <Route path='/edit/:id' element={<AddOrUpdateContact isEdit={true}/>}/>
        <Route path='/profile' element={<Profile/>}/>
        <Route path='*' element={<NotFound/>}/>
      </Routes>
    </>
  );
}

export default App;
