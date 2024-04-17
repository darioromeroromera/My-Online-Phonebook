import { useEffect, useState } from "react";
import Header from "./Header";
import './css/RegistrationForm.css';
import { Helmet } from "react-helmet";

const RegistrationForm = () => {

    const [username, setUsername] = useState('');

    const [email, setEmail] = useState('');

    const [password, setPassword] = useState('');

    const [confirmPassword, setConfirmPassword] = useState('');

    const [errors, setErrors] = useState({username: "", email: "", password: "", confirmPassword: ""});

    const [usernameFlag, setUsernameFlag] = useState(false);

    const [emailFlag, setEmailFlag] = useState(false);

    const [passwordFlag, setPasswordFlag] = useState(false);

    const [confirmPasswordFlag, setConfirmPasswordFlag] = useState(false);

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

    const checkPassword = () => {
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
        setErrors(errros => {
            if (confirmPassword.trim() == '')
                return {...errors, confirmPassword: 'El campo de confirmar contraseña no puede estar vacío'};
            else if (password.trim() != '' && confirmPassword != password)
                return {...errors, confirmPassword: 'Las contraseñas no coinciden'};
            else
                return {...errors, confirmPassword: ''};
        })
    };

    useEffect(() => {
        usernameFlag ? checkUsername() : setUsernameFlag(true);
    }, [username]);

    useEffect(() => {
        emailFlag ? checkEmail() : setEmailFlag(true);
    }, [email]);

    useEffect(() => {
        passwordFlag ? checkPassword() : setPasswordFlag(true);
    }, [password]);

    useEffect(() => {
        confirmPasswordFlag ? checkConfirmPassword() : setConfirmPasswordFlag(true);
    }, [confirmPassword]);

    const register = () => {
        alert(JSON.stringify({username, email, password}));
        const res = fetch('http://192.168.1.133:8080/api/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            mode: 'cors',
            body: JSON.stringify({username, email, password})
        });
    };

    return (
        <div className="RegistrationForm__Container">

            <Helmet>
                <title>Registrarse</title>
            </Helmet>

            <Header/>

            <form className="RegistrationForm__Form" onSubmit={e => {
                e.preventDefault();
                checkUsername();
                checkEmail();
                checkPassword();
                checkConfirmPassword();

                if (errors.username == '' && errors.email == '' && errors.password == '' && errors.confirmPassword == '')
                    register();
            }}>
                <p>Regístrate</p>
                <input className="RegistrationForm__Input" type="text" placeholder="Nombre de usuario" value={username}
                    onChange={e => {
                        setUsername(e.target.value);
                    }}/>
                <p className="RegistrationForm__Error">{errors.username}</p>
                <input className="RegistrationForm__Input" type="email" placeholder="Correo electrónico" value={email}
                    onChange={e => setEmail(e.target.value)}/>
                <p className="RegistrationForm__Error">{errors.email}</p>
                <input className="RegistrationForm__Input" type="password" placeholder="Contraseña" value={password}
                    onChange={e => setPassword(e.target.value)}/>
                <p className="RegistrationForm__Error">{errors.password}</p>
                <input className="RegistrationForm__Input" type="password" placeholder="Confirmar contraseña" value={confirmPassword}
                    onChange={e => setConfirmPassword(e.target.value)}/>
                <p className="RegistrationForm__Error">{errors.confirmPassword}</p>

                <button className="RegistrationForm__Btn">Registrarse</button>
            </form>
        </div>
    );
};

export default RegistrationForm;