import { Helmet } from "react-helmet";
import { Link, useNavigate } from "react-router-dom";
import Header from "./Header";
import './css/HomeAndContacts.css';
import { useEffect, useState } from "react";
import NavBar from "./NavBar";

const Home = () => {
    const navigate = useNavigate();

    const username = localStorage.getItem('username');

    const [profilePicture, setProfilePicture] = useState(null);

    const getProfilePicture = async () => {
        try {
            const data = await fetch('http://localhost:8080/api/user/profile-picture', {
                headers: {
                    token: localStorage.getItem('token')
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
        <div className="Home__Container">
            <Helmet>
                <title>Inicio - My Online Phonebook</title>
            </Helmet>

            <Header/>

            <NavBar/>

            <div className="Home__Profile">
                {username && <h2 className="Home__Profile__Message">Bienvenido, {username}</h2>}
                <div className="Home__Profile__ImgSet" onClick={() => navigate('/profile')}>
                    <img className="Home__Profile__ProfilePicture" src={profilePicture === null ? "empty-profile-logo.png" : profilePicture} alt="Foto de perfil"/>
                    <img className="Home__Profile__EditIcon" src="edit.png" alt="Icono de lápiz"/>
                </div>
                <div className={isProfileErrorVisible ? 'Home__Error' : 'Home__Hidden'}>
                    <p>{profileError}</p>
                </div>
            </div>

            <button className="Home__Button" onClick={() => {
                localStorage.removeItem('token');
                localStorage.removeItem('username');
                localStorage.removeItem('email');
                localStorage.removeItem('id');
                navigate('/login');
            }}>Cerrar Sesión</button>
    
        </div>
    );
    
};

export default Home;
