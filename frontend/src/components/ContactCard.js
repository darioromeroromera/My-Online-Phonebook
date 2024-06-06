import React from 'react';
import './css/ContactCard.css';
import {useNavigate} from 'react-router-dom';

const ContactCard = React.memo(({name, fullname, phone, details, picture, removeContact, removePicture, group, id}) => {

    const navigate = useNavigate();

    return (
        <div className="ContactCard">
            <img className="ContactCard__Picture" src={picture === null ? 'empty-profile-logo.png' : picture} alt="Foto de contacto"/>
            <p className="ContactCard__Name">{name}</p>
            <p className="ContactCard__FullName">{fullname}</p>
            <p className="ContactCard__Phone">{phone}</p>
            <p className="ContactCard__Details">{details}</p>
            <p className="ContactCard__Details">Grupo: {group}</p>
            <button className="ContactCard__Button" onClick={() => {navigate('/contacts/edit/' + id)}}>Modificar</button>
            <button className="ContactCard__Button ContactCard__DeleteButton" onClick={() => {removeContact(id)}}>Eliminar</button>
            {picture != null ? <button className="ContactCard__Button ContactCard__DeleteButton" onClick={() => {removePicture(id)}}>Eliminar foto</button> : ''}
        </div>
    )
});

export default ContactCard;