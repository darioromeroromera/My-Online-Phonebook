import React from 'react';
import './css/ContactCard.css';

const ContactCard = React.memo(({name, fullname, phone, details, picture, removeContact, id}) => {
    return (
        <div className="ContactCard">
            <img className="ContactCard__Picture" src={picture === null ? 'empty-profile-logo.png' : picture} alt="Foto de contacto"/>
            <p className="ContactCard__Name">{name}</p>
            <p className="ContactCard__FullName">{fullname}</p>
            <p className="ContactCard__Phone">{phone}</p>
            <p className="ContactCard__Details">{details}</p>
            <button className="ContactCard__Button" onClick={() => {removeContact(id)}}>Eliminar</button>
        </div>
    )
});

export default ContactCard;