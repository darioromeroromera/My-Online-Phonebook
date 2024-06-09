import { Helmet } from "react-helmet";
import Header from "./Header";
import NavBar from "./NavBar";
import ProfilePicture from "./ProfilePicture";
import { useParams } from "react-router-dom";

const SingleMessage = () => {
    const {id} = useParams();


    return (
        <div>
            <Helmet>
                <title>Mensajes - My Online Phonebook</title>
            </Helmet>

            <Header/>
            <NavBar/>

            <ProfilePicture/>

        </div>
    );
};

export default SingleMessage;