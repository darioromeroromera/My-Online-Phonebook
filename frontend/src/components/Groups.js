import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './css/Groups.css';
import NavBar from './NavBar';
import ProfilePicture from './ProfilePicture';
import { Helmet } from 'react-helmet';
import Header from './Header';

const Groups = () => {
    const navigate = useNavigate();
    const [groups, setGroups] = useState([]);
    const [newGroupName, setNewGroupName] = useState('');
    const [error, setError] = useState('');
    const [isErrorVisible, setIsErrorVisible] = useState(false);
    const [loading, setLoading] = useState(false);

    const [editingGroupId, setEditingGroupId] = useState(null);
    const [editedGroupName, setEditedGroupName] = useState('');

    const [search, setSearch] = useState("");
    const [filteredGroups, setFilteredGroups] = useState([]);

    const getGroups = async () => {
        if (loading) return;
        setLoading(true);

        try {
            const response = await fetch('http://localhost:8080/api/groups', {
                headers: {
                    Bearer: localStorage.getItem('token'),
                },
                mode: 'cors',
            });

            const json = await response.json();
            if (json.result === undefined) {
                setError('Ha ocurrido un error desconocido. Inténtelo más tarde');
                setIsErrorVisible(true);
            } else if (json.result === 'error') {
                setError(json.details);
                setIsErrorVisible(true);
            } else {
                setGroups(json.data);
                setIsErrorVisible(false);
            }
        } catch (err) {
            setError('Error: could not connect to the server');
            setIsErrorVisible(true);
        }
        setLoading(false);
    };

    useEffect(() => {
        getGroups();
    }, []);

    const filterGroups = () => {
        setFilteredGroups(groups.filter(group => group.name.toLowerCase().includes(search.toLowerCase())));
    };

    useEffect(() => {
        filterGroups();
    }, [groups, search]);

    const addGroup = async () => {
        if (loading)
            return;
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/groups', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Bearer: localStorage.getItem('token'),
                },
                mode: 'cors',
                body: JSON.stringify({ name: newGroupName }),
            });

            const json = await response.json();
            if (json.result === 'error') {
                setError(json.details);
                setIsErrorVisible(true);
            } else {
                setGroups([...groups, json.data]);
                setNewGroupName('');
                setIsErrorVisible(false);
            }
        } catch (err) {
            setError('Error: no se ha podido establecer conexión con el servidor');
            setIsErrorVisible(true);
        }
        setLoading(false);
    };

    const deleteGroup = async (id) => {
        if (loading)
            return;
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/api/groups/${id}`, {
                method: 'DELETE',
                headers: {
                    Bearer: localStorage.getItem('token'),
                },
                mode: 'cors',
            });

            const data = await response.json();
            if (data.result === 'error') {
                setError(data.details);
                setIsErrorVisible(true);
            } else {
                setGroups(groups.filter(group => group.id !== id));
                setIsErrorVisible(false);
            }
        } catch (err) {
            setError('Error: no se ha podido establecer conexión con el servidor');
            setIsErrorVisible(true);
        }
        setLoading(false);
    };

    const handleEditGroup = (groupId, groupName) => {
    setEditingGroupId(groupId);
    setEditedGroupName(groupName);
};

const handleSaveEditedGroup = async (groupId) => {
    try {
        const response = await fetch(`http://localhost:8080/api/groups/${groupId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                Bearer: localStorage.getItem('token'),
            },
            mode: 'cors',
            body: JSON.stringify({ name: editedGroupName }),
        });

        const json = await response.json();
        if (json.result === 'error') {
            setError(json.details);
            setIsErrorVisible(true);
        } else {
            setGroups(groups.map(group => (group.id === groupId ? { ...group, name: editedGroupName } : group)));
            setEditingGroupId(null);
            setEditedGroupName('');
            setIsErrorVisible(false);
        }
    } catch (err) {
        setError('Error: no se ha podido establecer conexión con el servidor');
        setIsErrorVisible(true);
    }
};

    const renderGroups = () => {
        return filteredGroups.length > 0 ? (
            <div className="Groups__List">
                {filteredGroups.map(group => (
                    <div key={group.id} className="Groups__Card">
                        {editingGroupId === group.id ? (
                            <input
                                type="text"
                                value={editedGroupName}
                                onChange={e => setEditedGroupName(e.target.value)}
                                onKeyDown={e => {
                                    if (e.key === 'Enter' ) {
                                        if (group.name == editedGroupName) {
                                            setEditedGroupName('');
                                            setEditingGroupId(null);
                                        } else {
                                            handleSaveEditedGroup(group.id);
                                        }
                                    }
                                }}
                            />
                        ) : (
                            <h3>{group.name}</h3>
                        )}
                        <div className='Groups__Card__Buttons'>
                            <button className='Groups__Card__EditButton' onClick={() => {
                                if (editingGroupId !== group.id) 
                                    handleEditGroup(group.id, group.name)
                                else if (group.name != editedGroupName) {
                                    handleSaveEditedGroup(group.id);
                                } else {
                                    setEditedGroupName('');
                                    setEditingGroupId(null);
                                }}}>
                                {editingGroupId === group.id ? 'Guardar' : 'Modificar'}
                            </button>
                            <button className='Groups__Card__DeleteButton' 
                            style={{display: editingGroupId === group.id ? 'block' : 'none'}}
                            onClick={() => {
                                setEditingGroupId(null);
                            }}>Cancelar</button>
                            <button className='Groups__Card__DeleteButton' onClick={() => deleteGroup(group.id)}>Eliminar</button>
                        </div>
                    </div>
            ))}
            </div>
        ) : (
            <p>{search == '' ? 'No hay contactos' : 'No se han encontrado contactos con ese filtro'}</p>
        );
    };

    return (
        <div className="Groups__Container">
            <Helmet>
                <title>Grupos - My Online Phonebook</title>
            </Helmet>

            <Header/>

            <NavBar/>

            <ProfilePicture/>

            <input
                className='Groups__Search__Input'
                type="text"
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Buscar grupos por nombre"
            />

            <div className="Groups__AddGroup">
                <input
                    type="text"
                    value={newGroupName}
                    onChange={(e) => setNewGroupName(e.target.value)}
                    placeholder="Nombre de nuevo grupo"
                    onKeyDown={e => {
                        if (e.key == 'Enter')
                            addGroup();
                    }}
                />
                <button onClick={addGroup}>Añadir Grupo</button>
            </div>
            <div className={isErrorVisible ? 'Groups__Error' : 'Groups__Hidden'}>
                <p>{error}</p>
            </div>
            {renderGroups()}

            <button className="Groups__LogoutButton" onClick={() => {
                localStorage.removeItem('token');
                localStorage.removeItem('username');
                localStorage.removeItem('email');
                localStorage.removeItem('id');
                navigate('/login');
            }}>Cerrar Sesión</button>
        </div>
    );
};

export default Groups;
