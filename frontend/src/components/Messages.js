import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import Header from "./Header";
import './css/HomeAndContacts.css';
import './css/Messages.css';
import { useEffect, useState } from "react";
import NavBar from "./NavBar";
import ProfilePicture from "./ProfilePicture";

const Messages = () => {
    const navigate = useNavigate();

    const [receivedMessages, setReceivedMessages] = useState([]);
    const [sentMessages, setSentMessages] = useState([]);
    const [isReceived, setIsReceived] = useState(true);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const getMessages = async () => {
        setLoading(true);
        try {
            const data = await fetch('http://localhost:8080/api/messages', {
                headers: {
                    Bearer: localStorage.getItem('token')
                },
                mode: 'cors'
            });
            const json = await data.json();

            console.log(json);

            if (json.result === undefined) {
                setError('Ha ocurrido un error desconocido. Inténtelo más tarde');
            } else if (json.result === 'error') {
                setError(json.details);
            } else {
                setReceivedMessages(json.messages_received);
                setSentMessages(json.messages_sent);
            }
            
        } catch (err) {
            setError('Error: no se ha podido establecer conexión con el servidor');
        }
        setLoading(false);
    }

    useEffect(() => {
        getMessages();
    }, []);

    const renderMessages = () => {
        if (isReceived) {
            return (
                <div className="Messages__List">
                    {receivedMessages.length > 0 ? 
                        receivedMessages.map(message => (
                            <div className="Message__Card" key={message.id} onClick={() => navigate(`/messages/${message.id}`)}>
                                <div className="Message__Details">
                                    <span className="Message__Sender">{message.contact_name}</span>
                                    <span className="Message__Subject">{message.subject}</span>
                                </div>
                            </div>
                        ))
                    : <p>No hay mensajes</p>}
                </div>
            ); 
        } else {
            return (
                <div className="Messages__List">
                    {sentMessages.length > 0 ? 
                        sentMessages.map(message => (
                            <div className="Message__Card" key={message.id} onClick={() => navigate(`/messages/${message.id}`)}>
                                <div className="Message__Details">
                                    <span className="Message__ContactName">{message.contact_name}</span>
                                    <span className="Message__Subject">{message.subject}</span>
                                </div>
                            </div>
                        ))
                    : <p>No hay mensajes</p>}
                </div>
            );
        }
    }

    return (
        <div className="Messages__Container">
            <Helmet>
                <title>Mensajes - My Online Phonebook</title>
            </Helmet>

            <Header />
            <NavBar />
            <ProfilePicture />

            <div className="Messages__Nav">
                <button onClick={() => setIsReceived(true)} className={isReceived ? 'active' : ''}>Recibidos</button>
                <button onClick={() => setIsReceived(false)} className={!isReceived ? 'active' : ''}>Enviados</button>
            </div>

            <div className="Messages__List__Container">
            {loading ? (
                    <div className="Messages__Spinner"></div>
                ) : error ? (
                    <div className="Messages__Error"><p>{error}</p></div>
                ) : (
                    renderMessages(renderMessages())
                )}
            </div>

            <div className="Messages__ButtonContainer">
                <button className="Messages__Button Messages__ComposeButton " onClick={() => navigate('/messages/compose')}>Escribir mensaje</button>
                <button className="Messages__Button" onClick={() => {
                    localStorage.removeItem('token');
                    localStorage.removeItem('username');
                    localStorage.removeItem('email');
                    localStorage.removeItem('id');
                    navigate('/login');
                }}>Cerrar sesión</button>
            </div>
        </div>
    );
};

export default Messages;
