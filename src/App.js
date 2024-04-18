import logo from './logo.svg';
import './App.css';
import RegistrationForm from './components/RegistrationForm';
import { Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import PrivateRoute from './components/PrivateRoute';

function App() {
  return (
    <>
      <Routes>
        <Route exact path='/' element={<PrivateRoute><Home/></PrivateRoute>}/>
        <Route path='/register' element={<RegistrationForm/>}/>
        <Route path='*' element={<p>Not Found</p>}/>
      </Routes>
    </>
  );
}

export default App;
