import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import Header from "./Header";
import './css/HomeAndContacts.css';
import { useEffect, useState } from "react";
import NavBar from "./NavBar";
import ProfilePicture from "./ProfilePicture";

const Home = () => {
    const navigate = useNavigate();

    const [contactNumber, setContactNumber] = useState(0);

    const [groupNumber, setGroupNumber] = useState(0);

    const getStatistics = async () => {
        try {
            const data = await fetch('http://localhost:8080/api/user/statistics', {
                headers: {
                    token: localStorage.getItem('token')
                },
                mode: 'cors'
            });
    
            const json = await data.json();

            if (json.result === undefined) {
                setStaticsError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                SetIsStatisticsErrorVisible(true);
            } else if (json.result === 'error') {
                setStaticsError(json.details);
                SetIsStatisticsErrorVisible(true);
            } else {
                SetIsStatisticsErrorVisible(false);
                setStaticsError('');
                setContactNumber(json.contact_number);
                setGroupNumber(json.group_number);
            }

        } catch (err) {
            setStaticsError('Error: no se ha podido establecer conexión con el servidor');
            SetIsStatisticsErrorVisible(true);
        }
    }



    useEffect(() => {
        getStatistics();
    }, []);

    const [isStatisticsErrorVisible, SetIsStatisticsErrorVisible] = useState(false);

    const [statisticsError, setStaticsError] = useState('');

    
    return (
        <div className="Home__Container">
            <Helmet>
                <title>Inicio - My Online Phonebook</title>
            </Helmet>

            <Header/>

            <NavBar/>

            <ProfilePicture/>

            <div className="Home__Statistics">
                <h2>Estadísticas:</h2>
                <p>Contactos: {contactNumber}</p>
                <p>Grupos de contactos: {groupNumber}</p>
                <div className={isStatisticsErrorVisible ? 'Home__Error' : 'Home__Hidden'}>
                    <p>{statisticsError}</p>
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
