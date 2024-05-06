import { useEffect, useState } from "react";
import ContactCard from "./ContactCard";



const ContactList = () => {
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
                    <ContactCard removeContact={removeContact} key={contact.id} id={contact.id} name={contact.contact_name} fullname={contact.full_name} 
                    phone={contact.telefono} details={contact.details} picture={contact.contact_picture}/>
                ))}
            </div> :
                <p>No contacts found.</p>
                
        }
    }

    const removeContact= async id => {
        if (loading)
            return;
        setLoading(true);
        try {
            const data = await fetch('http://localhost:8080/api/contacts/' + id, {
                method: 'DELETE',
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
                setContacts(contacts => contacts.filter(contact => contact.id != id));
            }

        } catch (err) {
            setContactError('Error: no se ha podido establecer conexión con el servidor');
            setIsContactErrorVisible(true);
        }
        setLoading(false);
    }

    <div className="Home__ContactList__Container">
        <div className={isContactsErrorVisible ? 'Home__Error' : 'Home__Hidden'}>
            <p>{contactError}</p>
        </div>
        {renderContacts()}
    </div>
}

export default ContactList;