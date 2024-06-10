import { useEffect, useState } from "react";
import { Helmet } from "react-helmet";
import { useParams, useNavigate } from "react-router-dom";
import Header from "./Header";
import NavBar from "./NavBar";
import ProfilePicture from "./ProfilePicture";
import './css/ComposeAndReply.css';

const ComposeAndReply = ({ isReply }) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [contacts, setContacts] = useState([]);
    const [message, setMessage] = useState(null);
    const [error, setError] = useState(null);
    const [isErrorVisible, setIsErrorVisible] = useState(false);
    const [formData, setFormData] = useState({
        destination_phone: '',
        subject: '',
        text: ''
    });
    const [isSending, setIsSending] = useState(false);

    const getAvailableContacts = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/contacts/availables', {
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
                setContacts(json.data);
                if (json.data.length === 0) {
                    setFormData({ ...formData, destination_phone: '' });
                } else {
                    setFormData({ ...formData, destination_phone: json.data[0].telefono });
                }
            }
        } catch (err) {
            setError('Error: no se ha podido establecer conexión con el servidor');
        }
    };

    const getMessage = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/messages/${id}`, {
                headers: {
                    Bearer: localStorage.getItem('token')
                },
                mode: 'cors'
            });
            const json = await response.json();

            if (json.result === undefined) {
                setIsErrorVisible(true);
                setError('Ha ocurrido un error desconocido. Inténtelo más tarde');
            } else if (json.result === 'error') {
                setIsErrorVisible(true);
                setError(json.details);
            } else {
                if (json.data.origin_phone == localStorage.getItem('telefono')) {
                    setIsErrorVisible(true);                
                    setError('No se puede responder a un mensaje enviado por ti mismo');
                }
                else {
                    setIsErrorVisible(false);
                    setError('');
                    setMessage(json.data);
                    if (isReply) {
                        setContacts([{contact_name: json.data.contact_name, telefono: json.data.origin_phone}]);
                        setFormData({ ...formData, destination_phone: json.data.origin_phone, subject: formatSubject(json.data.subject) });
                    }
                }
            }
        } catch (err) {
            setIsErrorVisible(true);
            setError('Error: no se ha podido establecer conexión con el servidor');
        }
    };

    const formatSubject = subject => {
        if (subject.startsWith('Re:'))
            return subject;
        else
            return 'Re: ' + subject;
    };

    useEffect(() => {
        if (isReply) {
            getMessage();
        } else {
            getAvailableContacts();
        }
    }, [id, isReply]);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isSending)
            return;

        setIsSending(true);
        try {
            const response = await fetch('http://localhost:8080/api/messages', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Bearer: localStorage.getItem('token')
                },
                mode: 'cors',
                body: JSON.stringify(formData)
            });
            const json = await response.json();

            if (json.result === undefined) {
                setError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                setIsErrorVisible(true);
            } else if (json.result === 'error') {
                setIsErrorVisible(true);
                setError(json.details);
            } else {
                setIsErrorVisible(false);
                setError('');
                navigate('/messages');
            }
        } catch (err) {
            setIsErrorVisible(true);
            setError('Error: no se ha podido establecer conexión con el servidor');
        }
        setIsSending(false);
    };

    return (
        <div className="ComposeAndReply__Container">
            <Helmet>
                <title>{isReply ? 'Responder Mensaje' : 'Nuevo Mensaje'} - My Online Phonebook</title>
            </Helmet>
            <Header />
            <NavBar />
            <ProfilePicture />
            <div className="ComposeAndReply__FormContainer">
                <form onSubmit={handleSubmit} className="ComposeAndReply__Form">
                    <div>
                        <label htmlFor="destination_phone">Destinatario:</label>
                        <select id="destination_phone" name="destination_phone" value={formData.destination_phone} onChange={handleChange} disabled={contacts.length === 0}>
                            {contacts.length === 0 && <option value="">No hay contactos disponibles</option>}
                            {contacts.map(contact => (
                                <option key={contact.telefono} value={contact.telefono}>{contact.contact_name} - {contact.telefono}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label htmlFor="subject">Asunto:</label>
                        <input type="text" id="subject" name="subject" value={formData.subject} onChange={handleChange} required />
                    </div>
                    <div>
                        <label htmlFor="text">Mensaje:</label>
                        <textarea id="text" name="text" value={formData.text} onChange={handleChange} required></textarea>
                    </div>
                    <button type="submit" disabled={isSending}>{isReply ? 'Responder' : 'Enviar'}</button>
                </form>
            </div>
            {isErrorVisible && <p className="ComposeAndReply__Error">{error}</p>}
        </div>
    );
};

export default ComposeAndReply;
