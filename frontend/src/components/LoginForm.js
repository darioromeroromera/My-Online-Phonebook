import {useState, useEffect} from 'react';

import {Helmet} from 'react-helmet';

import Header from './Header';
import { Link, useNavigate } from 'react-router-dom';

import { jwtDecode} from 'jwt-decode';

const LoginForm = () => {
    const [username, setUsername] = useState('');

    const [password, setPassword] = useState('');

    const [errors, setErrors] = useState({username: "", password: ""});

    const [usernameFlag, setUsernameFlag] = useState(false);

    const [passwordFlag, setPasswordFlag] = useState(false);

    const [isErrorVisible, setIsErrorVisible] = useState(false);

    const [apiError, setApiError] = useState('');

    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const checkUsername = () => {
        setErrors(errors => {
            return username.trim() == '' ?  { ...errors, username: 'El campo nombre no puede estar vacío' } : { ...errors, username: '' };
        });
    };

    const checkPassword = () => {
        setErrors(errors => {
            if (password.trim() == '')
                return {...errors, password: 'El campo password no puede estar vacío'};
            else
                return {...errors, password: ''};
        });
    };

    useEffect(() => {
        usernameFlag ? checkUsername() : setUsernameFlag(true);
    }, [username]);

    useEffect(() => {
        passwordFlag ? checkPassword() : setPasswordFlag(true);
    }, [password]);

    const login = async () => {
        if (loading)
            return;
        setLoading(true)
        try {
            const res = await fetch('http://localhost:8080/api/auth', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                mode: 'cors',
                body: JSON.stringify({username, password})
            })
    
            const json = await res.json();
            
            if (json.result === undefined) {
                setApiError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                setIsErrorVisible(true);
            } else if (json.result === 'error') {
                setApiError(json.details);
                setIsErrorVisible(true);
            } else {
                localStorage.setItem('token', json.token);
                localStorage.setItem('id', jwtDecode(json.token).id);
                localStorage.setItem('username', jwtDecode(json.token).username);
                localStorage.setItem('email', jwtDecode(json.token).email);
                localStorage.setItem('telefono', jwtDecode(json.token).telefono);
                navigate('/');
            }
        } catch (err) {
            setApiError('Error: no se ha podido establecer conexión con el servidor');
            setIsErrorVisible(true);
        }
        setLoading(false);
    };

    return (
        <div className="RegistrationAndLoginForm__Container">

            <Helmet>
                <title>Iniciar Sesión - My Online Phonebook</title>
            </Helmet>

            <Header/>

            {loading && 
                <div className='RegistrationAndLoginForm__SpinnerDiv'>
                    <div className='RegistrationAndLoginForm__Spinner'></div>    
                </div>}

            <form className="RegistrationAndLoginForm__Form" onSubmit={e => {
                e.preventDefault();
                checkUsername();
                checkPassword();

                if (errors.username == '' && errors.password == '')
                    login();
            }}>
                <p>Inicia Sesión</p>
                <input className="RegistrationAndLoginForm__Input" type="text" placeholder="Nombre de usuario" value={username}
                    onChange={e => {
                        setUsername(e.target.value);
                    }} required/>
                <p className="RegistrationAndLoginForm__InputError">{errors.username}</p>
                <input className="RegistrationAndLoginForm__Input" type="password" placeholder="Contraseña" value={password}
                    onChange={e => setPassword(e.target.value)} required/>
                <p className="RegistrationAndLoginForm__InputError">{errors.password}</p>

                <p>¿No dispone de cuenta? <Link to='/register'>Regístrate</Link></p>

                <button className="RegistrationAndLoginForm__Btn">Iniciar Sesión</button>

                <div className={isErrorVisible ? 'RegistrationAndLoginForm__Error' : 'RegistrationAndLoginForm_Hidden'}>
                    <p>{apiError}</p>
                </div>
            </form>
        </div>
    );
};

export default LoginForm;