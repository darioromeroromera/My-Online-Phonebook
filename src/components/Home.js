import { Helmet } from "react-helmet";
import { Link, useNavigate } from "react-router-dom";
import Header from "./Header";
import './css/Home.css';

const Home = () => {
    const navigate = useNavigate();

    const mockUsername = localStorage.getItem('username');
    
    return (
        <div className="Home__Container">
            <Helmet>
                <title>Inicio - My Online Phonebook</title>
            </Helmet>

            <Header/>

            <div className="Home__Profile">
                <h2 className="Home__Profile__Message">Bienvenido, {mockUsername}</h2>
                <div className="Home__Profile__ImgSet" onClick={() => navigate('/profile')}>
                <img className="Home__Profile__ProfilePicture" src="img.jpg" alt="Foto de perfil"/>
                <img className="Home__Profile__EditIcon" src="edit.png" alt="Icono de lápiz"/>
                </div>
            </div>

            <div>
                <input className="Home__Search__Input" type="text" placeholder="Busca contactos por nombre o número"/>
            </div>
            <div className="Home__ContactList__Container">

            <div className="Home__ContactList">
                <div className="Home__ContactCard">
                    <img className="Home__ContactCard__Picture" src="img.jpg" alt="Foto de contacto"/>
                    <p className="Home__ContactCard__Name">Ramiro</p>
                    <p className="Home__ContactCard__FullName">Ramiro Móstoles Hichünen</p>
                    <p className="Home__ContactCard__Phone">612345678</p>
                    <p className="Home__ContactCard__Details">Es mi vecino de al lado. De vez en cuando le presto sal :D</p>
                    <button className="Home__ContactCard__Button">Eliminar</button>
                </div>

                <div className="Home__ContactCard">
                    <img className="Home__ContactCard__Picture" src="img.jpg" alt="Foto de contacto"/>
                    <p className="Home__ContactCard__Name">Ramiro</p>
                    <p className="Home__ContactCard__FullName">Ramiro Móstoles Hichünen</p>
                    <p className="Home__ContactCard__Phone">612345678</p>
                    <p className="Home__ContactCard__Details">Es mi vecino de al lado. De vez en cuando le presto sal :D</p>
                    <button className="Home__ContactCard__Button">Eliminar</button>
                </div>

                <div className="Home__ContactCard">
                    <img className="Home__ContactCard__Picture" src="img.jpg" alt="Foto de contacto"/>
                    <p className="Home__ContactCard__Name">Ramiro</p>
                    <p className="Home__ContactCard__FullName">Ramiro Móstoles Hichünen</p>
                    <p className="Home__ContactCard__Phone">612345678</p>
                    <p className="Home__ContactCard__Details">Es mi vecino de al lado. De vez en cuando le presto sal :D</p>
                    <button className="Home__ContactCard__Button">Eliminar</button>
                </div>

                <div className="Home__ContactCard">
                    <img className="Home__ContactCard__Picture" src="img.jpg" alt="Foto de contacto"/>
                    <p className="Home__ContactCard__Name">Ramiro</p>
                    <p className="Home__ContactCard__FullName">Ramiro Móstoles Hichünen</p>
                    <p className="Home__ContactCard__Phone">612345678</p>
                    <p className="Home__ContactCard__Details">Es mi vecino de al lado. De vez en cuando le presto sal :D</p>
                    <button className="Home__ContactCard__Button">Eliminar</button>
                </div>

                <div className="Home__ContactCard">
                    <img className="Home__ContactCard__Picture" src="img.jpg" alt="Foto de contacto"/>
                    <p className="Home__ContactCard__Name">Ramiro</p>
                    <p className="Home__ContactCard__FullName">Ramiro Móstoles Hichünen</p>
                    <p className="Home__ContactCard__Phone">612345678</p>
                    <p className="Home__ContactCard__Details">Es mi vecino de al lado. De vez en cuando le presto sal :D</p>
                    <button className="Home__ContactCard__Button">Eliminar</button>
                </div>

                <div className="Home__ContactCard">
                    <img className="Home__ContactCard__Picture" src="img.jpg" alt="Foto de contacto"/>
                    <p className="Home__ContactCard__Name">Ramiro</p>
                    <p className="Home__ContactCard__FullName">Ramiro Móstoles Hichünen</p>
                    <p className="Home__ContactCard__Phone">612345678</p>
                    <p className="Home__ContactCard__Details">Es mi vecino de al lado. De vez en cuando le presto sal :D</p>
                    <button className="Home__ContactCard__Button">Eliminar</button>
                </div>
            </div>
            </div>

            <button className="Home__Logout__Button" onClick={() => {
                localStorage.removeItem('token');
                localStorage.removeItem('username');
                localStorage.removeItem('email');
                navigate('/login');
            }}>Cerrar Sesión</button>
        </div>
    );
};

export default Home;
