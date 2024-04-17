import { useEffect, useState } from "react";
import Header from "./Header";
import './css/RegistrationForm.css';

const RegistrationForm = () => {

    const [username, setUsername] = useState('');

    const [email, setEmail] = useState('');

    const [password, setPassword] = useState('');

    const [confirmPassword, setConfirmPassword] = useState('');

    const [errors, setErrors] = useState({username: "", email: "", password: "", confirmPassword: ""});

    const [usernameFlag, setUsernameFlag] = useState(false);

    const checkUsername = () => {
        setErrors(errors => {
            return username.trim() == '' ?  { ...errors, username: 'El campo nombre no puede estar vacío' } : { ...errors, username: '' };
        });
    }

    const checkEmail = () => {
        setErrors(errors => {
            if (email.trim() == '')
                return {...errors, email: 'El campo email no puede estar vacío'};
            else if (/asd/.test(email))
        })
    }

    useEffect(() => {
        usernameFlag ? checkUsername() : setUsernameFlag(true);
    }, [username]);

    return (
        <div className="RegistrationForm__Container">
            <Header/>

            <form className="RegistrationForm__Form">
                <p>Regístrate</p>
                <input className="RegistrationForm__Input" type="text" placeholder="Nombre de usuario" value={username}
                    onChange={e => {
                        setUsername(e.target.value);
                    }}/>
                <p>{errors.username}</p>
                <input className="RegistrationForm__Input" type="email" placeholder="Correo electrónico" value={email}
                    onChange={e => setEmail(e.target.value)}/>
                <p>{errors.email}</p>
                <input className="RegistrationForm__Input" type="password" placeholder="Contraseña" value={password}
                    onChange={e => setPassword(e.target.value)}/>
                <input className="RegistrationForm__Input" type="password" placeholder="Confirmar contraseña" value={confirmPassword}
                    onChange={e => setConfirmPassword(e.target.value)}/>

                <button className="RegistrationForm__Btn">Registrarse</button>
            </form>
        </div>
    );
};

export default RegistrationForm;