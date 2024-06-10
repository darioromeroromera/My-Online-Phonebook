import React, { useState, useEffect } from 'react';
import { NavLink } from 'react-router-dom';
import './css/NavBar.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBell } from '@fortawesome/free-solid-svg-icons';

const NavBar = () => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [newMessages, setNewMessages] = useState(0);

    const toggleMenu = () => {
        setIsMenuOpen(!isMenuOpen);
    };

    const checkNewMessages = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/messages/check-new', {
                headers: {
                    Bearer: localStorage.getItem('token')
                },
                mode: 'cors'
            });
            const json = await response.json();
            if (json.result === 'ok') {
                setNewMessages(json.data);
            }
        } catch (error) {
            
        }
    };

    useEffect(() => {
        checkNewMessages();
    }, []);

    return (
        <nav className="NavBar__NavBar">
            <button className="NavBar__Hamburger" onClick={toggleMenu}>
                <span>☰</span>
            </button>
            <div className={`NavBar__Links ${isMenuOpen ? 'NavBar__Links--open' : ''}`}>
                <NavLink to="/" className="NavBar__NavLink" activeclassname="active">Inicio</NavLink>
                <NavLink to="/contacts" className="NavBar__NavLink" activeclassname="active">Contactos</NavLink>
                <NavLink to="/groups" className="NavBar__NavLink" activeclassname="active">Grupos</NavLink>
                <NavLink to="/messages" className="NavBar__NavLink" activeclassname="active">
                <div className='NavBar__MessageContainer'>
                    <span>Mensajes</span>
                    {newMessages > 0 && 
                        <button type="button" className="NavBar__IconButton">
                            <FontAwesomeIcon icon={faBell} />
                            <span class="NavBar__MessageNumber">{newMessages}</span>
                        </button>}
                </div>
                </NavLink>
                <NavLink to="/profile" className="NavBar__NavLink" activeclassname="active">Perfil</NavLink>
            </div>
        </nav>
    );
};

export default NavBar;
