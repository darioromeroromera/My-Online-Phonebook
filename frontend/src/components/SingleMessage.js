import { useEffect, useState } from "react";
import { Helmet } from "react-helmet";
import { useParams, useNavigate } from "react-router-dom";
import Header from "./Header";
import NavBar from "./NavBar";
import ProfilePicture from "./ProfilePicture";
import './css/SingleMessage.css';

const SingleMessage = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [message, setMessage] = useState(null);
    const [error, setError] = useState(null);

    const fetchMessage = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/messages/${id}`, {
                headers: {
                    Bearer: localStorage.getItem('token')
                },
                mode: 'cors'
            });

            const json = await response.json();

            if (json.result === undefined) {
                setError('Ha ocurrido un error desconocido. Inténtelo más tarde');
            } else if (json.result === 'error') {
                setError(json.details);
            } else {
                setMessage(json.data);
            }
        } catch (err) {
            setError('Error: no se ha podido establecer conexión con el servidor');
        }
    };

    useEffect(() => {
        fetchMessage();
    }, [id]);

    return (
        <div className="SingleMessage__Container">
            <Helmet>
                <title>Mensaje - My Online Phonebook</title>
            </Helmet>

            <Header />
            <NavBar />
            <ProfilePicture />

            <div className={error ? 'SingleMessage__Error' : 'SingleMessage__Hidden'}>
                <p>{error}</p>
            </div>
            {message ? (
                <div className="SingleMessage__Content">
                    <h1>{message.contact_name}</h1>
                    <p><strong>De:</strong> {message.origin_phone}</p>
                    <p><strong>Para:</strong> {message.destination_phone}</p>
                    <p><strong>Asunto:</strong> {message.subject}</p>
                    <p>{message.text}</p>
                </div>
            ) : ''}

            <div className="SingleMessage__ButtonContainer">
                {message && message.origin_phone != localStorage.getItem('telefono') ? 
                    <button className="SingleMessage__Button SingleMessage__Reply__Button" 
                        onClick={() => navigate(`/messages/${id}/reply`)}>Contestar mensaje</button>
                : ''}
                <button className="SingleMessage__Button" 
                    onClick={() => {navigate('/messages');}}>Volver</button>
                </div>
        </div>
    );
};

export default SingleMessage;
