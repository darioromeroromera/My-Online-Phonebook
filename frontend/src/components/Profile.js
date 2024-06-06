import React, { useState, useEffect } from "react";
import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import Header from "./Header";
import './css/Profile.css';
import NavBar from "./NavBar";

const Profile = () => {
    const navigate = useNavigate();

    const [loading, setLoading] = useState(false);
    const [profilePicture, setProfilePicture] = useState(null);
    const [profileRender, setProfileRender] = useState('');

    const [isErrorVisible, setIsErrorVisible] = useState(false);
    const [isErrorPasswordVisible, setIsErrorPasswordVisible] = useState(false);

    const [apiError, setApiError] = useState('');
    const [apiPasswordError, setApiPasswordError] = useState('');

    const [isSuccessVisible, setIsSuccessVisible] = useState(false);
    const [isSuccessPasswordVisible, setIsSuccessPasswordVisible] = useState(false);

    const [successMessage, setSuccessMessage] = useState('');
    const [successPasswordMessage, setSuccessPasswordMessage] = useState('');

    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    const [oldPasswordFlag, setOldPasswordFlag] = useState(false);
    const [newPasswordFlag, setNewPasswordFlag] = useState(false);
    const [confirmPasswordFlag, setConfirmPasswordFlag] = useState(false);

    const [errors, setErrors] = useState({oldPassword: '', newPassword: '', confirmPassword: ''});

    useEffect(() => {
        oldPasswordFlag ? checkOldPassword() : setOldPasswordFlag(true);
    }, [oldPassword]);

    useEffect(() => {
        newPasswordFlag ? checkNewPassword() : setNewPasswordFlag(true);
    }, [newPassword]);

    useEffect(() => {
        confirmPasswordFlag ? checkConfirmPassword() : setConfirmPasswordFlag(true);
    }, [confirmPassword]);

    const checkOldPassword = () => {
        setErrors(errors => {
            return oldPassword.trim() == '' ?  { ...errors, oldPassword: 'El campo contraseña antigua no puede estar vacío' } : { ...errors, oldPassword: '' };
        });
    };

    const checkNewPassword = () => {
        if (confirmPassword.trim() != '')
            checkConfirmPassword();
        setErrors(errors => {
            if (newPassword.trim() == '')
                return {...errors, newPassword: 'El campo contraseña nueva no puede estar vacío'};
            else if (newPassword.length < 8 || !/.*[a-z]+.*/.test(newPassword) || !/.*[A-Z]+.*/.test(newPassword) || !/.*\d+.*/.test(newPassword))
                return {...errors, newPassword:  'El password debe tener al menos 8 caracteres, minúsculas, mayúsculas y números'};
            else
                return {...errors, newPassword: ''};
        });
    };

    const checkConfirmPassword = () => {
        setErrors(errors => {
            if (confirmPassword.trim() == '')
                return {...errors, confirmPassword: 'El campo de confirmar contraseña no puede estar vacío'};
            else if (newPassword.trim() != '' && confirmPassword != newPassword)
                return {...errors, confirmPassword: 'Las contraseñas no coinciden'};
            else
                return {...errors, confirmPassword: ''};
        });
    };

    const checkVisibility = field => {
        return errors[field] == '' ? 'Profile__Hidden' : 'Profile__FormInputError';
    };

    const changePassword = async () => {
        if (loading)
            return;
        setLoading(true);
        try {
            const data = await fetch('http://localhost:8080/api/user/change-password', {
                headers: {
                    'Content-Type': 'application/json',
                    Bearer: localStorage.getItem('token')
                },
                method: 'PUT',
                body: JSON.stringify({'old_password': oldPassword, 'new_password': newPassword})
            });
    
            const json = await data.json();
    
            if (json.result == undefined) {
                setApiPasswordError('Ha ocurrido un error descoocido. Inténtelo más tarde');
                setIsErrorPasswordVisible(true);
            } else if (json.result === 'error') {
                setApiPasswordError(json.details);
                setIsErrorPasswordVisible(true);
            } else {
                setIsErrorPasswordVisible(false);
                setSuccessPasswordMessage('¡Contraseña cambiada correctamente!');
                setIsSuccessPasswordVisible(true);
            }
            setLoading(false);
        } catch (error) {
            setLoading(false);
            setApiPasswordError(error);
            setIsErrorPasswordVisible(true);
        }
    };

    useEffect(() => {
        fetchProfilePicture();
    }, []);

    const fetchProfilePicture = async () => {
        if (loading)
            return;
        setLoading(true);
        try {
            const data = await fetch('http://localhost:8080/api/user/profile-picture', {
                headers: {
                    Bearer: localStorage.getItem('token')
                }
            });

            const json = await data.json();
            if (json.result === undefined) {
                setApiError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                setIsErrorVisible(true);
            } else if (json.result === 'error') {
                setApiError(json.details);
                setIsErrorVisible(true);
            } else {
                if (json.picture == null) {
                    setProfileRender('empty-profile-logo.png');
                } else {
                    setProfileRender(json.picture);
                }
            }
            setLoading(false);
        } catch (error) {
            setLoading(false);
            setApiError('Error al obtener la imagen de perfil. Inténtelo más tarde.');
            setIsErrorVisible(true);
        }
    };

    const readFileAsBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
    
            reader.onload = () => {
                resolve(`data:${file.type};base64,${reader.result.split(',')[1]}`);
            };
    
            reader.onerror = () => {
                reject(reader.error);
            };

            reader.readAsDataURL(file);
        });
    };

    const handleProfilePictureUpload = async (event) => {
        if (loading)
            return;
        const file = event.target.files[0];
        if (!file) return;
    
        setLoading(true);
    
        try {
            const imageData = await readFileAsBase64(file);
            const response = await fetch('http://localhost:8080/api/user/profile-picture', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Bearer: localStorage.getItem('token')
                },
                body: JSON.stringify({ profile_picture: imageData })
            });
    
            const json = await response.json();
            if (json.result === undefined) {
                setApiError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                setIsErrorVisible(true);
            } else if (json.result === 'error') {
                setApiError(json.details);
                setIsErrorVisible(true);
            } else {
                setProfilePicture(file);
                setProfileRender(URL.createObjectURL(file));
                setIsSuccessVisible(true);
                setSuccessMessage('¡Foto de perfil actualizada satisfactoriamente!');
            }
            setLoading(false);
        } catch (error) {
            setLoading(false);
            setApiError('Error al actualizar la imagen de perfil. Inténtelo más tarde.');
            setIsErrorVisible(true);
        }
    };
    
    
    const deleteProfilePicture = async () => {
        if (loading)
            return;
        setLoading(true);
        try {
            const data = await fetch('http://localhost:8080/api/user/profile-picture', {
                method: 'DELETE',
                headers: {
                    Bearer: localStorage.getItem('token')
                }
            });

            const json = await data.json();
            if (json.result == undefined) {
                setIsSuccessVisible(false);
                setApiError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                setIsErrorVisible(true);
            } else if (json.result == 'error') {
                setApiError(json.details);
                setIsSuccessVisible(false);
                setIsErrorVisible(true);
            } else {
                setIsErrorVisible(false);
                setProfilePicture(null);
                setProfileRender("empty-profile-logo.png");
                setIsSuccessVisible(true);
                setSuccessMessage('¡Foto de perfil borrada satisfactoriamente!');
            }
            setLoading(false);
        } catch (error) {
            alert(error);
            setLoading(false);
            setApiError('Error al borrar la imagen de perfil. Inténtelo más tarde.');
            setIsErrorVisible(true);
        }
    };

    return (
        <div className="Profile__Container">
            <Helmet>
                <title>Perfil - My Online Phonebook</title>
            </Helmet>

            <Header />

            <NavBar/>

            <div className="Profile__Content">
                <div className="Profile__Section">
                    <h2>Foto de perfil</h2>
                    <div className="Profile__ImageContainer">
                        <img className="Profile__Image" src={profileRender} alt="Foto de perfil" />
                        <div className="Profile__FileInputContainer Profile__Button" >
                            <span htmlFor="profilePictureInput">Cambiar foto de perfil</span>
                            <input id="profilePictureInput" className="Profile__FileInput" type="file" onChange={handleProfilePictureUpload} />
                        </div>

                    </div>
                    <div className="Profile__ButtonGroup">
                        <button className="Profile__Button Profile__ButtonDelete" onClick={deleteProfilePicture}>Borrar foto de perfil</button>
                    </div>
                    {isErrorVisible && <p className="Profile__Error">{apiError}</p>}
                    {isSuccessVisible && <p className="Profile__Success">{successMessage}</p>}
                </div>
                <div className="Profile__Section">
                    <h2>Cambiar contraseña</h2>
                    <form className="Profile__PasswordForm" onSubmit={e => {
                        e.preventDefault();
                        checkOldPassword();
                        checkNewPassword();
                        checkConfirmPassword();

                        if (errors.oldPassword === '' && errors.newPassword === '' && errors.confirmPassword === '')
                            changePassword();
                    }}>
                        <label className="Profile__FormLabel">Contraseña Antigua</label>
                        <input className="Profile__FormInput" type="password" value={oldPassword}  placeholder="Contraseña Antigua" required
                            onChange={e => setOldPassword(e.target.value)} />
                        <p className={checkVisibility('oldPassword')}>{errors.oldPassword}</p>
                        <label className="Profile__FormLabel">Contraseña Nueva</label>
                        <input className="Profile__FormInput" type="password" placeholder="Contraseña Nueva" value={newPassword} required
                            onChange={e => setNewPassword(e.target.value)}/>
                        <p className={checkVisibility('newPassword')}>{errors.newPassword}</p>
                        <label className="Profile__FormLabel">Confirmar Contraseña</label>
                        <input className="Profile__FormInput" type="password" placeholder="Confirmar Contraseña" value={confirmPassword} required
                            onChange={e => setConfirmPassword(e.target.value)} />
                        <p className={checkVisibility('confirmPassword')}>{errors.confirmPassword}</p>
                        <button className="Profile__Button Profile__ButtonSubmit" type="submit">Cambiar contraseña</button>
                    </form>
                    {isErrorPasswordVisible && <p className="Profile__Error">{apiPasswordError}</p>}
                    {isSuccessPasswordVisible && <p className="Profile__Success">{successPasswordMessage}</p>}
                </div>
            </div>
            {loading && 
                <div className='Profile__SpinnerDiv'>
                    <div className='Profile__Spinner'></div>    
                </div>}

            <button className="Profile__Button Profile__ButtonBack" onClick={() => {
                localStorage.removeItem('token');
                localStorage.removeItem('username');
                localStorage.removeItem('email');
                localStorage.removeItem('id');
                navigate('/login');
            }}>Cerrar Sesión</button>
        </div>
    );
};

export default Profile;
