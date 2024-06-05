import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import './css/NavBar.css';

const NavBar = () => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);

    const toggleMenu = () => {
        setIsMenuOpen(!isMenuOpen);
    };

    return (
        <nav className="NavBar__NavBar">
            <button className="NavBar__Hamburger" onClick={toggleMenu}>
                <span>☰</span>
            </button>
            <div className={`NavBar__Links ${isMenuOpen ? 'NavBar__Links--open' : ''}`}>
                <NavLink to="/" className="NavBar__NavLink" activeclassname="active">Inicio</NavLink>
                <NavLink to="/contacts" className="NavBar__NavLink" activeclassname="active">Contactos</NavLink>
                <NavLink to="/groups" className="NavBar__NavLink" activeclassname="active">Grupos</NavLink>
                <NavLink to="/profile" className="NavBar__NavLink" activeclassname="active">Perfil</NavLink>
            </div>
        </nav>
    );
};

export default NavBar;
