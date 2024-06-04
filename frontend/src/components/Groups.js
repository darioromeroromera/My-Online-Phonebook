import { Helmet } from "react-helmet";
import Header from "./Header";
import NavBar from "./NavBar";

const Groups = () => {
    return (
        <div className="Home__Container">
            <Helmet>
                <title>Contactos - My Online Phonebook</title>
            </Helmet>

            <Header/>

            <NavBar/>   

        </div>
    );
};

export default Groups;