import { Helmet } from "react-helmet";
import { useNavigate } from "react-router-dom";
import Header from "./Header";
import './css/HomeAndContacts.css';
import { useEffect, useState } from "react";
import ContactCard from "./ContactCard";
import NavBar from "./NavBar";
import ProfilePicture from "./ProfilePicture";

const Contacts = () => {
    const navigate = useNavigate();

    const [contacts, setContacts] = useState([]);

    const [groups, setGroups] = useState([]);

    const [groupedContacts, setGroupedContacts] = useState([]);

    const [isContactsErrorVisible, setIsContactErrorVisible] = useState(false);

    const [contactError, setContactError] = useState('');

    const [loading, setLoading] = useState(false);

    const [search, setSearch] = useState("");

    const [filteredContacts, setFilteredContacts] = useState([]);

    const [selectedGroup, setSelectedGroup] = useState('Todos');

    const getContacts = async () => {
        if (loading)
            return;
        setLoading(true);
        try {
            const data = await fetch('http://localhost:8080/api/contacts', {
                headers: {
                    Bearer: localStorage.getItem('token')
                },
                mode: 'cors'
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
                setGroupedContacts(json.data);
                console.log(json.data);
            }

        } catch (err) {
            setContactError('Error: no se ha podido establecer conexión con el servidor');
            setIsContactErrorVisible(true);
        }
        setLoading(false);
    }

    const getGroups = async () => {
        try {
            const data = await fetch('http://localhost:8080/api/groups', {
                headers: {
                    Bearer: localStorage.getItem('token')
                },
                mode: 'cors'
            });

            const json = await data.json();

            if (json.result === undefined) {
                setContactError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                setIsContactErrorVisible(true);
            } else if (json.result === 'error') {
                setContactError(json.details);
                setIsContactErrorVisible(true);
            } else {
                setGroups(json.data);
            }

        } catch (err) {
            setContactError('Error: no se ha podido establecer conexión con el servidor');
            setIsContactErrorVisible(true);
        }
    }

    useEffect(() => {
        getContacts();
        getGroups();
    }, []);

    const filterContacts = () => {
        setFilteredContacts(groupedContacts.filter(contact => contact.contact_name.toLowerCase().includes(search.toLowerCase()) || contact.full_name.toLowerCase().includes(search.toLowerCase()) || contact.telefono.includes(search)));
    }

    useEffect(() => {
        if (contacts != []) {
            handleGroupFilter();
        }
    }, [contacts]);

    useEffect(() => {
        filterContacts();
    }, [groupedContacts, search]);

    const groupMap = {};
    groups.forEach(group => {
        groupMap[group.id] = group.name;
    });

    const handleGroupFilter = () => {
        if (selectedGroup == 'Todos') {
            setGroupedContacts(contacts);
        } else {
            setGroupedContacts(contacts.filter(contact => contact.group_id == selectedGroup));
        }
    };

    const renderContacts = () => {
        if (loading) 
            return (
                <div className="Home__Spinner__Div">
                    <div className="Home__Spinner"></div>    
                </div>
            )
        else if (!isContactsErrorVisible) {
            return filteredContacts.length > 0 ?
                <div className="Contacts__ContactList">
                {filteredContacts.map(contact => (
                    <ContactCard removeContact={removeContact} removePicture={removeContactPicture} key={contact.id} id={contact.id} name={contact.contact_name} fullname={contact.full_name} 
                    phone={contact.telefono} details={contact.details} picture={contact.contact_picture} group={groupMap[contact.group_id] || 'Ninguno'}/>
                ))}
            </div> :
                <p>{search == '' ? 'No hay contactos' : 'No se han encontrado contactos con ese filtro'}</p>
                
        }
    }

    const removeContact = async id => {
        try {
            const data = await fetch('http://localhost:8080/api/contacts/' + id, {
                method: 'DELETE',
                headers: {
                    Bearer: localStorage.getItem('token')
                },
                mode: 'cors'
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
                handleGroupFilter();
            }

        } catch (err) {
            setContactError('Error: no se ha podido establecer conexión con el servidor');
            setIsContactErrorVisible(true);
        }
    }

    const removeContactPicture = async id => {
        try {
            const data = await fetch('http://localhost:8080/api/contacts/' + id + '/picture', {
                method: 'DELETE',
                headers: {
                    Bearer: localStorage.getItem('token')
                },
                mode: 'cors'
            });
    
            const json = await data.json();

            if (json.result === undefined) {
                setContactError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                setIsContactErrorVisible(true);
            } else if (json.result === 'error') {
                setContactError(json.details);
                setIsContactErrorVisible(true);
            } else {
                const updatedContact = json.data;
                setContacts(contacts => contacts.map(contact => contact.id === id ? updatedContact : contact));
                handleGroupFilter();
            }

        } catch (err) {
            setContactError('Error: no se ha podido establecer conexión con el servidor');
            setIsContactErrorVisible(true);
        }
    }

    
    return (
        <div className="Home__Container">
            <Helmet>
                <title>Contactos - My Online Phonebook</title>
            </Helmet>

            <Header/>

            <NavBar/>

            <ProfilePicture/>

            <div>
                <input className="Contacts__Search__Input" type="text" value={search} placeholder="Busca contactos por nombre o número"
                    onChange={e => {
                        setSearch(e.target.value);
                    }}/>
            </div>

            <div className="Contacts__Filter__Container">
                <select value={selectedGroup} onChange={e => setSelectedGroup(e.target.value)}>
                    <option value="Todos">Todos</option>
                    {groups.map(group => (
                        <option key={group.id} value={group.id}>
                            {group.name}
                        </option>
                    ))}
                </select>
                <button onClick={handleGroupFilter}>Filtrar</button>
            </div>

            <div className="Contacts__ContactList__Container">
                <div className={isContactsErrorVisible ? 'Home__Error' : 'Home__Hidden'}>
                    <p>{contactError}</p>
                </div>
                {renderContacts()}
            </div>

            <div className="Home__ButtonContainer">
            <button className="Home__Button Home__AddUser__Button" onClick={() => navigate('/contacts/add')}>Añadir usuario</button>

            <button className="Home__Button" onClick={() => {
                localStorage.removeItem('token');
                localStorage.removeItem('username');
                localStorage.removeItem('email');
                localStorage.removeItem('id');
                navigate('/login');
            }}>Cerrar Sesión</button>
            </div>

        </div>
    );
    
};

export default Contacts;
