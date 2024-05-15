import React, { useState, useEffect } from "react";
import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import Header from "./Header";
import './css/Profile.css';

const Profile = () => {
    const navigate = useNavigate();

    // State variables and functions for profile management
    const [loading, setLoading] = useState(false);
    const [profilePicture, setProfilePicture] = useState(null);
    const [profileRender, setProfileRender] = useState('');
    const [isErrorVisible, setIsErrorVisible] = useState(false);
    const [apiError, setApiError] = useState('');
    const [isSuccessVisible, setIsSuccessVisible] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');

    useEffect(() => {
        fetchProfilePicture();
    }, []);

    // Function to fetch user's profile picture
    const fetchProfilePicture = async () => {
        setLoading(true);
        try {
            const data = await fetch('http://localhost:8080/api/user/profile-picture', {
                headers: {
                    token: localStorage.getItem('token')
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

    // Function to handle profile picture upload
    const handleProfilePictureUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;
    
        setLoading(true);
    
        try {
            const imageData = await readFileAsBase64(file);
            const response = await fetch('http://localhost:8080/api/user/profile-picture', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    token: localStorage.getItem('token')
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
        setLoading(true);
        try {
            const data = await fetch('http://localhost:8080/api/user/profile-picture', {
                method: 'DELETE',
                headers: {
                    token: localStorage.getItem('token')
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

            <div className="Profile__Content">
                <div className="Profile__Section">
                    <h2>Foto de perfil</h2>
                    <div className="Profile__ImageContainer">
                        <img className="Profile__Image" src={profileRender} alt="Foto de perfil" />
                        <div className="Profile__FileInputContainer Profile__Button" >
                            <span for="profilePictureInput">Cambiar foto de perfil</span>
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
                    <form className="Profile__PasswordForm">
                        <label className="Profile__FormLabel">Antigua Contraseña</label>
                        <input className="Profile__FormInput" type="password" placeholder="Antigua Contraseña" />
                        <label className="Profile__FormLabel">Nueva Contraseña</label>
                        <input className="Profile__FormInput" type="password" placeholder="Nueva Contraseña" />
                        <label className="Profile__FormLabel">Confirmar Contraseña</label>
                        <input className="Profile__FormInput" type="password" placeholder="Confirmar Contraseña" />
                        <button className="Profile__Button Profile__ButtonSubmit" type="submit">Cambiar contraseña</button>
                    </form>
                </div>
            </div>
            <button className="Profile__Button Profile__ButtonBack" onClick={() => navigate('/')}>Volver</button>
            {loading && 
                <div className='Profile__SpinnerDiv'>
                    <div className='Profile__Spinner'></div>    
                </div>}
        </div>
    );
};

export default Profile;
