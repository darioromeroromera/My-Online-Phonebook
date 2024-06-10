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
import Messages from './components/Messages';
import SingleMessage from './components/SingleMessage';
import ComposeAndReply from './components/ComposeAndReply';

function App() {
  

  return (
    <>
      <Routes>
        <Route exact path='/' element={<PrivateRoute><Home/></PrivateRoute>}/>
        <Route path='/contacts' element={<PrivateRoute><Contacts/></PrivateRoute>}/>
        <Route path='/groups' element={<PrivateRoute><Groups/></PrivateRoute>}/>
        <Route path='/messages' element={<PrivateRoute><Messages/></PrivateRoute>}/>
        <Route path='/messages/compose' element={<PrivateRoute><ComposeAndReply isReply={false}/></PrivateRoute>}/>
        <Route path='/messages/:id/reply' element={<PrivateRoute><ComposeAndReply isReply={true}/></PrivateRoute>}/>
        <Route path='/messages/:id' element={<PrivateRoute><SingleMessage/></PrivateRoute>}/>
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
