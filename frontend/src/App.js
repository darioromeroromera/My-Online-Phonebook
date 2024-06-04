import logo from './logo.svg';
import './App.css';
import RegistrationForm from './components/RegistrationForm';
import { Route, Routes } from 'react-router-dom';
import Contacts from './components/Contacts';
import Home from './components/Home';
import PrivateRoute from './components/PrivateRoute';
import LoginForm from './components/LoginForm';
import NotFound from './components/NotFound';
import AddOrUpdateContact from './components/AddOrUpdateContact';
import Profile from './components/Profile';
import Groups from './components/Groups';

function App() {
  return (
    <>
      <Routes>
        <Route exact path='/' element={<PrivateRoute><Home/></PrivateRoute>}/>
        <Route path='/contacts' element={<PrivateRoute><Contacts/></PrivateRoute>}/>
        <Route path='/groups' element={<PrivateRoute><Groups/></PrivateRoute>}/>
        <Route path='/register' element={<RegistrationForm/>}/>
        <Route path='/login' element={<LoginForm/>}/>
        <Route path='/contacts/add' element={<AddOrUpdateContact isEdit={false}/>}/>
        <Route path='/contacts/edit/:id' element={<AddOrUpdateContact isEdit={true}/>}/>
        <Route path='/profile' element={<Profile/>}/>
        <Route path='*' element={<NotFound/>}/>
      </Routes>
    </>
  );
}

export default App;
