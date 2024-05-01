import { Helmet } from "react-helmet";
import { Link, useNavigate } from "react-router-dom";
import Header from "./Header";
import './css/Home.css';
import { useEffect, useState } from "react";
import ContactCard from "./ContactCard";

const Home = () => {
    const navigate = useNavigate();

    const mockUsername = localStorage.getItem('username');

    const [profilePicture, setProfilePicture] = useState(null);

    const getProfilePicture = async () => {
        try {
            const data = await fetch('http://localhost:8080/api/user/profile-picture', {
                headers: {
                    token: localStorage.getItem('token')
                }
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

    const [contacts, setContacts] = useState([]);

    const getContacts = async () => {
        if (loading)
            return;
        setLoading(true);
        try {
            const data = await fetch('http://localhost:8080/api/contacts', {
                headers: {
                    token: localStorage.getItem('token')
                }
            });
    
            const json = await data.json();

            if (json.result === undefined) {
                setContactError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                setIsContactErrorVisible(true);
            } else if (json.result === 'error') {
                setContactError(json.details);
                setIsContactErrorVisible(true);
            } else {
                setContacts(json.data);
                console.log(json.data);
            }

        } catch (err) {
            setContactError('Error: no se ha podido establecer conexión con el servidor');
            setIsContactErrorVisible(true);
        }
        setLoading(false);
        console.log('Fetch done. Loading: ' + loading);
    }

    useEffect(() => {
        getContacts();
    }, []);

    const [isContactsErrorVisible, setIsContactErrorVisible] = useState(false);

    const [contactError, setContactError] = useState('');

    const [loading, setLoading] = useState(false);

    const renderContacts = () => {
        if (loading) 
            return (
                <div className="Home__Spinner__Div">
                    <div className="Home__Spinner"></div>    
                </div>
            )
        else if (!isContactsErrorVisible) {
            return contacts.length > 0 ?
                <div className="Home__ContactList">
                {contacts.map(contact => (
                    <ContactCard key={contact.id} name={contact.contact_name} fullname={contact.full_name} 
                    phone={contact.telefono} details={contact.details} picture={contact.contact_picture}/>
                ))}
            </div> :
                <p>No contacts found.</p>
                
        }

    }

    
    return (
        <div className="Home__Container">
            <Helmet>
                <title>Inicio - My Online Phonebook</title>
            </Helmet>

            <Header/>

            <div className="Home__Profile">
                <h2 className="Home__Profile__Message">Bienvenido, {mockUsername}</h2>
                <div className="Home__Profile__ImgSet" onClick={() => navigate('/profile')}>
                    <img className="Home__Profile__ProfilePicture" src={profilePicture === null ? "empty-profile-logo.png" : profilePicture} alt="Foto de perfil"/>
                    <img className="Home__Profile__EditIcon" src="edit.png" alt="Icono de lápiz"/>
                </div>
                <div className={isProfileErrorVisible ? 'Home__Error' : 'Home__Hidden'}>
                    <p>{profileError}</p>
                </div>
            </div>

            <div>
                <input className="Home__Search__Input" type="text" placeholder="Busca contactos por nombre o número"/>
            </div>
            <div className="Home__ContactList__Container">
                <div className={isContactsErrorVisible ? 'Home__Error' : 'Home__Hidden'}>
                    <p>{contactError}</p>
                </div>
                {renderContacts()}
            </div>


            <button className="Home__Logout__Button" onClick={() => {
                localStorage.removeItem('token');
                localStorage.removeItem('username');
                localStorage.removeItem('email');
                navigate('/login');
            }}>Cerrar Sesión</button>
        </div>
    );
    
};

export default Home;
