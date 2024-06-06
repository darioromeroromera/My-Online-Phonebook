import { useEffect, useState } from "react";
import Header from "./Header";
import './css/RegistrationAndLoginForm.css';
import { Helmet } from "react-helmet";
import { Link, useNavigate } from "react-router-dom";

const RegistrationForm = () => {

    const [username, setUsername] = useState('');

    const [email, setEmail] = useState('');

    const [password, setPassword] = useState('');

    const [confirmPassword, setConfirmPassword] = useState('');

    const [errors, setErrors] = useState({username: "", email: "", phoneNumber: ".", password: "", confirmPassword: ""});

    const [usernameFlag, setUsernameFlag] = useState(false);

    const [emailFlag, setEmailFlag] = useState(false);

    const [phoneNumber, setPhoneNumber] = useState('');

    const [phoneNumberFlag, setPhoneNumberFlag] = useState(false);

    const [passwordFlag, setPasswordFlag] = useState(false);

    const [confirmPasswordFlag, setConfirmPasswordFlag] = useState(false);

    const [isErrorVisible, setIsErrorVisible] = useState(false);

    const [apiError, setApiError] = useState('');

    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const checkUsername = () => {
        setErrors(errors => {
            return username.trim() == '' ?  { ...errors, username: 'El campo nombre no puede estar vacío' } : { ...errors, username: '' };
        });
    };

    const checkEmail = () => {
        setErrors(errors => {
            if (email.trim() == '')
                return {...errors, email: 'El campo email no puede estar vacío'};
            else if (!/[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)?\.[A-Za-z]{2,6}/.test(email))
                return {...errors, email: 'El campo email no tiene el formato correcto'};
            else
                return {...errors, email: ''};
        });
    };

    const checkPhoneNumber = () => {
        setErrors(errors => {
            return phoneNumber.trim() == '' ?  { ...errors, phoneNumber: 'El campo telefono no puede estar vacío' } : /^([679])\d{8}$/.test(phoneNumber) ? { ...errors, phoneNumber: '' } : { ...errors, phoneNumber: 'Formato de teléfono incorrecto'};
        });
    }

    const checkPassword = () => {
        if (confirmPassword.trim() != '')
            checkConfirmPassword();
        setErrors(errors => {
            if (password.trim() == '')
                return {...errors, password: 'El campo password no puede estar vacío'};
            else if (password.length < 8 || !/.*[a-z]+.*/.test(password) || !/.*[A-Z]+.*/.test(password) || !/.*\d+.*/.test(password))
                return {...errors, password:  'El password debe tener al menos 8 caracteres, minúsculas, mayúsculas y números'};
            else
                return {...errors, password: ''};
        });
    };

    const checkConfirmPassword = () => {
        setErrors(errors => {
            if (confirmPassword.trim() == '')
                return {...errors, confirmPassword: 'El campo de confirmar contraseña no puede estar vacío'};
            else if (password.trim() != '' && confirmPassword != password)
                return {...errors, confirmPassword: 'Las contraseñas no coinciden'};
            else
                return {...errors, confirmPassword: ''};
        });
    };

    useEffect(() => {
        usernameFlag ? checkUsername() : setUsernameFlag(true);
    }, [username]);

    useEffect(() => {
        emailFlag ? checkEmail() : setEmailFlag(true);
    }, [email]);

    useEffect(() => {
        phoneNumberFlag ? checkPhoneNumber() : setPhoneNumberFlag(true);
    }, [phoneNumber]);

    useEffect(() => {
        passwordFlag ? checkPassword() : setPasswordFlag(true);
    }, [password]);

    useEffect(() => {
        confirmPasswordFlag ? checkConfirmPassword() : setConfirmPasswordFlag(true);
    }, [confirmPassword]);

    const register = async () => {
        setLoading(true);
        try {
            const res = await fetch('http://localhost:8080/api/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                mode: 'cors',
                body: JSON.stringify({username, email, 'telefono': phoneNumber, password})
            })
    
            const json = await res.json();
            
            if (json.result === undefined) {
                setApiError("Ha ocurrido un error desconocido. Inténtelo más tarde");
                setIsErrorVisible(true);
            } else if (json.result === 'error') {
                setApiError(json.details);
                setIsErrorVisible(true);
            } else {
                navigate('/login');
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
                <title>Registrarse - My Online Phonebook</title>
            </Helmet>

            <Header/>

            {loading && 
                <div className="RegistrationAndLoginForm__SpinnerDiv">
                    <div className="RegistrationAndLoginForm__Spinner"></div>    
                </div>}

            <form className="RegistrationAndLoginForm__Form" onSubmit={e => {
                e.preventDefault();
                checkUsername();
                checkEmail();
                checkPassword();
                checkConfirmPassword();

                if (errors.username == '' && errors.email == '' && errors.password == '' && errors.confirmPassword == '')
                    register();
            }}>
                <p>Regístrate</p>
                <input className="RegistrationAndLoginForm__Input" type="text" placeholder="Nombre de usuario" value={username}
                    onChange={e => {
                        setUsername(e.target.value);
                    }} required/>
                <p className="RegistrationAndLoginForm__InputError">{errors.username}</p>
                <input className="RegistrationAndLoginForm__Input" type="email" placeholder="Correo electrónico" value={email}
                    onChange={e => setEmail(e.target.value)} required/>
                <p className="RegistrationAndLoginForm__InputError">{errors.email}</p>
                <input className="RegistrationAndLoginForm__Input" type="tel" placeholder="Teléfono (ej. 612345678)" value={phoneNumber}
                    onChange={e => setPhoneNumber(e.target.value)} required
                    />
                <p className="RegistrationAndLoginForm__InputError">{errors.phoneNumber}</p>
                <input className="RegistrationAndLoginForm__Input" type="password" placeholder="Contraseña" value={password}
                    onChange={e => setPassword(e.target.value)} required/>
                <p className="RegistrationAndLoginForm__InputError">{errors.password}</p>
                <input className="RegistrationAndLoginForm__Input" type="password" placeholder="Confirmar contraseña" value={confirmPassword}
                    onChange={e => setConfirmPassword(e.target.value)} required />
                <p className="RegistrationAndLoginForm__InputError">{errors.confirmPassword}</p>

                <p>¿Ya tienes una cuenta? <Link to='/login'>Iniciar Sesión</Link></p>

                <button className="RegistrationAndLoginForm__Btn">Registrarse</button>

                <div className={isErrorVisible ? 'RegistrationAndLoginForm__Error' : 'RegistrationAndLoginForm_Hidden'}>
                    <p>{apiError}</p>
                </div>
            </form>
        </div>
    );
};

export default RegistrationForm ;