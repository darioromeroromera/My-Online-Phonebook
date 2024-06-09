import './css/ProfilePicture.css';

import { useEffect, useState } from "react";

import { useNavigate } from "react-router-dom";

const ProfilePicture = () => {

    const navigate = useNavigate();

    const username = localStorage.getItem('username');

    const [profilePicture, setProfilePicture] = useState(null);

    const getProfilePicture = async () => {
        try {
            const data = await fetch('http://localhost:8080/api/user/profile-picture', {
                headers: {
                    Bearer: localStorage.getItem('token')
                },
                mode: 'cors'
            });

            const json = await data.json();

            if (json.result === undefined) {
                setProfileError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                setIsProfileErrorVisible(true);
            } else if (json.result === 'error') {
                setProfileError(json.details);
                setIsProfileErrorVisible(true);
            } else {
                setIsProfileErrorVisible(false);
                setProfileError('');
                setProfilePicture(json.picture);
            }

        } catch (err) {
            setProfileError('Error: no se ha podido establecer conexión con el servidor');
            setIsProfileErrorVisible(true);
        }
    }

    useEffect(() => {
        getProfilePicture();
    }, []);

    const [isProfileErrorVisible, setIsProfileErrorVisible] = useState(false);

    const [profileError, setProfileError] = useState('');

    return (
        <div className="ProfilePicture">
            {username && <h2 className="ProfilePicture__Message">Bienvenido, {username}</h2>}
            <div className="ProfilePicture__ImgSet" onClick={() => navigate('/profile')}>
                    <img className="ProfilePicture__Picture" src={profilePicture === null ? "http://localhost:3000/empty-profile-logo.png" : profilePicture} alt="Foto de perfil"/>
                    <img className="ProfilePicture__EditIcon" src="http://localhost:3000/edit.png" alt="Icono de lápiz"/>
            </div>
            <div className={isProfileErrorVisible ? 'ProfilePicture__Error' : 'ProfilePicture__Hidden'}>
                <p>{profileError}</p>
            </div>
        </div>
    );
};

export default ProfilePicture;